package priv.dotjabber.tournament.jsf.bean;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import priv.dotjabber.tournament.service.TournamentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Component
@Scope(value = "view")
public class ApplicationBean implements Serializable {
    private Map<String, String> playerData;

    @Autowired
    private TournamentService tournamentService;

    public void uploadFile(FileUploadEvent event) throws IOException {
        UploadedFile applicationFile = event.getFile();
        String applicationFileName = applicationFile.getFileName();

        StringWriter writer = new StringWriter();
        InputStream stream = applicationFile.getInputstream();
        IOUtils.copy(stream, writer, StandardCharsets.UTF_8);

        playerData = tournamentService.addApplication(applicationFileName, writer.toString());
    }

    public Map<String, String> getPlayerData() {
        return playerData;
    }

    public void setPlayerData(Map<String, String> playerData) {
        this.playerData = playerData;
    }
}
