package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.constant.Utils;
import vn.iotstar.jobhub_hcmute_be.dto.Apply.JobApplyResponseDTO;
import vn.iotstar.jobhub_hcmute_be.dto.EmployerUpdateDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.dto.ReplyRequest;
import vn.iotstar.jobhub_hcmute_be.dto.UpdateStateRequest;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobApplyRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.EmployerService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    @Autowired
    TemplateEngine templateEngine;




    public Page<JobApply> findAllByJob_Employer_UserId(Pageable pageable, String userId) {
        return jobApplyRepository.findAllByJob_Employer_UserId(pageable, userId);
    }

    @Override
    public <S extends Employer> S save(S entity) {
        return employerRepository.save(entity);
    }

    @Override
    public Optional<Employer> findById(String s) {
        return employerRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return employerRepository.existsById(s);
    }

    @Override
    public long count() {
        return employerRepository.count();
    }

    @Override
    public void deleteById(String s) {
        employerRepository.deleteById(s);
    }

    @Override
    public void delete(Employer entity) {
        employerRepository.delete(entity);
    }



    @Override
    public <S extends Employer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return employerRepository.findBy(example, queryFunction);
    }

    @Override
    public Optional<Employer> findByPhoneAndIsActiveIsTrue(String phone) {
        return employerRepository.findByPhoneAndIsActiveIsTrue(phone);
    }

    @Override
    public Optional<Employer> findByUserIdAndIsActiveIsTrue(String userId) {
        return employerRepository.findByUserIdAndIsActiveIsTrue(userId);
    }


    @Override
    public ResponseEntity<GenericResponse> getProfile(String userId) {
        Optional<Employer> optional = findByUserIdAndIsActiveIsTrue(userId);
        if (optional.isEmpty())
            throw new RuntimeException("User not found");

        Employer user = optional.get();

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Retrieving user profile successfully")
                        .result(user)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @Override
    public ResponseEntity<GenericResponse> changeLogo(String userId, MultipartFile imageFile) throws IOException {
        Employer user = findById(userId).get();
        String avatarOld = user.getLogo();

        //upload new avatar
        user.setLogo(cloudinaryService.uploadImage(imageFile));
        save(user);

        //delete old avatar
        if (avatarOld != null) {
            cloudinaryService.deleteImage(avatarOld);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully").result(user.getLogo()).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ActionResult addBg(String userId, MultipartFile imageFile) throws IOException {
        ActionResult actionResult = new ActionResult();
        Employer user = findById(userId).get();

        List<String> bg = user.getBackGround();

        if(bg.size() >= 5){
            actionResult.setErrorCode(ErrorCodeEnum.MAXIMUM_BACKGROUND);
        }
        else {
            String bgUrl = cloudinaryService.uploadImage(imageFile);
            bg.add(bgUrl);
            user.setBackGround(bg);
           user =  save(user);
            actionResult.setData(user.getBackGround());
            actionResult.setErrorCode(ErrorCodeEnum.UPDATE_BACKGROUND_SUCCESSFULLY);
        }
        return actionResult;
    }


    @Override
    public ActionResult deleteBg(String userId, String imgeUrl) throws IOException {
        ActionResult actionResult = new ActionResult();
        Employer user = findById(userId).get();

        List<String> bg = user.getBackGround();
        bg.stream().filter(bgUrl -> bgUrl.equals(imgeUrl)).findFirst().ifPresent(bg::remove);

        if (imgeUrl != null) {
            cloudinaryService.deleteImage(imgeUrl);
        }

        user.setBackGround(bg);
        user =  save(user);
        actionResult.setData(user.getBackGround());
        actionResult.setErrorCode(ErrorCodeEnum.UPDATE_BACKGROUND_SUCCESSFULLY);

        return actionResult;
    }


    @Override
    public ResponseEntity<GenericResponse> updateCompanyProfile(String userId, EmployerUpdateDTO request) throws Exception {
        Optional<Employer> employerOptional = findById(userId);
        String phone = request.getPhone();
        if (employerOptional.isEmpty()) throw new Exception("User doesn't exist");

        if(!phone.isEmpty()){
            Optional<Employer> optional = employerRepository.findByPhoneAndIsActiveIsTrue(request.getPhone());
            if(optional.isPresent() && !optional.get().getUserId().equals(userId))
                throw new Exception("Phone number already in use");
        }

        Employer employer = employerOptional.get();
        BeanUtils.copyProperties(request, employer);

        employer = save(employer);

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Update successful")
                        .result(employer)
                        .statusCode(200)
                        .build()
        );
    }

    public Page<JobApply> findAllByJob_Employer_UserIdAndState(Pageable pageable, String userId, State state) {
        return jobApplyRepository.findAllByJob_Employer_UserIdAndState(pageable, userId, state);
    }

    @Override
    public ActionResult getApplicants(String employerId, Pageable pageable, String state) {
        ActionResult actionResult = new ActionResult();

        Page<JobApply> jobApplies;


        if(state.equals("ALL")){
            jobApplies = findAllByJob_Employer_UserId(pageable, employerId);
        } else {
            State stateEnum = State.getStatusName(state);
            jobApplies = findAllByJob_Employer_UserIdAndState(pageable, employerId, stateEnum);
        }

        List<JobApplyResponseDTO> jobApplyDtos = new ArrayList<>();

        for(JobApply jobApply : jobApplies.getContent()) {
           JobApplyResponseDTO jobApplyDto = new JobApplyResponseDTO();
            BeanUtils.copyProperties(jobApply, jobApplyDto);
            BeanUtils.copyProperties(jobApply.getJob(), jobApplyDto);
            BeanUtils.copyProperties(jobApply.getStudent(), jobApplyDto);
            jobApplyDtos.add(jobApplyDto);
        }


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("content", jobApplyDtos);
        map.put("pageNumber", jobApplies.getPageable().getPageNumber());
        map.put("pageSize", jobApplies.getSize());
        map.put("totalPages", jobApplies.getTotalPages());
        map.put("totalElements", jobApplies.getTotalElements());

        actionResult.setData(map);
        actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_APPLY_SUCCESSFULLY);

        return actionResult;
    }



    @Override
    public ActionResult updateCandidateState(String recruiterId, String userId, UpdateStateRequest updateStateRequest) {
        ActionResult actionResult = new ActionResult();
        try{
            Optional<Student> student = studentRepository.findById(userId);
            if(student.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            Optional<Job> job = jobRepository.findById(updateStateRequest.getJobId());
            if(job.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.JOB_NOT_FOUND);
                return actionResult;
            }
            Optional<JobApply> optionalJobApply = jobApplyRepository.findByStudentAndJob(student.get(), job.get());

            if(optionalJobApply.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.CANDIDATE_NOT_FOUND);
                return actionResult;
            }

            try {
                JobApply jobApply = optionalJobApply.get();
                State newState = State.getStatusName(updateStateRequest.getStatus());
                jobApply.setState(newState);
                JobApply updatedJobApply = jobApplyRepository.save(jobApply);
                actionResult.setData(updatedJobApply);
                actionResult.setErrorCode(ErrorCodeEnum.UPDATE_STATE_APPLY_SUCCESSFULLY);

            } catch (IllegalArgumentException e) {
                actionResult.setErrorCode(ErrorCodeEnum.INVALID_STATE_VALUE);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    @Override
    public ActionResult replyCandidate(ReplyRequest request) throws MessagingException, UnsupportedEncodingException {
        ActionResult actionResult = new ActionResult();
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));
        context.setVariable("content", request.getContent());
        String content = templateEngine.process("mail-template", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true, "utf-8");
        helper.setSubject(request.getSubject());
        helper.setTo(request.getEmail());
        helper.setText(Utils.cleanHTML(request.getContent()), true);
        helper.setFrom(env.getProperty("spring.mail.username"),"Recruiment Manager");
        javaMailSender.send(message);
        actionResult.setErrorCode(ErrorCodeEnum.SEND_MAIL_SUCCESSFULLY);
        return actionResult;
    }

    @Async
    @Override
    public String reply(ReplyRequest request) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));
        context.setVariable("content", request.getContent());
        String content = templateEngine.process("mail-template", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true, "utf-8");
        helper.setSubject(request.getSubject());
        helper.setTo(request.getEmail());
        helper.setText(Utils.cleanHTML(request.getContent()), true);
        helper.setFrom(env.getProperty("spring.mail.username"), request.getCompanyName());
        javaMailSender.send(message);
        return "Email Sent!";
    }
}
