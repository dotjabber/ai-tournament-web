package priv.dotjabber.tournament.dao;

import org.springframework.stereotype.Repository;
import priv.dotjabber.tournament.entity.Application;
import priv.dotjabber.tournament.utils.ResultUtils;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ApplicationDAO extends AbstractDAOOperations {

    public List<Application> getApplicationsByGame(String gameName) {
        String queryString = getSql("getApplicationsByGame");

        Map<String, String> params = new HashMap<>();
        params.put("gameName", gameName);

        Query query = executeQuery(Application.class, queryString, params);
        return ResultUtils.getResultList(Application.class, query);
    }
}
