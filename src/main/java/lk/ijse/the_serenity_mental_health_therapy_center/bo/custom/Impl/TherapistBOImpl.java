package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapistBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapistDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapistDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.enums.TherapistAvailability;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.Therapist;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ValidationUtil;

import lk.ijse.the_serenity_mental_health_therapy_center.config.FactoryConfiguration;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import java.util.ArrayList;
import java.util.List;

public class TherapistBOImpl implements TherapistBO {

    private final TherapistDAO therapistDAO = (TherapistDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPIST);

    @Override
    public boolean saveTherapist(TherapistDTO dto) {
        ValidationUtil.checkRequiredField(dto.getTherapistFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getTherapistLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());
        ValidationUtil.validatePhone(dto.getPhone());
        ValidationUtil.checkRequiredField(dto.getSpecialization(), "Specialization");

        Therapist therapist = new Therapist();
        therapist.setFirstName(dto.getTherapistFirstName());
        therapist.setLastName(dto.getTherapistLastName());
        therapist.setEmail(dto.getEmail());
        therapist.setPhone(dto.getPhone());
        therapist.setSpecialization(dto.getSpecialization());
        if (dto.getAvailability() != null) {
            therapist.setStatus(Therapist.Availability.valueOf(dto.getAvailability().name()));
        } else {
            therapist.setStatus(Therapist.Availability.ACTIVE);
        }

        return therapistDAO.save(therapist);
    }

    @Override
    public boolean updateTherapist(TherapistDTO dto) {
        ValidationUtil.checkRequiredField(dto.getTherapistFirstName(), "First Name");
        ValidationUtil.checkRequiredField(dto.getTherapistLastName(), "Last Name");
        ValidationUtil.validateEmail(dto.getEmail());
        ValidationUtil.validatePhone(dto.getPhone());
        ValidationUtil.checkRequiredField(dto.getSpecialization(), "Specialization");

        Therapist therapist = therapistDAO.search(dto.getTherapistId());
        if (therapist == null) return false;

        therapist.setFirstName(dto.getTherapistFirstName());
        therapist.setLastName(dto.getTherapistLastName());
        therapist.setEmail(dto.getEmail());
        therapist.setPhone(dto.getPhone());
        therapist.setSpecialization(dto.getSpecialization());
        if (dto.getAvailability() != null) {
            therapist.setStatus(Therapist.Availability.valueOf(dto.getAvailability().name()));
        }

        return therapistDAO.update(therapist);
    }

    @Override
    public boolean deleteTherapist(String id) {
        return therapistDAO.delete(id);
    }

    @Override
    public TherapistDTO searchTherapist(String id) {
        Therapist therapist = therapistDAO.search(id);
        if (therapist == null) return null;
        return toDTO(therapist);
    }

    @Override
    public List<TherapistDTO> getAllTherapists() {
        List<Therapist> list = therapistDAO.getAll();
        List<TherapistDTO> dtoList = new ArrayList<>();
        for (Therapist therapist : list) {
            dtoList.add(toDTO(therapist));
        }
        return dtoList;
    }

    private TherapistDTO toDTO(Therapist therapist) {
        return new TherapistDTO(
                therapist.getTherapistId().toString(),
                therapist.getFirstName(),
                therapist.getLastName(),
                therapist.getEmail(),
                therapist.getPhone(),
                therapist.getSpecialization(),
                TherapistAvailability.valueOf(therapist.getStatus().name()),
                null, // programIds not populated for general list to avoid deep nesting
                null  // sessionIds not populated
        );
    }

    @Override
    public boolean assignProgramToTherapist(String therapistId, String programId) {
        org.hibernate.Session session = FactoryConfiguration.getInstance().getSession();
        org.hibernate.Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Therapist therapist = session.find(Therapist.class, Integer.parseInt(therapistId));
            TherapyProgram program = session.find(TherapyProgram.class, Integer.parseInt(programId));
            if (therapist != null && program != null) {
                if (!therapist.getPrograms().contains(program)) {
                    therapist.getPrograms().add(program);
                    session.merge(therapist);
                }
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
}
