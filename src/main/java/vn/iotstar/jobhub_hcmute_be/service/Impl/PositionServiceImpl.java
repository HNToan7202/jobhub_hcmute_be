package vn.iotstar.jobhub_hcmute_be.service.Impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.PositionDTO;
import vn.iotstar.jobhub_hcmute_be.entity.Position;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.PositionRepository;
import vn.iotstar.jobhub_hcmute_be.service.PositionService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    final
    PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public Optional<Position> findByName(String positionName) {
        return positionRepository.findByName(positionName);
    }

    @Override
    public ActionResult getAllPosition() {
        ActionResult actionResult = new ActionResult();
        List<Position> positionList = positionRepository.findTop9ByOrderByAmountPositionDesc();
        if (positionList == null) {
            actionResult.setErrorCode(ErrorCodeEnum.NOT_FOUND);
            return actionResult;
        }
        actionResult.setErrorCode(ErrorCodeEnum.GET_POSITION_SUCCESS);
        actionResult.setData(positionList);
        return actionResult;
    }

    @Override
    public ActionResult createPosition(PositionDTO positionDTO) {
        ActionResult actionResult = new ActionResult();
        try {
            Position position = new Position();
            position.setName(positionDTO.getName());
            position.setIcon(positionDTO.getIcon());
            position.setAmountPosition(positionDTO.getAmountPosition());
            positionRepository.save(position);
            actionResult.setErrorCode(ErrorCodeEnum.CREATE_POSITION_SUCCESS);
            actionResult.setData(position);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;

    }

    @Override
    public ActionResult topPopularJobByPosition() {
        ActionResult actionResult = new ActionResult();
        try {
            List<Position> listPos = positionRepository.findTop9ByOrderByAmountPositionDesc();
            actionResult.setErrorCode(ErrorCodeEnum.OK);
            actionResult.setData(listPos);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            actionResult.setData(e.getMessage());
        }
        return actionResult;
    }
}
