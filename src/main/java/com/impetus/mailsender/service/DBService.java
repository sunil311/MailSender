package com.impetus.mailsender.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;

@Service
public class DBService implements DataService {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public List<Employee> getEmployees(Filter filter) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Employee> employees = new ArrayList<Employee>();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Criteria criteria = session.createCriteria(Employee.class);
            employees = criteria.list();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            transaction.commit();
        }
        return employees;
    }

    public void loadEmployees(List<Employee> employees) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        for (Employee employee : employees) {
            entityManager.persist(employee);
        }
    }

}
