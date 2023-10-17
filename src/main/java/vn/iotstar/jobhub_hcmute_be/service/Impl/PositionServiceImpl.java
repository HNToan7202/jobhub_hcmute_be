package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.PositionRepository;
import vn.iotstar.jobhub_hcmute_be.service.PositionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    @Autowired
    PositionRepository positionRepository;

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public Optional<Position> findByName(String positionName) {
        return positionRepository.findByName(positionName);
    }

    @Override
    public ActionResult getAllPosition(){
        ActionResult actionResult = new ActionResult();
        List<Position> positionList = findAll();
        if(positionList == null)
        {
            positionList = new ArrayList<>();
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            return actionResult;
        }

        actionResult.setErrorCode(ErrorCodeEnum.GET_POSITION_SUCCESS);
        actionResult.setData(positionList);
        return actionResult;
    }
}
