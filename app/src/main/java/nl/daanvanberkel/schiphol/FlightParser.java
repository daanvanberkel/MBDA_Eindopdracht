package nl.daanvanberkel.schiphol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FlightParser {
    private FlightParser() {
    }

    public static Flight parse(JSONObject flightJson) {
        Flight flight = new Flight();

        String id = flightJson.optString("id");
        String scheduleDate = flightJson.optString("scheduleDate");
        String scheduleTime = flightJson.optString("scheduleTime");
        String name = flightJson.optString("flightName");
        int terminal = flightJson.optInt("terminal", 0);
        String mainFlight = flightJson.optString("mainFlight");


        flight.setId(id);
        flight.setScheduleDate(scheduleDate);
        flight.setScheduleTime(scheduleTime);
        flight.setName(name);
        flight.setTerminal(terminal);
        flight.setMainFlight(mainFlight);

        // Set gate
        if (flightJson.isNull("gate")) {
            flight.setGate("-");
        } else {
            flight.setGate(flightJson.optString("gate"));
        }

        // Set flight states
        if (flightJson.has("publicFlightState")) {
            JSONObject publicFlightState = flightJson.optJSONObject("publicFlightState");

            if (publicFlightState != null && publicFlightState.has("flightStates")) {
                JSONArray flightStatesJson = publicFlightState.optJSONArray("flightStates");

                if (flightStatesJson != null) {
                    String[] flightStates = new String[flightStatesJson.length()];

                    for (int j = 0; j < flightStatesJson.length(); j++) {
                        flightStates[j] = parseState(flightStatesJson.optString(j));
                    }

                    flight.setFlightStates(flightStates);
                }
            }
        }

        // Set flight destinations
        if (flightJson.has("route")) {
            JSONObject route = flightJson.optJSONObject("route");

            if (route != null && route.has("destinations")) {
                JSONArray destinationsJson = route.optJSONArray("destinations");

                if (destinationsJson != null) {
                    String[] destinations = new String[destinationsJson.length()];

                    for (int j = 0; j < destinationsJson.length(); j++) {
                        destinations[j] = destinationsJson.optString(j);
                    }

                    flight.setDestinations(destinations);
                }
            }
        }

        // Set aircraft type
        JSONObject aircraftTypeJson = flightJson.optJSONObject("aircraftType");

        if (aircraftTypeJson != null && aircraftTypeJson.has("iataMain") && aircraftTypeJson.has("iataSub")) {
            Flight.AircraftType aircraftType = new Flight.AircraftType();
            aircraftType.setIataMain(aircraftTypeJson.optString("iataMain"));
            aircraftType.setIataSub(aircraftTypeJson.optString("iataSub"));

            flight.setAircraftType(aircraftType);
        }

        return flight;
    }

    public static List<Flight> parse(JSONArray flightsJson) {
        List<Flight> flights = new ArrayList<>();

        if (flightsJson == null) {
            return flights;
        }

        for(int i = 0; i < flightsJson.length(); i++) {
            JSONObject flightJson = flightsJson.optJSONObject(i);

            if (flightJson != null) {
                flights.add(parse(flightJson));
            }
        }

        return flights;
    }

    public static String parseState(String state) {
        switch(state) {
            case "SCH":
                return "Scheduled";
            case "DEL":
                return "Delayed";
            case "WIL":
                return "Wait in lounge";
            case "GTO":
                return "Gate open";
            case "BRD":
                return "Boarding";
            case "GCL":
                return "Gate closing";
            case "GTD":
                return "Gate closed";
            case "DEP":
                return "Departed";
            case "CNX":
                return "Cancelled";
            case "GCH":
                return "Gate change";
            case "TOM":
                return "Tomorrow";
            default:
                return "State unknown";
        }
    }
}
