package priv.dotjabber.tournament.jsf.bean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import priv.dotjabber.tournament.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Component
@Scope(value = "view")
public class DownloadBean {

    @Autowired
    @Qualifier("jarPath")
    private String jarPath;

    public StreamedContent getLibrary(String fileName) throws IOException {
        File file = new File(jarPath.replace("\\", "/"));
        InputStream inputStream = FileUtils.getInputStream(file);
        return new DefaultStreamedContent(inputStream, "application/octet-stream", fileName);
    }
}
