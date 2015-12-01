package priv.dotjabber.tournament.utils;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

public final class ResultUtils {

    public static Integer getSingleInteger(final Query query) {
        return getSingleResult(BigInteger.class, query).intValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getResultList(final Class<T> clazz, final Query query) {
        return (List<T>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSingleResult(final Class<T> clazz, final Query query) {
        return (T) query.getSingleResult();
    }
}
