package lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.Impl;

import lk.ijse.the_serenity_mental_health_therapy_center.bo.custom.TherapyProgramBO;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.DAOFactory;
import lk.ijse.the_serenity_mental_health_therapy_center.dao.custom.TherapyProgramDAO;
import lk.ijse.the_serenity_mental_health_therapy_center.dto.TherapyProgramDTO;
import lk.ijse.the_serenity_mental_health_therapy_center.entity.TherapyProgram;
import lk.ijse.the_serenity_mental_health_therapy_center.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class TherapyProgramBOImpl implements TherapyProgramBO {

    private final TherapyProgramDAO programDAO = (TherapyProgramDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);

    @Override
    public boolean saveTherapyProgram(TherapyProgramDTO dto) {
        ValidationUtil.checkRequiredField(dto.getProgramCode(), "Program Code");
        ValidationUtil.checkRequiredField(dto.getProgramName(), "Program Name");
        ValidationUtil.checkRequiredField(dto.getDuration(), "Duration");
        if (dto.getProgramFee() == null) {
            throw new RuntimeException("Program Fee cannot be empty!");
        }

        TherapyProgram program = new TherapyProgram();
        program.setProgramCode(dto.getProgramCode());
        program.setName(dto.getProgramName());
        program.setDescription(dto.getDescription());
        program.setDurationWeeks(dto.getDuration());
        program.setFee(dto.getProgramFee());

        return programDAO.save(program);
    }

    @Override
    public boolean updateTherapyProgram(TherapyProgramDTO dto) {
        ValidationUtil.checkRequiredField(dto.getProgramCode(), "Program Code");
        ValidationUtil.checkRequiredField(dto.getProgramName(), "Program Name");
        ValidationUtil.checkRequiredField(dto.getDuration(), "Duration");
        if (dto.getProgramFee() == null) {
            throw new RuntimeException("Program Fee cannot be empty!");
        }

        TherapyProgram program = programDAO.search(dto.getProgramId());
        if (program == null) return false;

        program.setProgramCode(dto.getProgramCode());
        program.setName(dto.getProgramName());
        program.setDescription(dto.getDescription());
        program.setDurationWeeks(dto.getDuration());
        program.setFee(dto.getProgramFee());

        return programDAO.update(program);
    }

    @Override
    public boolean deleteTherapyProgram(String id) {
        return programDAO.delete(id);
    }

    @Override
    public TherapyProgramDTO searchTherapyProgram(String id) {
        TherapyProgram program = programDAO.search(id);
        if (program == null) return null;
        return toDTO(program);
    }

    @Override
    public List<TherapyProgramDTO> getAllTherapyPrograms() {
        List<TherapyProgram> list = programDAO.getAll();
        List<TherapyProgramDTO> dtoList = new ArrayList<>();
        for (TherapyProgram program : list) {
            dtoList.add(toDTO(program));
        }
        return dtoList;
    }

    private TherapyProgramDTO toDTO(TherapyProgram program) {
        return new TherapyProgramDTO(
                program.getProgramId().toString(),
                program.getProgramCode(),
                program.getName(),
                program.getDescription(),
                program.getDurationWeeks(),
                program.getFee(),
                null // therapists not populated to prevent circular/deep nesting issues
        );
    }
}
