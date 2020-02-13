package nl.daanvanberkel.schiphol.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

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

    public List<Flight> getFavoriteFlights() {
        return loadFavoriteFlights().getFlights();
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

    public void getAircraftType(Flight flight, final Response.Listener<AircraftType> listener) {
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/aircrafttypes?iataMain=" + flight.getAircraftType().getIataMain() + "&iataSub=" + flight.getAircraftType().getIataSub();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<AircraftType> aircraftTypes = new ArrayList<>();

                if (response.has("aircraftTypes")) {
                     aircraftTypes = AircraftTypeParser.parse(response.optJSONArray("aircraftTypes"));
                }

                if (aircraftTypes.size() > 0) {
                    listener.onResponse(aircraftTypes.get(0));
                } else {
                    listener.onResponse(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(null);
            }
        }) {
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
    }

    public void getDestination(String iata, final Response.Listener<Destination> listener) {
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/destinations/" + iata;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Destination destination = DestinationParser.parse(response);

                listener.onResponse(destination);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(null);
            }
        }) {
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
    }

    public void getAirline(String icao, final Response.Listener<Airline> listener) {
        RequestQueue queue = Volley.newRequestQueue(getApplication().getApplicationContext());

        String url = "https://api.schiphol.nl/public-flights/airlines/" + icao;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Airline airline = AirlineParser.parse(response);

                listener.onResponse(airline);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(null);
            }
        }) {
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
    }
}
