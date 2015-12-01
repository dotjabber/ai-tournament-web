package priv.dotjabber.tournament.utils;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author mkowalski@opi.org.pl
 */
public class SQLUtils {
    private static final Logger LOG = Logger.getLogger(SQLUtils.class);

    /**
     *
     */
    private final static String SQL_PACKAGE = "sql";

    /**
     *
     * @param entityName
     * @param queryName
     * @return
     */
    private static String getSql(String entityName, String queryName) {
        String sqlPath = SQL_PACKAGE + "/" + entityName + "/" + queryName + ".sql";
        InputStream stream = SQLUtils.class.getClassLoader().getResourceAsStream(sqlPath);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return writer.toString();
    }

    public static String getSql(Class<?> clazz, String queryName) {
        return getSql(clazz.getSimpleName(), queryName);
    }

    public static String getSqlCommon(String tableName, String queryName) {
        String sqlQuery = getSql("Common", queryName);
        return (sqlQuery != null) ? sqlQuery.replace(":table", tableName) : null;
    }

}
