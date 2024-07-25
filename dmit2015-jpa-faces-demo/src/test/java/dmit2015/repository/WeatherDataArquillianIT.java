package dmit2015.repository;

import common.config.ApplicationConfig;
import dmit2015.entity.WeatherData;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)
public class WeatherDataArquillianIT { // The class must be declared as public

    static String mavenArtifactIdId;

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class, archiveName)
                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:2.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.25.3").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("net.datafaker:datafaker:2.2.2").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.2.224").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.4.0.24.05").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.postgresql.jdbc:postgresql:42.7.3").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j:8.4.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client:3.4.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial:6.5.2.Final").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson:3.0.3").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(WeatherData.class, WeatherDataRepository.class)
                // TODO Add any additional libraries, packages, classes or resource files required
//                .addAsLibraries(pomFile.resolve("jakarta.platform:jakarta.jakartaee-api:10.0.0").withTransitivity().asFile())
                // .addPackage("dmit2015.entity")
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    private WeatherDataRepository _weatherDataRepository;

    @Resource
    private UserTransaction _beanManagedTransaction;

    @BeforeAll
    static void beforeAllTestMethod() {
        // code to execute before test methods are executed
    }

    @BeforeEach
    void beforeEachTestMethod() {
        // Code to execute before each method such as creating the test data
    }

    @AfterEach
    void afterEachTestMethod() {
        // code to execute after each test method such as deleting the test data
    }


    @Order(1)
    @ParameterizedTest
    @CsvSource(value = {
            "15,South Forest,Sleet,42,Erdmanbury,Snow,30"
    })
    void findAll_Size_BoundaryValues(int expectedSize,
                                     String expectedFirstCity,
                                     String expectedFirstDescription,
                                     int expectedFirstCelsius,
                                     String expectedLastCity,
                                     String expectedLastDescription,
                                     int expectedLastCelsius) {
        assertThat(_weatherDataRepository).isNotNull();
        // Arrange and Act
        List<WeatherData> weatherDataList = _weatherDataRepository.findAll();
        // Assert
        assertThat(weatherDataList.size())
                .isEqualTo(expectedSize);

        // Get the first entity and compare with expected results
        var firstWeatherData = weatherDataList.getFirst();
        assertThat(firstWeatherData.getCity()).isEqualTo(expectedFirstCity);
        assertThat(firstWeatherData.getDescription()).isEqualTo(expectedFirstDescription);
        assertThat(firstWeatherData.getTemperatureCelsius()).isEqualTo(expectedFirstCelsius);

        // Get the last entity and compare with expected results
        var lastWeatherData = weatherDataList.getLast();
        assertThat(lastWeatherData.getCity()).isEqualTo(expectedLastCity);
        assertThat(lastWeatherData.getDescription()).isEqualTo(expectedLastDescription);
        assertThat(lastWeatherData.getTemperatureCelsius()).isEqualTo(expectedLastCelsius);
    }


//    @Order(2)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "primaryKey,expectedProperty1Value,expectedProperty2Value",
//            "primaryKey,expectedProperty1Value,expectedProperty2Value"
//    })
//    void findById_ExistingId_IsPresent(Long weatherDataId, String expectedProperty1, String expectedProperty2) {
//        // Arrange and Act
//        Optional<WeatherData> optionalWeatherData = _weatherDataRepository.findById(weatherDataId);
//        assertThat(optionalWeatherData.isPresent())
//                .isTrue();
//        WeatherData existingWeatherData = optionalWeatherData.orElseThrow();
//
//        // Assert
//        assertThat(existingWeatherData)
//                .isNotNull();
//        // TODO Assert that each property matches the expected result
//        // assertThat(existingWeatherData.getProperty1())
//        //     .isEqualTo(expectedProperty1);
//
//    }


    @Order(3)
    @ParameterizedTest
    @CsvSource(value = {
            "Edmonton, Oilers, 20",
            "Florida, Pathers, 35",
    })
    void add_ValidData_Added(String city, String description, int celsius) throws SystemException, NotSupportedException {
        // Arrange
        WeatherData newWeatherData = new WeatherData();
        // TODO Set each property of the new entity
        newWeatherData.setCity(city);
        newWeatherData.setDescription(description);
        newWeatherData.setTemperatureCelsius(celsius);

        _beanManagedTransaction.begin();

        try {
            // Act
            _weatherDataRepository.add(newWeatherData);

            // Assert
             Optional<WeatherData> optionalWeatherData = _weatherDataRepository.findById(newWeatherData.getId());
             assertThat(optionalWeatherData.isPresent())
                 .isTrue();

        } catch (Exception ex) {
            fail("Failed to add entity with exception %s", ex.getMessage());
        } finally {
            _beanManagedTransaction.rollback();
        }

    }


