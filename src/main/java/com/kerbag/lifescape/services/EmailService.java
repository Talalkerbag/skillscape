package com.kerbag.lifescape.services;

import com.kerbag.lifescape.models.PendingRegistration;
import com.kerbag.lifescape.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@example.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendVerificationEmail(PendingRegistration pendingRegistration) throws MessagingException {
        String subject = "Please verify your registration";
        String senderName = "LifeScapePath";

        // Prepare the evaluation context for the template
        String mailContent = "<p>Dear " + pendingRegistration.getFirstName() + ",</p>"
                + "<p>Please click the link below to verify your registration:</p>"
                + "<h3><a href=\"" + this.baseUrl + "/api/users/verify?code="
                + pendingRegistration.getVerificationCode() + "\">VERIFY</a></h3>"
                + "<p>Thank you<br>The " + senderName + " Team</p>";

        // Create a MIME message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setFrom("kerbagtalal94@gmail.com");
        helper.setTo(pendingRegistration.getEmail());
        helper.setSubject(subject);
        helper.setText(mailContent, true); // true indicates HTML content

        mailSender.send(mimeMessage);
    }

    public void sendPasswordResetEmail(User user, String resetToken) throws MessagingException {
        String resetLink = "http://lifeskillpath.com/reset-password?token=" + resetToken;
        // Code to send email
        mailSender.send(prepareResetEmail(user, resetLink));
    }

    private MimeMessage prepareResetEmail(User user, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("noreply@lifescape.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Reset Your Password");

        String content = "<p>Hello, " + user.getFirstName() + "</p>" +
                "<p>You have requested to reset your password. Please click the link below to set a new password:</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a>" +
                "<p>If you did not request this, please ignore this email.</p>";
        helper.setText(content, true);
        return message;
    }


    // Additional methods for email formatting or other types of emails
}