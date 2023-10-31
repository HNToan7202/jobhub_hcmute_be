package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.security.JwtTokenProvider;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;
import vn.iotstar.jobhub_hcmute_be.service.ResumeService;
import vn.iotstar.jobhub_hcmute_be.service.StudentService;
import vn.iotstar.jobhub_hcmute_be.service.UserService;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/student")
@Tag(name = "Student", description = "Student API")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    final ResumeService resumeService;

    final UserService userService;

    final JobApplyService jobApplyService;

    private final JwtTokenProvider jwtTokenProvider;

    private final StudentService studentService;

    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    private CacheManager cacheManager;


    public StudentController(ResumeService resumeService, UserService userService, JobApplyService jobApplyService, JwtTokenProvider jwtTokenProvider, StudentService studentService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobApplyService = jobApplyService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    //@Operation(security = {@SecurityRequirement(name = "api-key")}) // Yêu cầu xác thực bằng API key
    public ResponseEntity<GenericResponse> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return studentService.getProfile(userId);
    }

    @Procedure("application/json")
    @ResponseBody
    @PostMapping("{studentId}/update-resume")
    public ResponseEntity<?> updateResume(@RequestBody ResumeDTO resumeDTO, @RequestHeader("Authorization") String authorizationHeader, @PathVariable("studentId") String studentId, BindingResult bindingResult) throws Exception {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        if (!userId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GenericResponse.builder().success(false).message("Unauthorized: Empty or invalid token").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
        }
        // Kiểm tra xem các trường bắt buộc có được điền hay không
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.updateResume(resumeDTO, user.getUserId());
    }


    @PostMapping("/{jobId}/apply-job")
    public ResponseModel applyForJob(@PathVariable("jobId") String jobId, @RequestBody ApplyJobRequest request) {
        ActionResult actionResult = new ActionResult();
        UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            actionResult = jobApplyService.applyForJob(userDetail.getUserId(), jobId, request.getResumeLink());
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }


    @PutMapping("/post-cv")
    public ResponseEntity<?> uploadResume(@ModelAttribute MultipartFile resumeFile, @RequestParam(defaultValue = "false") Boolean isMain) throws IOException {

        // Kiểm tra kích thước tệp
        if (resumeFile.getSize() > 5 * 1024 * 1024) { // 5MB
            return ResponseEntity.badRequest().body(GenericResponse.builder().success(false).message("File size exceeds the maximum limit of 5MB.").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GenericResponse.builder().success(false).message("Unauthorized: Empty or invalid token").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
        }
        UserDetail user = (UserDetail) authentication.getPrincipal();
        String originalFileName = resumeFile.getOriginalFilename();
        assert originalFileName != null;
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        // Kiểm tra xem tệp có đúng định dạng (chỉ cho phép .doc, .docx hoặc .pdf)
        if (!fileExtension.equals("doc") && !fileExtension.equals("docx") && !fileExtension.equals("pdf")) {
            return ResponseEntity.badRequest().body(GenericResponse.builder().success(false).message("Invalid file format. Only .doc, .docx, and .pdf are allowed.").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        // Nếu tệp hợp lệ, gọi dịch vụ để xử lý tệp CV
        return resumeService.uploadResumeFile(resumeFile, user.getUserId(), isMain);
    }

    @DeleteMapping("/cv/{resumeUploadID}")
    public ResponseModel deleteCV(@RequestHeader("Authorization") String token, @PathVariable String resumeUploadID) {
        ActionResult actionResult = new ActionResult();
        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        try {
            actionResult = resumeService.deleteResume(resumeUploadID, userId);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }

    @GetMapping("/detail-resume")
    public ResponseEntity<?> getDetailResume(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.getDetailResume(user.getUserId());
    }

    @GetMapping("/resume-upload")
    public ResponseEntity<?> getResume(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return resumeService.getResumeUpload(user.getUserId());
    }

    @PutMapping("/change-avatar")
    public ResponseEntity<GenericResponse> uploadAvatar(@RequestParam MultipartFile imageFile, @RequestHeader("Authorization") String token) throws IOException {

        String jwt = token.substring(7);
        String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
        return studentService.changeAvatar(userId, imageFile);
    }

    @PutMapping("/update-profile")
    public ResponseModel update(@Validated @RequestBody UserUpdateRequest request, @RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {

        ActionResult actionResult = new ActionResult();
        if(bindingResult.hasErrors()) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            ResponseModel responseModel = responseBuild.build(actionResult);
            responseModel.setMessage(bindingResult.getFieldError().getDefaultMessage());
            return responseModel;
        }
        try
        {
            String token = authorizationHeader.substring(7);
            String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);
            actionResult = studentService.updateProfile(userIdFromToken, request);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);

    }

    @Cacheable("applicants")
    @GetMapping("/jobs/applicants")
    public ResponseModel getAppliedJobs(@RequestParam(defaultValue = "0") int index, @RequestParam(defaultValue = "10") int size, @RequestHeader("Authorization") String authorizationHeader) {
        ActionResult actionResult = new ActionResult();
        try {
            String token = authorizationHeader.substring(7);
            String studentId = jwtTokenProvider.getUserIdFromJwt(token);
            Pageable pageable = PageRequest.of(index, size);
            actionResult = jobApplyService.findJobAppliesByCandidate(studentId, pageable);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return responseBuild.build(actionResult);
    }

}
