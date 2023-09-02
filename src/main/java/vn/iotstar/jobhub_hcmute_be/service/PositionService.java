package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.entity.Position;

import java.util.Optional;

public interface PositionService {
    Optional<Position> findByName(String positionName);
}
