package dmit2015.repository;

import dmit2015.entity.WeatherData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This Jakarta Persistence class contains methods for performing CRUD operations on a
 * Jakarta Persistence managed entity.
 */
@ApplicationScoped
@RequiresAuthentication
public class WeatherDataRepository {

    @Inject
    private Subject _subject;

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext (unitName="oracle-jpa-user2015-pu")
    private EntityManager _entityManager;

    @Transactional
    @RequiresRoles(value = {"Sales"})
    public void add(@Valid WeatherData newWeatherData) {
        final String username = (String) _subject.getPrincipal();
        newWeatherData.setUsername(username);

        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        _entityManager.persist(newWeatherData);
    }

    public Optional<WeatherData> findById(Long weatherDataId) {
        try {
            WeatherData querySingleResult = _entityManager.find(WeatherData.class, weatherDataId);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // weatherDataId value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    /**
     *  For the Sales role return only data associated with the current authenticated user.
     *  For the IT role return all data.
     *  For other users not in Sales or IT return an empty list.
     * @return
     */
    public List<WeatherData> findAll() {
        if (_subject.hasRole("Sales")) {
            return _entityManager.createQuery(
                    "SELECT o FROM WeatherData o where o.username = :usernameValue", WeatherData.class)
                    .setParameter("usernameValue", _subject.getPrincipal())
                    .getResultList();
        } else if (_subject.hasRole("IT")) {
            return _entityManager.createQuery("SELECT o FROM WeatherData o ", WeatherData.class)
                    .getResultList();
        }
        return Collections.emptyList();
    }

    @RequiresRoles(value = {"Sales","IT"}, logical = Logical.OR)
    @Transactional
    public WeatherData update(Long id, @Valid WeatherData updatedWeatherData) {
        Optional<WeatherData> optionalWeatherData = findById(id);
        if (optionalWeatherData.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", id);
            throw new RuntimeException(errorMessage);
        } else {
            var existingWeatherData = optionalWeatherData.orElseThrow();
            // Update only properties that is editable by the end user
            existingWeatherData.setDate(updatedWeatherData.getDate());
            existingWeatherData.setCity(updatedWeatherData.getCity());
            existingWeatherData.setDescription(updatedWeatherData.getDescription());
            existingWeatherData.setTemperatureCelsius(updatedWeatherData.getTemperatureCelsius());

            updatedWeatherData = _entityManager.merge(existingWeatherData);
        }
        return updatedWeatherData;
    }

    @RequiresRoles(value = {"Sales","IT"}, logical = Logical.OR)
    @Transactional
    public void delete(WeatherData existingWeatherData) {
        // Write code to throw a RuntimeException if this entity contains child records

        if (_entityManager.contains(existingWeatherData)) {
            _entityManager.remove(existingWeatherData);
        } else {
            _entityManager.remove(_entityManager.merge(existingWeatherData));
        }
    }

    @RequiresRoles(value = {"Sales","IT"}, logical = Logical.OR)
    @Transactional
    public void deleteById(Long weatherDataId) {
        Optional<WeatherData> optionalWeatherData = findById(weatherDataId);
        if (optionalWeatherData.isPresent()) {
            WeatherData existingWeatherData = optionalWeatherData.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records

            _entityManager.remove(existingWeatherData);
        }
    }

    public long count() {
        return _entityManager.createQuery("SELECT COUNT(o) FROM WeatherData o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        _entityManager.flush();
        _entityManager.clear();
        _entityManager.createQuery("DELETE FROM WeatherData").executeUpdate();
    }

}