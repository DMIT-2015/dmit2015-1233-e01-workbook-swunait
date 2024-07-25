package common.batch;

import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.runtime.context.JobContext;
import jakarta.batch.runtime.BatchStatus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This Batchlet reading native SQL statements from a sql script and executes
 * each line in the sql script file
 * <p>
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
@Dependent
public class ExecuteSingleLineSQLStatementBatchlet extends AbstractBatchlet {

    @Inject
    private JobContext _jobContext;

    @Inject
    private Logger _logger;

    @PersistenceContext(unitName = "mssql-dwpubsales-jpa-pu")
    private EntityManager _entityManager;

    @Inject
    @BatchProperty(name = "sql_script_file")
    private String sqlScriptFile;


    /**
     * Perform a task and return "COMPLETED" if the job has successfully completed
     * otherwise return "FAILED" to indicate the job failed to complete.
     */
    @Transactional
    @Override
    public String process() throws Exception {
        String batchStatus = BatchStatus.COMPLETED.toString();

        try {
//            try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(sqlScriptFile).toFile()))) { // for reading external files
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(sqlScriptFile))))) { // for reading internal files
                String line;
                while ((line = reader.readLine()) != null) {
                    _entityManager.createNativeQuery(line).executeUpdate();
                }
            }
        } catch (Exception ex) {
            batchStatus = BatchStatus.FAILED.toString();

            _logger.fine("Batchlet failed with exception: " + ex.getMessage());
        }

        return batchStatus;
    }
}