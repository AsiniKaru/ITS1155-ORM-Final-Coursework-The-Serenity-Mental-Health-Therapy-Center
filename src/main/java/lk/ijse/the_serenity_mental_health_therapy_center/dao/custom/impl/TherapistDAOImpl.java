package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapistDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Therapist;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TherapistDAOImpl implements TherapistDAO {
    @Override
    public boolean save(Therapist entity) {
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
    public boolean update(Therapist entity) {
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
            Therapist therapist = session.find(Therapist.class, Integer.parseInt(id));
            if (therapist != null) {
                session.remove(therapist);
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
    public Therapist search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(Therapist.class, Integer.parseInt(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<Therapist> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Therapist> query = session.createQuery("FROM Therapist", Therapist.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
