package priv.dotjabber.tournament.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public final class ExceptionHandlerUtils {
    private final static String SERVLET_EXCEPTION_ATTR = "javax.servlet.error.exception";
    private final static String SERVLET_STATUS_CODE_ATTR = "javax.servlet.error.status_code";
    private final static String SERVLET_REQUEST_URI_ATTR = "javax.servlet.error.request_uri";
    private final static String SERVLET_NAME_ATTR = "javax.servlet.error.servlet_name";
    private final static String SERVLET_CONTEXT_PATH = "javax.servlet.forward.context_path";

    public static String getRemoteAddress() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();

        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletAttributes = (ServletRequestAttributes) attributes;
            return servletAttributes.getRequest().getRemoteAddr();
        }

        return StringUtils.EMPTY;
    }

    public static Exception getLastError(HttpServletRequest request) {
        Object ex = request.getAttribute(SERVLET_EXCEPTION_ATTR);
        if (ex instanceof Exception) {
            return (Exception) ex;
        }

        return null;
    }

    public static String getLastErrorURI(HttpServletRequest request) {
        Object value = request.getAttribute(SERVLET_REQUEST_URI_ATTR);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public static String getLastErrorCode(HttpServletRequest request) {
        Object value = request.getAttribute(SERVLET_STATUS_CODE_ATTR);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public static String getLastErrorServlet(HttpServletRequest request) {
        Object value = request.getAttribute(SERVLET_NAME_ATTR);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public static String getContextPath(HttpServletRequest request) {
        Object value = request.getAttribute(SERVLET_CONTEXT_PATH);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public static String getLastReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public static String getLastRequestQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String[]> params = request.getParameterMap();

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            for (String value : param.getValue()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }

                queryString.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.name()));
                queryString.append("=");
                queryString.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            }
        }

        return queryString.toString();
    }
}
