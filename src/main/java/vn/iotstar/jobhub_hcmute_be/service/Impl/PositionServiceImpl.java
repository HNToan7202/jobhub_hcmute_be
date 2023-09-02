package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.repository.PositionRepository;
import vn.iotstar.jobhub_hcmute_be.service.PositionService;

import java.util.Optional;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    PositionRepository positionRepository;

    @Override
    public Optional<Position> findByName(String positionName) {
        return positionRepository.findByName(positionName);
    }
}
