package com.impetus.mailsender.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;

public class DataHelper {
    public static List<Employee> readEmployeesFromCSV(String csvPath) throws ParseException {
        List<Employee> employees = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(new File(csvPath)));
            int counter = 0;
            while (scanner.hasNextLine()) {
                counter = counter + 1;
                String line = scanner.nextLine();
                if (counter != 1) {
                    // Employee ID,Name,Client Dept.,Project,DOJ,Birthday,Anniversary,Email,Location
                    String[] columns = line.split(",");
                    Employee employee = new Employee();
                    employee.setEmpId(columns[0]);
                    employee.setNAME(columns[1]);
                    employee.setClient(columns[2]);
                    employee.setProject(columns[3]);
                    employee.setDoj(formateDate(columns[4]));
                    employee.setbDay(formateDate(columns[5]));
                    employee.setAnniversary(formateDate(columns[6]));
                    employee.setEMAIL(columns[7]);
                    employee.setLocation(columns[8]);
                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
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

    public static List<Employee> applyFilter(List<Employee> employees, Filter filter) {
        List<Employee> updatedEmployees = new ArrayList<>();
        try {
            // for now only date filter
            for (Employee employee : employees) {
                Date ddMM = getDDMM(new Date());
                Date dob = getDDMM(employee.getbDay());
                Date doj = getDDMM(employee.getDoj());
                Date anniversary = getDDMM(employee.getAnniversary());
                if (dob != null && ddMM.compareTo(dob) == 0) {
                    employee.setSUBJECT("Birthday");
                    Employee emp = employee.copy();
                    updatedEmployees.add(emp);
                }
                if (doj != null && ddMM.compareTo(doj) == 0) {
                    employee.setSUBJECT("Date Of Joinging");
                    Employee emp = employee.copy();
                    updatedEmployees.add(emp);
                }
                if (anniversary != null && ddMM.compareTo(anniversary) == 0) {
                    employee.setSUBJECT("Anniversary");
                    Employee emp = employee.copy();
                    updatedEmployees.add(emp);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updatedEmployees;
    }


    private static Date getDDMM(Date date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd MM");
        if (date != null) {
            String dateString = dateFormat.format(date);
            return dateFormat.parse(dateString);
        }
        return null;
    }
}
