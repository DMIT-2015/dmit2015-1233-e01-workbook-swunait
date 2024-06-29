package dmit2015.restclient;

import dmit2015.restclient.Country;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * This class contains starter code for testing REST API endpoints for CRUD operations using REST-assured.
 *
 * <a href="https://github.com/rest-assured/rest-assured">REST Assured GitHub repo</a>
 * <a href="https://github.com/rest-assured/rest-assured/wiki/Usage">REST Assured Usage</a>
 * <a href="http://www.mastertheboss.com/jboss-frameworks/resteasy/restassured-tutorial">REST Assured Tutorial</a>
 * <a href="https://hamcrest.org/JavaHamcrest/tutorial">Hamcrest Tutorial</a>
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson Data-Binding</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CountryResourceRestAssuredParameterizedIT {

    final String countryResourceUrl = "http://localhost:8080/restapi/CountryDtos";

    @BeforeAll
    static void beforeAllTests() {
        // code to execute before all tests in the current test class
    }

    @AfterAll
    static void afterAllTests() {
        // code to execute after all tests in the current test class
    }

    @BeforeEach
    void beforeEachTestMethod() {
        // Code to execute before each test such as creating the test data
    }

    @AfterEach
    void afterEachTestMethod() {
        // code to execute after each test such as deleting the test data
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "PE, Planet Earth, 70",
            "PM, Planet Mars, 70",
    }, nullValues = {"null"})
    void givenCountryData_whenCreateCountry_thenCountryIsCreated(
            String countryId,
            String countryName,
            Long regionId) throws Exception {
        // Arrange: Set up the initial state
        var currentCountry = new Country();
        currentCountry.setId(countryId);
        currentCountry.setName(countryName);
        currentCountry.setRegionId(regionId);

        // Act & Assert
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentCountry);
            given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(countryResourceUrl)
                    .then()
                    .statusCode(201)
                    .header("location", containsString(countryResourceUrl))
            ;
        }
    }

//    @Order(2)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Property1Value, Property2Value, Property3Value",
//            "Property1Value, Property2Value, Property3Value",
//    }, nullValues = {"null"})
//    void givenExistingCountryId_whenFindCountryById_thenReturnCountry(String property1, String property2, String property3) throws Exception {
//        // Arrange: Set up the initial state
//        var currentCountry = new Country();
//        currentCountry.setProperty1(property1);
//        currentCountry.setProperty2(property2);
//        currentCountry.setProperty3(property3);
//
//        // Act & Assert
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            String jsonBody = jsonb.toJson(currentCountry);
//
//            Response response = given()
//                    .contentType(ContentType.JSON)
//                    .body(jsonBody)
//                    .when()
//                    .post(countryResourceUrl);
//            var postedDataLocation = response.getHeader("location");
//
//            // Act & Assert
//            given()
//                    .when()
//                    .get(postedDataLocation)
//                    .then()
//                    .statusCode(200)
//                    .body("id", notNullValue())
//                    .body("property1", equalTo(currentCountry.getProperty1()))
//                    .body("property2", equalTo(currentCountry.getProperty2()))
//                    .body("property3", equalTo(currentCountry.getProperty3()))
//            ;
//        }
//
//    }
//
    @Order(3)
    @ParameterizedTest
    @CsvSource(value = {
            "X1, X1 Canada, 70, X2, X2 Canada, 70",
    }, nullValues = {"null"})
    void givenCountryExist_whenFindAllCountrys_thenReturnCountryList(
            String countryId1, String countryName1, Long regionId1,
            String countryId2, String countryName2, Long regionId2
    ) throws Exception {
        // Arrange: Set up the initial state
        try (Jsonb jsonb = JsonbBuilder.create()) {
            var firstCountry = new Country();
            firstCountry.setId(countryId1);
            firstCountry.setName(countryName1);
            firstCountry.setRegionId(regionId1);

            given()
                    .contentType(ContentType.JSON)
                    .body(jsonb.toJson(firstCountry))
                            .when()
                            .post(countryResourceUrl)
                            .then()
                            .statusCode(201);

            var lastCountry = new Country();
            lastCountry.setId(countryId2);
            lastCountry.setName(countryName2);
            lastCountry.setRegionId(regionId2);

            given()
                    .contentType(ContentType.JSON)
                    .body(jsonb.toJson(lastCountry))
                    .when()
                    .post(countryResourceUrl)
                    .then()
                    .statusCode(201);

            // Act & Assert: Perform the action and verify the expected outcome
            given()
                    .when()
                    .get(countryResourceUrl)
                    .then()
                    .statusCode(200)
                    .body("size()", greaterThan(0))
                    .body("name", hasItems(firstCountry.getName(), lastCountry.getName()))
                    .body("regionId", hasItems(firstCountry.getRegionId().intValue(), lastCountry.getRegionId().intValue()))
//                    .body("property3", hasItems(firstCountry.getProperty3(), lastCountry.getProperty3()))
            ;

        }

    }
