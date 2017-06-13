package com.impetus.mailsender.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.exception.BWisherException;

@SuppressWarnings("deprecation")
public class EmailHelper {

    static Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    /** Preparing MessagePreparator: includes all required content for sending mail.
     * 
     * @param velocityEngine
     * @param employee
     * @return */
    public static MimeMessagePreparator getMessagePreparator(VelocityEngine velocityEngine, final Employee employee) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");

                messageHelper.setSubject(employee.getSUBJECT());
                messageHelper.setFrom("sparkbd@impetus.co.in");
                messageHelper.setTo(employee.getEMAIL());
                Map<String, Object> model = new HashMap<>();
                model.put("name", employee.getNAME());
                /* model.put("picUrl", employee.getIMGURL()); */
                model.put("picUrl", "https://pivot.impetus.co.in/digite/upload/skgupta.jpg");
                model.put("host", InetAddress.getLocalHost().getHostAddress());
                model.put("port", "7777");
                messageHelper.setText(geVelocityTemplateContent(velocityEngine, model, employee.getTemplateName()), true);
                // messageHelper.addInline("background_image", new File("/home/impadmin/git/BDayWisher/src/main/resources/public/template-1.png"));
            }
        };
        return preparator;
    }

    /** Evaluating velocity template content, executing with velocity engine.
     * 
     * @param velocityEngine
     * @param model
     * @param templateName
     * @return */
    public static String geVelocityTemplateContent(VelocityEngine velocityEngine, Map<String, Object> model, String templateName) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateName, model));
            return content.toString();
        } catch (BWisherException e) {
            throw new BWisherException("Unable to parse velocity template contant.", e);
        }
    }

    /** Populating BirthdayEmail object for a particular employee from JsonNode received from pivot service.
     * 
     * @param employee
     * @return */
    public static Employee prepareEmail(Employee employee) {
        String template = selectTemplateName();
        logger.debug("template name :" + template);
        employee.setTemplateName(template);
        return employee;
    }

    /** This method is choosing random email template from available list of templates. we are expecting templates will be place inside
     * /src/main/resources/main/templates folder.
     * 
     * @return */
    private static String selectTemplateName() {
        String templatePath = "mail/templates";
        File file = new File(EmailHelper.class.getClassLoader().getResource(templatePath).getFile());
        File[] templates = null;
        String template = "";
        if (file.isDirectory()) {
            templates = file.listFiles();
            int index = RandomUtils.nextInt(templates.length - 1);
            if (templates[index].isFile()) {
                template = "/" + templatePath + "/" + templates[index].getName();
            } else {
                template = "/mail/templates/default/default.vm";
            }
        } else {
            template = "/mail/templates/default/default.vm";
        }
        return template;
    }

    /** @return
     * @throws ParseException */
    public static int getMailCounter() throws ParseException {
        Date lastSentDate = getLastSentDate();
        if (lastSentDate == null) {
            return 1;
        }
        DateTime dtLast = new DateTime(lastSentDate.getTime());
        int mailCounter = Days.daysBetween(dtLast.toLocalDate(), new DateTime().toLocalDate()).getDays();

        // If month is different

        int previousMailsDay = 10;
        if (mailCounter > previousMailsDay) {
            mailCounter = previousMailsDay;
        }
        return mailCounter;
    }

    /** @return
     * @throws ParseException */
    public static Date getLastSentDate() throws ParseException {
        File flagFile = getFlagFile();
        if (flagFile != null) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(flagFile));
                DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
                Date mailDate = formater.parse(p.getProperty("mail.sent.date"));
                return mailDate;
            } catch (FileNotFoundException e) {
                logger.info("Flag file does not exists.");
            } catch (IOException e) {
                logger.info("Unable to read flag file");
            }
        }
        return null;
    }

    /** @throws FileNotFoundException
     * @throws IOException */
    public static void updateFlagFile(Date mailDate) throws FileNotFoundException, IOException {
        Properties p = new Properties();
        DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
        p.setProperty("mail.sent.date", formater.format(mailDate));
        p.store(new FileOutputStream(getFlagFile()), "Flag File Update for " + formater.format(mailDate));
    }

    /** @return */
    public static File getFlagFile() {
        String flagFile = null;
        if (SystemUtils.IS_OS_LINUX) {
            flagFile = "/usr/local/bwisher.flag";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            flagFile = "c:\bwisher.flag";
        }
        File file = new File(flagFile);
        return file;
    }

    /** @param mailDate2
     * @return
     * @throws ParseException */
    public static boolean checkMailStatus(Date mailDate) throws ParseException {
        File flagFile = getFlagFile();
        boolean mailSent = false;
        if (flagFile != null) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(flagFile));
                DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
                Date lastMailDate = formater.parse(p.getProperty("mail.sent.date"));
                if (mailDate.getDate() == lastMailDate.getDate()) {
                    mailSent = true;
                }
            } catch (FileNotFoundException e) {
                logger.info("Flag file does not exists.");
            } catch (IOException e) {
                logger.info("Unable to read flag file");
            }
        }
        return mailSent;
    }
}
