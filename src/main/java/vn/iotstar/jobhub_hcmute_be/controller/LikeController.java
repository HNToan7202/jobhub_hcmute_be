package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.constant.PostEmotions;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.LikeService;
import vn.iotstar.jobhub_hcmute_be.service.PostService;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
@RequestMapping("/api/v1/like")
@Tag(name = "Like", description = "Like API")
public class
LikeController {
    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    LikeService likeService;
    @GetMapping("/likeOrUnlikePost/{postId}")
    public ResponseModel likeOrUnlikePost(@PathVariable("postId") int postId,
                                          @RequestParam(defaultValue = "LIKE") String emotions) {
        return responseBuild.build(likeService.likePost(postId, emotions));

    }
    @GetMapping("/likeOrUnlikeComment/{commentId}")
    public ResponseModel likeOrUnlikeComment(@PathVariable("commentId") int commentId,
                                            @RequestParam(defaultValue = "LIKE") String emotions) {
        return responseBuild.build(likeService.likeComment(commentId, emotions));
    }


}
