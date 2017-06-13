package com.impetus.mailsender.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;
import com.impetus.mailsender.util.DataHelper;

@Service
public class DBService implements DataService {
    private final static Logger logger = LoggerFactory.getLogger(DBService.class);
    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public List<Employee> getEmployees(Filter filter) {
        List<Employee> employees = new ArrayList<Employee>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Query qlString = entityManager.createQuery("from Employee", Employee.class);
            List<Object> list = qlString.getResultList();
            if (list != null && !list.isEmpty()) {
                for (Object o : list) {
                    Employee employee = (Employee) o;
                    employee.setSUBJECT("");
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        List<Employee> updatedEmployees = DataHelper.applyFilter(employees, filter);
        System.out.println(updatedEmployees);
        return updatedEmployees;
    }

    public void loadEmployees(List<Employee> employees) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (Employee employee : employees) {
                if (getEmployeeByEmpId(employee.getEmpId()) == null) {
                    entityManager.setFlushMode(FlushModeType.COMMIT);
                    entityManager.persist(employee);
                }
            }
            logger.info("All records inserted....");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }

    public Employee getEmployeeByEmpId(String empId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Employee employee = null;
        try {
            entityManager.getTransaction().begin();
            Query qlString = entityManager.createQuery("from Employee where empId = '" + empId + "'", Employee.class);
            List<Object> list = qlString.getResultList();
            if (list != null && !list.isEmpty()) {
                employee = (Employee) list.get(0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
        return employee;
    }
}
