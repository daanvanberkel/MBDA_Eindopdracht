package nl.daanvanberkel.schiphol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class FlightDetailFragment extends Fragment {

    public static final String ARG_FLIGHT = "nl.daanvanberkel.schiphol.ARG_FLIGHT";

    public FlightDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Flight flight = (Flight) getArguments().getSerializable(ARG_FLIGHT);

        View view = inflater.inflate(R.layout.flight_detail_fragment, container, false);

        if (flight == null) {
            return view;
        }

        TextView flightNameView = view.findViewById(R.id.flight_detail_name);
        TextView flightDateTimeView = view.findViewById(R.id.flight_detail_datetime);
        TextView flightGateView = view.findViewById(R.id.flight_detail_gate);
        TextView flightTerminalView = view.findViewById(R.id.flight_detail_terminal);
        TextView flightDestinationView = view.findViewById(R.id.flight_detail_destination);
        TextView flightStateView = view.findViewById(R.id.flight_detail_state);
        TextView flightAircraftView = view.findViewById(R.id.flight_detail_aircraft);

        flightNameView.setText(flight.getName());
        flightDateTimeView.setText(String.format("%s %s", flight.getScheduleDate(), flight.getScheduleTime()));
        flightGateView.setText(flight.getGate());
        flightTerminalView.setText(String.format(Locale.getDefault(),"%d", flight.getTerminal()));
        flightDestinationView.setText(String.join(",\n", flight.getDestinations()));
        flightStateView.setText(String.join(",\n", flight.getFlightStates()));
        flightAircraftView.setText(String.format("%s - %s", flight.getAircraftType().getIataMain(), flight.getAircraftType().getIataSub()));

        return view;
    }
}