//
//    @Order(4)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Property1Value1, Property2Value1, Property3Value1, Property1Value2, Property2Value2, Property3Value3",
//    }, nullValues = {"null"})
//    void givenUpdatedCountryData_whenUpdatedCountry_thenCountryIsUpdated(
//            String createProperty1Value, String createProperty2Value, String createProperty3Value,
//            String updateProperty1Value, String updateProperty2Value, String updateProperty3Value
//    ) throws Exception {
//        // Arrange: Set up the initial state
//        var createCountry = new Country();
//        createCountry.setProperty1(createProperty1Value);
//        createCountry.setProperty2(createProperty2Value);
//        createCountry.setProperty3(createProperty3Value);
//
//        var updateCountry = new Country();
//        updateCountry.setProperty1(updateProperty1Value);
//        updateCountry.setProperty2(updateProperty2Value);
//        updateCountry.setProperty3(updateProperty3Value);
//
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            String createJsonBody = jsonb.toJson(createCountry);
//
//            Response response = given()
//                    .contentType(ContentType.JSON)
//                    .body(createJsonBody)
//                    .when()
//                    .post(countryResourceUrl);
//            var postedDataLocation = response.getHeader("location");
//            Long entityId = Long.parseLong(postedDataLocation.substring(postedDataLocation.lastIndexOf("/") + 1));
//            updateCountry.setId(entityId);
//            // Act & Assert
//            String updateJsonBody = jsonb.toJson(updateCountry);
//            given()
//                    .contentType(ContentType.JSON)
//                    .body(updateJsonBody)
//                    .when()
//                    .put(postedDataLocation)
//                    .then()
//                    .statusCode(200)
//                    .body("id", equalTo(entityId.intValue()))
//                    .body("property1", equalTo(updateCountry.getProperty1()))
//                    .body("property2", equalTo(updateCountry.getProperty2()))
//                    .body("property3", equalTo(updateCountry.getProperty3()))
//            ;
//        }
//
//    }
//
//    @Order(5)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Property1Value, Property2Value, Property3Value",
//            "Property1Value, Property2Value, Property3Value",
//    }, nullValues = {"null"})
//    void givenExistingCountryId_whenDeleteCountry_thenCountryIsDeleted(String property1, String property2, String property3) throws Exception {
//        // Arrange: Set up the initial state
//        var currentCountry = new Country();
//        currentCountry.setProperty1(property1);
//        currentCountry.setProperty2(property2);
//        currentCountry.setProperty3(property3);
//
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            String jsonBody = jsonb.toJson(currentCountry);
//
//            Response response = given()
//                    .contentType(ContentType.JSON)
//                    .body(jsonBody)
//                    .when()
//                    .post(countryResourceUrl);
//            var postedDataLocation = response.getHeader("location");
//
//            // Act & Assert
//            given()
//                    .when()
//                    .delete(postedDataLocation)
//                    .then()
//                    .statusCode(204);
//
//            // Verify deletion
//            given()
//                    .when()
//                    .delete(postedDataLocation)
//                    .then()
//                    .statusCode(404);
//        }
//
//    }
}