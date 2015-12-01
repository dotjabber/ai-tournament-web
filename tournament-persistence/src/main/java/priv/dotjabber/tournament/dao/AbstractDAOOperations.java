package priv.dotjabber.tournament.dao;

import org.jboss.logging.Logger;
import priv.dotjabber.tournament.utils.ResultUtils;
import priv.dotjabber.tournament.utils.SQLUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author mkowalski@opi.org.pl
 */
public abstract class AbstractDAOOperations {
    private static final Logger LOG = Logger.getLogger(AbstractDAOOperations.class);

    /**
     *
     */
    private Class<?> clazz;

    /**
     *
     */
    private String tableName;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     */
    public AbstractDAOOperations() {
        try {
            String entityClassName = this.getClass().getSimpleName().replace("DAO", "");
            clazz = Class.forName("priv.dotjabber.tournament.entity." + entityClassName);
            tableName = clazz.getSimpleName();

        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);

            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @return
     */
    public Integer getCount() {
        String queryString = SQLUtils.getSqlCommon(tableName, "getCount");
        Query query = executeQuery(clazz, queryString, null);

        return ResultUtils.getSingleInteger(query);
    }

    /**
     *
     * @param limit
     * @param offset
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(int limit, int offset) {
        String queryString = SQLUtils.getSqlCommon(tableName, "getAll");
        Query query = executeQuery(clazz, queryString, null);

        return (List<T>) ResultUtils.getResultList(clazz, query);
    }

    /**
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll() {
        String queryString = SQLUtils.getSqlCommon(tableName, "getAll");
        Query query = executeQuery(clazz, queryString, null);

        return (List<T>) ResultUtils.getResultList(clazz, query);
    }

    /**
     *
     * @param id
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getById(Serializable id) {
        return (T) findById(clazz, id);
    }

    /**
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    public <T> T findById(Class<T> clazz, Serializable id) {
        return entityManager.find(clazz, id);
    }

    /**
     *
     * @param entities
     */
    public void persist(Serializable... entities) {
        for (Serializable entity : entities) {
            entityManager.persist(entity);
        }

        entityManager.flush();
        entityManager.clear();
    }

    /**
     *
     * @param entities
     */
    public void merge(Serializable... entities) {
        for (Serializable entity : entities) {
            entityManager.merge(entity);
        }

        entityManager.flush();
        entityManager.clear();
    }

    /**
     *
     * @param entities
     */
    public void remove(Serializable... entities) {
        for (Serializable entity : entities) {
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }

        entityManager.flush();
        entityManager.clear();
    }

    /**
     *
     * @param clazz
     * @param queryString
     * @param params
     * @param <T>
     * @return
     */
    protected <T> Query executeQuery(final Class<T> clazz,
                                       final String queryString, final Map<String, String> params) {
        return executeQuery(clazz, queryString, params, null, null);
    }

    /**
     *
     * @param clazz
     * @param queryString
     * @param params
     * @param offset
     * @param limit
     * @param <T>
     * @return
     */
    protected <T> Query executeQuery(final Class<T> clazz, String queryString, final Map<String, String> params,
                                       final Integer offset, final Integer limit) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Object object = entry.getValue();

                if (object != null) {
                    queryString = queryString.replace(":" + entry.getKey(), object.toString());
                }
            }
        }

        Query query = null;
        if(clazz != null) {
            query = getEntityManager().createNativeQuery(queryString, clazz);

        } else {
            query = getEntityManager().createNativeQuery(queryString);
        }

        if (offset != null && limit != null) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        return query;
    }

    /**
     *
     * @param sqlName
     * @return
     */
    public String getSql(String sqlName) {
        return SQLUtils.getSql(clazz, sqlName);
    }

    /**
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     *
     * @return
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     *
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
