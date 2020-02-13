package nl.daanvanberkel.schiphol.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteFlights implements Serializable {
    private List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight flight) {
        if (hasFlight(flight)) {
            return;
        }

        flights.add(flight);
    }

    public void removeFlight(Flight flight) {
        Integer index = getFlightIndex(flight);

        if (index == null) {
            return;
        }

        flights.remove(index.intValue());
    }

    public boolean hasFlight(Flight flight) {
        return getFlightIndex(flight) != null;
    }

    public List<Flight> getFlights() {
        return Collections.unmodifiableList(flights);
    }

    private Integer getFlightIndex(Flight flight) {
        for(int i = 0; i < getFlights().size(); i++) {
            if (getFlights().get(i).getId().equals(flight.getId())) {
                return i;
            }
        }

        return null;
    }
}
