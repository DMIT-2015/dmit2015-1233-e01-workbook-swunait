package dmit2015.repository;

import dmit2015.entity.CallerUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * This Jakarta Persistence class contains methods for performing CRUD operations on a
 * Jakarta Persistence managed entity.
 */
@ApplicationScoped
public class CallerUserRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext (unitName="oracle-jpa-user2015-pu")
    private EntityManager _entityManager;

    @Transactional
    public void add(@Valid CallerUser newCallerUser) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        _entityManager.persist(newCallerUser);
    }

    public Optional<CallerUser> findById(String callerUserId) {
        try {
            CallerUser querySingleResult = _entityManager.find(CallerUser.class, callerUserId);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // callerUserId value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public List<CallerUser> findAll() {
        return _entityManager.createQuery("SELECT o FROM CallerUser o ", CallerUser.class)
                .getResultList();
    }

    @Transactional
    public CallerUser update(String id, @Valid CallerUser updatedCallerUser) {
        Optional<CallerUser> optionalCallerUser = findById(id);
        if (optionalCallerUser.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", id);
            throw new RuntimeException(errorMessage);
        } else {
            var existingCallerUser = optionalCallerUser.orElseThrow();
            // Update only properties that is editable by the end user
            // TODO: Copy each edit property from updatedCoffeeBean to existingCoffeeBean
            // existingCallerUser.setPropertyName(updatedCallerUser.getPropertyName());

            updatedCallerUser = _entityManager.merge(existingCallerUser);
        }
        return updatedCallerUser;
    }

    @Transactional
    public void delete(CallerUser existingCallerUser) {
        // Write code to throw a RuntimeException if this entity contains child records

        if (_entityManager.contains(existingCallerUser)) {
            _entityManager.remove(existingCallerUser);
        } else {
            _entityManager.remove(_entityManager.merge(existingCallerUser));
        }
    }

    @Transactional
    public void deleteById(String callerUserId) {
        Optional<CallerUser> optionalCallerUser = findById(callerUserId);
        if (optionalCallerUser.isPresent()) {
            CallerUser existingCallerUser = optionalCallerUser.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records

            _entityManager.remove(existingCallerUser);
        }
    }

    public long count() {
        return _entityManager.createQuery("SELECT COUNT(o) FROM CallerUser o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        _entityManager.flush();
        _entityManager.clear();
        _entityManager.createQuery("DELETE FROM CallerUser").executeUpdate();
    }

}