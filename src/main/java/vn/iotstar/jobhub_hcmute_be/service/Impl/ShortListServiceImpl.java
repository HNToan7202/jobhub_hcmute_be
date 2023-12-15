package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.JobModel;
import vn.iotstar.jobhub_hcmute_be.dto.ShortListModel;
import vn.iotstar.jobhub_hcmute_be.dto.UserModel;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.ShortList;
import vn.iotstar.jobhub_hcmute_be.entity.Student;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.JobRepository;
import vn.iotstar.jobhub_hcmute_be.repository.ShortListRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.ShortListService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShortListServiceImpl implements ShortListService {

    @Autowired
    ShortListRepository shortListRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public void deleteAllInBatch(Iterable<ShortList> entities) {
        shortListRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        shortListRepository.deleteAllByIdInBatch(strings);
    }

    @Override
    public void deleteAllInBatch() {
        shortListRepository.deleteAllInBatch();
    }

    @Override
    public ShortList getReferenceById(String s) {
        return shortListRepository.getReferenceById(s);
    }

    @Override
    public <S extends ShortList> List<S> findAll(Example<S> example) {
        return shortListRepository.findAll(example);
    }

    @Override
    public <S extends ShortList> List<S> findAll(Example<S> example, Sort sort) {
        return shortListRepository.findAll(example, sort);
    }

    @Override
    public List<ShortList> findAll() {
        return shortListRepository.findAll();
    }

    @Override
    public List<ShortList> findAllById(Iterable<String> strings) {
        return shortListRepository.findAllById(strings);
    }

    @Override
    public <S extends ShortList> S save(S entity) {
        return shortListRepository.save(entity);
    }

    @Override
    public Optional<ShortList> findById(String s) {
        return shortListRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return shortListRepository.existsById(s);
    }

    @Override
    public long count() {
        return shortListRepository.count();
    }

    @Override
    public void deleteById(String s) {
        shortListRepository.deleteById(s);
    }

    @Override
    public void delete(ShortList entity) {
        shortListRepository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        shortListRepository.deleteAllById(strings);
    }

    @Override
    public void deleteAll(Iterable<? extends ShortList> entities) {
        shortListRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        shortListRepository.deleteAll();
    }

    @Override
    public List<ShortList> findAll(Sort sort) {
        return shortListRepository.findAll(sort);
    }

    @Override
    public Page<ShortList> findAll(Pageable pageable) {
        return shortListRepository.findAll(pageable);
    }

    @Override
    public ActionResult addShortList(String userId, String jobId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Student> optionalUser = studentRepository.findById(userId);
            Optional<Job> jobOptional = jobRepository.findById(jobId);
            if (optionalUser.isPresent() && jobOptional.isPresent()) {
                ShortList shortList = new ShortList();
                Student user = optionalUser.get();
                Job job = jobOptional.get();
                shortList.setUser(user);
                shortList.setJob(job);

                shortList = shortListRepository.save(shortList);

                actionResult.setData(shortList);
                actionResult.setErrorCode(ErrorCodeEnum.ADD_SHORT_LIST_SUCCESS);

            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            }

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }

    @Override
    public ActionResult getShortListByUser(String userId, Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Page<ShortList> shortLists = shortListRepository.findAllByUser_UserId(user.getUserId(), pageable);
                Map<String, Object> response = new HashMap<>();
                response.put("shortLists", shortLists.getContent());
                response.put("totalPages", shortLists.getTotalPages());
                response.put("totalElements", shortLists.getTotalElements());
                response.put("currentPage", shortLists.getNumber());
                response.put("totalItems", shortLists.getNumberOfElements());

                actionResult.setData(response);
                actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_USER_SUCCESSFULLY);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }


    @Override
    public ActionResult getShortByJob(String jobId, Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Job> optionalJob = jobRepository.findById(jobId);
            if (optionalJob.isPresent()) {
                Job job = optionalJob.get();
                Page<ShortList> shortLists = shortListRepository.findAllByJob_JobId(job.getJobId(), pageable);
                Map<String, Object> response = new HashMap<>();
                response.put("shortLists", shortLists.getContent());
                response.put("totalPages", shortLists.getTotalPages());
                response.put("totalElements", shortLists.getTotalElements());
                response.put("currentPage", shortLists.getNumber());
                response.put("totalItems", shortLists.getNumberOfElements());

                actionResult.setData(response);
                actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_USER_SUCCESSFULLY);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }

    @Override
    public ActionResult getShortListByEmployer(String userId, Pageable pageable) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Page<ShortList> shortLists = shortListRepository.findAllByJob_Employer_UserId(user.getUserId(), pageable);
                Map<String, Object> response = new HashMap<>();
                List<ShortListModel> shortListModelList = shortLists.getContent().stream().map(shortList -> {
                    ShortListModel shortListModel = new ShortListModel();
                    JobModel jobModel = new JobModel();
                    UserModel userModel = new UserModel();
                    BeanUtils.copyProperties(shortList, shortListModel);
                    BeanUtils.copyProperties(shortList.getJob(), jobModel);
                    shortListModel.setJob(jobModel);
                    BeanUtils.copyProperties(shortList.getUser(), userModel);
                    shortListModel.setUser(userModel);
                    return shortListModel;
                }).toList();

                response.put("shortLists", shortListModelList);
                response.put("totalPages", shortLists.getTotalPages());
                response.put("totalElements", shortLists.getTotalElements());
                response.put("currentPage", shortLists.getNumber());
                response.put("totalItems", shortLists.getNumberOfElements());

                actionResult.setData(response);
                actionResult.setErrorCode(ErrorCodeEnum.GET_ALL_USER_SUCCESSFULLY);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            }
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }

    @Override
    public ActionResult deleteShortListById(String jobId, String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<ShortList> optionalShortList = shortListRepository.findByJob_JobIdAndUser_UserId(jobId, userId);
            if (optionalShortList.isPresent()) {
                ShortList shortList = optionalShortList.get();
                if (shortList.getUser().getUserId().equals(userId)) {
                    deleteById(optionalShortList.get().getId());
                    actionResult.setErrorCode(ErrorCodeEnum.DELETE_SHORT_LIST_SUCCESS);
                } else {
                    actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
                }
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            }

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }
}
