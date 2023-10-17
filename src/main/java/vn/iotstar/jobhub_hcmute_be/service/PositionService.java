package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

import java.util.Optional;

public interface PositionService {
    Optional<Position> findByName(String positionName);

    ActionResult getAllPosition();
}
