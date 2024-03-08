package vn.iotstar.jobhub_hcmute_be.service;

import jakarta.transaction.Transactional;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface FriendService {
    ActionResult getFriends(Integer page, Integer size);

    ActionResult getFriendsUser(String userId, Integer page, Integer size);

    ActionResult getFriendRequest(Integer page, Integer size);

    ActionResult sendFriendRequest(String receiverId);
    ActionResult acceptFriendRequest(String friendToAcceptId);

    ActionResult cancelSendFriendRequest(String receiverId);

    //Hủy kết bạn
    ActionResult deleteFriend(String friendId);
}
