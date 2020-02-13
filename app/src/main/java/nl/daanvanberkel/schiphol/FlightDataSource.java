package nl.daanvanberkel.schiphol;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PageKeyedDataSource;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.daanvanberkel.schiphol.helpers.FlightParser;
import nl.daanvanberkel.schiphol.models.Flight;

public class FlightDataSource extends PageKeyedDataSource<Integer, Flight> {

    private Context context;

    public FlightDataSource(Context context) {
        this.context = context;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Flight> callback) {
        loadFlights(0, new LoadCallback<Integer, Flight>() {
            @Override
            public void onResult(@NonNull List<Flight> data, @Nullable Integer adjacentPageKey) {
                callback.onResult(data, null, 1);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Flight> callback) {
        loadFlights(params.key, callback);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Flight> callback) {
        loadFlights(params.key, callback);
    }

    private void loadFlights(final int page, final LoadCallback<Integer, Flight> callback) {
        VolleyLog.DEBUG = true;

        RequestQueue queue = Volley.newRequestQueue(context);

        Calendar currentTime = Calendar.getInstance();

        // Get current date to filter flights
        String date = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d",
                currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH) + 1,
                currentTime.get(Calendar.DAY_OF_MONTH),
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                currentTime.get(Calendar.SECOND));

        String url = "https://api.schiphol.nl/public-flights/flights?flightDirection=D&includedelays=false&page=" + page + "&sort=%2BscheduleDate%2C%2BscheduleTime&fromDateTime=" + date + "&searchDateTimeField=scheduleDateTime";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Flight> flights = new ArrayList<>();

                if (response.has("flights")) {
                    flights = FlightParser.parse(response.optJSONArray("flights"));
                }

                Integer adjacentPage;

                if (flights.size() < 1) {
                    adjacentPage = null;
                } else {
                    adjacentPage = page + 1;
                }

                callback.onResult(flights, adjacentPage);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("app_id", "a22cc89c");
                params.put("app_key", "5b4221b0f185295c5a3bbbbb84a7c356");
                params.put("ResourceVersion", "v4");

                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
