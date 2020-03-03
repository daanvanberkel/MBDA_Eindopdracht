package nl.daanvanberkel.schiphol.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import nl.daanvanberkel.schiphol.R;
import nl.daanvanberkel.schiphol.models.Flight;

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
        String icao = flightJson.optString("prefixICAO");

        flight.setId(id);
        flight.setScheduleDate(scheduleDate);
        flight.setScheduleTime(scheduleTime);
        flight.setName(name);
        flight.setTerminal(terminal);
        flight.setMainFlight(mainFlight);

        if (!flightJson.isNull("prefixICAO")) {
            flight.setIcao(icao);
        } else {
            flight.setIcao("-");
        }

        // Set scheduleDateTime
        if (!flightJson.isNull("scheduleDateTime")) {
            String dateTime = flightJson.optString("scheduleDateTime");
            dateTime = dateTime.replace(".000", "");
            flight.setScheduleDateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME));
        }

        // Set estimatedDateTime
        if (!flightJson.isNull("publicEstimatedOffBlockTime")) {
            String dateTime = flightJson.optString("publicEstimatedOffBlockTime");
            dateTime = dateTime.replace(".000", "");
            flight.setEstimatedDateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME));
        }

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
                        flightStates[j] = flightStatesJson.optString(j);
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

            if (!aircraftTypeJson.isNull("iataMain")) {
                aircraftType.setIataMain(aircraftTypeJson.optString("iataMain"));
            }

            if (!aircraftTypeJson.isNull("iataSub")) {
                aircraftType.setIataSub(aircraftTypeJson.optString("iataSub"));
            }

            flight.setAircraftType(aircraftType);
        }

        // Set codeshares
        JSONObject codeSharesJson = flightJson.optJSONObject("codeshares");

        if (codeSharesJson != null && codeSharesJson.has("codeshares")) {
            JSONArray codeSharesJsonArray = codeSharesJson.optJSONArray("codeshares");

            if (codeSharesJsonArray != null) {
                String[] codeShares = new String[codeSharesJsonArray.length()];

                for(int i = 0; i < codeSharesJsonArray.length(); i++) {
                    codeShares[i] = codeSharesJsonArray.optString(i);
                }

                flight.setCodeShares(codeShares);
            }
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

    public static String parseState(String state, Context context) {
        switch(state) {
            case "SCH":
                return context.getString(R.string.scheduled);
            case "DEL":
                return context.getString(R.string.delayed);
            case "WIL":
                return context.getString(R.string.wait_in_lounge);
            case "GTO":
                return context.getString(R.string.gate_open);
            case "BRD":
                return context.getString(R.string.boarding);
            case "GCL":
                return context.getString(R.string.gate_closing);
            case "GTD":
                return context.getString(R.string.gate_closed);
            case "DEP":
                return context.getString(R.string.departed);
            case "CNX":
                return context.getString(R.string.cancelled);
            case "GCH":
                return context.getString(R.string.gate_change);
            case "TOM":
                return context.getString(R.string.tomorrow);
            default:
                return String.format(context.getString(R.string.state_unknown), state);
        }
    }

    public static String[] parseStates(String[] states, Context context) {
        String[] output = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            output[i] = parseState(states[i], context);
        }

        return output;
    }
}
