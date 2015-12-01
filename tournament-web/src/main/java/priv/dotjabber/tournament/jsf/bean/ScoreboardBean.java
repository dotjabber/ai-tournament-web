package priv.dotjabber.tournament.jsf.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import priv.dotjabber.tournament.dto.ApplicationDTO;
import priv.dotjabber.tournament.service.TournamentService;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Component
@Scope(value = "view")
public class ScoreboardBean implements Serializable {

    @Autowired
    private TournamentService tournamentService;

    public List<String> getGameNames() {
        return tournamentService.getGameNames();
    }

    public List<ApplicationDTO> getResults(String gameName) {
        List<ApplicationDTO> results = tournamentService.getApplications(gameName);
        Collections.sort(results);

        return results;
    }
}
