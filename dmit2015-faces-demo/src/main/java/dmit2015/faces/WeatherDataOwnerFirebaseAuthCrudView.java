package dmit2015.faces;

import dmit2015.model.WeatherDataOwner;
import dmit2015.restclient.FirebaseLoginSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

@Named("currentWeatherDataOwnerFirebaseAuthCrudView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
public class WeatherDataOwnerFirebaseAuthCrudView implements Serializable {

    /**
     * The list of WeatherDataOwner objects fetched from the Firebase Realtime Database
     */
    @Getter
    private List<WeatherDataOwner> weatherDataOwners;

    @Getter
    @Setter
    private WeatherDataOwner selectedWeatherDataOwner;

    @Getter
    @Setter
    private String selectedId;

    @Inject
    @ConfigProperty(name = "firebase.rtdb.WeatherDataOwner.base.url")
    private String firebaseRtdbBaseUrl;

    /**
     * The URL to the Firebase Realtime Database to access all data.
     */
    private String _jsonAllDataPath;

    @Inject
    private FirebaseLoginSession _firebaseLoginSession;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            // Get the Firebase Authenticated userId and token.
            String firebaseUserId = _firebaseLoginSession.getFirebaseUser().getLocalId();
            String firebaseToken = _firebaseLoginSession.getFirebaseUser().getIdToken();

            // Set the path in the database for content-owner access only data
            _jsonAllDataPath = String.format("%s/%s/%s.json?auth=%s",
                    firebaseRtdbBaseUrl,
                    WeatherDataOwner.class.getSimpleName(),
                    firebaseUserId,
                    firebaseToken);

            fetchFirebaseData();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    public void onOpenNew() {
        selectedWeatherDataOwner = new WeatherDataOwner();
    }

    public void onSave() {
        if (selectedId == null) {
            try (Jsonb jsonb = JsonbBuilder.create();
                 var httpClient = HttpClient.newHttpClient();) {

                // Get the Firebase Authenticated userId and token.
                String firebaseUserId = _firebaseLoginSession.getFirebaseUser().getLocalId();
                String firebaseToken = _firebaseLoginSession.getFirebaseUser().getIdToken();

                // Set the path in the database for content-owner access only data
                _jsonAllDataPath = String.format("%s/%s/%s.json?auth=%s",
                        firebaseRtdbBaseUrl,
                        WeatherDataOwner.class.getSimpleName(),
                        firebaseUserId,
                        firebaseToken);

                // Convert the currentWeatherDataOwner to a JSON string using JSONB
                String requestBodyJson = jsonb.toJson(selectedWeatherDataOwner);

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

                    selectedWeatherDataOwner = null;

                    fetchFirebaseData();
                } else {
                    Messages.addGlobalInfo("Add was not successful, server return status {0}", httpResponse.statusCode());
                }


            } catch (Exception e) {
                Messages.addGlobalError("Error adding data {0}", e.getMessage());
            }

        } else {

            try (Jsonb jsonb = JsonbBuilder.create();
                 var httpClient = HttpClient.newHttpClient();) {

                // Get the name/key of the unique identifier node to delete.
                String name = selectedWeatherDataOwner.getName();

                // Get the Firebase Authenticated userId and token.
                String firebaseUserId = _firebaseLoginSession.getFirebaseUser().getLocalId();
                String firebaseToken = _firebaseLoginSession.getFirebaseUser().getIdToken();

                // Set the path in the database for content-owner access only data to update
                String _jsonSingleDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                        firebaseRtdbBaseUrl,
                        WeatherDataOwner.class.getSimpleName(),
                        firebaseUserId,
                        name,
                        firebaseToken);

                // Convert the selectedWeatherDataOwner to a JSON string using JSONB
                String requestBodyJson = jsonb.toJson(selectedWeatherDataOwner);

                var httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(_jsonSingleDataPath))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                        .build();
                var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (httpResponse.statusCode() == 200) {
                    var responseBodyJson = httpResponse.body();
                    WeatherDataOwner updatedWeatherDataOwner = jsonb.fromJson(responseBodyJson, WeatherDataOwner.class);
                    Messages.addGlobalInfo("Successfully updated WeatherDataOwner {0}",
                            updatedWeatherDataOwner.toString());

                    fetchFirebaseData();
                } else {
                    Messages.addGlobalInfo("Update was not successful, server return status {0}", httpResponse.statusCode());
                }

            } catch (Exception e) {
                Messages.addGlobalError("Error updating Firebase Realtime Database data {0}", e.getMessage());
            }
        }

        PrimeFaces.current().executeScript("PF('manageWeatherDataOwnerDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-WeatherDataOwners");
    }

    public void onDelete() {

        // Get the Firebase Authenticated userId and token.
        String firebaseUserId = _firebaseLoginSession.getFirebaseUser().getLocalId();
        String firebaseToken = _firebaseLoginSession.getFirebaseUser().getIdToken();

        // Get the name/key of the unique identifier node to delete.
        String name = selectedWeatherDataOwner.getName();

        // Set the path in the database for content-owner access only data to delete
        String _jsonSingleDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                WeatherDataOwner.class.getSimpleName(),
                firebaseUserId,
                name,
                firebaseToken);

        try (Jsonb jsonb = JsonbBuilder.create();
             var httpClient = HttpClient.newHttpClient();) {

            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonSingleDataPath))
                    .DELETE()
                    .build();
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                Messages.addGlobalInfo("Successfully deleted data with name {0}",
                        name);
                selectedWeatherDataOwner = null;
                fetchFirebaseData();
                PrimeFaces.current().ajax().update("form:messages", "form:dt-WeatherDataOwners");
            } else {
                Messages.addGlobalInfo("Delete was not successful, server return status {0}", httpResponse.statusCode());
            }

        } catch (Exception e) {
            Messages.addGlobalError("Error deleting Firebase Realtime Database data {0}", e.getMessage());
        }
    }

    /**
     * Get currentWeatherDataOwner to Firebase Realtime Database using the REST API
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
                LinkedHashMap<String, WeatherDataOwner> responseData = jsonb.fromJson(responseBodyJson, new LinkedHashMap<String, WeatherDataOwner>() {
                }.getClass().getGenericSuperclass());
                // Convert the LinkedHashMap<String, WeatherDataOwner> to List<WeatherDataOwner>
//             weatherDataOwners = responseData.values().stream().toList();
                weatherDataOwners = responseData.entrySet()
                        .stream()
                        .map(item -> {
                            var currentWeatherDataOwner = new WeatherDataOwner();
                            currentWeatherDataOwner.setName(item.getKey());

                            currentWeatherDataOwner.setCity(item.getValue().getCity());
                            currentWeatherDataOwner.setDate(item.getValue().getDate());
                            currentWeatherDataOwner.setDescription(item.getValue().getDescription());
                            currentWeatherDataOwner.setTemperatureCelsius(item.getValue().getTemperatureCelsius());

                            return currentWeatherDataOwner;
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

}