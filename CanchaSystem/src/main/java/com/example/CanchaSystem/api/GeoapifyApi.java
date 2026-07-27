package com.example.CanchaSystem.api;

import com.example.CanchaSystem.dto.AddressDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoapifyApi implements StreetLookupApi {
    private static final String BASE_URL = "https://api.geoapify.com/v1/geocode/";

    @Value("${app.geoapify-autocomplete-key}")
    private String AUTOCOMPLETE_KEY;
    @Value("${app.geoapify-reversegeocoding-key}")
    private String REVERSEGEOCODING_KEY;

    private static final HttpClient client = HttpClient.newHttpClient();

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
            String body = client.send(
                    HttpRequest.newBuilder()
                            .uri(endpoint)
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();

            obj = JsonParser.parseString(body)
                    .getAsJsonObject()
                    .getAsJsonArray("results")
                    .get(0)
                    .getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return getAddress(obj);
    }

    private AddressDTO getAddress(JsonObject obj) {
        double latitude = obj.get("lat").getAsDouble();
        double longitude = obj.get("lon").getAsDouble();

        String houseNumber = obj.get("house_number") == null ? null : obj.get("house_number").getAsString();
        String road = obj.get("street").getAsString();
        String city = obj.get("city").getAsString();
        String state = obj.get("state").getAsString();

        String fullAddress = fullAddress(road, houseNumber, city, state);

        return new AddressDTO(fullAddress, latitude, longitude);
    }

    private String fullAddress(String road, String houseNumber, String city, String state) {
        return "%s%s, %s, %s".formatted(road, houseNumber == null ? "" : " " + houseNumber, city, state);
    }

    @Override
    public List<AddressDTO> autocompleteAddress(String text) {
        Params param = Params.of(
                "text", text + URLEncoder.encode(", Mar del Plata, Buenos Aires, Argentina", StandardCharsets.UTF_8),
                "lang", "es",
                "bias", "proximity:-38.003838,-57.556553", // Para que el test busque direcciones centradas en Mar del Plata
                "type", "street",
                "format", "json",
                "apiKey", AUTOCOMPLETE_KEY
        );
        URI endpoint = URI.create(BASE_URL + "autocomplete?" + param);

        JsonArray res;
        try {
            String body = client.send(
                    HttpRequest.newBuilder()
                            .uri(endpoint)
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();

            res = JsonParser.parseString(body)
                    .getAsJsonObject()
                    .getAsJsonArray("results");
        } catch (Exception e) {
            System.err.println("Results not found!");
            return List.of();
        }

        List<AddressDTO> addresses = new ArrayList<>();

        res.forEach(element -> {
            JsonObject obj = (JsonObject) element;
            AddressDTO address = getAddress(obj);

            if (isNotConfident(obj) || isRepeated(address, addresses)) return;

            addresses.add(address);
        });

        return addresses;
    }

    private static boolean isNotConfident(JsonObject obj) {
        return obj.get("rank")
                .getAsJsonObject()
                .get("confidence")
                .getAsDouble() < 0.85;
    }

    private static boolean isRepeated(AddressDTO address, List<AddressDTO> addresses) {
        return addresses
                .stream()
                .anyMatch(addr ->
                        addr.street().equals(address.street())
                );
    }
}
