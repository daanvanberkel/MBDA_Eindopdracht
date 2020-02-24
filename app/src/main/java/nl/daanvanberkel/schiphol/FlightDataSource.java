package nl.daanvanberkel.schiphol;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PageKeyedDataSource;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nl.daanvanberkel.schiphol.helpers.FlightParser;
import nl.daanvanberkel.schiphol.models.Flight;
import nl.daanvanberkel.schiphol.requests.SchipholRequest;

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

        String url = "/public-flights/flights?flightDirection=D&includedelays=false&page=" + page + "&sort=+scheduleDateTime&fromDateTime=" + date + "&searchDateTimeField=scheduleDateTime";

        SchipholRequest jsonObjectRequest = new SchipholRequest(Request.Method.GET, url, null, response -> {
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
        }, error -> {
            error.printStackTrace();
            Toast.makeText(context, "Vluchten kunnen niet worden weergegeven zonder internetverbinding", Toast.LENGTH_LONG).show();
        });

        queue.add(jsonObjectRequest);
    }
}
