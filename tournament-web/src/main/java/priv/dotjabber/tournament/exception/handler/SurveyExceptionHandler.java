package priv.dotjabber.tournament.exception.handler;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import priv.dotjabber.tournament.service.MailService;
import priv.dotjabber.tournament.utils.ExceptionHandlerUtils;
import priv.dotjabber.tournament.utils.MailUtils;


import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class SurveyExceptionHandler extends ExceptionHandlerWrapper {
    private static final Logger LOG = Logger.getLogger(SurveyExceptionHandler.class);
    private static final String EXCEPTION_PAGE = "/pages/error/503";
    private static final String EXPIRATION_PAGE = "/pages/error/401";

    private ExceptionHandler wrapped;
    private MailService mailService;

    public SurveyExceptionHandler(ExceptionHandler exception) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();

        JavaMailSenderImpl mailSender = application.evaluateExpressionGet(
                facesContext,
                "#{mailSender}",
                JavaMailSenderImpl.class
        );

        String systemAdmin = application.evaluateExpressionGet(
                facesContext,
                "#{mailTeam}",
                String.class
        );

        mailService = new MailService();
        mailService.setJavaMailSender(mailSender);
        mailService.setSystemMail(mailSender.getUsername());
        mailService.setSystemMailEncoding(mailSender.getDefaultEncoding());
        mailService.setSystemAdmin(systemAdmin);

        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        for (ExceptionQueuedEvent event : getUnhandledExceptionQueuedEvents()) {
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            Throwable throwable = context.getException();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

            String redirectPage = ExceptionHandlerUtils.getContextPath(request);

            try {
                if(throwable instanceof ViewExpiredException) {
                    redirectPage = redirectPage + EXPIRATION_PAGE;

                } else {
                    redirectPage = redirectPage + EXCEPTION_PAGE;

                    Map<String, String> errorParams = new HashMap<>();
                    errorParams.put("IP", StringUtils.stripToEmpty(ExceptionHandlerUtils.getRemoteAddress()));
                    errorParams.put("URI", StringUtils.stripToEmpty(ExceptionHandlerUtils.getLastErrorURI(request)));
                    errorParams.put("QUERY_STRING", StringUtils.stripToEmpty(ExceptionHandlerUtils.getLastRequestQueryString(request)));
                    errorParams.put("REFERER", StringUtils.stripToEmpty(ExceptionHandlerUtils.getLastReferer(request)));
                    errorParams.put("STACK_TRACE", Throwables.getStackTraceAsString(throwable));

                    String errorMessage = MailUtils.getTemplate(MailUtils.EXCEPTION_TPL, errorParams);
                    mailService.sendError(errorMessage);
                }

                facesContext.getExternalContext().redirect(redirectPage);

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }

            LOG.error("pojawił się błąd: ", throwable);
        }

        getWrapped().handle();
    }
}
