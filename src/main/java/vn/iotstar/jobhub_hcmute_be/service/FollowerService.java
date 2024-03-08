package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface FollowerService {
    ActionResult getFollowerList(Integer page, Integer size);

    ActionResult getFollowerList(Integer page, Integer size, String userId);

    ActionResult sendFollowerRequest(String receiverId);

    ActionResult candelFollowerRequest(String userId);
}
