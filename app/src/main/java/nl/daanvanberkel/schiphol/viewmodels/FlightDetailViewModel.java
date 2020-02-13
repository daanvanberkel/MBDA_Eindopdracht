package nl.daanvanberkel.schiphol.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nl.daanvanberkel.schiphol.models.FavoriteFlights;

public class FlightDetailViewModel extends AndroidViewModel {
    private static final String FAVORITE_FLIGHTS_FILENAME = "favorite_flights";

    public FlightDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean hasFavoriteFlight(String id) {
        return loadFavoriteFlights().hasFlightId(id);
    }

    public List<String> getFavoriteFlightIds() {
        return loadFavoriteFlights().getFlightIds();
    }

    public void addFavoriteFlight(String id) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();

        if (favoriteFlights.hasFlightId(id)) {
            return;
        }

        favoriteFlights.addFlightId(id);
        saveFavoriteFlights(favoriteFlights);
    }

    public void removeFavoriteFlight(String id) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();

        if (!favoriteFlights.hasFlightId(id)) {
            return;
        }

        favoriteFlights.removeFlightId(id);
        saveFavoriteFlights(favoriteFlights);
    }

    private FavoriteFlights loadFavoriteFlights() {
        try {
            FileInputStream fileInputStream = getApplication().getApplicationContext().openFileInput(FAVORITE_FLIGHTS_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            FavoriteFlights favoriteFlights = (FavoriteFlights) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return favoriteFlights;
        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
            return new FavoriteFlights();
        }
    }

    private void saveFavoriteFlights(FavoriteFlights favoriteFlights) {
        try {
            FileOutputStream fileOutputStream = getApplication().getApplicationContext().openFileOutput(FAVORITE_FLIGHTS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(favoriteFlights);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
