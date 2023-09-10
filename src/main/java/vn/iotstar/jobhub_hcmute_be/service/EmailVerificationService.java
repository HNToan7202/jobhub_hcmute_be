package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import vn.iotstar.jobhub_hcmute_be.entity.EmailVerification;

import java.util.Optional;

public interface EmailVerificationService {


    @Async
    String sendOtp(String email);

    @Async
    String sendOtpEmployer(String email, String fullname);

    boolean verifyOtp(String email, String otp);

    String generateOtp();

    Optional<EmailVerification> findByEmail(String email);

    void deleteExpiredOtp();

    @Scheduled(fixedDelay = 30000) // 5 minutes
    void cleanupExpiredOtp();
}
