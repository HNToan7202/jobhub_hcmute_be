package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.entity.Comment;
import vn.iotstar.jobhub_hcmute_be.entity.Like;
import vn.iotstar.jobhub_hcmute_be.entity.Post;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.CommentRepository;
import vn.iotstar.jobhub_hcmute_be.repository.LikeRepository;
import vn.iotstar.jobhub_hcmute_be.repository.PostRepository;
import vn.iotstar.jobhub_hcmute_be.repository.UserRepository;
import vn.iotstar.jobhub_hcmute_be.service.LikeService;
import vn.iotstar.jobhub_hcmute_be.utils.CurrentUserUtils;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    private void updatePostEmotions(Post post, PostEmotions emotions, Boolean isAdd){
        try{
            if(isAdd){
                switch (emotions){
                    case LIKE:
                        post.setTotalLike(post.getTotalLike() + 1);
                        break;
                    case LOVE:
                        post.setTotalLove(post.getTotalLove() + 1);
                        break;
                    case HAHA:
                        post.setTotalHaha(post.getTotalHaha() + 1);
                        break;
                    case WOW:
                        post.setTotalWow(post.getTotalWow() + 1);
                        break;
                    case SAD:
                        post.setTotalSad(post.getTotalSad() + 1);
                        break;
                    case ANGRY:
                        post.setTotalAngry(post.getTotalAngry() + 1);
                        break;
                }
                post.setTotalEmotions(post.getTotalEmotions() + 1);
            }
            else{
                switch (emotions){
                    case LIKE:
                        post.setTotalLike(post.getTotalLike() - 1);
                        break;
                    case LOVE:
                        post.setTotalLove(post.getTotalLove() - 1);
                        break;
                    case HAHA:
                        post.setTotalHaha(post.getTotalHaha() - 1);
                        break;
                    case WOW:
                        post.setTotalWow(post.getTotalWow() - 1);
                        break;
                    case SAD:
                        post.setTotalSad(post.getTotalSad() - 1);
                        break;
                    case ANGRY:
                        post.setTotalAngry(post.getTotalAngry() - 1);
                        break;
                }
                post.setTotalEmotions(post.getTotalEmotions() - 1);
            }
            postRepository.save(post);
        }
        catch (Exception e){
           System.out.println(e);
        }
    }


    @Override
    public ActionResult likePost(int postId, String emotionsName) {
        ActionResult actionResult = new ActionResult();
        try{
            PostEmotions emotions = PostEmotions.valueOf(emotionsName);
            Optional<Post> post = postRepository.findById(postId);
            if(!post.isPresent()){
                actionResult.setErrorCode(ErrorCodeEnum.POST_NOT_FOUND);
                return actionResult;
            }
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<Like> like = likeRepository.findByPostAndUser(post.get(), user.get());
            if(like.isPresent()){
                if(like.get().getEmotions().equals(emotions)){
                    likeRepository.delete(like.get());
                    updatePostEmotions(post.get(), emotions, false);
                    actionResult.setErrorCode(ErrorCodeEnum.OK);
                }
                else{
                    updatePostEmotions(post.get(),like.get().getEmotions(), false);
                    like.get().setEmotions(emotions);
                    likeRepository.save(like.get());
                    updatePostEmotions(post.get(), emotions, true);
                    actionResult.setErrorCode(ErrorCodeEnum.OK);
                }
            }
            else{
                Like newLike = new Like();
                newLike.setPost(post.get());
                newLike.setUser(user.get());
                newLike.setEmotions(emotions);
                likeRepository.save(newLike);
                updatePostEmotions(post.get(), emotions, true);
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            }
        }
        catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            System.out.println(e);
        }
        return actionResult;
    }
    private void updateCommentEmotions(Comment comment, PostEmotions emotions, Boolean isAdd){
        try{
            if(isAdd){
                switch (emotions){
                    case LIKE:
                        comment.setTotalLike(comment.getTotalLike() + 1);
                        break;
                    case LOVE:
                        comment.setTotalLove(comment.getTotalLove() + 1);
                        break;
                    case HAHA:
                        comment.setTotalHaha(comment.getTotalHaha() + 1);
                        break;
                    case WOW:
                        comment.setTotalWow(comment.getTotalWow() + 1);
                        break;
                    case SAD:
                        comment.setTotalSad(comment.getTotalSad() + 1);
                        break;
                    case ANGRY:
                        comment.setTotalAngry(comment.getTotalAngry() + 1);
                        break;
                }
                comment.setTotalEmotions(comment.getTotalEmotions() + 1);
            }
            else{
                switch (emotions){
                    case LIKE:
                        comment.setTotalLike(comment.getTotalLike() - 1);
                        break;
                    case LOVE:
                        comment.setTotalLove(comment.getTotalLove() - 1);
                        break;
                    case HAHA:
                        comment.setTotalHaha(comment.getTotalHaha() - 1);
                        break;
                    case WOW:
                        comment.setTotalWow(comment.getTotalWow() - 1);
                        break;
                    case SAD:
                        comment.setTotalSad(comment.getTotalSad() - 1);
                        break;
                    case ANGRY:
                        comment.setTotalAngry(comment.getTotalAngry() - 1);
                        break;
                }
                comment.setTotalEmotions(comment.getTotalEmotions() - 1);
            }
            commentRepository.save(comment);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public ActionResult likeComment(int commentId, String emotionsName) {
        ActionResult actionResult = new ActionResult();
        try{
            PostEmotions emotions = PostEmotions.valueOf(emotionsName);
            Optional<Comment> comment = commentRepository.findById(commentId);
            if(!comment.isPresent()){
                actionResult.setErrorCode(ErrorCodeEnum.COMMENT_NOT_FOUND);
                return actionResult;
            }
            Optional<User> user = userRepository.findById(CurrentUserUtils.getCurrentUserId());
            Optional<Like> like = likeRepository.findByCommentAndUser(comment.get(), user.get());
            if(like.isPresent()){
                if(like.get().getEmotions().equals(emotions)){
                    likeRepository.delete(like.get());
                    updateCommentEmotions(comment.get(), emotions, false);
                    actionResult.setErrorCode(ErrorCodeEnum.OK);
                }
                else{
                    updateCommentEmotions(comment.get(),like.get().getEmotions(), false);
                    like.get().setEmotions(emotions);
                    likeRepository.save(like.get());
                    updateCommentEmotions(comment.get(), emotions, true);
                    actionResult.setErrorCode(ErrorCodeEnum.OK);
                }
            }
            else{
                Like newLike = new Like();
                newLike.setComment(comment.get());
                newLike.setUser(user.get());
                newLike.setEmotions(emotions);
                likeRepository.save(newLike);
                updateCommentEmotions(comment.get(), emotions, true);
                actionResult.setErrorCode(ErrorCodeEnum.OK);
            }
        }
        catch (Exception e){
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
            System.out.println(e);
        }
        return actionResult;
    }

    public <S extends Like> S save(S entity) {
        return likeRepository.save(entity);
    }

    public Optional<Like> findById(Integer integer) {
        return likeRepository.findById(integer);
    }

    public boolean existsById(Integer integer) {
        return likeRepository.existsById(integer);
    }

    public long count() {
        return likeRepository.count();
    }

    public void delete(Like entity) {
        likeRepository.delete(entity);
    }
}
