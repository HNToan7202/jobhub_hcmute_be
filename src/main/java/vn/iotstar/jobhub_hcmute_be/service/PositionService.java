package vn.iotstar.jobhub_hcmute_be.service;

import org.springframework.data.domain.Pageable;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.util.Optional;

public interface PositionService {
    Optional<Position> findByName(String positionName);

    ActionResult getAllPosition();

    ActionResult topPopularJobByPosition();
}
