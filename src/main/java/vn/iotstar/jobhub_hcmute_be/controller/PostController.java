package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.dto.CreatePostRequestDTO;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.PostService;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "Post API")
public class PostController {
    @Autowired
    ResponseBuild responseBuild;

    @Autowired
    PostService postService;

    @GetMapping
    public ResponseModel getListNewPosts(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(defaultValue = "0") Integer type) {
        ActionResult actionResult = postService.getListNewPosts(page, size, type);
        return responseBuild.build(actionResult);
    }

    @GetMapping("/user/{userId}")
    public ResponseModel getPostsByUserId(@PathVariable("userId") String userId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = postService.getPostsByUserId(userId, page, size);
        return responseBuild.build(actionResult);
    }

    @GetMapping("/attach/{postId}")
    public ResponseModel getImgagesAndVideoAndFile(@PathVariable("postId") Integer postId,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = postService.getImgagesAndVideoAndFile(postId, page, size);
        return responseBuild.build(actionResult);
    }

    @PostMapping("/create")
    public ResponseModel createPost(@ModelAttribute @Valid CreatePostRequestDTO createPostRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ActionResult actionResult = new ActionResult();
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            ResponseModel responseModel = responseBuild.build(actionResult);
            responseModel.setMessage(bindingResult.getFieldError().getDefaultMessage());
            return responseModel;
        }
        ActionResult actionResult = postService.createUserPost(createPostRequestDTO);
        return responseBuild.build(actionResult);
    }
    @PutMapping("/update/{postId}")
    public ResponseModel updatePost(@PathVariable("postId") Integer postId, @ModelAttribute @Valid CreatePostRequestDTO RequestDTO) {
        ActionResult actionResult = postService.updatePost( RequestDTO,postId);
        return responseBuild.build(actionResult);
    }
    @DeleteMapping("/delete/{postId}")
    public ResponseModel deletePost(@PathVariable("postId") Integer postId) {
        ActionResult actionResult = postService.deletePost(postId);
        return responseBuild.build(actionResult);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/unlockPost/{postId}")
    public ResponseModel unlockPost(@PathVariable("postId") Integer postId) {
        ActionResult actionResult = postService.unlockPost(postId);
        return responseBuild.build(actionResult);
    }


}
