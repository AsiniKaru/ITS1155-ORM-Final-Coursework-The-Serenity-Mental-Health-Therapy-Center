package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapySessionDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TherapySessionDAOImpl implements TherapySessionDAO {
    @Override
    public boolean save(TherapySession entity) {
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
    public boolean update(TherapySession entity) {
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
            TherapySession therapySession = session.find(TherapySession.class, Integer.parseInt(id));
            if (therapySession != null) {
                session.remove(therapySession);
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
    public TherapySession search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(TherapySession.class, Integer.parseInt(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<TherapySession> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session.createQuery("FROM TherapySession", TherapySession.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
