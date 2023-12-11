package vn.iotstar.jobhub_hcmute_be.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.jobhub_hcmute_be.dto.SendInvoiceRequest;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.model.ResponseBuild;
import vn.iotstar.jobhub_hcmute_be.model.ResponseModel;
import vn.iotstar.jobhub_hcmute_be.service.MailService;

@RestController
@RequestMapping("/api/v1/mail")
@Validated
@Tag(name = "Mail", description = "Mail API")
public class MailController {

    @Autowired
    MailService mailService;

    @Autowired
    ResponseBuild responseBuild;

    @PostMapping("/sendInvoice")
    public ResponseModel sendInvoice(@RequestBody SendInvoiceRequest invoiceRequest) {
        ActionResult actionResult = new ActionResult();
        try {
            actionResult = mailService.sendInvoice(invoiceRequest);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.BAD_REQUEST);
            System.err.println(e.getMessage());
        }
        return responseBuild.build(actionResult);
    }

}
