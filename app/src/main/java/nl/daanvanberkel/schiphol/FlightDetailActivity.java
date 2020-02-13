package nl.daanvanberkel.schiphol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import nl.daanvanberkel.schiphol.models.Flight;

public class FlightDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FLIGHT = "nl.daanvanberkel.schiphol.EXTRA_FLIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);

        Intent intent = getIntent();
        Flight flight = (Flight) intent.getSerializableExtra(EXTRA_FLIGHT);

        if (flight == null) {
            return;
        }

        setTitle(String.format("Vlucht %s", flight.getName()));

        Bundle arguments = new Bundle();
        arguments.putSerializable(FlightDetailFragment.ARG_FLIGHT, flight);

        // Load detail fragment
        FlightDetailFragment detailFragment = new FlightDetailFragment();
        detailFragment.setArguments(arguments);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flight_detail_fragment_container, detailFragment)
                .commit();
    }
}
