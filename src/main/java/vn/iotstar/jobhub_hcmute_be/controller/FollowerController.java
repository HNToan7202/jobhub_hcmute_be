package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.FollowerService;

@RestController
@RequestMapping("/api/v1/follower")
@Tag(name = "Follower", description = "Follower API")
public class FollowerController {
    @Autowired
    ResponseBuild responseBuild;
    @Autowired
    FollowerService followerService;

    //Lấy danh sách người theo dõi của curentUser
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping()
    public ResponseModel getFollowerList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = followerService.getFollowerList(page, size);
        return responseBuild.build(actionResult);
    }

    //Lấy danh sách người theo dõi của user
    @GetMapping("/{userId}")
    public ResponseModel getFollowerListUser(@PathVariable("userId") String userId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = followerService.getFollowerList(page, size, userId);
        return responseBuild.build(actionResult);
    }
    //sendFollowRequest
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/sendfollowrequest/{receiverId}")
    public ResponseModel sendFollowRequest(@PathVariable("receiverId") String receiverId) {
        ActionResult actionResult = followerService.sendFollowerRequest(receiverId);
        return responseBuild.build(actionResult);
    }
    //cancelFollowRequest
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/cancelfollowrequest/{userId}")
    public ResponseModel cancelFollowRequest(@PathVariable("userId") String userId) {
        ActionResult actionResult = followerService.candelFollowerRequest(userId);
        return responseBuild.build(actionResult);
    }
}
