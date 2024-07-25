package dmit2015.tools;

import dmit2015.entity.WeatherData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class JakartaPersistenceDataGenerator {

    public static void main(String[] args) {
        // The persistenceUnitName can be passed as an argument otherwise it defaults to "local-mssql-dmit2015-jpa-pu"
        String persistenceUnitName = (args.length == 1) ? args[0] : "resource-local-mssql-dmit2015-jpa-pu";
        // https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.html#obtaining-an-entity-manager-factory-in-a-java-se-environment
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        JakartaPersistenceDataGenerator.generateData(em);
        em.getTransaction().commit();
        em.close();
        emf.close();
        System.out.printf("Finished creating data for persistence unit %s\n", persistenceUnitName);
    }

    public static void generateData(EntityManager em) {
        var faker = new Faker();
        for (int counter = 1; counter <= 15; counter++) {

            var currentWeatherData = new WeatherData();
            currentWeatherData.setCity(faker.address().city());
            currentWeatherData.setDate(LocalDate.parse(faker.date().past(10, TimeUnit.DAYS, "yyyy-MM-dd")));
            currentWeatherData.setDescription(faker.weather().description());
            currentWeatherData.setTemperatureCelsius(faker.number().numberBetween(-20,50));

            em.persist(currentWeatherData);
        }
    }
}