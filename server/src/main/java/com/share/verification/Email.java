package com.share.verification;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.share.Database;
import com.share.OTP;

import org.jsoup.nodes.Document;

import javax.mail.internet.InternetAddress;
import javax.mail.PasswordAuthentication;

import java.util.Properties;

public class Email {
    private String host = "smtp.gmail.com";
    private String port = "587";
    private String username = "shareaze24@gmail.com";
    private String password = "clrp vlxt bmsf rglq";

    private Properties properties;
    private static Session session;
    private static Message message;
    private static Document document;
    private static Element user;
    private static Element code;

    public Email() {
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static void send(String from, String reciver, int uid, String vcode) {
        String body = OTP.getTemplateText("html", "verification.html");
        document = Jsoup.parse(body);
        user = document.getElementById("username");
        code = document.getElementById("vcode");
        if (user != null && code != null) {
            user.text(Database.STS_USER.getUsername(uid));
            code.text(vcode);
        }

        body = document.html();
        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciver));
            message.setSubject("STS Verification");
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
