package dmit2015.batch;

import dmit2015.entity.EnforcementZoneCentre;

import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Batchlets are task oriented step that is called once.
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
@Dependent
public class EnforcementZoneCentreBatchlet extends AbstractBatchlet {

    @PersistenceContext//(unitName = "mssql-jpa-pu")
    private EntityManager _entityManager;

    @Inject
    private JobContext _jobContext;

    @Inject
    @BatchProperty(name = "input_file")
    private String inputFile;

    /**
     * Perform a task and return "COMPLETED" if the job has successfully completed
     * otherwise return "FAILED" to indicate the job failed to complete.
     * If this method throws an exception, the batchlet step ends with a batch status of FAILED.
     */
    @Transactional
    @Override
    public String process() throws Exception {
//        Properties jobParameters = _jobContext.getProperties();
//        String inputFile = jobParameters.getProperty("input_file");

        // For reading external files outside of the project use the code below:
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(inputFile).toFile())))	{
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(_inputFile))))	{
            String line;
            final String delimiter = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
            // Skip the first line as it is containing column headings
            reader.readLine();
            while ((line = reader.readLine()) != null) {

                Optional<EnforcementZoneCentre> optionalEnforcementZoneCentre = EnforcementZoneCentre.parseCsv(line);
                EnforcementZoneCentre currentEnforcementZoneCentre = optionalEnforcementZoneCentre.orElseThrow();

                _entityManager.persist(currentEnforcementZoneCentre);
            }
        }

        return "COMPLETED";     // The job has successfully completed
    }
}