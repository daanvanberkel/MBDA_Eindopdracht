package nl.daanvanberkel.schiphol.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteFlights implements Serializable {
    private List<String> flightIds = new ArrayList<>();

    public void addFlightId(String id) {
        flightIds.add(id);
    }

    public void removeFlightId(String id) {
        flightIds.remove(id);
    }

    public boolean hasFlightId(String id) {
        return flightIds.contains(id);
    }

    public List<String> getFlightIds() {
        return Collections.unmodifiableList(flightIds);
    }
}
