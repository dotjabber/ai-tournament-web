package priv.dotjabber.tournament.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class FileUtils {
    public static InputStream getResourceAsInputStream(String path) {
        return FileUtils.class.getClassLoader().getResourceAsStream(path);
    }

    public static InputStream getInputStream(File file) throws IOException {
        return org.apache.commons.io.FileUtils.openInputStream(file);
    }
}
