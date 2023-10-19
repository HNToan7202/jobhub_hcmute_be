package vn.iotstar.jobhub_hcmute_be.service.Impl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Skill;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.SkillRepository;
import vn.iotstar.jobhub_hcmute_be.service.SkillService;
import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {

    final
    SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    @Query("Select s From Skill s Where s.name = :skillName")
    public Optional<Skill> findSkillBySkillName(String skillName) {
        return skillRepository.findSkillBySkillName(skillName);
    }

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    @Override
    public Optional<Skill> findById(String s) {
        return skillRepository.findById(s);
    }

    @Override
    public void deleteById(String s) {
        skillRepository.deleteById(s);
    }

    @Override
    public List<Skill> findAll(Sort sort) {
        return skillRepository.findAll(sort);
    }

    @Override
    public Page<Skill> findAll(Pageable pageable) {
        return skillRepository.findAll(pageable);
    }

    @Override
    public ActionResult getAllSkill(){
        ActionResult actionResult = new ActionResult();
        List<Skill> skillList = findAll();
        if(skillList == null)
        {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            return actionResult;
        }
        actionResult.setErrorCode(ErrorCodeEnum.GET_SKILL_SUCCESS);
        actionResult.setData(skillList);
        return actionResult;

    }
}
