package priv.dotjabber.tournament.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ResourceBundle;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class MessageUtils {
    private static final ResourceBundle MSG = ResourceBundle.getBundle("messages/messages");

    public static String getBundle(String key) {
        if(MSG.containsKey(key)) {
            return StringEscapeUtils.escapeHtml4(
                    MSG.getString(key)
            );

        } else {
            return null;
        }
    }

    public static String getMessage(String message) {
        return StringEscapeUtils.escapeHtml4(
                message
        );
    }
}