package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.jobhub_hcmute_be.entity.Post;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.PrivacyLevel;

import java.util.List;
import java.util.Set;

@Hidden
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByUserInOrderByCreatedAtDesc(Set<User> allIds, Pageable pageable);

    Page<Post> findByUserInAndPrivacyLevelInOrderByCreatedAtDesc(Set<User> allFriendsUsers, List<PrivacyLevel> list,  Pageable pageable);

    Page<Post> findByUserAndPrivacyLevelInOrderByCreatedAtDesc(User user, List<PrivacyLevel> list,  Pageable pageable);
}