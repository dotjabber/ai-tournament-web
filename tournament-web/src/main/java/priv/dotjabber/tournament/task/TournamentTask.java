package priv.dotjabber.tournament.task;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import priv.dotjabber.tournament.dao.ApplicationDAO;
import priv.dotjabber.tournament.entity.Application;
import priv.dotjabber.tournament.service.MailService;
import priv.dotjabber.tournament.service.TournamentService;
import priv.dotjabber.tournament.utils.MailUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Component
@Scope("prototype")
public class TournamentTask {
    private static final Logger LOG = Logger.getLogger(TournamentTask.class);

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Transactional
    public void execute() {
        LOG.debug("TournamentTask @ execute()");

        for(String gameName : tournamentService.getGameNames()) {
            List<Application> applications = applicationDAO.getApplicationsByGame(gameName);
            applications.forEach(Application::clear);

            for(Application one : applications) {
                for(Application two : applications) {

                    if(one != two) {
                        try {
                            tournamentService.rate(one, two);

                        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                            LOG.error(e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            Collections.sort(applications);
            for(int i = 0; i < applications.size(); i++) {
                Application application = applications.get(i);

                Map<String, String> mailParams = new HashMap<>();
                mailParams.put("NAME", application.getName() );
                mailParams.put("GAME", application.getGameName() );
                mailParams.put("PLACE", String.valueOf(i + 1) );
                mailParams.put("WINS", String.valueOf(application.getWins()) );
                mailParams.put("TIES", String.valueOf(application.getTies()) );
                mailParams.put("LOSES", String.valueOf(application.getLoses()) );
                mailParams.put("SCORE", String.valueOf(application.getScore()) );

                String message = MailUtils.getTemplate(MailUtils.SCOREBOARD_TPL, mailParams);
                String subject = MessageFormat.format(MailService.SCOREBOARD_SUBJECT, new Date());

                mailService.sendMessage(application.getEmail(), subject, message, null, null, false);
                applicationDAO.merge(application);
            }
        }
    }
}
