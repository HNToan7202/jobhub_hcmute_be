package vn.iotstar.jobhub_hcmute_be.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.PositionService;

@RestController
@RequestMapping("/api/v1/positions")
@Validated
@Tag(name = "Position", description = "Position API")
public class PositionController {

    final
    PositionService positionService;

    final
    ResponseBuild responseBuild;

    public PositionController(PositionService positionService, ResponseBuild responseBuild) {
        this.positionService = positionService;
        this.responseBuild = responseBuild;
    }

    @GetMapping("")
    public ResponseModel getAllSKills() {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = positionService.getAllPosition();
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
        }
        return responseBuild.build(actionResult);
    }
}
