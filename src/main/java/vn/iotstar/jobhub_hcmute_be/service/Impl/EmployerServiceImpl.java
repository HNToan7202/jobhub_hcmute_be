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
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.dto.Apply.JobApplyResponseDTO;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.InterviewModel;
import vn.iotstar.jobhub_hcmute_be.repository.*;
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

    @Autowired
    InterviewRepository interviewRepository;

    @Autowired
    ShortListRepository shortListRepository;


    @Autowired
    TransactionsRepository transactionsRepository;


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

        if (bg.size() >= 5) {
            actionResult.setErrorCode(ErrorCodeEnum.MAXIMUM_BACKGROUND);
        } else {
            String bgUrl = cloudinaryService.uploadImage(imageFile);
            bg.add(bgUrl);
            user.setBackGround(bg);
            user = save(user);
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
        user = save(user);
        actionResult.setData(user.getBackGround());
        actionResult.setErrorCode(ErrorCodeEnum.UPDATE_BACKGROUND_SUCCESSFULLY);

        return actionResult;
    }


    @Override
    public ResponseEntity<GenericResponse> updateCompanyProfile(String userId, EmployerUpdateDTO request) throws Exception {
        Optional<Employer> employerOptional = findById(userId);
        String phone = request.getPhone();
        if (employerOptional.isEmpty()) throw new Exception("User doesn't exist");

        if (!phone.isEmpty()) {
            Optional<Employer> optional = employerRepository.findByPhoneAndIsActiveIsTrue(request.getPhone());
            if (optional.isPresent() && !optional.get().getUserId().equals(userId))
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


        if (state.equals("ALL")) {
            jobApplies = findAllByJob_Employer_UserId(pageable, employerId);
        } else {
            State stateEnum = State.getStatusName(state);
            jobApplies = findAllByJob_Employer_UserIdAndState(pageable, employerId, stateEnum);
        }

        List<JobApplyResponseDTO> jobApplyDtos = new ArrayList<>();

        for (JobApply jobApply : jobApplies.getContent()) {
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
        try {
            Optional<Student> student = studentRepository.findById(userId);
            if (student.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            Optional<Job> job = jobRepository.findById(updateStateRequest.getJobId());
            if (job.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.JOB_NOT_FOUND);
                return actionResult;
            }
            Optional<JobApply> optionalJobApply = jobApplyRepository.findByStudentAndJob(student.get(), job.get());

            if (optionalJobApply.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.CANDIDATE_NOT_FOUND);
                return actionResult;
            }
            JobApply jobApply = optionalJobApply.get();
            State newState = State.getStatusName(updateStateRequest.getStatus());
            jobApply.setState(newState);
            JobApply updatedJobApply = jobApplyRepository.save(jobApply);
            actionResult.setData(updatedJobApply);
            actionResult.setErrorCode(ErrorCodeEnum.UPDATE_STATE_APPLY_SUCCESSFULLY);
            return actionResult;
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            System.err.println(e.getMessage());
            return actionResult;
        }
    }

    @Async
    @Override
    public ActionResult replyCandidate(ReplyRequest request) throws MessagingException, UnsupportedEncodingException {
        ActionResult actionResult = new ActionResult();
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));
        context.setVariable("content", request.getContent());
        String content = templateEngine.process("mail-template", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setSubject(request.getSubject());
        helper.setTo(request.getEmail());
        helper.setText(Utils.cleanHTML(request.getContent()), true);
        helper.setFrom(env.getProperty("spring.mail.username"), "Recruiment Manager");
        javaMailSender.send(message);
        actionResult.setErrorCode(ErrorCodeEnum.SEND_MAIL_SUCCESSFULLY);
        return actionResult;
    }

    @Async
    @Override
    public void reply(ReplyRequest request) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setLocale(new Locale("vi", "VN"));
        context.setVariable("content", request.getContent());
        String content = templateEngine.process("mail-template", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setSubject(request.getSubject());
        helper.setTo(request.getEmail());
        helper.setText(Utils.cleanHTML(request.getContent()), true);
        helper.setFrom(env.getProperty("spring.mail.username"), request.getCompanyName());
        javaMailSender.send(message);
    }

    @Override
    public ActionResult topCompany(Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        Page<Employer> employerPage = employerRepository.findByTransactionMoneyGreaterThanEqualOrderByTransactionMoneyDesc(1L, pageable);
        actionResult.setData(employerPage.getContent());
        actionResult.setErrorCode(ErrorCodeEnum.OK);
        return actionResult;
    }

    @Override
    public ActionResult createInterview(String jobApplyId, InterViewDTO interViewDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<JobApply> optionalJobApply = jobApplyRepository.findById(jobApplyId);
            if (optionalJobApply.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.CANDIDATE_NOT_FOUND);
                return actionResult;
            }
            JobApply jobApply = optionalJobApply.get();
            if (jobApply.getState() == State.PENDING && jobApply.getInterview() == null) {
                Interview interview = new Interview();
                interview.setJobApply(jobApply);
                interview.setInterviewLink(interViewDTO.getInterviewLink());
                interview.setTime(interViewDTO.getTime());
                interview.setStartTime(interViewDTO.getStartTime());
                interview.setEndTime(interViewDTO.getEndTime());
                jobApply.setInterview(interview);
                jobApply.setState(State.RECEIVED);
                jobApplyRepository.save(jobApply);
                interview = interviewRepository.save(interview);
                InterviewModel interviewModel = InterviewModel.transform(interview);
                actionResult.setData(interviewModel);
                actionResult.setErrorCode(ErrorCodeEnum.CREATE_INTERVIEW_SUCCESSFULLY);
                sendMailInterview(interview);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.INTERVIEW_HAS_BEEN_CREATED);
            }
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    public ActionResult createInterviews(String jobApplyId, InterViewDTO interViewDTO) {
        ActionResult actionResult = new ActionResult();/*(1)*/
        Optional<JobApply> optionalJobApply = jobApplyRepository.findById(jobApplyId);/*(2)*/
        if (optionalJobApply.isEmpty()) {/*(3)*/
            actionResult.setErrorCode(ErrorCodeEnum.CANDIDATE_NOT_FOUND);/*(4)*/
            return actionResult;/*(5)*/
        }
        JobApply jobApply = optionalJobApply.get(); /*(6)*/
        if (jobApply.getState() == State.PENDING ){ /*(7)*/

            Interview interview = new Interview(); /*(8)*/
            interview.setJobApply(jobApply);/*(9)*/
            interview.setInterviewLink(interViewDTO.getInterviewLink());
            interview.setTime(interViewDTO.getTime());
            interview.setStartTime(interViewDTO.getStartTime());
            interview.setEndTime(interViewDTO.getEndTime());
            interview = interviewRepository.save(interview);/*(10)*/

            jobApply.setInterview(interview);/*(11)*/
            jobApply.setState(State.RECEIVED);
            jobApplyRepository.save(jobApply);/*(12)*/

            InterviewModel interviewModel = InterviewModel.transform(interview);/*(13)*/

            actionResult.setData(interviewModel);/*(14)*/
            actionResult.setErrorCode(ErrorCodeEnum.CREATE_INTERVIEW_SUCCESSFULLY);
            sendMailInterview(interview);
        } else {
            actionResult.setErrorCode(ErrorCodeEnum.INTERVIEW_HAS_BEEN_CREATED);/*(15)*/
        }
        return actionResult;/*(16)*/
    }

    @Async
    public void sendMailInterview(Interview interview) {
        try {
            Context context = new Context();
            context.setLocale(new Locale("vi", "VN"));
            context.setVariables(
                    Map.of(
                            "name", interview.getJobApply().getStudent().getFullName(),
                            "jobName", interview.getJobApply().getJob().getName(),
                            "companyName", interview.getJobApply().getJob().getEmployer().getCompanyName(),
                            "startTime", interview.getStartTime(),
                            "endTime", interview.getEndTime(),
                            "interviewLink", interview.getInterviewLink(),
                            "resumeLink", interview.getJobApply().getResumeUpoad()
                    )
            );
            String content = templateEngine.process("interview-invitation", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setSubject("Interview with " + interview.getJobApply().getJob().getEmployer().getCompanyName());
            helper.setTo(interview.getJobApply().getStudent().getEmail());
            helper.setText(content, true);

            String companyName = interview.getJobApply().getJob().getEmployer().getCompanyName();
            helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")), companyName);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public ActionResult getAllInterview(String employerId, Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        try {
            Page<Interview> interviews = interviewRepository.findByJobApply_Job_Employer_UserId(employerId, pageable);
            List<InterviewModel> interviewModels = new ArrayList<>();
            for (Interview interview : interviews.getContent()) {
                InterviewModel interviewModel = InterviewModel.transform(interview);
                interviewModels.add(interviewModel);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("interviews", interviewModels);
            map.put("pageNumber", interviews.getPageable().getPageNumber());
            map.put("pageSize", interviews.getSize());
            map.put("totalPages", interviews.getTotalPages());
            map.put("totalElements", interviews.getTotalElements());

            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    @Override
    public ActionResult getDetailInterview(String employerId, String interviewId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Interview> optionalInterview = interviewRepository.findById(interviewId);
            if (optionalInterview.isEmpty()) {
                actionResult.setErrorCode(ErrorCodeEnum.INTERVIEW_NOT_FOUND);
                return actionResult;
            }
            Interview interview = optionalInterview.get();
            if (!interview.getJobApply().getJob().getEmployer().getUserId().equals(employerId)) {
                actionResult.setErrorCode(ErrorCodeEnum.INTERVIEW_NOT_FOUND);
                return actionResult;
            }
            InterviewModel interviewModel = InterviewModel.transform(interview);
            actionResult.setData(interviewModel);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    @Override
    public ActionResult getDashboard(String employerId){
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Employer> optionalEmployer = employerRepository.findById(employerId);
            if(optionalEmployer.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            Employer employer = optionalEmployer.get();
            Map<String, Object> map = new HashMap<String, Object>();
            Long totalApply = jobApplyRepository.countByJob_Employer_UserId(employerId);
            Long totalJob = jobRepository.countByEmployer_UserId(employerId);
            Long totalShortList = shortListRepository.countByJob_Employer_UserId(employerId);
            Long totalInterview = interviewRepository.countByJobApply_Job_Employer_UserId(employerId);
            map.put("totalApply", totalApply);
            map.put("totalJob", totalJob);
            map.put("totalShortList", totalShortList);
            map.put("totalInterview", totalInterview);
            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }catch (Exception e){
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;

    }

    @Override
    public ActionResult getTransactionByMonth(String employerId, long monthsAgo){
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Employer> optionalEmployer = employerRepository.findById(employerId);
            if(optionalEmployer.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }

            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }catch (Exception e){
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

    @Override
    public ActionResult getAllTransaction(String employerId, Pageable pageable){
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Employer> optionalEmployer = employerRepository.findById(employerId);
            if(optionalEmployer.isEmpty()){
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                return actionResult;
            }
            Page<Transactions> transactions = transactionsRepository.findAllByEmployer(optionalEmployer.get(), pageable);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("totalMoney", optionalEmployer.get().getTransactionMoney());
            map.put("transactions", transactions.getContent());
            map.put("pageNumber", transactions.getPageable().getPageNumber());
            map.put("pageSize", transactions.getSize());
            map.put("totalPages", transactions.getTotalPages());
            map.put("totalElements", transactions.getTotalElements());

            actionResult.setData(map);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        }catch (Exception e){
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }

}
