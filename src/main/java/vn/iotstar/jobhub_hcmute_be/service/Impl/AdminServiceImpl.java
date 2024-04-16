package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.EmployState;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Admin;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Event;
import vn.iotstar.jobhub_hcmute_be.dto.EventDto;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.service.AdminService;
import vn.iotstar.jobhub_hcmute_be.utils.Constants;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AdminServiceImpl extends RedisServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    InterviewRepository interviewRepository;

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Autowired
    ShortListRepository shortListRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TransactionsRepository transactionsRepository;

    public AdminServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    @Deprecated
    public Admin getOne(String s) {
        return adminRepository.getOne(s);
    }

    @Override
    public <S extends Admin> List<S> findAll(Example<S> example) {
        return adminRepository.findAll(example);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public <S extends Admin> S save(S entity) {
        return adminRepository.save(entity);
    }

    @Override
    public Optional<Admin> findById(String s) {
        return adminRepository.findById(s);
    }

    @Override
    public void deleteById(String s) {
        adminRepository.deleteById(s);
    }

    @Override
    public ActionResult changeStateEmployer(EmployerRequest employerRequest) {
        ActionResult actionResult = new ActionResult();
        Optional<Employer> optionalUsers = employerRepository.findById(employerRequest.getEmployerId());

        if (optionalUsers.isPresent()) {
            if (optionalUsers.get().getEmployState() == EmployState.ACTIVE) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_IS_ACTIVED);
                return actionResult;
            }
            Employer employer = optionalUsers.get();
            if (employerRequest.getEmployState() == EmployState.ACTIVE) {
                employer.setEmployState(EmployState.ACTIVE);
                employerRepository.save(employer);
                if (this.exists(Constants.EMPLOYERS)) this.delete(Constants.EMPLOYERS);
                actionResult.setErrorCode(ErrorCodeEnum.EMPLOYR_ACCEPT_SUCCESS);
                return actionResult;
            }
            if (employerRequest.getEmployState() == EmployState.NOT_ACTIVE) {
                employer.setEmployState(EmployState.NOT_ACTIVE);
                employerRepository.save(employer);
                if (this.exists(Constants.EMPLOYERS)) this.delete(Constants.EMPLOYERS);
                actionResult.setErrorCode(ErrorCodeEnum.EMPLOYER_NOT_ACTIVE_SUCCESS);
                return actionResult;
            }
        }
        actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
        return actionResult;
    }

    @Override
    public ActionResult addNewEvent(EventDto eventDto,  String adminId){
        ActionResult actionResult = new ActionResult();
        Optional<Admin> optional = adminRepository.findById(adminId);

        if (optional.isPresent()) {
            Event event = new Event();

            BeanUtils.copyProperties(eventDto, event);
            event.setAdmin(optional.get());

            eventRepository.save(event);
            actionResult.setErrorCode(ErrorCodeEnum.POST_EVENT_SUCCESS);
        } else {
            actionResult.setErrorCode(ErrorCodeEnum.ADMIN_NOT_FOUND);
        }
        return actionResult;/*(10)*/
    }

    @Override
    public ActionResult getDashBoard(String adminId) {
        ActionResult actionResult = new ActionResult();
        Optional<Admin> optional = adminRepository.findById(adminId);
        if (optional.isPresent()) {
            Long totalEmployer = employerRepository.countByUserIdIsNotNull();
            Long totalJob = jobRepository.countByJobIdIsNotNull();
            Long totalInterview = interviewRepository.countByInterviewIdIsNotNull();
            Long totalJobApply = jobApplyRepository.countByJobApplyIdIsNotNull();
            Long totalShortList = shortListRepository.countByIdIsNotNull();
            Long totalStudent = studentRepository.countByUserIdIsNotNull();
            Long totalUser = userRepository.countByRole_NameNot("ADMIN");
            Long totalEvent = eventRepository.countByIdIsNotNull();
            Long totalTransaction = transactionsRepository.countByIdIsNotNull();
            Map<String, Long> map = Map.of(
                    "totalEmployer", totalEmployer,
                    "totalJob", totalJob,
                    "totalInterview", totalInterview,
                    "totalJobApply", totalJobApply,
                    "totalShortList", totalShortList,
                    "totalStudent", totalStudent,
                    "totalUser", totalUser,
                    "totalEvent", totalEvent,
                    "totalTransaction", totalTransaction
            );

            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.GET_DASHBOARD_SUCCESS);
        } else {
            actionResult.setErrorCode(ErrorCodeEnum.ADMIN_NOT_FOUND);
        }
        return actionResult;
    }

}
