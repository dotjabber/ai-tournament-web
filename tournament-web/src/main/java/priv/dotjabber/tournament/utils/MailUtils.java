package priv.dotjabber.tournament.utils;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class MailUtils {
    private static final Logger LOG = Logger.getLogger(MailUtils.class);
    private static final Pattern MARKER_PATTERN = Pattern.compile("\\{([A-Z_]+)\\}");

    public static String EXCEPTION_TPL = "/template/exception.tpl";
    public static String SCOREBOARD_TPL = "/template/scoreboard.tpl";

    public static String getTemplate(String templateName, Map<String, String> parameters) {
        String result = "";

        try {
            String templateContent = IOUtils.toString(FileUtils.getResourceAsInputStream(templateName), Charsets.UTF_8);
            result = getMessage(templateContent, parameters);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static String getMessage(String messageTemplate, Map<String, String> parameters) {
        Matcher matcher = MARKER_PATTERN.matcher(messageTemplate);
        String result = new String(messageTemplate);

        while(matcher.find()) {
            String marker = matcher.group(1);

            if(!parameters.containsKey(marker)) {
                parameters.put(marker, "");
            }

            result = result.replace("{" + marker + "}", parameters.get(marker));
        }

        return result;
    }
}
