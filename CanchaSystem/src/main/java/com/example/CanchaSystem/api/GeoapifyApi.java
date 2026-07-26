package com.example.CanchaSystem.api;

import com.example.CanchaSystem.dto.AddressDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GeoapifyApi implements StreetLookupApi {
    private static final String BASE_URL = "https://api.geoapify.com/v1/geocode/";

    @Value("${app.geoapify-autocomplete-key}")
    private static String AUTOCOMPLETE_KEY;
    @Value("${app.geoapify-reversegeocoding-key}")
    private static String REVERSEGEOCODING_KEY;

    private static final HttpClient client = HttpClient.newHttpClient();

    public static final StreetLookupApi INSTANCE = new GeoapifyApi();

    @Override
    public AddressDTO reverseGeocodeAddress(Double lat, Double lng) {
        Params param = Params.of(
                "lat", lat.toString(),
                "lon", lng.toString(),
                "lang", "es",
                "format", "json",
                "apiKey", REVERSEGEOCODING_KEY
        );
        URI endpoint = URI.create(BASE_URL + "reverse?" + param);

        JsonObject obj;
        try {
            obj = JsonParser.parseString(
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(endpoint)
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    ).body()
            ).getAsJsonObject()
                    .getAsJsonArray("features")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("properties");
        } catch (Exception e) {
            return null;
        }

        return getAddress(obj);
    }

    private AddressDTO getAddress(JsonObject obj) {
        double latitude = obj.get("lat").getAsDouble();
        double longitude = obj.get("lon").getAsDouble();

        String houseNumber = obj.get("house_number").getAsString();
        String road = obj.get("road").getAsString();
        String city = obj.get("city").getAsString();
        String state = obj.get("state").getAsString();

        String fullAddress = fullAddress(road, houseNumber, city, state);

        return new AddressDTO(fullAddress, latitude, longitude);
    }

    private String fullAddress(String road, String houseNumber, String city, String state) {
        return "%s %s, %s, %s".formatted(road, houseNumber, city, state);
    }

    @Override
    public List<AddressDTO> autocompleteAddress(String text) {
        Params param = Params.of(
                "text", text,
                "lang", "es",
                "bias", "proximity:-38.003838,-57.556553", // Para que el test busque direcciones centradas en Mar del Plata
                "format", "json",
                "apiKey", AUTOCOMPLETE_KEY
        );
        URI endpoint = URI.create(BASE_URL + "autocomplete?" + param);

        JsonArray res;
        try {
            res = JsonParser.parseString(
                    client.send(
                            HttpRequest.newBuilder()
                                    .uri(endpoint)
                                    .POST(HttpRequest.BodyPublishers.ofString(param.toString()))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    ).body()
            )
                    .getAsJsonObject()
                    .getAsJsonArray("results");
        } catch (Exception e) {
            return List.of();
        }

        List<AddressDTO> addresses = new ArrayList<>();

        res.forEach(element -> {
            JsonObject obj = (JsonObject) element;

            if (isNotConfident(obj)) return;

            addresses.add(getAddress(obj));
        });

        return addresses;
    }

    private static boolean isNotConfident(JsonObject obj) {
        return obj.get("rank")
                .getAsJsonObject()
                .get("confidence")
                .getAsDouble() < 0.85;
    }
}
