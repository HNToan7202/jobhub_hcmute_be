package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.CoursesDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Courses;
import vn.iotstar.jobhub_hcmute_be.entity.Employer;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.repository.CoursesRepository;
import vn.iotstar.jobhub_hcmute_be.repository.EmployerRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.CoursesService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.util.Optional;

@Service
public class CoursesServiceImpl implements CoursesService {
    private CoursesRepository coursesRepository;
    private UserRepository userRepository;
    private EmployerRepository employerRepository;
    ActionResult actionResult;

    public CoursesServiceImpl(CoursesRepository coursesRepository, UserRepository userRepository, EmployerRepository employerRepository) {
        this.coursesRepository = coursesRepository;
        this.userRepository = userRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    public ActionResult getCoursesList(Integer page, Integer size, String type) {
        actionResult = new ActionResult();
        try {
            String userId = CurrentUserUtils.getCurrentUserId();
            Optional<Employer> employer = employerRepository.findById(userId);
            Page<Courses> courses;
            if (type == null)
                courses = coursesRepository.findAllByEmployerOrderByCreatedAtDesc(
                        employer.get(),
                        PageRequest.of(page - 1, size)
                );
            else
                courses = coursesRepository.findAllByEmployerAndTypeOrderByCreatedAtDesc(
                        employer.get(),
                        type,
                        PageRequest.of(page - 1, size)
                );
            PageModel pageModel = PageModel.transform(courses);
            actionResult.setData(pageModel);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }


    @Override
    public ActionResult getCoursesListAdmin(Integer page, Integer size, String type) {
        actionResult = new ActionResult();
        try {
            Page<Courses> courses;
            if (type == null)
                courses = coursesRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page - 1, size));
            else
                courses = coursesRepository.findAllByTypeOrderByCreatedAtDesc(
                        type,
                        PageRequest.of(page - 1, size)
                );
            PageModel pageModel = PageModel.transform(courses);
            actionResult.setData(pageModel);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }



    @Override
    public ActionResult addCourses(CoursesDTO courseDTO) {
        actionResult = new ActionResult();
        try {
            String userId = CurrentUserUtils.getCurrentUserId();
            Optional<User> user = userRepository.findByUserId(userId);
            Courses courses = new Courses();
            BeanUtils.copyProperties(courseDTO, courses);
            if (user.get().getRole().getName().equals("ADMIN")) {
                courses.setEmployer(null);
                courses.setActive(true);
                courses.setStatus(true);
            } else {
                Optional<Employer> employer = employerRepository.findById(userId);
                if (!employer.isPresent()) {
                    actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_FOUND);
                    return actionResult;
                }
                courses.setEmployer(employer.get());
                courses.setActive(false);
                courses.setStatus(true);
            }
            coursesRepository.save(courses);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult updateCourses(String courseId, CoursesDTO courseDTO) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            if (!CurrentUserUtils.getCurrentUserId().equals(courses.get().getEmployer().getUserId())) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            Courses course = courses.get();
            BeanUtils.copyProperties(courseDTO, course);
            course.setActive(false);
            coursesRepository.save(course);
            actionResult.setData(course);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult updateCoursesAdmin(String courseId, CoursesDTO courseDTO) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            if (courses.get().getEmployer() != null) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            Courses course = courses.get();
            BeanUtils.copyProperties(courseDTO, course);
            coursesRepository.save(course);
            actionResult.setData(course);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult deleteCourses(String courseId) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            //Admin chỉ xóa nhưng courses mà có employer là null
            if (courses.get().getEmployer() != null) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            coursesRepository.delete(courses.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult deleteCoursesEmployer(String courseId) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            if (!CurrentUserUtils.getCurrentUserId().equals(courses.get().getEmployer().getUserId())) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            coursesRepository.delete(courses.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult changeStatusCourses(String courseId) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            Courses course = courses.get();
            course.setStatus(!course.isStatus());
            coursesRepository.save(course);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult changeActiveCourses(String courseId) {
        actionResult = new ActionResult();
        try {
            Optional<Courses> courses = coursesRepository.findById(courseId);
            if (!courses.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COURSE_NOT_FOUND);
                return actionResult;
            }
            Courses course = courses.get();
            course.setActive(!course.isActive());
            coursesRepository.save(course);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

}
