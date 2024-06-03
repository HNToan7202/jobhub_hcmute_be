package vn.iotstar.jobhub_hcmute_be.service;

import vn.iotstar.jobhub_hcmute_be.model.ActionResult;

public interface LanguageService {
    ActionResult addLanguage(String name, String style);
}
