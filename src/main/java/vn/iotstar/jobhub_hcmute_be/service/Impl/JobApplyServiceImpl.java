package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import vn.iotstar.jobhub_hcmute_be.constant.State;
import vn.iotstar.jobhub_hcmute_be.dto.GenericResponse;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.JobApply;
import vn.iotstar.jobhub_hcmute_be.entity.ResumeUpload;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.JobApplyRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.service.JobApplyService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class JobApplyServiceImpl implements JobApplyService {

    @Autowired
    JobApplyRepository jobApplyRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public <S extends JobApply> List<S> saveAll(Iterable<S> entities) {
        return jobApplyRepository.saveAll(entities);
    }

    @Override
    public List<JobApply> findAll() {
        return jobApplyRepository.findAll();
    }

    @Override
    public List<JobApply> findAllById(Iterable<String> strings) {
        return jobApplyRepository.findAllById(strings);
    }

    @Override
    public <S extends JobApply> S save(S entity) {
        return jobApplyRepository.save(entity);
    }

    @Override
    public Optional<JobApply> findById(String s) {
        return jobApplyRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return jobApplyRepository.existsById(s);
    }

    @Override
    public long count() {
        return jobApplyRepository.count();
    }

    @Override
    public void deleteById(String s) {
        jobApplyRepository.deleteById(s);
    }

    @Override
    public void delete(JobApply entity) {
        jobApplyRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        jobApplyRepository.deleteAllById(strings);
    }

    @Override
    public void deleteAll(Iterable<? extends JobApply> entities) {
        jobApplyRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        jobApplyRepository.deleteAll();
    }

    @Override
    public List<JobApply> findAll(Sort sort) {
        return jobApplyRepository.findAll(sort);
    }

    @Override
    public Page<JobApply> findAll(Pageable pageable) {
        return jobApplyRepository.findAll(pageable);
    }

    @Override
    public <S extends JobApply> Page<S> findAll(Example<S> example, Pageable pageable) {
        return jobApplyRepository.findAll(example, pageable);
    }

    @Override
    public <S extends JobApply> long count(Example<S> example) {
        return jobApplyRepository.count(example);
    }

    @Override
    public <S extends JobApply, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return jobApplyRepository.findBy(example, queryFunction);
    }

    @Override
    public ActionResult applyForJob(String userId, String jobId, String resumeUploadId) {
        ActionResult actionResult = new ActionResult();

        // Kiểm tra xem công việc có tồn tại hay không
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.JOB_NOT_FOUND);
            return actionResult;
        }
        Job job = optionalJob.get();

        // Kiểm tra xem công việc còn hạn apply hay không
        Date expirationDate = job.getDeadline();
        Date currentDate = new Date();
        boolean isJobActive = job.getIsActive();

        if ((isJobActive && expirationDate.before(currentDate)) || !isJobActive) {
            // Nếu công việc đang active nhưng đã hết hạn apply, set trạng thái isActive thành false
            job.setIsActive(false);
            jobRepository.save(job);
            actionResult.setErrorCode(ErrorCodeEnum.JOB_EXPIRED);
            return actionResult;
        }

        // Tìm ứng viên trong cơ sở dữ liệu
        Optional<Student> optionalCandidate = studentRepository.findById(userId);
        if (optionalCandidate.isEmpty()) {
            actionResult.setErrorCode(ErrorCodeEnum.CANDIDATE_NOT_FOUND);
            return actionResult;
        }
        Student candidate = optionalCandidate.get();

        // Kiểm tra xem ứng viên đã apply vào công việc này chưa
        if (jobApplyRepository.findByStudentAndJob(candidate, job) != null) {
            actionResult.setErrorCode(ErrorCodeEnum.ALREADY_APPLY);
            return actionResult;
        }

        // Kiểm tra xem CV của ứng viên tồn tại
        boolean isResumeFound = candidate.getResume().getResumeUploads()
                .stream()
                .anyMatch(upload -> upload.getResumeId().equals(resumeUploadId));
        String resumeLink = "";
        if (isResumeFound) {
            Optional<ResumeUpload> optionalResumeUpload = candidate.getResume().getResumeUploads()
                    .stream()
                    .filter(upload -> upload.getResumeId().equals(resumeUploadId))
                    .findFirst();
            if (optionalResumeUpload.isPresent()) {
                resumeLink = optionalResumeUpload.get().getLinkUpload();
            }
        }
        if (!isResumeFound) {
            actionResult.setErrorCode(ErrorCodeEnum.CV_NOT_FOUND);
            return actionResult;
        }

        // Tạo đơn ứng tuyển
        JobApply application = new JobApply();
        application.setJob(job);
        application.setState(State.NOT_RECEIVED);
        application.setStudent(candidate);
        application.setResume(candidate.getResume());
        application.setResumeUpoad(resumeLink);
        // Ánh xạ những thứ còn lại của candidate qua cho JobApply
        BeanUtils.copyProperties(candidate, application);

        // Lưu đơn ứng tuyển vào database
        JobApply jobApply = jobApplyRepository.save(application);
        actionResult.setData(jobApply);
        actionResult.setErrorCode(ErrorCodeEnum.APPLICATION_SUCCESSFULLY);

        return actionResult;
    }

}
