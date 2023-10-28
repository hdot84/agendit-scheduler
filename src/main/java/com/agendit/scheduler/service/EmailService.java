package com.agendit.scheduler.service;

import java.util.Map;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment) throws MessagingException;

    void sendMessageUsingThymeleafTemplate(
            String to, String subject, Map<String, Object> templateModel) throws MessagingException;

}