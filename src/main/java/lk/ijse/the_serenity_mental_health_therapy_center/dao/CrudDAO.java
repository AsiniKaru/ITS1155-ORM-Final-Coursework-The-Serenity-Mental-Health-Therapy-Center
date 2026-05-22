package lk.ijse.the_serenity_mental_health_therapy_center.dao;

import java.util.List;

public interface CrudDAO<T> extends SuperDAO {

    boolean save(T entity);
    boolean update(T entity);
    boolean delete(String id);
    T search(String id);
    List<T> getAll();

}
