package dmit2015.persistence;

import dmit2015.entity.Job;
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
public class HumanResourcesRepositoryRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext (unitName="oracle-jpa-hr-pu")
    private EntityManager _entityManager;

    /**
     * Return a list of Job sorted by the JobTitle
     * @return a list of Job sorted by jobTitle
     */
    public List<Job> findAllJobs() {
        var query = _entityManager.createQuery("""
select j
from Job j
order by j.jobTitle
""", Job.class);
        return query.getResultList();
    }


}