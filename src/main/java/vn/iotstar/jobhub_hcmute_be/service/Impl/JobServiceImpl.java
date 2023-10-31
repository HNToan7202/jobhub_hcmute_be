package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.JobType;
import vn.iotstar.jobhub_hcmute_be.dto.*;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.ElasticsearchRepository.JobElasticsearchRepository;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.PositionRepository;
import vn.iotstar.jobhub_hcmute_be.repository.SkillRepository;
import vn.iotstar.jobhub_hcmute_be.service.JobService;

import java.util.*;

@Service
@Transactional
@Slf4j
public class JobServiceImpl implements JobService {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    SkillRepository skillRepository;

    ActionResult actionResult;

    Map<String, Object> response;

    @Autowired
    JobElasticsearchRepository jobElasticsearchRepository;


    @Scheduled(cron = "0 0 0 * * *") // Chạy mỗi ngày lúc 00:00:00
    public void checkJobDeadlines() {
        // Lấy danh sách tất cả công việc từ cơ sở dữ liệu
        Iterable<Job> jobs = jobRepository.findAll();

        // Lặp qua từng công việc và kiểm tra thời hạn
        for (Job job : jobs) {
            if (job.getDeadline() != null && job.getDeadline().before(new Date())) {
                // Nếu thời hạn đã qua, cập nhật trạng thái isActive thành false
                job.setIsActive(false);
                jobRepository.save(job); // Lưu công việc đã cập nhật
            }
        }
    }



    @Override
    public List<Job> findAll() throws InterruptedException {
        return jobRepository.findAll();
    }

    @Override
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public <S extends Job> S save(S entity) {
        return jobRepository.save(entity);
    }

    @Override
    public Optional<Job> findById(String id) {
        return jobRepository.findById(id);
    }

    @Override
    public long count() {
        return jobRepository.count();
    }

    @Override
    public void deleteById(String s) {
        jobRepository.deleteById(s);
    }

    @Override
    public void delete(Job entity) {
        jobRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        jobRepository.deleteAll();
    }

    @Override
    public List<Job> findAll(Sort sort) {
        return jobRepository.findAll(sort);
    }

    @Override
    public Page<Job> findAll(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    public Page<Job> findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(String employerId, Pageable pageable) {
        return jobRepository.findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(employerId, pageable);
    }

    public Page<JobModel> searchSimilar(JobModel entity, String[] fields, Pageable pageable) {
        return jobElasticsearchRepository.searchSimilar(entity, fields, pageable);
    }

    public <S extends JobModel> S save(S entity) {
        System.out.println("Saving job to Elasticsearch...");
        return jobElasticsearchRepository.save(entity);
    }

    public <S extends JobModel> Iterable<S> saveAll(Iterable<S> entities) {
        return jobElasticsearchRepository.saveAll(entities);
    }

    public boolean existsById(String s) {
        return jobElasticsearchRepository.existsById(s);
    }

    public Iterable<JobModel> findAllById(Iterable<String> strings) {
        return jobElasticsearchRepository.findAllById(strings);
    }

    public void delete(JobModel entity) {
        jobElasticsearchRepository.delete(entity);
    }

    public void deleteAllById(Iterable<? extends String> strings) {
        jobElasticsearchRepository.deleteAllById(strings);
    }

    public void deleteAll(Iterable<? extends JobModel> entities) {
        jobElasticsearchRepository.deleteAll(entities);
    }

    @Override
    @CacheEvict(value = "applicationCache", allEntries = true)
    public void clearAllCache() {
        System.out.println("Clearing cache...");
    }

    @Cacheable(value = "applicationCache", key = "#jobId")
    @Override
    public ActionResult getDetail(String jobId) {
        ActionResult actionResult = new ActionResult();
        try {

            Optional<Job> optional = findById(jobId);

            if (optional.isPresent()) {
                JobDTO jobDTO = new JobDTO();
                BeanUtils.copyProperties(optional.get(), jobDTO);
                CompanyDTO companyDTO = new CompanyDTO();
                BeanUtils.copyProperties(optional.get().getEmployer(), companyDTO);
                jobDTO.setCompany(companyDTO);

                actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_DETAIL_SUCCESS);
                actionResult.setData(jobDTO);
                return actionResult;

            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
                return actionResult;
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;
        }
    }

    @Override
    public ActionResult getDetail(String jobId, String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Job> optional = findById(jobId);

            if (optional.isPresent()) {
                Job job = optional.get();
                // kiểm tra xem userId đã apply vao job hay chưa
                boolean isApplied = job.getJobApplies().stream().anyMatch(jobApply -> jobApply.getStudent().getUserId().equals(userId));
                JobDTO jobDTO = new JobDTO();
                BeanUtils.copyProperties(optional.get(), jobDTO);
                jobDTO.setIsApplied(isApplied);
                CompanyDTO companyDTO = new CompanyDTO();
                BeanUtils.copyProperties(optional.get().getEmployer(), companyDTO);
                jobDTO.setCompany(companyDTO);

                actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_DETAIL_SUCCESS);
                actionResult.setData(jobDTO);
                return actionResult;

            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
                return actionResult;
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;
        }
    }


