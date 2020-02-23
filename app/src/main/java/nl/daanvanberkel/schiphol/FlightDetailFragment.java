package nl.daanvanberkel.schiphol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import nl.daanvanberkel.schiphol.helpers.FlightParser;
import nl.daanvanberkel.schiphol.models.Flight;
import nl.daanvanberkel.schiphol.viewmodels.FlightDetailViewModel;

public class FlightDetailFragment extends Fragment {

    public static final String ARG_FLIGHT = "nl.daanvanberkel.schiphol.ARG_FLIGHT";

    private FlightDetailViewModel viewModel;
    private Flight flight;

    public FlightDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flight_detail_fragment, container, false);
        setHasOptionsMenu(true);

        try {
            viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(FlightDetailViewModel.class);
            flight = (Flight) getArguments().getSerializable(ARG_FLIGHT);
        } catch (NullPointerException e) {
            return view;
        }


        TextView flightNameView = view.findViewById(R.id.flight_detail_name);
        TextView flightDateTimeView = view.findViewById(R.id.flight_detail_datetime);
        TextView flightGateView = view.findViewById(R.id.flight_detail_gate);
        TextView flightTerminalView = view.findViewById(R.id.flight_detail_terminal);
        final TextView flightDestinationView = view.findViewById(R.id.flight_detail_destination);
        TextView flightStateView = view.findViewById(R.id.flight_detail_state);
        final TextView flightAircraftView = view.findViewById(R.id.flight_detail_aircraft);
        ImageView aircraftHelp = view.findViewById(R.id.aircraft_help_button);
        Button viewOnMapButton = view.findViewById(R.id.view_on_map_button);
        TextView flightCodeshareView = view.findViewById(R.id.flight_detail_codeshare);
        final TextView flightAirlineView = view.findViewById(R.id.flight_detail_airline);

        flightNameView.setText(flight.getName());
        flightDateTimeView.setText(flight.getScheduleDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        flightGateView.setText(flight.getGate());
        flightTerminalView.setText(String.format(Locale.getDefault(),"%d", flight.getTerminal()));
        flightDestinationView.setText(String.join(",\n", flight.getDestinations()));
        flightStateView.setText(String.join(",\n", FlightParser.parseStates(flight.getFlightStates())));
        flightAircraftView.setText(String.format("%s - %s", flight.getAircraftType().getIataMain(), flight.getAircraftType().getIataSub()));
        flightAirlineView.setText(flight.getIcao());

        if (flight.getCodeShares() != null) {
            flightCodeshareView.setText(String.join(",\n", flight.getCodeShares()));
        } else {
            flightCodeshareView.setText(flight.getMainFlight());
        }

        // Change schedule date/time to red when delayed
        if (flight.getDelayedInMinutes() > 0) {
            flightDateTimeView.setTextColor(getResources().getColor(android.R.color.holo_red_light, getActivity().getTheme()));
            flightDateTimeView.setText(String.format("%s +%s minuten", flightDateTimeView.getText(), flight.getDelayedInMinutes()));
        }

        // Gate text red on gate change
        if (flight.hasState("GCH")) {
            flightGateView.setTextColor(getResources().getColor(android.R.color.holo_red_light, getActivity().getTheme()));
        }

        // Handle "search plane type" button click
        aircraftHelp.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.google.com/search?q=" + flightAircraftView.getText()));
            startActivity(intent);
        });

        // Handle "show on map" help button
        viewOnMapButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + flightDestinationView.getText()));
            startActivity(intent);
        });

        // Load human readable aircraft type description
        viewModel.getAircraftType(flight).observe(this, aircraftType -> flightAircraftView.setText(aircraftType.getLongDescription()));

        // Load human readable destination name, city and country
        viewModel.getDestination(flight.getDestinations()[flight.getDestinations().length - 1])
                .observe(this, destination ->
                        flightDestinationView.setText(String.format("%s, %s, %s", destination.getDutchName(), destination.getCity(), destination.getCountry())));

        // Load human readable airline name
        viewModel.getAirline(flight.getIcao()).observe(this, airline -> flightAirlineView.setText(airline.getName()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (!viewModel.menuSet) {
            inflater.inflate(R.menu.detail_fragment_menu, menu);
            viewModel.menuSet = true;
        }

        MenuItem favoriteItem = menu.findItem(R.id.favorite_menu_item);

        if (viewModel.hasFavoriteFlight(flight)) {
            favoriteItem.setIcon(R.drawable.ic_favorite);
        }

        // Handle "favorite" menu item click
        favoriteItem.setOnMenuItemClickListener(item -> {
            if (viewModel.hasFavoriteFlight(flight)) {
                viewModel.removeFavoriteFlight(flight);
                item.setIcon(R.drawable.ic_favorite_border);

                Toast.makeText(getContext(), "Vlucht " + flight.getName() + " verwijderd als favoriet", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.addFavoriteFlight(flight);
                item.setIcon(R.drawable.ic_favorite);

                Toast.makeText(getContext(), "Vlucht " + flight.getName() + " opgeslagen als favoriet", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
