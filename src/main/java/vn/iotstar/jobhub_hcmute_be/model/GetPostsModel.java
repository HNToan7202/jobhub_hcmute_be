package vn.iotstar.jobhub_hcmute_be.model;

import lombok.Builder;
import lombok.Data;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.entity.Photo;
import vn.iotstar.jobhub_hcmute_be.entity.Post;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class GetPostsModel {
    private int postId;
    private List<Photo> photos;
    private int havePictures;
    private String files;
    private int haveFiles;
    private String location;
    private String content;
    private String privacyLevel;
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
    private int totalEmotions;
    private int totalComment;
    private Date createdAt;
    private Date updatedAt;
    private String userId;
    private String name;
    private String avatar;

    private static List<Photo> setPhoto(List<Photo> photos) {
        if (photos.size() > 0){
            if (photos.size() > 2) {
                return photos.subList(0, 2);
            }
            return photos;
        }
        return null;
    }

    public static GetPostsModel transform(Post post, String name, String avatar, PostEmotions emotions) {
        return GetPostsModel.builder()
                .postId(post.getPostId())
                .photos(setPhoto(post.getPhotos()))
                .havePictures(post.getHavePictures())
                .files(post.getFiles())
                .haveFiles(post.getHaveFiles())
                .location(post.getLocation())
                .content(post.getContent())
                .privacyLevel(post.getPrivacyLevel().toString())
                .totalLike(post.getTotalLike())
                .totalLove(post.getTotalLove())
                .totalHaha(post.getTotalHaha())
                .totalWow(post.getTotalWow())
                .totalSad(post.getTotalSad())
                .totalAngry(post.getTotalAngry())
                .isLiked(PostEmotions.checkEmotion(emotions, PostEmotions.LIKE))
                .isLoved(PostEmotions.checkEmotion(emotions, PostEmotions.LOVE))
                .isHaha(PostEmotions.checkEmotion(emotions, PostEmotions.HAHA))
                .isWow(PostEmotions.checkEmotion(emotions, PostEmotions.WOW))
                .isSad(PostEmotions.checkEmotion(emotions, PostEmotions.SAD))
                .isAngry(PostEmotions.checkEmotion(emotions, PostEmotions.ANGRY))
                .totalEmotions(post.getTotalEmotions())
                .totalComment(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .userId(post.getUser().getUserId())
                .name(name)
                .avatar(avatar)
                .build();
    }

}