    @Override
    public ActionResult findAllByEmployer(String id, Pageable pageable) {
        actionResult = new ActionResult();
        try {
            //Page<Job> jobs = findAll(pageable);

            Page<Job> jobs = findAllByEmployer_UserIdAndIsActiveIsTrueOrderByCreatedAtDesc(id, pageable);
            List<JobDTO> jobDTOs = convertJobToJobDTO(jobs.getContent());

            response = new HashMap<>();
            response.put("jobs", jobDTOs);
            response.put("currentPage", jobs.getNumber());
            response.put("totalItems", jobs.getTotalElements());
            response.put("totalPages", jobs.getTotalPages());

            actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_BY_EMPLOYER_SUCCESS);
            actionResult.setData(response);
            return actionResult;
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;
        }
    }


    public List<JobDTO> convertJobToJobDTO(List<Job> jobs) {
        List<JobDTO> jobDTOs = new ArrayList<>();
        for (Job job : jobs) {
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            CompanyDTO companyDTO = new CompanyDTO();
            BeanUtils.copyProperties(job.getEmployer(), companyDTO);
            jobDTO.setTotalApply(job.getJobApplies() == null ? 0 : job.getJobApplies().size());
            jobDTO.setCompany(companyDTO);
            jobDTOs.add(jobDTO);
        }
        return jobDTOs;
    }

    @Override
    public Page<Job> findAllByIsActiveIsTrueOrderByCreatedAtDesc(Boolean isActive, Pageable pageable) {
        return jobRepository.findByIsActiveOrderByCreatedAtDesc(isActive, pageable);
    }

    @Override
    public List<Job> findAllByIsActive(Boolean isActive) {
        return jobRepository.findJobsByIsActive(isActive);
    }


    @Cacheable("jobs")
    @Override
    public ActionResult getAllJobs(Pageable pageable, Boolean isActive) {
        actionResult = new ActionResult();
        try {
            Page<Job> jobs = findAllByIsActiveIsTrueOrderByCreatedAtDesc(isActive, pageable);
            response = new HashMap<>();
            response.put("jobs", jobs.getContent());
            response.put("currentPage", jobs.getNumber());
            response.put("totalItems", jobs.getTotalElements());
            response.put("totalPages", jobs.getTotalPages());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
            actionResult.setData(response);
            return actionResult;
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;
        }
    }


    @Cacheable("jobList")
    @Override
    public ActionResult getAlls(Boolean isActive){
        actionResult = new ActionResult();
        List<Job> jobs = findAllByIsActive(isActive);
        List<JobDTO> jobDTOs = convertJobToJobDTO(jobs);
        response = new HashMap<>();
        response.put("jobs", jobDTOs);
        response.put("total", jobDTOs.size());
        actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_JOB_SUCCESS);
        actionResult.setData(response);
        return actionResult;
    }


    @CachePut(value = "applicationCache", key = "#employerId")
    @Override
    public ActionResult postJob(PostJobRequest jobRequest, String employerId) {
        actionResult = new ActionResult();
        //check job có tồn tại hay chưa
        Optional<Job> opt = jobRepository.findJobByNameAndIsActiveIsTrue(jobRequest.getName());
        if (opt.isPresent()) {
            actionResult.setErrorCode(ErrorCodeEnum.JOB_EXISTED);
            return actionResult;
        }

        try {

            Job job = new Job();
            job.setName(jobRequest.getName());
            job.setJobType(JobType.valueOf(jobRequest.getJobType()));
            job.setQuantity(jobRequest.getQuantity());
            //job.setJobTitle(jobRequest.getJobTitle());
            job.setBenefit(jobRequest.getBenefit());
            job.setSalaryRange(jobRequest.getSalaryRange());
            job.setRequirement(jobRequest.getRequirement());
            job.setLocation(jobRequest.getLocation());
            job.setDescription(jobRequest.getDescription());
            job.setDeadline(jobRequest.getDeadline());
            job.setTime(jobRequest.getTime());
            job.setLink(jobRequest.getLink());


            // Check if position is existed
            Optional<Position> position = positionRepository.findByName(jobRequest.getPositionName());
            if (position.isPresent()) {
                job.setPosition(position.get());
            } else {
                Position newPosition = new Position();
                newPosition.setName(jobRequest.getPositionName());
                //newPosition = positionRepository.save(newPosition);
                job.setPosition(newPosition);
            }
            List<Skill> skills = new ArrayList<>();

            for (String skillName : jobRequest.getSkillsRequired()) {
                Optional<Skill> skillOptional = skillRepository.findSkillBySkillName(skillName);
                if (skillOptional.isPresent()) {
                    Skill skill;
                    skill = skillOptional.get();
                    skills.add(skill);
                } else {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillName);
                    //newSkill = skillRepository.save(newSkill);
                    skills.add(newSkill);
                }
            }
            job.setSkills(skills);
            job.setIsActive(true);
            Optional<Employer> optional = employerRepository.findById(employerId);
            if (optional.isPresent()) {
                job.setEmployer(optional.get());
                job.setLogo(optional.get().getLogo());
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.UNAUTHORIZED);
                return actionResult;
            }

            job = jobRepository.save(job);
            JobModel jobModel = convertJobToJobModel(job);
            save(jobModel);


            JobDTO jobResponse = new JobDTO();
            BeanUtils.copyProperties(job, jobResponse);

            actionResult.setErrorCode(ErrorCodeEnum.POST_JOB_SUCCESS);
            actionResult.setData(jobResponse);
            return actionResult;

        } catch (Exception e) {

            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            return actionResult;

        }
    }

    public JobModel convertJobToJobModel(Job job) {
        JobModel jobModel = new JobModel();
        BeanUtils.copyProperties(job, jobModel);
        jobModel.setPosition(job.getPosition().getName());
        jobModel.setSkills(List.of(job.getSkills().stream().map(Skill::getName).toArray(String[]::new)));
        return jobModel;
    }


    @CachePut(value = "applicationCache", key = "#jobId")
    @Override
    public ActionResult updateJob(String jobId, JobUpdateRequest jobUpdateRequest, String employerId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Job> optionalJob = findById(jobId);
            if (optionalJob.isPresent()) {
                Job job = optionalJob.get();
                if (employerId.equals(optionalJob.get().getEmployer().getUserId())) {
                    job.setName(jobUpdateRequest.getName());
                    job.setJobType(JobType.valueOf(jobUpdateRequest.getJobType()));
                    job.setTime(jobUpdateRequest.getTime());
                    job.setLink(jobUpdateRequest.getLink());
                    job.setQuantity(jobUpdateRequest.getQuantity());
                    job.setBenefit(jobUpdateRequest.getBenefit());
                    job.setSalaryRange(jobUpdateRequest.getSalaryRange());
                    job.setRequirement(jobUpdateRequest.getRequirement());
                    job.setLocation(jobUpdateRequest.getLocation());
                    job.setDescription(jobUpdateRequest.getDescription());
                    job.setDeadline(jobUpdateRequest.getDeadline());

                    // Check if position is existed
                    Optional<Position> position = positionRepository.findByName(jobUpdateRequest.getPositionName());
                    if (position.isPresent()) {
                        job.setPosition(position.get());
                    } else {
                        Position newPosition = new Position();
                        newPosition.setName(jobUpdateRequest.getPositionName());
                        newPosition = positionRepository.save(newPosition);
                        job.setPosition(newPosition);
                    }
                    List<Skill> skills = new ArrayList<>();
                    for (String skillName : jobUpdateRequest.getSkillsRequired()) {
                        Optional<Skill> skillOptional = skillRepository.findSkillBySkillName(skillName);
                        if (skillOptional.isPresent()) {
                            Skill skill;
                            skill = skillOptional.get();
                            skills.add(skill);
                        } else {
                            Skill newSkill = new Skill();
                            newSkill.setName(skillName);
                            newSkill = skillRepository.save(newSkill);
                            skills.add(newSkill);
                        }
                    }
                    job.setSkills(skills);

                    Job updatedJob = updateJob(job);
                    JobDTO jobResponse = new JobDTO();
                    BeanUtils.copyProperties(updatedJob, jobResponse);
                    if (updatedJob != null) {
                        actionResult.setErrorCode(ErrorCodeEnum.UPDATE_JOB_SUCCESS);
                        actionResult.setData(jobResponse);
                    }

                } else {
                    actionResult.setErrorCode(ErrorCodeEnum.UPDATE_JOB_FAILED);

                }
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.UNAUTHORIZED);
            }

        } catch (IllegalArgumentException e) {

            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getAllJobsByFilters(String name, String posName, String location, Pageable pageable){
        ActionResult actionResult = new ActionResult();
        Page<Job> jobs = jobRepository.findJobs(name, posName, location, pageable);

        List<JobDTO> jobDTOs =  jobs.getContent().stream().filter(job -> job.getIsActive() == true).map(job -> {
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            CompanyDTO companyDTO = new CompanyDTO();
            BeanUtils.copyProperties(job.getEmployer(), companyDTO);
            jobDTO.setCompany(companyDTO);
            return jobDTO;
        }).toList();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("content", jobDTOs);
        map.put("pageNumber", jobs.getNumber());
        map.put("pageSize", jobs.getSize());
        map.put("totalPages", jobs.getTotalPages());
        map.put("totalElements", jobs.getTotalElements());
        actionResult.setData(map);
        actionResult.setErrorCode(ErrorCodeEnum.GET_JOB_BY_FILTER_SUCCESS);

        return actionResult;
    }

}
