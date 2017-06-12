package com.impetus.mailsender.service;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.util.EmailHelper;

@Service
public class MailService {

    private static Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;

    public void sendEmail(Employee employee) {
        logger.info("Sending mail to " + employee);
        logger.debug("birthday details " + employee.toString());
        MimeMessagePreparator preparator = EmailHelper.getMessagePreparator(
                velocityEngine, employee);
        try {
            mailSender.send(preparator);
        } catch (MailException ex) {
            throw new BWisherException("Email Sending failed.", ex);
        }
    }
}
