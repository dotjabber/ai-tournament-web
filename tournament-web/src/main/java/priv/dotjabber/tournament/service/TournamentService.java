package priv.dotjabber.tournament.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.logging.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.dotjabber.tournament.control.GameController;
import priv.dotjabber.tournament.control.GameFactory;
import priv.dotjabber.tournament.dao.ApplicationDAO;
import priv.dotjabber.tournament.dto.ApplicationDTO;
import priv.dotjabber.tournament.entity.Application;
import priv.dotjabber.tournament.model.GameResult;
import priv.dotjabber.tournament.player.Player;
import priv.dotjabber.tournament.player.PlayerType;
import priv.dotjabber.tournament.task.TournamentTask;
import priv.dotjabber.tournament.transformer.ApplicationTransformer;
import priv.dotjabber.tournament.transformer.Transformers;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Service
public class TournamentService {
    private static final Logger LOG = Logger.getLogger(TournamentService.class);

    private final static String TASK_NAME = "ARBITER";
    private final static String TASK_GROUP = "ARBITER-GROUP";
    private final static String TASK_IDENTITY = "ARBITER-ITEM";

    private final static int SCORE_WIN = 3;
    private final static int SCORE_TIE = 1;

    @Autowired
    @Qualifier("sourcesPath")
    private String sourcesPath;

    @Resource
    private ArrayList<String> gameNames;

    @Autowired
    private CompilationService compilationService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Map<String, GameController> gameControllers;

    @PostConstruct
    @Transactional
    public void init() {

        try {
            // fill in game controllers
            gameControllers = new HashMap<>();
            gameNames.stream().filter(StringUtils::isNotEmpty).forEach(gameName -> {
                gameControllers.put(gameName, GameFactory.get(gameName).getController());
            });

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.start();

            TournamentTask taskObject = (TournamentTask) applicationContext.getBean("tournamentTask");

            MethodInvokingJobDetailFactoryBean jobFactory = new MethodInvokingJobDetailFactoryBean();
            jobFactory.setTargetObject(taskObject);
            jobFactory.setTargetMethod("execute");
            jobFactory.setName(TASK_NAME);
            jobFactory.setGroup(TASK_GROUP);
            jobFactory.setConcurrent(false);
            jobFactory.afterPropertiesSet();

            JobDetail jobDetail = jobFactory.getObject();
            Set<Trigger> jobTrigger = new HashSet<>();

            jobTrigger.add(
                    TriggerBuilder.newTrigger()
                            .withIdentity(TASK_IDENTITY)
                            .forJob(jobDetail)
                            .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 1))
                            .build()
            );

            scheduler.scheduleJob(jobDetail, jobTrigger, false);

        } catch (SchedulerException | ClassNotFoundException | NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param fileName
     * @param fileContent
     */
    @Transactional
    public Map<String, String> addApplication(String fileName, String fileContent) {
        Map<String, String> playerData = new HashMap<>();

        try {
            Pair<String, String> playerMeta = compilationService.parse(fileName, fileContent);
            String playerClassName = playerMeta.getLeft();
            String playerSource = playerMeta.getRight();

            // save the player class
            FileUtils.writeByteArrayToFile(new File(sourcesPath + File.separator + playerClassName), playerSource.getBytes());

            Player playerObject = compilationService.compile(playerClassName, playerSource);
            playerData.put("Nazwa gracza", playerObject.getName());
            playerData.put("Adres email", playerObject.getEmail());
            playerData.put("Gra", playerObject.getGameName());

            Application application = new Application();
            application.setName(playerObject.getName());
            application.setEmail(playerObject.getEmail());
            application.setGameName(playerObject.getGameName());
            application.setPlayerClassName(playerClassName);

            applicationDAO.persist(application);

        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return playerData;
    }

    public List<String> getGameNames() {
        return gameNames;
    }

    public List<ApplicationDTO> getApplications(String gameName) {
        List<Application> entityList = applicationDAO.getApplicationsByGame(gameName);
        return Transformers.toDTOs(entityList, new ApplicationTransformer());
    }

    public void rate(Application one, Application two) throws IOException, IllegalAccessException,
            InstantiationException, ClassNotFoundException {

        if(one.getGameName().equals(two.getGameName()) && gameControllers.containsKey(one.getGameName())) {
            GameController gameController = gameControllers.get(one.getGameName());

            String playerOneCode = FileUtils.readFileToString(new File(sourcesPath + File.separator + one.getPlayerClassName()));
            String playerTwoCode = FileUtils.readFileToString(new File(sourcesPath + File.separator + two.getPlayerClassName()));

            Player playerOne = compilationService.compile(one.getPlayerClassName(), playerOneCode);
            Player playerTwo = compilationService.compile(two.getPlayerClassName(), playerTwoCode);

            gameController.clearGame();
            gameController.setPlayer(PlayerType.PLAYER_ONE, playerOne);
            gameController.setPlayer(PlayerType.PLAYER_TWO, playerTwo);
            gameController.startGame();

            try {
                synchronized (gameController) {
                    gameController.wait();
                }

            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }

            if (gameController.getGameResult() == GameResult.PLAYER_ONE_WINS) {
                one.incScore(SCORE_WIN);
                one.incWins();
                two.incLoses();

            } else if (gameController.getGameResult() == GameResult.PLAYER_TWO_WINS) {
                two.incScore(SCORE_WIN);
                two.incWins();
                one.incLoses();

            } else if (gameController.getGameResult() == GameResult.TIE) {
                one.incScore(SCORE_TIE);
                two.incScore(SCORE_TIE);

                one.incTies();
                two.incTies();
            }
        }
    }
}
