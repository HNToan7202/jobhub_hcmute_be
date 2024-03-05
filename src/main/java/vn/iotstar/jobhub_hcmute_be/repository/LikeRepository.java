package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Comment;
import vn.iotstar.jobhub_hcmute_be.entity.Like;
import vn.iotstar.jobhub_hcmute_be.entity.Post;
import vn.iotstar.jobhub_hcmute_be.entity.User;

import java.util.Optional;

@Hidden

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByPostAndUser(Post post, User user);

    Optional<Like> findByUserAndComment(User user, Comment comment);

    Optional<Like> findByCommentAndUser(Comment comment, User user);
}