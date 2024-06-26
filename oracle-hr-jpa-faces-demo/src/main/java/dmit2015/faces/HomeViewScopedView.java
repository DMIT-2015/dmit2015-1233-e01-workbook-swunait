package dmit2015.faces;

import dmit2015.entity.Job;
import dmit2015.persistence.HumanResourcesRepository;
import jakarta.inject.Inject;
import lombok.Getter;
import org.omnifaces.util.Messages;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

import java.io.Serializable;
import java.util.List;

@Named("currentHomeViewScopedView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
// class must implement Serializable
public class HomeViewScopedView implements Serializable {

    // Declare read/write properties (field + getter + setter) for each form field

    // Declare read only properties (field + getter) for data sources
    @Getter
    private List<Job> jobs;

    // Declare private fields for internal usage only objects
    @Inject
    private HumanResourcesRepository _hrRepository;

    @PostConstruct // This method is executed after DI is completed (fields with @Inject now have values)
    public void init() { // Use this method to initialized fields instead of a constructor
        // Code to access fields annotated with @Inject
        try {
            jobs = _hrRepository.findAllJobs();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }

    public void onClear() {
        // Set all fields to default values

    }
}