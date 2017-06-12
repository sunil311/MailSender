package com.impetus.mailsender.service;

import java.util.List;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;

public interface DataService {
    public List<Employee> getEmployees(Filter filter);
}
