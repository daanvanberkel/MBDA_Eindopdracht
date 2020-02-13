package nl.daanvanberkel.schiphol.helpers;

import org.json.JSONObject;

import nl.daanvanberkel.schiphol.models.Destination;

public class DestinationParser {
    private DestinationParser() {}

    public static Destination parse(JSONObject destinationJson) {
        Destination destination = new Destination();

        String city = destinationJson.optString("city");
        String country = destinationJson.optString("country");
        String iata = destinationJson.optString("iata");

        destination.setCity(city);
        destination.setCountry(country);
        destination.setIata(iata);

        JSONObject publicName = destinationJson.optJSONObject("publicName");

        if (publicName != null) {
            String dutchName = publicName.optString("dutch");
            String englishName = publicName.optString("english");

            destination.setDutchName(dutchName);
            destination.setEnglishName(englishName);
        }

        return destination;
    }
}
