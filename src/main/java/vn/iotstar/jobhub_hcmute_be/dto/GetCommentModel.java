package vn.iotstar.jobhub_hcmute_be.dto;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.entity.Comment;

import java.util.Date;

@Data
@Builder
public class GetCommentModel {
    private int commentId;
    private String content;
    private String photos;
    private int postId;
    private String userId;
    private int commentReplyId;
    private String name;
    private String avatar;
    private int totalLike;
    private int totalLove;
    private int totalHaha;
    private int totalWow;
    private int totalSad;
    private int totalAngry;
    private Boolean isLiked;
    private Boolean isLoved;
    private Boolean isHaha;
    private Boolean isWow;
    private Boolean isSad;
    private Boolean isAngry;
    private Boolean isReply;
    private int totalEmotions;
    private int totalReply;
    private Date createdAt;
    private Date updatedAt;
    public static GetCommentModel transform(Comment comment, String name, String avatar, PostEmotions emotions) {
        return GetCommentModel.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .photos(comment.getPhotos())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .commentReplyId(comment.getCommentReply() != null ? comment.getCommentReply().getCommentId() : null)
                .name(name)
                .avatar(avatar)
                .totalLike(comment.getTotalLike())
                .totalLove(comment.getTotalLove())
                .totalHaha(comment.getTotalHaha())
                .totalWow(comment.getTotalWow())
                .totalSad(comment.getTotalSad())
                .totalAngry(comment.getTotalAngry())
                .isLiked(PostEmotions.checkEmotion(emotions, PostEmotions.LIKE))
                .isLoved(PostEmotions.checkEmotion(emotions, PostEmotions.LOVE))
                .isHaha(PostEmotions.checkEmotion(emotions, PostEmotions.HAHA))
                .isWow(PostEmotions.checkEmotion(emotions, PostEmotions.WOW))
                .isSad(PostEmotions.checkEmotion(emotions, PostEmotions.SAD))
                .isAngry(PostEmotions.checkEmotion(emotions, PostEmotions.ANGRY))
                .isReply(comment.getIsReply())
                .totalEmotions(comment.getTotalEmotions())
                .totalReply(comment.getSubComments().size())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
