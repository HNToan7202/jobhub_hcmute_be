package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Follower;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Optional;

@Hidden
public interface FollowerRepository extends JpaRepository<Follower, String> {
    Optional<Follower> findByFollowerAndFollowee(User user, User user1);

    Page<Follower> findByFollowee(User user, PageRequest of);
}