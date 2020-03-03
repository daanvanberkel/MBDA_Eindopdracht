package nl.daanvanberkel.schiphol.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.time.LocalDateTime;
import java.util.List;

import nl.daanvanberkel.schiphol.R;
import nl.daanvanberkel.schiphol.helpers.FlightParser;
import nl.daanvanberkel.schiphol.models.FavoriteFlights;
import nl.daanvanberkel.schiphol.models.Flight;
import nl.daanvanberkel.schiphol.requests.SchipholRequest;
import nl.daanvanberkel.schiphol.viewmodels.FlightDetailViewModel;

public class FavoriteFlightService extends JobService {
    public static final String CHANNEL_ID = "nl.daanvanberkel.schiphol.CHANNEL_NAME";
    public static final int GATE_CHANGE_NOTIFICATION_ID = 1;
    public static final int DELAYED_NOTIFICATION_ID = 2;
    public static final int CANCELLED_NOTIFICATION_ID = 3;
    public static final int GATE_OPEN_NOTIFICATION_ID = 4;
    public static final int GATE_CLOSED_NOTIFICATION_ID = 5;
    public static final int BOARDING_NOTIFICATION_ID = 6;
    public static final int DEPARTED_NOTIFICATION_ID = 7;
    public static final int FLIGHT_REMOVED_NOTIFICATION_ID = 8;

    @Override
    public boolean onStartJob(JobParameters params) {
        // Create notification channel
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        FavoriteFlights favoriteFlights = loadFavoriteFlights();
        List<Flight> flights = favoriteFlights.getFlights();

        if (flights.size() > 0) {
            for(int i = 0; i < flights.size(); i++) {
                final Flight flight = flights.get(i);

                getFlight(flight.getId(), response -> {
                    if (response == null) {
                        return;
                    }

                    LocalDateTime flightDate;

                    if (flight.getEstimatedDateTime() != null) {
                        flightDate = flight.getEstimatedDateTime();
                    } else {
                        flightDate = flight.getScheduleDateTime();
                    }

                    // Remove out-dated flights
                    if (flightDate.compareTo(LocalDateTime.now()) < 0) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.flight_departed_title))
                                .setContentText(String.format(getApplicationContext().getString(R.string.flight_departed_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), FLIGHT_REMOVED_NOTIFICATION_ID, builder.build());

                        removeFavoriteFlight(flight);
                        return;
                    }

                    replaceFavoriteFlight(flight, response);

                    // Check for changed gate
                    if (!response.getGate().equals(flight.getGate())) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.gate_change))
                                .setContentText(String.format(getApplicationContext().getString(R.string.gate_change_desc), response.getName(), flight.getGate(), response.getGate()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), GATE_CHANGE_NOTIFICATION_ID, builder.build());
                    }

                    // Check for delayed
                    if (response.hasState("DEL") && !flight.hasState("DEL")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.flight_delayed_title))
                                .setContentText(String.format(getApplicationContext().getString(R.string.flight_delayed_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), DELAYED_NOTIFICATION_ID, builder.build());
                    }

                    // Check for cancelled
                    if (response.hasState("CNX") && !flight.hasState("CNX")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.flight_cancelled_title))
                                .setContentText(String.format(getApplicationContext().getString(R.string.flight_cancelled_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), CANCELLED_NOTIFICATION_ID, builder.build());
                    }

                    // Check for gate open
                    if (response.hasState("GTO") && !flight.hasState("GTO")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.gate_open))
                                .setContentText(String.format(getApplicationContext().getString(R.string.gate_open_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), GATE_OPEN_NOTIFICATION_ID, builder.build());
                    }

                    // Check for gate closed
                    if (response.hasState("GTD") && !flight.hasState("GTD")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.gate_closed))
                                .setContentText(String.format(getApplicationContext().getString(R.string.gate_closed_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), GATE_CLOSED_NOTIFICATION_ID, builder.build());
                    }

                    // Check for boarding
                    if (response.hasState("BRD") && !flight.hasState("BRD")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.boarding_start_title))
                                .setContentText(String.format(getApplicationContext().getString(R.string.boarding_start_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), BOARDING_NOTIFICATION_ID, builder.build());
                    }

                    // Check for departed
                    if (response.hasState("DEP") && !flight.hasState("DEP")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FavoriteFlightService.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_airplane)
                                .setContentTitle(getApplicationContext().getString(R.string.flight_departed_title))
                                .setContentText(String.format(getApplicationContext().getString(R.string.flight_departed_desc), response.getName()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(flight.getId(), DEPARTED_NOTIFICATION_ID, builder.build());
                    }
                });

                if (i == flights.size() - 1) {
                    jobFinished(params, true);
                }
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void replaceFavoriteFlight(Flight oldFlight, Flight newFlight) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();
        favoriteFlights.removeFlight(oldFlight);
        favoriteFlights.addFlight(newFlight);
        saveFavoriteFlights(favoriteFlights);
    }

    private void removeFavoriteFlight(Flight flight) {
        FavoriteFlights favoriteFlights = loadFavoriteFlights();
        favoriteFlights.removeFlight(flight);
        saveFavoriteFlights(favoriteFlights);
    }

    private FavoriteFlights loadFavoriteFlights() {
        try {
            FileInputStream fileInputStream = openFileInput(FlightDetailViewModel.FAVORITE_FLIGHTS_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            FavoriteFlights favoriteFlights = (FavoriteFlights) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return favoriteFlights;
        } catch (IOException |ClassNotFoundException e) {
            e.printStackTrace();
            return new FavoriteFlights();
        }
    }

    private void saveFavoriteFlights(FavoriteFlights favoriteFlights) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(FlightDetailViewModel.FAVORITE_FLIGHTS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(favoriteFlights);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFlight(String id, final Response.Listener<Flight> listener) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "/public-flights/flights/" + id;

        SchipholRequest jsonObjectRequest = new SchipholRequest(Request.Method.GET, url, null, response -> {
            Flight flight = FlightParser.parse(response);

            listener.onResponse(flight);
        }, error -> listener.onResponse(null));

        queue.add(jsonObjectRequest);
    }
}
