package com.impetus.mailsender.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.impetus.mailsender.beans.Employee;

public class DataHelper {
    public static List<Employee> readEmployeesFromCSV(String csvPath) throws ParseException {
        List<Employee> employees = new ArrayList<>();
        try {
            Reader in = new FileReader(new File(csvPath));
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
            Iterator<CSVRecord> itr = records.iterator();
            // Employee ID, Name, Client Dept., Project, DOJ, Birthday, Anniversary, Email, Location
            while (itr.hasNext()) {
                CSVRecord record = itr.next();
                if (record.getRecordNumber() != 0) {
                    Employee employee = new Employee();
                    employee.setNAME(record.get("Name"));
                    employee.setEmpId(record.get("Employee ID"));
                    employee.setClient(record.get("Client Dept."));
                    employee.setProject(record.get("Project"));
                    employee.setDoj(formateDate(record.get("DOJ")));
                    employee.setbDay(formateDate(record.get("Birthday")));
                    employee.setAnniversary(formateDate(record.get("Anniversary")));
                    employee.setEMAIL(record.get("Email"));
                    employee.setLocation(record.get("Location"));
                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static Date formateDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        if (StringUtils.isNotBlank(dateString)) {
            return dateFormat.parse(dateString.trim());
        } else {
            return null;
        }
    }
}
