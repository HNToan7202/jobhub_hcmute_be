package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Pageable;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface EventService {
    ActionResult getAllEvent(Pageable pageable);
}
