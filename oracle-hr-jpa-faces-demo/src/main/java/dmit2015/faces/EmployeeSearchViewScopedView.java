package dmit2015.faces;

import dmit2015.entity.Employee;
import dmit2015.persistence.HumanResourcesRepository;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import org.omnifaces.util.Messages;

import java.io.Serializable;
import java.util.List;

@Named("currentEmployeeSearchViewScopedView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
// class must implement Serializable
public class EmployeeSearchViewScopedView implements Serializable {

    // Declare read/write properties (field + getter + setter) for each form field
    @Getter @Setter
    private String searchJobTitle1;

    // Declare read only properties (field + getter) for data sources
    @Getter
    private List<Employee> queryEmployeeResultList;

    // Declare private fields for internal usage only objects
    @Inject
    private HumanResourcesRepository _hrRepository;

    public List<String> completeJobTitle(String query) {
        return _hrRepository.findJobTitlesByPartialValue(query);
    }

    public void onSearchByJobTitle1() {
        try {
            queryEmployeeResultList = _hrRepository.findEmployeesByJobTitle(searchJobTitle1);
            Messages.addGlobalInfo("Query returned {0} results.", queryEmployeeResultList.size());
        } catch (Exception ex) {
            Messages.addGlobalError("Query failed with exception: {0}", ex.getMessage());
        }
    }

    @PostConstruct // This method is executed after DI is completed (fields with @Inject now have values)
    public void init() { // Use this method to initialized fields instead of a constructor
        // Code to access fields annotated with @Inject

    }

    public void onClear() {
        // Set all fields to default values
        queryEmployeeResultList = null;
        searchJobTitle1 = null;
    }
}