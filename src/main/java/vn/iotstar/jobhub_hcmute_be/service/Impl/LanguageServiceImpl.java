package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Language;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.LanguageRepository;
import vn.iotstar.jobhub_hcmute_be.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService {
    private LanguageRepository languageRepository;
    ActionResult actionResult;

    public LanguageServiceImpl(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public ActionResult addLanguage(String name, String style) {
        actionResult = new ActionResult();
        try {
            Language language = new Language();
            language.setLanguageName(name);
            language.setActive(true);
            language.setStatus(true);
            language.setType(style);
            languageRepository.save(language);
            actionResult.setErrorCode(ErrorCodeEnum.OK);
        } catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return actionResult;
    }
}
