package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.CreateCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.ReplyCommentPostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.dto.UpdateCommentRequestDTO;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.CommentService;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment", description = "Comment API")
public class CommentController {
    @Autowired
    ResponseBuild responseBuild;
    @Autowired
    CommentService commentService;
    @GetMapping("/getCommentToPost/{postId}")
    public ResponseModel getCommentToPost(@PathVariable("postId") int postId,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return responseBuild.build(commentService.getCommentToPost(page,size,postId));
    }
    @GetMapping("/getCommentToComment/{commentId}")
    public ResponseModel getCommentToComment(@PathVariable("commentId") int commentId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return responseBuild.build(commentService.getCommentReply(page,size,commentId));
    }
    @PostMapping("/createComment")
    public ResponseModel createComment(@Valid @ModelAttribute CreateCommentPostRequestDTO requestDTO) {
        return responseBuild.build(commentService.createCommentPost(requestDTO));
    }
    @PostMapping("/createCommentReply")
    public ResponseModel createCommentReply(@Valid @ModelAttribute ReplyCommentPostRequestDTO requestDTO) {
        return responseBuild.build(commentService.replyCommentPost(requestDTO));
    }
    @PutMapping("/updateComment/{commentId}")
    public ResponseModel updateComment(@PathVariable("commentId") int commentId,
                                      @Valid @ModelAttribute UpdateCommentRequestDTO requestDTO) {
        return responseBuild.build(commentService.updateCommentPost(requestDTO,commentId));
    }
    @DeleteMapping("/deleteComment/{commentId}")
    public ResponseModel deleteComment(@PathVariable("commentId") int commentId) {
        return responseBuild.build(commentService.deleteCommentPost(commentId));
    }

}
