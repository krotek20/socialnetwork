package socialnetwork.repository.database;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import socialnetwork.config.ApplicationContext;

public class PooledDataSource {
    private static final BasicDataSource basicDS;

    static {
        basicDS = new BasicDataSource();
        // Loading properties file
        basicDS.setUrl(ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.url"));
        basicDS.setUsername(ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.username"));
        basicDS.setPassword(ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.password"));
        // Parameters for connection pooling
        basicDS.setInitialSize(10);
        basicDS.setMaxTotal(10);
    }

    public static DataSource getDataSource() {
        return basicDS;
    }
}
