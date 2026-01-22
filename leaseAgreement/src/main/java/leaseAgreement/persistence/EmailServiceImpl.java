package leaseAgreement.persistence;

import leaseAgreement.api.service.EmailService;

import leaseAgreement.api.service.EmailService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component(service = EmailService.class)
public class EmailServiceImpl implements EmailService {

    private Session mailSession;

    // Hardcoded for teaching/demo
    private static final String USERNAME = "soonjit.tech@gmail.com";
    private static final String PASSWORD = "ivzautkirfoxjirx";

    @Activate
    public void activate() {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
