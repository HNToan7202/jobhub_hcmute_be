package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstar.jobhub_hcmute_be.dto.NotificationRequest;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.EmailVerificationRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobApplyRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.NotificationService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final int OTP_LENGTH = 6;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Autowired
    private Environment env;

    Context context;

    @Override
    @Async
    public void sendApplyJob(String mail, String student, String jobName) {
        try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(mail);
                // Load Thymeleaf template
                Context context = new Context();
                context.setVariable("jobName",jobName);
                context.setVariable("student", student);
                String mailContent = templateEngine.process("notify-mail", context);
                helper.setText(mailContent, true);
                helper.setSubject("Bạn có đơn ứng tuyển mới mới cho công việc " + jobName);
                mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    @Override
    public ActionResult notificationMessage(NotificationRequest request) {
        ActionResult actionResult = new ActionResult();
        try{
            context = new Context();
            String content = templateEngine.process("notify-mail", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true, "utf-8");
            helper.setSubject("You have a new message from " + request.getFromName());
            helper.setTo(request.getMailTo());
            helper.setText(content, true);
            helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")),"JobHub Support");
            mailSender.send(message);
            actionResult.setErrorCode(ErrorCodeEnum.SEND_MAIL_SUCCESSFULLY);
        }
        catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return actionResult;
    }
}
