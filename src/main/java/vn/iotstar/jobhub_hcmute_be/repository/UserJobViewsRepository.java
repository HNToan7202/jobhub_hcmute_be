package vn.iotstar.jobhub_hcmute_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Job;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.entity.UserJobViews;

import java.util.Optional;

public interface UserJobViewsRepository extends JpaRepository<UserJobViews, String> {

    Optional<UserJobViews> findByJobAndUser(Job job, User user);
}