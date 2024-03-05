package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface LikeService {
    ActionResult likePost(int postId, String emotions);

    ActionResult likeComment(int commentId, String emotionsName);
}
