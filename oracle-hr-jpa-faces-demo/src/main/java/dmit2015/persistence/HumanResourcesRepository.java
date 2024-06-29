package dmit2015.persistence;

import dmit2015.entity.Employee;
import dmit2015.entity.Job;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * This Jakarta Persistence class contains methods for performing CRUD operations on a
 * Jakarta Persistence managed entity.
 */
@ApplicationScoped
public class HumanResourcesRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext (unitName="oracle-jpa-hr-pu")
    private EntityManager _entityManager;

    public List<Employee> findEmployeesByJobTitle(String jobTitle) {
        var query = _entityManager.createQuery("""
select e
from Employee e join fetch e.department join fetch e.manager
where lower(e.job.jobTitle) like :jobTitleValue
""", Employee.class);
        query.setParameter("jobTitleValue", "%" + jobTitle.toLowerCase() + "%");
        return query.getResultList();
    }

    public Job findJobByJobTitle(String jobTitle) {
        var query = _entityManager.createQuery("""
select j from Job j where j.jobTitle = :jobTitleValue
""", Job.class);
        query.setParameter("jobTitleValue",jobTitle);
        return query.getSingleResult();
    }

    public List<String> findJobTitlesByPartialValue(String jobTitle) {
        var query = _entityManager.createQuery("""
select j.jobTitle
from Job j
where lower(j.jobTitle) like :jobTitleValue
order by j.jobTitle
""", String.class);
        query.setParameter("jobTitleValue", "%" + jobTitle.toLowerCase() + "%");
        return query.getResultList();
    }

    public List<Job> findJobsByPartialValue(String jobTitle) {
        var query = _entityManager.createQuery("""
select j
from Job j
where lower(j.jobTitle) like :jobTitleValue
order by j.jobTitle
""", Job.class);
        query.setParameter("jobTitleValue", "%" + jobTitle.toLowerCase() + "%");
        return query.getResultList();
    }


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