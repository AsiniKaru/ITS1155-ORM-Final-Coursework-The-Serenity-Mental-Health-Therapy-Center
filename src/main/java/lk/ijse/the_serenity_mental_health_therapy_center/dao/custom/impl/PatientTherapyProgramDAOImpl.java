package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PatientTherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.PatientTherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PatientTherapyProgramDAOImpl implements PatientTherapyProgramDAO {
    @Override
    public boolean save(PatientTherapyProgram entity) {
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
    public boolean update(PatientTherapyProgram entity) {
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
            PatientTherapyProgram enrollment = session.find(PatientTherapyProgram.class, Long.parseLong(id));
            if (enrollment != null) {
                session.remove(enrollment);
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
    public PatientTherapyProgram search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(PatientTherapyProgram.class, Long.parseLong(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<PatientTherapyProgram> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<PatientTherapyProgram> query = session.createQuery("FROM PatientTherapyProgram", PatientTherapyProgram.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
