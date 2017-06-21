package com.impetus.mailsender.configuration;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactory;

@Configuration
@SuppressWarnings("deprecation")
public class BeansConfig {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private int smtpPort;

    @Value("${mail.smtp.username}")
    private String smtpUserName;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.transport.protocol}")
    private String smtpProtocol;

    @Value("${mail.debug}")
    private String smtpDebug;

    /** Velocity configuration.
     * 
     * @return
     * @throws VelocityException
     * @throws IOException */
    @Bean
    public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
        VelocityEngineFactory factory = new VelocityEngineFactory();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        factory.setVelocityProperties(props);
        return factory.createVelocityEngine();
    }

    /** JavaMail configuration.
     * 
     * @return */
    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8"); // Using mail from impetus.
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(smtpUserName);
        mailSender.setPassword(smtpPassword);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", smtpAuth);
        javaMailProperties.put("mail.transport.protocol", smtpProtocol);
        javaMailProperties.put("mail.debug", smtpDebug);

        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUserName() {
        return smtpUserName;
    }

    public void setSmtpUserName(String smtpUserName) {
        this.smtpUserName = smtpUserName;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(String smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public String getSmtpProtocol() {
        return smtpProtocol;
    }

    public void setSmtpProtocol(String smtpProtocol) {
        this.smtpProtocol = smtpProtocol;
    }

    public String getSmtpDebug() {
        return smtpDebug;
    }

    public void setSmtpDebug(String smtpDebug) {
        this.smtpDebug = smtpDebug;
    }

}
