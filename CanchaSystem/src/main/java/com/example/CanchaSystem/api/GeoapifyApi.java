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
    private static final String BASE_API = "https://api.geoapify.com/v1/geocode/";
    @Value("${app.geoapify-autocomplete-key}")
    private static String API_KEY;
    private static final HttpClient client = HttpClient.newHttpClient();

    public static final StreetLookupApi INSTANCE = new GeoapifyApi();

    @Override
    public List<AddressDTO> autocompleteAddress(String text) {
        Params param = Params.of(
                "text", text,
                "lang", "es",
                "bias", "proximity:-38.003838,-57.556553", // Para que el test busque direcciones centradas en Mar del Plata
                "format", "json",
                "apiKey", API_KEY
        );
        URI endpoint = URI.create(BASE_API + "autocomplete?" + param);

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

            double latitude = obj.get("lat").getAsDouble();
            double longitude = obj.get("lon").getAsDouble();
            String houseNumber = obj.get("house_number").getAsString();
            String road = obj.get("road").getAsString();
            String city = obj.get("city").getAsString();
            String state = obj.get("state").getAsString();

            String fullAddress = "%s %s, %s, %s".formatted(road, houseNumber, city, state);

            addresses.add(new AddressDTO(fullAddress, latitude, longitude));
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
