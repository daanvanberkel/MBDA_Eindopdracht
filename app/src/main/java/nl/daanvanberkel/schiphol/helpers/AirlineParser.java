package nl.daanvanberkel.schiphol.helpers;

import org.json.JSONObject;

import nl.daanvanberkel.schiphol.models.Airline;

public class AirlineParser {
    private AirlineParser() {}

    public static Airline parse(JSONObject airlineJson) {
        Airline airline = new Airline();

        String iata = airlineJson.optString("iata");
        String icao = airlineJson.optString("icao");
        int nvls = airlineJson.optInt("nvls", 0);
        String name = airlineJson.optString("publicName");

        airline.setIata(iata);
        airline.setIcao(icao);
        airline.setNvls(nvls);
        airline.setName(name);

        return airline;
    }
}
