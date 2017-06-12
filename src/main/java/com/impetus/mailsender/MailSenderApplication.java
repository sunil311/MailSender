package com.impetus.mailsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({ "classpath:db-context.xml" })
public class MailSenderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailSenderApplication.class, args);
    }
}
