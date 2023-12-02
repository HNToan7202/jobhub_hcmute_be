package vn.iotstar.jobhub_hcmute_be.service;

import jakarta.mail.MessagingException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.function.Function;

public interface EmployerService {
    <S extends Employer> S save(S entity);

    Optional<Employer> findById(String s);

    boolean existsById(String s);

    long count();

    void deleteById(String s);

    void delete(Employer entity);

    <S extends Employer, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    Optional<Employer> findByPhoneAndIsActiveIsTrue(String phone);

    Optional<Employer> findByUserIdAndIsActiveIsTrue(String userId);

    ResponseEntity<GenericResponse> getProfile(String userId);

    ResponseEntity<GenericResponse> changeLogo(String userId, MultipartFile imageFile) throws IOException;


    ActionResult addBg(String userId, MultipartFile imageFile) throws IOException;

    ActionResult deleteBg(String userId, String imgeUrl) throws IOException;

    ResponseEntity<GenericResponse> updateCompanyProfile(String userId, EmployerUpdateDTO request) throws Exception;

    ActionResult getApplicants(String employerId, Pageable pageable, String state);

    ActionResult updateCandidateState(String recruiterId, String userId, UpdateStateRequest updateStateRequest);

    ActionResult replyCandidate(ReplyRequest request) throws MessagingException, UnsupportedEncodingException;

    void reply(ReplyRequest request) throws MessagingException, UnsupportedEncodingException;

    ActionResult topCompany(Pageable pageable);

    ActionResult createInterview(String jobApplyId, InterViewDTO interViewDTO);

    ActionResult getAllInterview(String employerId, Pageable pageable);

    ActionResult getDetailInterview(String employerId, String interviewId);

    ActionResult getDashboard(String employerId);


    ActionResult getTransactionByMonth(String employerId, long monthsAgo);

    ActionResult getAllTransaction(String employerId, Pageable pageable);
}
