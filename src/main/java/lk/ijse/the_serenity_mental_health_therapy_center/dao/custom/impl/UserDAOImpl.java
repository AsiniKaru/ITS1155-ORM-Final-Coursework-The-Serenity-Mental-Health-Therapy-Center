package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.impl;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.UserDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public boolean save(User entity) {
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
    public boolean update(User entity) {
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
            User user = session.find(User.class, Long.parseLong(id));
            if (user != null) {
                session.remove(user);
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
    public User search(String id) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            return session.find(User.class, Long.parseLong(id));
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> getAll() {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    @Override
    public User findByUsername(String username) {
        Session session = FactoryConfiguration.getInstance().getSession();
        try {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
