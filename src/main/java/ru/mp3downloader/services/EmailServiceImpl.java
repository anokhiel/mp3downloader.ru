package ru.mp3downloader.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Slf4j
@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.sender.username}")
    private String fromAddress;

    public void sendMailMultipart(String toEmail, String subject, String message, File file) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(fromAddress);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(message);

        if(file != null){
            helper.addAttachment(file.getName(), file);
        }
        log.info(fromAddress);
        javaMailSender.send(mimeMessage);
    }

    public void sendMail(String toEmail, String subject, String message) throws MessagingException {
        sendMailMultipart(toEmail, subject, message, null);
    }

    public void sendMail(String toEmail, String subject, String message, File file) throws MessagingException {
        sendMailMultipart(toEmail, subject, message, file);
    }

}
