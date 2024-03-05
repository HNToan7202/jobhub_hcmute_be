package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.dto.CreateCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.GetCommentModel;
import vn.iotstar.jobhub_hcmute_be.dto.ReplyCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.UpdateCommentRequestDTO;
import vn.iotstar.jobhub_hcmute_be.entity.*;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.PageModel;
import vn.iotstar.jobhub_hcmute_be.repository.*;
import vn.iotstar.jobhub_hcmute_be.service.CloudinaryService;
import vn.iotstar.jobhub_hcmute_be.service.CommentService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EmployerRepository employerRepository;

    private PostEmotions checkIfLikeAndGetEmotions(User user, Comment comment) {
        Optional<Like> like = likeRepository.findByUserAndComment(user, comment);
        if (like.isPresent()) {
            return like.get().getEmotions();
        }
        return null;
    }

    private GetCommentModel transform(Comment comment) {
        String userId = comment.getUser().getUserId();
        Optional<User> user = userRepository.findById(userId);
        Optional<User> userCurrent = userRepository.findById(CurrentUserUtils.getCurrentUserId());
        Optional<Student> student = studentRepository.findById(userId);
        Optional<Employer> employer = employerRepository.findById(userId);
        String name = student.isPresent() ? student.get().getFullName() : employer.isPresent() ? employer.get().getCompanyName() : null;
        String avatar = student.isPresent() ? student.get().getAvatar() : employer.isPresent() ? employer.get().getLogo() : null;
        PostEmotions emotions = checkIfLikeAndGetEmotions(userCurrent.get(), comment);
        return GetCommentModel.transform(comment, name, avatar, emotions);
    }

    private List<GetCommentModel> transformList(List<Comment> comments) {
        return comments.stream().map(this::transform).toList();
    }

    @Override
    public ActionResult getCommentToPost(Integer page, Integer size, Integer postId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Post> post = postRepository.findById(postId);
            if (!post.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            Page<Comment> comments = commentRepository.findByPostAndIsReplyOrderByCreatedAtDesc(post.get(), false, PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(comments, transformList(comments.getContent())));
            actionResult.setErrorCode(ErrorCodeEnum.OK);

        } catch (Exception e) {
            System.out.println(e);
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult getCommentReply(Integer page, Integer size, Integer commentId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Comment> comment = commentRepository.findById(commentId);
            if(!comment.isPresent()){
                actionResult.setErrorCode(ErrorCodeEnum.COMMENT_NOT_FOUND);
                return actionResult;
            }
            Page<Comment> comments = commentRepository.findByCommentReplyAndIsReplyOrderByCreatedAtDesc(comment.get(), true, PageRequest.of(page - 1, size));
            actionResult.setData(PageModel.transform(comments, transformList(comments.getContent())));
            actionResult.setErrorCode(ErrorCodeEnum.OK);

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult createCommentPost(CreateCommentPostRequestDTO requestDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<Post> post = postRepository.findById(requestDTO.getPostId());
            if (!post.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            Comment comment = new Comment();
            comment.setContent(requestDTO.getContent());
            comment.setPost(post.get());
            comment.setUser(user.get());
            comment.setIsReply(false);
            boolean checkPhoto = requestDTO.getPhotos() != null && requestDTO.getPhotos().getContentType() != null;
            comment.setPhotos(checkPhoto ? cloudinaryService.uploadImage(requestDTO.getPhotos()) : null);
            commentRepository.save(comment);
            post.get().setTotalComment(post.get().getTotalComment() + 1);
            postRepository.save(post.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult updateCommentPost(UpdateCommentRequestDTO requestDTO, Integer commentId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<Comment> comment = commentRepository.findById(commentId);
            if (!CurrentUserUtils.getCurrentUserId().equals(comment.get().getUser().getUserId())) {
                actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                return actionResult;
            }
            if (!comment.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COMMENT_NOT_FOUND);
                return actionResult;
            }
            comment.get().setContent(requestDTO.getContent());
            boolean checkPhoto = requestDTO.getPhotos() != null && requestDTO.getPhotos().getContentType() != null;
            comment.get().setPhotos(checkPhoto ? cloudinaryService.uploadImage(requestDTO.getPhotos()) : null);
            commentRepository.save(comment.get());
            actionResult.setErrorCode(ErrorCodeEnum.OK);

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult deleteCommentPost(Integer commentId) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<Comment> comment = commentRepository.findById(commentId);
            if (!CurrentUserUtils.getCurrentUserId().equals(comment.get().getUser().getUserId())) {
                if (!CurrentUserUtils.getCurrentUserId().equals(comment.get().getPost().getUser().getUserId())) {
                    actionResult.setErrorCode(ErrorCodeEnum.USER_NOT_PERMISSION);
                    return actionResult;
                }
            }
            if (!comment.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COMMENT_NOT_FOUND);
                return actionResult;
            }
            commentRepository.delete(comment.get());
            Post post = comment.get().getPost();
            post.setTotalComment(post.getTotalComment() - 1);
            postRepository.save(post);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

    @Override
    public ActionResult replyCommentPost(ReplyCommentPostRequestDTO requestDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<Comment> commentReply = commentRepository.findById(requestDTO.getCommentId());
            Post post = commentReply.get().getPost();
            if (!commentReply.isPresent()) {
                actionResult.setErrorCode(ErrorCodeEnum.COMMENT_NOT_FOUND);
                return actionResult;
            }
            Comment comment = new Comment();
            comment.setContent(requestDTO.getContent());
            comment.setPost(post);
            comment.setUser(user.get());
            boolean checkPhoto = requestDTO.getPhotos() != null && requestDTO.getPhotos().getContentType() != null;
            comment.setPhotos(checkPhoto ? cloudinaryService.uploadImage(requestDTO.getPhotos()) : null);
            comment.setCommentReply(commentReply.get());
            comment.setIsReply(true);
            commentRepository.save(comment);
            post.setTotalComment(post.getTotalComment() + 1);
            postRepository.save(post);
            actionResult.setErrorCode(ErrorCodeEnum.OK);

        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }

}
