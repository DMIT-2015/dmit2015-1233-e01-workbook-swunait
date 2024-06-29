package dmit2015.restclient;

import dmit2015.restclient.Region;
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
public class RegionResourceRestAssuredParameterizedIT {

    final String regionResourceUrl = "http://localhost:8080/restapi/RegionDtos";

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
            "North America",
            "South America",
    }, nullValues = {"null"})
    void givenRegionData_whenCreateRegion_thenRegionIsCreated(
            String regionName) throws Exception {
        // Arrange: Set up the initial state
        var currentRegion = new Region();
        currentRegion.setName(regionName);

        // Act & Assert
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentRegion);
            given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(regionResourceUrl)
                    .then()
                    .statusCode(201)
                    .header("location", containsString(regionResourceUrl))
            ;
        }
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource(value = {
            "Planet Earth",
            "Planet Mars",
    }, nullValues = {"null"})
    void givenExistingRegionId_whenFindRegionById_thenReturnRegion(
            String regionName) throws Exception {
        // Arrange: Set up the initial state
        var currentRegion = new Region();
        currentRegion.setName(regionName);

        // Act & Assert
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonBody = jsonb.toJson(currentRegion);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(jsonBody)
                    .when()
                    .post(regionResourceUrl);
            var postedDataLocation = response.getHeader("location");

            // Act & Assert
            given()
                    .when()
                    .get(postedDataLocation)
                    .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("name", equalTo(currentRegion.getName()))
            ;
        }

    }
//
//    @Order(3)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Property1Value1, Property2Value1, Property3Value1, Property1Value2, Property2Value2, Property3Value3",
//    }, nullValues = {"null"})
//    void givenRegionExist_whenFindAllRegions_thenReturnRegionList(
//            String property1Value1, String property2Value1, String property3Value1,
//            String property1Value2, String property2Value2, String property3Value2
//    ) throws Exception {
//        // Arrange: Set up the initial state
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            var firstRegion = new Region();
//            firstRegion.setProperty1(property1Value1);
//            firstRegion.setProperty2(property2Value1);
//            firstRegion.setProperty3(property3Value1);
//
//            given()
//                    .contentType(ContentType.JSON)
//                    .body(jsonb.toJson(firstRegion)
//                            .when()
//                            .post(regionResourceUrl)
//                            .then()
//                            .statusCode(201);
//
//            var lastRegion = new Region();
//            lastRegion.setProperty1(property1Value2);
//            lastRegion.setProperty2(property2Value2);
//            lastRegion.setProperty3(property3Value2);
//
//            given()
//                    .contentType(ContentType.JSON)
//                    .body(jsonb.toJson(lastRegion))
//                    .when()
//                    .post(regionResourceUrl)
//                    .then()
//                    .statusCode(201);
//
//            // Act & Assert: Perform the action and verify the expected outcome
//            given()
//                    .when()
//                    .get(regionResourceUrl)
//                    .then()
//                    .statusCode(200)
//                    .body("size()", greaterThan(0))
//                    .body("property1", hasItems(firstRegion.getProperty1(), lastRegion.getProperty1()))
//                    .body("property2", hasItems(firstRegion.getProperty2(), lastRegion.getProperty2()))
//                    .body("property3", hasItems(firstRegion.getProperty3(), lastRegion.getProperty3()))
//            ;
//
//        }
//
//    }
//
//    @Order(4)
//    @ParameterizedTest
//    @CsvSource(value = {
//            "Property1Value1, Property2Value1, Property3Value1, Property1Value2, Property2Value2, Property3Value3",
//    }, nullValues = {"null"})
//    void givenUpdatedRegionData_whenUpdatedRegion_thenRegionIsUpdated(
//            String createProperty1Value, String createProperty2Value, String createProperty3Value,
//            String updateProperty1Value, String updateProperty2Value, String updateProperty3Value
//    ) throws Exception {
//        // Arrange: Set up the initial state
//        var createRegion = new Region();
//        createRegion.setProperty1(createProperty1Value);
//        createRegion.setProperty2(createProperty2Value);
//        createRegion.setProperty3(createProperty3Value);
//
//        var updateRegion = new Region();
//        updateRegion.setProperty1(updateProperty1Value);
//        updateRegion.setProperty2(updateProperty2Value);
//        updateRegion.setProperty3(updateProperty3Value);
//
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            String createJsonBody = jsonb.toJson(createRegion);
//
//            Response response = given()
//                    .contentType(ContentType.JSON)
//                    .body(createJsonBody)
//                    .when()
//                    .post(regionResourceUrl);
//            var postedDataLocation = response.getHeader("location");
//            Long entityId = Long.parseLong(postedDataLocation.substring(postedDataLocation.lastIndexOf("/") + 1));
//            updateRegion.setId(entityId);
//            // Act & Assert
//            String updateJsonBody = jsonb.toJson(updateRegion);
//            given()
//                    .contentType(ContentType.JSON)
//                    .body(updateJsonBody)
//                    .when()
//                    .put(postedDataLocation)
//                    .then()
//                    .statusCode(200)
//                    .body("id", equalTo(entityId.intValue()))
//                    .body("property1", equalTo(updateRegion.getProperty1()))
//                    .body("property2", equalTo(updateRegion.getProperty2()))
//                    .body("property3", equalTo(updateRegion.getProperty3()))
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
//    void givenExistingRegionId_whenDeleteRegion_thenRegionIsDeleted(String property1, String property2, String property3) throws Exception {
//        // Arrange: Set up the initial state
//        var currentRegion = new Region();
//        currentRegion.setProperty1(property1);
//        currentRegion.setProperty2(property2);
//        currentRegion.setProperty3(property3);
//
//        try (Jsonb jsonb = JsonbBuilder.create()) {
//            String jsonBody = jsonb.toJson(currentRegion);
//
//            Response response = given()
//                    .contentType(ContentType.JSON)
//                    .body(jsonBody)
//                    .when()
//                    .post(regionResourceUrl);
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