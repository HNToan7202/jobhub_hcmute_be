package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.GetFollowerModel;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.FollowerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.StudentRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.FollowerService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FollowerServiceImpl implements FollowerService {
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EmployerRepository employerRepository;
    private GetFollowerModel transformGetFollowerList (Follower follower ) {
        Optional<User> user = userRepository.findById(follower.getFollower().getUserId());
        Optional<Student> student = studentRepository.findById(user.get().getUserId());
        Optional<Employer> employer = employerRepository.findById(user.get().getUserId());
        if (student.isPresent()) {
            return GetFollowerModel.transformStudent(student.get(),follower.getFollowerId(), user.get().getUserId());
        } else if (employer.isPresent()) {
            return GetFollowerModel.transformEmployer(employer.get(),follower.getFollowerId(), user.get().getUserId());
        }
        return null;
    }
    private ActionResult getFollowerListHandle(Integer page, Integer size , User user) {
        Page<Follower> followers = followerRepository.findByFollowee(user, PageRequest.of(page-1, size));
        List<GetFollowerModel>  followerList = followers.getContent().stream().map(this::transformGetFollowerList).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("followerList", PageModel.transform(followers, followerList));
        ActionResult actionResult = new ActionResult();
        actionResult.setErrorCode(ErrorCodeEnum.OK);
        actionResult.setData(response);
        return actionResult;
    }
    @Override
    public ActionResult getFollowerList(Integer page, Integer size) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            if (!user.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
               actionResult = getFollowerListHandle(page, size, user.get());

        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getFollowerList(Integer page, Integer size, String userId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findByUserId(userId);
            if (!user.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            actionResult = getFollowerListHandle(page, size, user.get());

        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult sendFollowerRequest(String receiverId) {
        ActionResult actionResult = new ActionResult();
        try {
            if(CurrentUserUtils.getCurrentUserId().equals(receiverId)){
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<User> follower = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> followee = userRepository.findByUserId(receiverId);
            if (!follower.isPresent() || !followee.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<Follower> followers = followerRepository.findByFollowerAndFollowee(follower.get(), followee.get());
            if (!followers.isPresent()) {
                Follower follower1 = new Follower();
                follower1.setFollower(follower.get());
                follower1.setFollowee(followee.get());
                followerRepository.save(follower1);
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
            }

        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult candelFollowerRequest(String userId){
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> follower = userRepository.findByUserId(CurrentUserUtils.getCurrentUserId());
            Optional<User> followee = userRepository.findByUserId(userId);
            if (!follower.isPresent() || !followee.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
                return actionResult;
            }
            Optional<Follower> followers = followerRepository.findByFollowerAndFollowee(follower.get(), followee.get());
            if (followers.isPresent()) {
                followerRepository.delete(followers.get());
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            } else {
                actionResult.setErrorCode(ErrorCodeEnum.IT_REQUEST_IS_INVALID);
            }
        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;

    }


    @Deprecated
    public Follower getById(String s) {
        return followerRepository.getById(s);
    }

    public <S extends Follower> S save(S entity) {
        return followerRepository.save(entity);
    }

    public Optional<Follower> findById(String s) {
        return followerRepository.findById(s);
    }

    public long count() {
        return followerRepository.count();
    }
}
