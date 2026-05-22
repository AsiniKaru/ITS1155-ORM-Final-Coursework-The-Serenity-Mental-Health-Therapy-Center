package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public boolean save(Patient entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Patient entity) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean delete(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Patient patient = session.find(Patient.class, Long.parseLong(id));
            if (patient != null) {
                session.remove(patient);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Patient search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(Patient.class, Long.parseLong(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Patient> query = session.createQuery("FROM Patient", Patient.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getPatientsEnrolledInAllPrograms() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT p FROM Patient p JOIN p.PatientTherapyPrograms pt " +
                         "GROUP BY p.patientId, p.firstName, p.lastName, p.email, p.phone, p.address, p.medicalHistory, p.registeredDate " +
                         "HAVING COUNT(distinct pt.program) = (SELECT COUNT(tp) FROM TherapyProgram tp)";
            Query<Patient> query = session.createQuery(hql, Patient.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Patient> getPatientsWithPrograms() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT DISTINCT p FROM Patient p " +
                         "LEFT JOIN FETCH p.PatientTherapyPrograms pt " +
                         "LEFT JOIN FETCH pt.program";
            Query<Patient> query = session.createQuery(hql, Patient.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
