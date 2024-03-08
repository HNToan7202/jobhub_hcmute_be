package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.FriendService;


@RestController
@RequestMapping("/api/v1/friend")
@Tag(name = "Friend", description = "Friend API")
public class FriendController {
    @Autowired
    ResponseBuild responseBuild;
    @Autowired
    FriendService friendService;

    //Lấy danh sách bạn bè của curentUser
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping()
    public ResponseModel getFriends(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = friendService.getFriends(page, size);
        return responseBuild.build(actionResult);
    }

    @GetMapping("/{userId}")
    public ResponseModel getFriendsUser(@PathVariable("userId") String userId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = friendService.getFriendsUser(userId, page, size);
        return responseBuild.build(actionResult);
    }
    //sendFriendRequest
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/sendfriendrequest/{receiverId}")
    public ResponseModel sendFriendRequest(@PathVariable("receiverId") String receiverId) {
        ActionResult actionResult = friendService.sendFriendRequest(receiverId);
        return responseBuild.build(actionResult);
    }
    //lấy danh sách lời mời kết bạn
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/getfriendrequest")
    public ResponseModel getFriendRequest(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        ActionResult actionResult = friendService.getFriendRequest(page, size);
        return responseBuild.build(actionResult);
    }
    //acceptFriendRequest
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/acceptfriendrequest/{friendToAcceptId}")
    public ResponseModel acceptFriendRequest(@PathVariable("friendToAcceptId") String friendToAcceptId) {
        ActionResult actionResult = friendService.acceptFriendRequest(friendToAcceptId);
        return responseBuild.build(actionResult);
    }
    //cancelSendFriendRequest
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @GetMapping("/cancelsendfriendrequest/{senderId}")
    public ResponseModel cancelSendFriendRequest(@PathVariable("senderId") String senderId) {
        ActionResult actionResult = friendService.cancelSendFriendRequest(senderId);
        return responseBuild.build(actionResult);
    }
    //deleteFriend
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'EMPLOYER')")
    @DeleteMapping("/deletefriend/{friendId}")
    public ResponseModel deleteFriend(@PathVariable("friendId") String friendId) {
        ActionResult actionResult = friendService.deleteFriend(friendId);
        return responseBuild.build(actionResult);
    }

}
