package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.dto.SendInvoiceRequest;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface MailService {

    ActionResult sendInvoice(SendInvoiceRequest request);
}
