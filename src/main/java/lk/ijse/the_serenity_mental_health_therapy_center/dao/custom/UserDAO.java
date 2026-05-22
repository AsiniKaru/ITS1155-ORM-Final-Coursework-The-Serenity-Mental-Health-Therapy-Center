package lk.ijse.the_serenity_mental_health_therapy_center.dao.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.dao.CrudDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.User;

public interface UserDAO extends CrudDAO<User> {
    public User findByUsername(String username);
}
