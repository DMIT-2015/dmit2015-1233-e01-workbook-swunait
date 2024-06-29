package dmit2015.faces;

import dmit2015.model.WeatherData;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Named("currentWeatherDataHttpClientFirebaseRtdbCrudView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
public class WeatherDataFirebaseRtdbHttpClientCrudView implements Serializable {

    /**
     * The current WeatherData instance to add to the database.
     */
    @Getter
    @Setter
    private WeatherData currentWeatherData = new WeatherData();

    /**
     * The list of WeatherData objects fetched from the Firebase Realtime Database
     */
    @Getter
    private List<WeatherData> weatherDatas;

    /**
     * The base URL to the Firebase Realtime Database
     */
    private static final String FIREBASE_REALTIME_DATABASE_BASE_URL = "https://dmit2015-1233-swu-default-rtdb.firebaseio.com";

    /**
     * The URL to the Firebase Realtime Database to access all data.
     */
    private String _jsonAllDataPath;

    /**
     * Fetch all WeatherData from the Firebase Realtime Database
     */
    @PostConstruct
    public void init() {
        _jsonAllDataPath = String.format("%s/%s.json", FIREBASE_REALTIME_DATABASE_BASE_URL, WeatherData.class.getSimpleName());
        fetchFirebaseData();
    }

    /**
     * Use the DataFaker to generate random data.
     */
    public void onGenerateData() {
        try {
            var faker = new Faker();
            currentWeatherData.setCity(faker.address().city());
            currentWeatherData.setDate(LocalDate.parse(faker.date().past(10, TimeUnit.DAYS, "yyyy-MM-dd")));
            currentWeatherData.setDescription(faker.weather().description());
            currentWeatherData.setTemperatureCelsius(faker.number().numberBetween(-20,50));
        } catch (Exception e) {
            Messages.addGlobalError("Error generating data {0}", e.getMessage());
        }

    }

    /**
     * Push currentWeatherData to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    public void onAddWeatherDataData() {
        try (Jsonb jsonb = JsonbBuilder.create();
             var httpClient = HttpClient.newHttpClient();) {
            // Convert the currentWeatherData to a JSON string using JSONB
            String requestBodyJson = jsonb.toJson(currentWeatherData);

            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonAllDataPath))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                var responseBodyJson = httpResponse.body();
                JsonObject responseJsonObject = jsonb.fromJson(responseBodyJson, JsonObject.class);
                Messages.addGlobalInfo("Successfully added data with name {0}", responseJsonObject.getString("name"));

                currentWeatherData = new WeatherData();

                fetchFirebaseData();
            } else {
                Messages.addGlobalInfo("Add was not successful, server return status {0}", httpResponse.statusCode());
            }


        } catch (Exception e) {
            Messages.addGlobalError("Error adding data {0}", e.getMessage());
        }


    }

    /**
     * Remove currentWeatherData to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    public void onDeleteWeatherDataData(String name) {
        try (Jsonb jsonb = JsonbBuilder.create();
             var httpClient = HttpClient.newHttpClient();) {
            // Convert the currentWeatherData to a JSON string using JSONB
            String requestBodyJson = jsonb.toJson(currentWeatherData);

            String _jsonSingleDataPath = String.format("%s/%s/%s.json",
                    FIREBASE_REALTIME_DATABASE_BASE_URL, WeatherData.class.getSimpleName(), name);
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonSingleDataPath))
                    .DELETE()
                    .build();
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                Messages.addGlobalInfo("Successfully deleted data with name {0}",
                        name);
                fetchFirebaseData();
            } else {
                Messages.addGlobalInfo("Delete was not successful, server return status {0}", httpResponse.statusCode());
            }

        } catch (Exception e) {
            Messages.addGlobalError("Error deleting Firebase Realtime Database data {0}", e.getMessage());
        }

    }

    /**
     * Write WeatherData to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    public void onUpdateWeatherDataData(WeatherData selectedWeatherData) {
        try (Jsonb jsonb = JsonbBuilder.create();
             var httpClient = HttpClient.newHttpClient();) {
            // Convert the selectedWeatherData to a JSON string using JSONB
            String requestBodyJson = jsonb.toJson(selectedWeatherData);
            String _jsonSingleDataPath = String.format("%s/%s/%s.json",
                    FIREBASE_REALTIME_DATABASE_BASE_URL, WeatherData.class.getSimpleName(), selectedWeatherData.getName());

            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonSingleDataPath))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                var responseBodyJson = httpResponse.body();
                WeatherData updatedWeatherData = jsonb.fromJson(responseBodyJson, WeatherData.class);
                Messages.addGlobalInfo("Successfully updated WeatherData {0}",
                        updatedWeatherData.toString());

                fetchFirebaseData();
            } else {
                Messages.addGlobalInfo("Update was not successful, server return status {0}", httpResponse.statusCode());
            }


        } catch (Exception e) {
            Messages.addGlobalError("Error updating Firebase Realtime Database data {0}", e.getMessage());
        }
    }

    /**
     * Get currentWeatherData to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    private void fetchFirebaseData() {
        try (Jsonb jsonb = JsonbBuilder.create();
             var httpClient = HttpClient.newHttpClient();) {

            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonAllDataPath))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                var responseBodyJson = httpResponse.body();
                // Convert the responseBodyJson to an ArrayList<WeatherData>
//            weatherDatas = jsonb.fromJson(responseBodyJson,  new ArrayList<WeatherData>() {}.getClass().getGenericSuperclass());
                LinkedHashMap<String, WeatherData> responseData = jsonb.fromJson(responseBodyJson, new LinkedHashMap<String, WeatherData>() {
                }.getClass().getGenericSuperclass());
                // Convert the LinkedHashMap<String, WeatherData> to ist<WeatherData>
//             weatherDatas = responseData.values().stream().toList();
                weatherDatas = responseData.entrySet()
                        .stream()
                        .map(item -> {
                            var currentWeatherData = new WeatherData();
                            currentWeatherData.setName(item.getKey());
                            // TODO: Set each property of the Java data object
                            currentWeatherData.setCity(item.getValue().getCity());
                            currentWeatherData.setDate(item.getValue().getDate());
                            currentWeatherData.setDescription(item.getValue().getDescription());
                            currentWeatherData.setTemperatureCelsius(item.getValue().getTemperatureCelsius());

                            return currentWeatherData;
                        })
                        .toList();

                Messages.addGlobalInfo("Successfully fetched Firebase Realtime Database data");
            } else {
                Messages.addGlobalInfo("Fetch data was not successful, server return status {0}", httpResponse.statusCode());
            }

        } catch (Exception e) {
            Messages.addGlobalError("Error adding Firebase Realtime Database data {0}", e.getMessage());
        }
    }

    public void onClearForm() {
        currentWeatherData = new WeatherData();
    }
}