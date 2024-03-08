package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.dto.CreateCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.ReplyCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.UpdateCommentRequestDTO;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface CommentService {
    ActionResult getCommentToPost(Integer page, Integer size, Integer postId);

    ActionResult getCommentReply(Integer page, Integer size, Integer commentId);

    ActionResult createCommentPost(CreateCommentPostRequestDTO requestDTO);

    ActionResult updateCommentPost(UpdateCommentRequestDTO requestDTO, Integer commentId);

    ActionResult deleteCommentPost(Integer commentId);

    ActionResult replyCommentPost(ReplyCommentPostRequestDTO requestDTO);
}
