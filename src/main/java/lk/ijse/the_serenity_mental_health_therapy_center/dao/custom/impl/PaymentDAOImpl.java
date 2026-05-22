package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.PaymentDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    @Override
    public boolean save(Payment entity) {
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
    public boolean update(Payment entity) {
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
            Payment payment = session.find(Payment.class, Integer.parseInt(id));
            if (payment != null) {
                session.remove(payment);
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
    public Payment search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(Payment.class, Integer.parseInt(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<Payment> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<Payment> query = session.createQuery("FROM Payment", Payment.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}
