package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.dto.CreatePostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Post;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.util.Optional;

public interface PostService {

    ActionResult getPostsByUserId(String userId, Integer page, Integer size);

    ActionResult getListNewPosts(Integer page, Integer size, int status);

    ActionResult getImgagesAndVideoAndFile(Integer postId, Integer page, Integer size);

    ActionResult createUserPost (CreatePostRequestDTO createPostRequestDTO);

    ActionResult updatePost(CreatePostRequestDTO updatePostDTO, Integer postId);

    ActionResult deletePost(Integer postId);

    ActionResult unlockPost(Integer postId);

    <S extends Post> S save(S entity);

    Optional<Post> findById(Integer integer);

    boolean existsById(Integer integer);

    long count();

    void deleteById(Integer integer);
}
