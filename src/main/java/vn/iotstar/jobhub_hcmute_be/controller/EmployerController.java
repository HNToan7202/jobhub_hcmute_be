package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.service.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@RestController
@PreAuthorize("hasRole('EMPLOYER')")
@RequestMapping("/api/v1/employer")
@Tag(name = "Employer", description = "Employer API")
public class EmployerController {

    final JobService jobService;
    private final JwtTokenProvider jwtTokenProvider;

    final EmployerService employerService;


    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    JobApplyService jobApplyService;

    @Autowired
    ShortListService shortListService;

    @Autowired
    TransactionsService transactionsService;

    private static final Map<String, Long> userLastRequestTimeMap = new HashMap<>();

    private static final long FIVE_MINUTES_IN_MILLIS = 60 * 1000;

    public EmployerController(JobService jobService, JwtTokenProvider jwtTokenProvider, EmployerService employerService) {
        this.jobService = jobService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.employerService = employerService;
    }

    @GetMapping("/profile")
    //@Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    public ResponseEntity<GenericResponse> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.getProfile(userId);
    }

    @Transactional
    //post 1 job
    @PostMapping("/post-job")
    public ResponseModel addJob(@RequestHeader("Authorization") String authorizationHeader,
                                @Valid @RequestBody PostJobRequest jobRequest,
                                BindingResult bindingResult) throws Exception {
        ActionResult actionResult = new ActionResult();
        String jwt = authorizationHeader.substring(7);
        String employerId = jwtTokenProvider.getUserIdFromJwt(jwt);
        // Kiểm tra xem các trường bắt buộc có được điền hay không
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        try {
            actionResult = jobService.postJob(jobRequest, employerId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/update-job/{jobId}")
    public ResponseModel updateJob(@PathVariable("jobId") String jobId,
                                   @Valid @RequestBody JobUpdateRequest jobUpdateRequest,
                                   @RequestHeader("Authorization") String authorizationHeader,
                                   BindingResult bindingResult) {
        String token = authorizationHeader.substring(7);
        String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = jobService.updateJob(jobId, jobUpdateRequest, recruiterId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }


    @PutMapping("/change-logo")
    public ResponseEntity<GenericResponse> uploadAvatar(@RequestParam MultipartFile imageFile,
                                                        @RequestHeader("Authorization") String token) throws IOException {
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return employerService.changeLogo(userId, imageFile);
    }


    @PutMapping("/add-bg")
    public ResponseModel uploadBackground(@RequestParam MultipartFile imageFile,
                                          @RequestHeader("Authorization") String token) throws IOException {
        ActionResult actionResult = new ActionResult();
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        try {
            actionResult = employerService.addBg(userId, imageFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/delete-bg")
    public ResponseModel deleteItemBg(@RequestBody DeleteItemRequest deleteItemRequest,
                                      @RequestHeader("Authorization") String token) throws IOException {
        ActionResult actionResult = new ActionResult();
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        try {
            actionResult = employerService.deleteBg(userId, deleteItemRequest.getImageUrl());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }


    @PutMapping("/update-profile-company")
    public ResponseEntity<GenericResponse> update(@Valid @RequestBody EmployerUpdateDTO request,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
        return employerService.updateCompanyProfile(userIdFromToken, request);
    }

    @GetMapping("/get-applicants")
    public ResponseModel getAllByUserIdAndDateFilters(@RequestParam(defaultValue = "0") int index,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "-1") int day,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            if (day < 0) {
                actionResult = employerService.getApplicants(userIdFromToken, PageRequest.of(index, size), state);
            } else
                actionResult = jobApplyService.getAllByUserIdAndDateFilters(PageRequest.of(index, size), userIdFromToken, day, state);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/get-applicants/{jobId}")
    public ResponseModel getAllJobByJobId(@PathVariable("jobId") String jobId,
                                          @RequestParam(defaultValue = "0") int index,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "ALL") String state,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = jobApplyService.getAllByJobIdAndEmployerId(PageRequest.of(index, size), jobId, userIdFromToken, state);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/get-all-interview")
    public ResponseModel getAllInterview(@RequestParam(defaultValue = "0") int index,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.getAllInterview(userIdFromToken, PageRequest.of(index, size));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("student/state/{usedId}")
    public ResponseModel updateCandidateState(@RequestHeader("Authorization") String authorizationHeader,
                                              @PathVariable String usedId,
                                              @Valid @RequestBody UpdateStateRequest updateStateRequest) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.updateCandidateState(recruiterId, usedId, updateStateRequest);
            if (updateStateRequest.getReplyRequest() != null)
                employerService.reply(updateStateRequest.getReplyRequest());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("/reply")
    public ResponseModel reply(@RequestBody ReplyRequest replyRequest) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = employerService.replyCandidate(replyRequest);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @DeleteMapping("/job/{jobId}")
    public ResponseEntity<GenericResponse> deleteJob(@PathVariable("jobId") String jobId) {
        try {
            Optional<Job> job = jobService.findById(jobId);
            if (job.isPresent()) {
                jobService.delete(job.get());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(GenericResponse.builder()
                                .success(true)
                                .message("Deleted Job")
                                .result(null)
                                .statusCode(HttpStatus.OK.value())
                                .build());
            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("Job Not Found")
                                .result(null)
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Request url error")
                            .result(null)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        } catch (Exception e) {
            String mes = "Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message(mes)
                            .result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/{jobApplyId}/create-interview")
    public ResponseModel createInterview(@PathVariable String jobApplyId,
                                         @RequestBody InterViewDTO interViewDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = employerService.createInterview(jobApplyId, interViewDTO);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PutMapping("/job/{jobId}/change-state")
    public ResponseModel changeStateJob(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String jobId) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = jobService.employerChangeStateJob(recruiterId, jobId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/{interviewId}/detail-interview")
    public ResponseModel getDetailInterview(@PathVariable String interviewId, @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String recruiterId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.getDetailInterview(recruiterId, interviewId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/short-list")
    public ResponseModel getShortList(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size, @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String employerId = jwtTokenProvider.getUserIdFromJwt(token);
            Pageable pageable = PageRequest.of(index, size);
            actionResult = shortListService.getShortListByEmployer(employerId, pageable);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/dashboard")
    public ResponseModel getDashboard(@RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String employerId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.getDashboard(employerId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/transaction/chart")
    public ResponseModel getTransactionChart(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "6") int monthsAgo) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String employerId = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = employerService.getTransactionByMonth(employerId, monthsAgo);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/transaction/get-all")
    public ResponseModel getAllTransaction(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String employerId = jwtTokenProvider.getUserIdFromJwt(token);
            Pageable pageable = PageRequest.of(index, size);
            actionResult = transactionsService.getAllTransaction(employerId, pageable);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/transaction/statics")
    public ResponseModel getTransactionStatics(@RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String employerId = jwtTokenProvider.getUserIdFromJwt(token);
            //    actionResult = transactionsService.staticsTrans(employerId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @PostMapping("/jobs/invite/{jobId}")
    public ResponseModel sendMailInviteJob(@PathVariable("jobId") String jobId,
                                           @RequestParam String userId) {
        ActionResult actionResult = new ActionResult();

        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = userLastRequestTimeMap.get(userId);

        if (lastRequestTime != null && currentTime - lastRequestTime < FIVE_MINUTES_IN_MILLIS) {
            actionResult.setErrorCode(ErrorCodeEnum.REQUEST_ALREADY_SENT);
            return responseBuild.build(actionResult);
        }

        try {
            userLastRequestTimeMap.put(userId, currentTime);
            actionResult = employerService.inviteApplyForJob(jobId, userId);
            return responseBuild.build(actionResult);
        } catch (MessagingException | UnsupportedEncodingException e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            return responseBuild.build(actionResult);
        }
    }


}
