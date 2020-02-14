package nl.daanvanberkel.schiphol.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.daanvanberkel.schiphol.helpers.AircraftTypeParser;
import nl.daanvanberkel.schiphol.helpers.AirlineParser;
import nl.daanvanberkel.schiphol.helpers.DestinationParser;
import nl.daanvanberkel.schiphol.helpers.SchipholApiCredentials;
import nl.daanvanberkel.schiphol.models.AircraftType;
import nl.daanvanberkel.schiphol.models.Airline;
import nl.daanvanberkel.schiphol.models.Destination;
import nl.daanvanberkel.schiphol.models.FavoriteFlights;
import nl.daanvanberkel.schiphol.models.Flight;

public class FlightDetailViewModel extends AndroidViewModel {
    public static final String FAVORITE_FLIGHTS_FILENAME = "favorite_flights";

    public FlightDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean hasFavoriteFlight(Flight flight) {
        return loadFavoriteFlights().hasFlight(flight);
    }

    public void addFavoriteFlight(Flight flight) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();

        if (favoriteFlights.hasFlight(flight)) {
            return;
        }

        favoriteFlights.addFlight(flight);
        saveFavoriteFlights(favoriteFlights);
    }

    public void removeFavoriteFlight(Flight flight) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();

        if (!favoriteFlights.hasFlight(flight)) {
            return;
        }

        favoriteFlights.removeFlight(flight);
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

    public LiveData<AircraftType> getAircraftType(Flight flight) {
        MutableLiveData<AircraftType> aircraftTypeLiveData = new MutableLiveData<>();

        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/aircrafttypes?iataMain=" + flight.getAircraftType().getIataMain() + "&iataSub=" + flight.getAircraftType().getIataSub();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            List<AircraftType> aircraftTypes = new ArrayList<>();

            if (response.has("aircraftTypes")) {
                 aircraftTypes = AircraftTypeParser.parse(response.optJSONArray("aircraftTypes"));
            }

            if (aircraftTypes.size() > 0) {
                aircraftTypeLiveData.postValue(aircraftTypes.get(0));
            }
        }, error -> error.printStackTrace()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("app_id", SchipholApiCredentials.APP_ID);
                params.put("app_key", SchipholApiCredentials.APP_KEY);
                params.put("ResourceVersion", SchipholApiCredentials.RESOURCE_VERSION);

                return params;
            }
        };

        queue.add(jsonObjectRequest);

        return aircraftTypeLiveData;
    }

    public LiveData<Destination> getDestination(String iata) {
        MutableLiveData<Destination> destinationLiveData = new MutableLiveData<>();

        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/destinations/" + iata;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Destination destination = DestinationParser.parse(response);

            destinationLiveData.postValue(destination);
        }, error -> error.printStackTrace()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("app_id", SchipholApiCredentials.APP_ID);
                params.put("app_key", SchipholApiCredentials.APP_KEY);
                params.put("ResourceVersion", SchipholApiCredentials.RESOURCE_VERSION);

                return params;
            }
        };

        queue.add(jsonObjectRequest);

        return destinationLiveData;
    }

    public LiveData<Airline> getAirline(String icao) {
        MutableLiveData<Airline> airlineLiveData = new MutableLiveData<>();

        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/airlines/" + icao;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                Airline airline = AirlineParser.parse(response);

                airlineLiveData.postValue(airline);
        }, error -> error.printStackTrace()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("app_id", SchipholApiCredentials.APP_ID);
                params.put("app_key", SchipholApiCredentials.APP_KEY);
                params.put("ResourceVersion", SchipholApiCredentials.RESOURCE_VERSION);

                return params;
            }
        };

        queue.add(jsonObjectRequest);

        return airlineLiveData;
    }
}
