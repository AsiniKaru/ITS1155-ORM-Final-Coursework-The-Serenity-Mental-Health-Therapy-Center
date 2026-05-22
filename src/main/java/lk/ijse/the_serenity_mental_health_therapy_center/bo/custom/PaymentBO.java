package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.SuperBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.PaymentDTo;

import java.util.List;

public interface PaymentBO extends SuperBO {
    boolean savePayment(PaymentDTo dto);
    boolean updatePayment(PaymentDTo dto);
    boolean deletePayment(String id);
    PaymentDTo searchPayment(String id);
    List<PaymentDTo> getAllPayments();
}
