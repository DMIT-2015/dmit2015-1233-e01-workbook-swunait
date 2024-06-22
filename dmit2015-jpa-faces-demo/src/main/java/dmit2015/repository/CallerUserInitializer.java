package dmit2015.repository;

import dmit2015.entity.CallerUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CallerUserInitializer {
    private final Logger _logger = Logger.getLogger(CallerUserInitializer.class.getName());

    @Inject
    private CallerUserRepository _callerUserRepository;


    /**
     * Using the combination of `@Observes` and `@Initialized` annotations, you can
     * intercept and perform additional processing during the phase of beans or events
     * in a CDI container.
     * <p>
     * The @Observers is used to specify this method is in observer for an event
     * The @Initialized is used to specify the method should be invoked when a bean type of `ApplicationScoped` is being
     * initialized
     * <p>
     * Execute code to create the test data for the entity.
     * This is an alternative to using a @WebListener that implements a ServletContext listener.
     * <p>
     * ]    * @param event
     */
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        _logger.info("Initializing callerUsers");

        if (_callerUserRepository.count() == 0) {
            /* You have three options for creating the test data:
                Option 2) Read the test data from a text file when testing a large dataset.
             */

            try {
                // Starter code to read data from a text file one line at a time.
                    try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/employees.csv"))))) {
                        String line;
                        reader.readLine();
                        while ((line = reader.readLine()) != null) {
                            Optional<CallerUser> optionalCallerUser = CallerUser.parseCsv(line);
                            if (optionalCallerUser.isPresent()) {
                                CallerUser csvCallerUser = optionalCallerUser.orElseThrow();
                                try {
                                    _callerUserRepository.add(csvCallerUser);
                                } catch (Exception ex) {
                                    String message = String.format("Failed to create username %s", csvCallerUser.getUsername());
                                    _logger.log(Level.SEVERE, message, ex);
                                }
                            }
                        }
                    }


            } catch (Exception ex) {
                _logger.fine(ex.getMessage());
            }

            _logger.info("Created " + _callerUserRepository.count() + " records.");
        }
    }
}