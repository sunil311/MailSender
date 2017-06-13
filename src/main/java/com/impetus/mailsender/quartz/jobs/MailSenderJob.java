/**
 * 
 */
package com.impetus.mailsender.quartz.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.velocity.exception.VelocityException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.service.EmployeeService;
import com.impetus.mailsender.service.MailService;
import com.impetus.mailsender.util.EmailHelper;

@Component
public class MailSenderJob implements Job {
    @Autowired
    private MailService mailService;

    @Autowired
    private EmployeeService employeeService;

    private final static Logger logger = LoggerFactory.getLogger(MailSenderJob.class);
    @Value("${cron.frequency.jobwithcrontrigger}")
    private String frequency;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            if (employeeService != null && employeeService.getDataService() != null) {
                int mailCounter = EmailHelper.getMailCounter();
                for (int i = mailCounter - 1; i >= 0; i--) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, -i);
                    Date mailDate = c.getTime();
                    sendMail(i, mailDate);
                }
            } else {
                throw new BWisherException("Email sending failed. EmployeeService is null. Please set EmployeeService first and try again.");
            }
        } catch (VelocityException | IOException | ParseException ex) {
            throw new BWisherException("Email sending failed.", ex);
        }
        logger.info("Running MailSenderJob | frequency {}", frequency);
    }

    private void sendMail(int i, Date mailDate) throws ParseException, FileNotFoundException, IOException {
        if (!EmailHelper.checkMailStatus(mailDate)) {
            Filter filter = new Filter(null, null, null, null, null, null);
            List<Employee> employees = employeeService.getDataService().getEmployees(filter);
            if (employees != null && !employees.isEmpty()) {
                for (Employee employee : employees) {
                    if (employee.getSUBJECT().equalsIgnoreCase("Birthday")) {
                        if (i != 0) {
                            employee.setSUBJECT("Belated " + employee.getSUBJECT());
                        } else {
                            employee.setSUBJECT(employee.getSUBJECT());
                        }
                        mailService.sendEmail(EmailHelper.prepareEmail(employee));
                    }
                }
                // Updating flag file
                EmailHelper.updateFlagFile(mailDate);
            }
        }
    }
}
