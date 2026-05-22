package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TherapyProgramDAOImpl implements TherapyProgramDAO {
    @Override
    public boolean save(TherapyProgram entity) {
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
    public boolean update(TherapyProgram entity) {
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
            TherapyProgram program = session.find(TherapyProgram.class, Integer.parseInt(id));
            if (program != null) {
                session.remove(program);
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
    public TherapyProgram search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(TherapyProgram.class, Integer.parseInt(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapyProgram> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapyProgram> query = session.createQuery("FROM TherapyProgram", TherapyProgram.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
