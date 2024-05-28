package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.dto.CourseDTO;
import vn.iotstar.jobhub_hcmute_be.dto.CoursesDTO;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface CoursesService {


    ActionResult getCoursesList(Integer page, Integer size, String type);

    ActionResult getCoursesListAdmin(Integer page, Integer size, String type);

    ActionResult addCourses(CoursesDTO courseDTO);



    ActionResult updateCourses(String courseId, CoursesDTO courseDTO);

    ActionResult updateCoursesAdmin(String courseId, CoursesDTO courseDTO);

    ActionResult deleteCourses(String courseId);

    ActionResult deleteCoursesEmployer(String courseId);

    ActionResult changeStatusCourses(String courseId);

    ActionResult changeActiveCourses(String courseId);
}
