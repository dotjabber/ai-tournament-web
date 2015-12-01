package priv.dotjabber.tournament.transformer;

import priv.dotjabber.tournament.dto.ApplicationDTO;
import priv.dotjabber.tournament.entity.Application;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class ApplicationTransformer implements ObjectTransformer<ApplicationDTO, Application> {

    @Override
    public Application toEntity(ApplicationDTO input) {
        Application application = new Application();
        application.setGameName(input.getGameName());
        application.setScore(input.getScore());
        application.setCreationDate(input.getCreationDate());
        application.setId(input.getId());
        application.setScoreDate(input.getScoreDate());
        application.setName(input.getName());
        application.setEmail(input.getEmail());
        application.setWins(input.getWins());
        application.setLoses(input.getLoses());
        application.setTies(input.getTies());

        return application;
    }

    @Override
    public ApplicationDTO toDTO(Application input) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setGameName(input.getGameName());
        applicationDTO.setScore(input.getScore());
        applicationDTO.setCreationDate(input.getCreationDate());
        applicationDTO.setId(input.getId());
        applicationDTO.setScoreDate(input.getScoreDate());
        applicationDTO.setName(input.getName());
        applicationDTO.setEmail(input.getEmail());
        applicationDTO.setWins(input.getWins());
        applicationDTO.setLoses(input.getLoses());
        applicationDTO.setTies(input.getTies());

        return applicationDTO;
    }
}
