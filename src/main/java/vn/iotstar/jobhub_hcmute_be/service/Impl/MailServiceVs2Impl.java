package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.dto.NewJobApplysDTO;
import vn.iotstar.jobhub_hcmute_be.dto.NewUserDTO;
import vn.iotstar.jobhub_hcmute_be.dto.RecommentJobForUserDTO;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.RoleRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.RecommendationService;
import vn.iotstar.jobhub_hcmute_be.utils.Constants;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MailServiceVs2Impl {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private RecommendationService recommendationService;
    private static final String username = "hotro.hcmute@gmail.com";
    private static final String password = "xskjqocjnyctaixj";

    private static NewUserDTO getNewUserDTO(User users, Role role) {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail(users.getEmail());
        newUserDTO.setPhone(users.getPhone());
        newUserDTO.setCreatedAt(users.getCreatedAt());
        newUserDTO.setLastLoginAt(users.getLastLoginAt());
        newUserDTO.setUserId(users.getUserId());
        newUserDTO.setVerified(users.isVerified());
        newUserDTO.setActive(users.getIsActive());
        if (role.getName().equals("STUDENT")) {
            newUserDTO.setFullName(((Student) users).getFullName());
            newUserDTO.setAvatar(((Student) users).getAvatar());
        } else if (role.getName().equals("EMPLOYER")) {
            newUserDTO.setFullName(((Employer) users).getCompanyName());
            newUserDTO.setAvatar(((Employer) users).getLogo());
        }

        return newUserDTO;
    }

    public void sendNewUser(int page, int size) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            Date startOfDayYesterday = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDayYesterday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

            System.out.println("Start of yesterday: " + startOfDayYesterday);
            System.out.println("End of yesterday: " + endOfDayYesterday);
            Role roleStudent = roleRepository.findByName("STUDENT");
            Role roleEmployer = roleRepository.findByName("EMPLOYER");
            Page<User> userstudents = userRepository.findByRoleAndCreatedAtBetween(roleStudent, startOfDayYesterday, endOfDayYesterday, PageRequest.of(page, size));
            Page<User> useremployers = userRepository.findByRoleAndCreatedAtBetween(roleEmployer, startOfDayYesterday, endOfDayYesterday, PageRequest.of(page, size));
//            Page<User> users = userRepository.findByCreatedAtBetween(startOfDayYesterday, endOfDayYesterday, PageRequest.of(page, size));
//            System.out.println("users: " + users.getContent().size());
            List<NewUserDTO> listStudent = new ArrayList<>();
            List<NewUserDTO> listEmployer = new ArrayList<>();

            for (User userstudent : userstudents.getContent()) {
                NewUserDTO newUserDTO = getNewUserDTO(userstudent, roleStudent);
                listStudent.add(newUserDTO);
            }
            for (User useremployer : useremployers.getContent()) {
                NewUserDTO newUserDTO = getNewUserDTO(useremployer, roleEmployer);
                listEmployer.add(newUserDTO);
            }

            Context contextStudent = new Context();
            contextStudent.setVariable("students", listStudent);
            contextStudent.setVariable("count", listStudent.size());
            contextStudent.setVariable("currentPage", userstudents.getNumber());
            contextStudent.setVariable("totalPage", userstudents.getTotalPages());
            contextStudent.setVariable("startOfDayYesterday", startOfDayYesterday);
            contextStudent.setVariable("endOfDayYesterday", endOfDayYesterday);

            String emailContent = templateEngine.process("newStudentTemplate", contextStudent);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(username));
            helper.setTo(Constants.RECEIVER_EMAIL);
            helper.setSubject("[JOB_HUB_Scheduler]NEW USERS LIST USER" + today );
            helper.setText(emailContent, true);
            javaMailSender.send(message);

            Context contextEmployer = new Context();
            contextEmployer.setVariable("employers", listEmployer);
            contextEmployer.setVariable("count", listEmployer.size());
            contextEmployer.setVariable("currentPage", useremployers.getNumber());
            contextEmployer.setVariable("totalPage", useremployers.getTotalPages());
            contextEmployer.setVariable("startOfDayYesterday", startOfDayYesterday);
            contextEmployer.setVariable("endOfDayYesterday", endOfDayYesterday);

            String emailContent1 = templateEngine.process("newEmployerTemplate", contextEmployer);
            MimeMessage message1 = javaMailSender.createMimeMessage();
            MimeMessageHelper helper1 = new MimeMessageHelper(message1, true);
            helper1.setFrom(new InternetAddress(username));
            helper1.setTo(Constants.RECEIVER_EMAIL);
            helper1.setSubject("[JOB_HUB_Scheduler]NEW USERS LIST EMPLOYER" + today);
            helper1.setText(emailContent1, true);
            javaMailSender.send(message1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendJobApplied() {
        try {
            Role roleEmployer = roleRepository.findByName("EMPLOYER");
            List<User> users = userRepository.findByRoleAndIsActiveAndIsVerifiedAndIsReceiveEmail(roleEmployer, true, true, true);

            for (User user : users) {
                String receiveEmail = user.getReceiveEmail() == null ? user.getEmail() : user.getReceiveEmail();
                Integer timeGetData = user.getTimeGetData() == null ? 1 : user.getTimeGetData();
                System.out.println("timeGetData: " + timeGetData);

                Date lastSendEmail = user.getLastSendEmail() == null ? new Date() : user.getLastSendEmail();
                System.out.println("lastSendEmail: " + lastSendEmail);
                LocalDate lastEmailSentLocalDate = lastSendEmail.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                LocalDate today = LocalDate.now();
                System.out.println("today: " + today);
                LocalDate yesterday = today.minusDays(timeGetData);
                Date startOfDayYesterday = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date endOfDayYesterday = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

                long daysDiff = today.toEpochDay() - lastEmailSentLocalDate.toEpochDay();

                System.out.println("daysDiff: " + daysDiff);
                System.out.println("aysDiff == timeGetData: " + (daysDiff == timeGetData));
                System.out.println("startOfDayYesterday: " + startOfDayYesterday);
                System.out.println("endOfDayYesterday: " + endOfDayYesterday);

                if (daysDiff == timeGetData) {
                    Optional<Employer> employer = employerRepository.findByUserId(user.getUserId());
                    if (employer.isPresent()) {
                        List<Job> jobs = employer.get().getJobs();
                        List<NewJobApplysDTO> jobApplied = new ArrayList<>();
                        for (Job job : jobs) {
                            List<JobApply> jobApplies = job.getJobApplies();
                            for (JobApply jobApply : jobApplies) {
                                if (jobApply.getCreatedAt().after(startOfDayYesterday) && jobApply.getCreatedAt().before(endOfDayYesterday)) {
                                    NewJobApplysDTO newJobApplysDTO = NewJobApplysDTO.transform(jobApply);
                                    jobApplied.add(newJobApplysDTO);
                                }
                            }
                        }
                        System.out.println("jobApplied: " + jobApplied.size());
                        if (jobApplied.size() > 0) {
                            Context context = new Context();
                            context.setVariable("jobs", jobApplied);
                            context.setVariable("count", jobApplied.size());
                            context.setVariable("startOfDayYesterday", yesterday);
                            context.setVariable("endOfDayYesterday", today);

                            String emailContent = templateEngine.process("jobAppliedTemplate", context);
                            MimeMessage message = javaMailSender.createMimeMessage();
                            MimeMessageHelper helper = new MimeMessageHelper(message, true);
                            helper.setFrom(new InternetAddress(username));
                            helper.setTo(receiveEmail);
                            helper.setSubject("[JOB_HUB_Scheduler]JOB APPLIED LIST"+today);
                            helper.setText(emailContent, true);
                            javaMailSender.send(message);
                        }
                    }
                    user.setLastSendEmail(new Date());
                    userRepository.save(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRecommentJobForUser() {
        try {
            Role roleStudent = roleRepository.findByName("STUDENT");
            List<User> users = userRepository.findByRoleAndIsActiveAndIsVerifiedAndIsReceiveEmail(roleStudent, true, true, true);

            for (User user : users) {
                String receiveEmail = user.getReceiveEmail() == null ? user.getEmail() : user.getReceiveEmail();
                Integer timeGetData = user.getTimeGetData() == null ? 30 : user.getTimeGetData();
                System.out.println("timeGetData: " + timeGetData);

                LocalDate today = LocalDate.now();
                System.out.println("today: " + today);
                LocalDate yesterday = today.minusDays(timeGetData);
                Date startOfDayYesterday = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
                System.out.println("startOfDayYesterday: " + startOfDayYesterday);

                Page<Job> pageJobs = recommendationService.getRecommendationJob(user.getUserId(), 1, 100);
                if (pageJobs.getTotalElements() > 0) {
                    List<Job> jobs = pageJobs.getContent();
                    System.out.println("jobs: " + jobs.size());
                    if (jobs.size() > 0) {
                        List<Job> jobfilter = jobs.stream().filter(job -> job.getCreatedAt().after(startOfDayYesterday)).toList();
                        List<RecommentJobForUserDTO>  jobList = jobfilter.stream().map(RecommentJobForUserDTO::transform).toList();
                        System.out.println("jobList: " + jobList.size());
                        Context context = new Context();
                        context.setVariable("jobs", jobList);
                        context.setVariable("count", jobList.size());
                        context.setVariable("startOfDayYesterday", yesterday);
                        context.setVariable("endOfDayYesterday", today);

                        String emailContent = templateEngine.process("recommendJobForUserTemplate", context);
                        MimeMessage message = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true);
                        helper.setFrom(new InternetAddress(username));
                        helper.setTo(receiveEmail);
                        helper.setSubject("[JOB_HUB_Scheduler]RECOMMENT JOB LIST"+today);
                        helper.setText(emailContent, true);
                        javaMailSender.send(message);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