//    @Order(4)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "PrimaryKey, Property1Value, Property2Value, Property3Value",
//            "PrimaryKey, Property1Value, Property2Value, Property3Value",
//    })
//    void update_ExistingId_UpdatedData(Long weatherDataId, String property1, String property2, String property3) throws SystemException, NotSupportedException {
//        // Arrange
//        Optional<WeatherData> optionalWeatherData = _weatherDataRepository.findById(weatherDataId);
//        assertThat(optionalWeatherData.isPresent()).isTrue();
//
//        WeatherData existingWeatherData = optionalWeatherData.orElseThrow();
//        assertThat(existingWeatherData).isNotNull();
//
//        // Act
//        // TODO Uncomment code below and assign new value to each property
//        // existingWeatherData.setProperty1(property1);
//        // existingWeatherData.setProperty2(property2);
//
//        _beanManagedTransaction.begin();
//
//        try {
//            WeatherData updatedWeatherData = _weatherDataRepository.update(weatherDataId, existingWeatherData);
//
//            // Assert
//            assertThat(existingWeatherData)
//                    .usingRecursiveComparison()
//                    // .ignoringFields("field1", "field2")
//                    .isEqualTo(updatedWeatherData);
//        } catch (Exception ex) {
//            fail("Failed to update entity with exception %s", ex.getMessage());
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//
//    @Order(5)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "primaryKey1",
//            "primaryKey2",
//    })
//    void deleteById_ExistingId_DeletedData(Long weatherDataId) throws SystemException, NotSupportedException {
//        _beanManagedTransaction.begin();
//
//        try {
//            // Arrange and Act
//            _weatherDataRepository.deleteById(weatherDataId);
//
//            // Assert
//            assertThat(_weatherDataRepository.findById(weatherDataId))
//                    .isEmpty();
//
//        } catch (Exception ex) {
//            fail("Failed to delete entity with exception message %s", ex.getMessage());
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }
//
//
//    @Order(6)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "primaryKey",
//            "primaryKey"
//    })
//    void findById_NonExistingId_IsEmpty(Long weatherDataId) {
//        // Arrange and Act
//        Optional<WeatherData> optionalWeatherData = _weatherDataRepository.findById(weatherDataId);
//
//        // Assert
//        assertThat(optionalWeatherData.isEmpty())
//                .isTrue();
//
//    }
//
//
//    @Order(7)
//    @ParameterizedTest
//    // TODO Change the value below
//    @CsvSource(value = {
//            "Invalid Property1Value, Property2Value, Property3Value, ExpectedExceptionMessage",
//            "Property1Value, Invalid Property2Value, Property3Value, ExpectedExceptionMessage",
//    }, nullValues = {"null"})
//    void create_beanValidation_throwsException(String property1, String property2, String property3, String expectedExceptionMessage) throws SystemException, NotSupportedException {
//        // Arrange
//        WeatherData newWeatherData = new WeatherData();
//        // TODO Change the code below to set each property
//        // newWeatherData.setProperty1(property1);
//        // newWeatherData.setProperty2(property2);
//        // newWeatherData.setProperty3(property3);
//
//        _beanManagedTransaction.begin();
//        try {
//            // Act
//            _weatherDataRepository.add(newWeatherData);
//            fail("An bean validation constraint should have been thrown");
//        } catch (Exception ex) {
//            // Assert
//            assertThat(ex)
//                    .hasMessageContaining(expectedExceptionMessage);
//        } finally {
//            _beanManagedTransaction.rollback();
//        }
//
//    }

}