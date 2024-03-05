package vn.iotstar.jobhub_hcmute_be.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.jobhub_hcmute_be.entity.Comment;
import vn.iotstar.jobhub_hcmute_be.entity.Post;

@Hidden
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPostAndIsReplyOrderByCreatedAtDesc(Post post, boolean b, Pageable pageable);

    Page<Comment> findByCommentReplyAndIsReplyOrderByCreatedAtDesc(Comment comment, boolean b, Pageable pageable);
}