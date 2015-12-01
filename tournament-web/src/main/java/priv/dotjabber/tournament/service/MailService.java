package priv.dotjabber.tournament.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
@Service
public class MailService {
    private static final Logger LOG = Logger.getLogger(MailService.class);
    public static final String EXCEPTION_SUBJECT = "AITournament exception @ {0}";
    public static final String SCOREBOARD_SUBJECT = "AITournament @ {0}";

    /**
     * Sysadmin.
     */
    @Value("${system.admin}")
    private String systemAdmin;

    /**
     * System email.
     */
    @Value("${system.mailer.user}")
    private String systemMail;

    /**
     * Default email encoding.
     */
    @Value("${system.mailer.encoding}")
    private String systemMailEncoding;

    /**
     * Mail sender (spring).
     */
    @Autowired
    private JavaMailSenderImpl javaMailSender;

    /**
     * Method sends an email to particular user. Adding an attachment is possible as well.
     * @param email recipient email.
     * @param subject email subject.
     * @param content email text.
     * @param attachmentName attachment name.
     * @param attachment attachment itself.
     * @return returns true if mail was sendMessage successfully, otherwise it returns false.
     */
    public boolean sendMessage(String email, String subject, String content, String attachmentName, InputStream attachment,
                               boolean htmlContent) {
        LOG.debug("MailService @ sendMessage(...)");
        LOG.debug("email: " + email);
        LOG.debug("subject: " + subject);
        LOG.debug("content: " + content);
        LOG.debug("attachmentName: " + attachmentName);
        LOG.debug("attachment: " + attachment);

        boolean isValid = false;

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, systemMailEncoding);
            helper.setTo(email);
            helper.setSentDate(new Date());
            helper.setSubject(subject);
            helper.setText(content, htmlContent);
            helper.setFrom(systemMail);

            if(StringUtils.isNotEmpty(attachmentName) && attachment != null) {
                helper.addAttachment(attachmentName, new ByteArrayResource(IOUtils.toByteArray(attachment)));
            }

            javaMailSender.send(message);
            isValid = true;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return isValid;
    }

    /**
     *
     * @param content
     * @return
     */
    public boolean sendError(String content) {
        LOG.debug("MailService @ sendError(...)");

        String errorSubject = MessageFormat.format(EXCEPTION_SUBJECT, new Date());
        return sendMessage(systemAdmin, errorSubject, content, null, null, false);
    }

    public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void setSystemMail(String systemMail) {
        this.systemMail = systemMail;
    }

    public void setSystemMailEncoding(String systemMailEncoding) {
        this.systemMailEncoding = systemMailEncoding;
    }

    public void setSystemAdmin(String systemAdmin) {
        this.systemAdmin = systemAdmin;
    }
}
