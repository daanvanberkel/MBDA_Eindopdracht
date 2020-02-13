package nl.daanvanberkel.schiphol.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nl.daanvanberkel.schiphol.models.AircraftType;

public class AircraftTypeParser {
    private AircraftTypeParser() {
    }

    public static AircraftType parse(JSONObject aircraftTypeJson) {
        AircraftType aircraftType = new AircraftType();

        String iataMain = aircraftTypeJson.optString("iataMain");
        String iataSub = aircraftTypeJson.optString("iataSub");
        String longDescription = aircraftTypeJson.optString("longDescription");
        String shortDescription = aircraftTypeJson.optString("shortDescription");

        aircraftType.setIataMain(iataMain);
        aircraftType.setIataSub(iataSub);
        aircraftType.setLongDescription(longDescription);
        aircraftType.setShortDescription(shortDescription);

        return aircraftType;
    }

    public static List<AircraftType> parse(JSONArray aircraftTypesJson) {
        List<AircraftType> aircraftTypes = new ArrayList<>();

        if (aircraftTypesJson == null) {
            return aircraftTypes;
        }

        for(int i = 0; i < aircraftTypesJson.length(); i++) {
            JSONObject aircraftTypeJson = aircraftTypesJson.optJSONObject(i);

            if (aircraftTypeJson != null) {
                aircraftTypes.add(parse(aircraftTypeJson));
            }
        }

        return aircraftTypes;
    }
}
