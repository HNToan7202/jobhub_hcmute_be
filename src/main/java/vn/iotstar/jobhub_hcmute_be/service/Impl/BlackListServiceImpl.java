package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.dto.BlackListDto;
import vn.iotstar.jobhub_hcmute_be.entity.BlackList;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.BlackListRepository;
import vn.iotstar.jobhub_hcmute_be.service.BlackListService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class BlackListServiceImpl implements BlackListService {
    @Autowired
    BlackListRepository blackListRepository;

    @Override
    public void deleteAllInBatch(Iterable<BlackList> entities) {
        blackListRepository.deleteAllInBatch(entities);
    }

    @Override
    public <S extends BlackList> List<S> findAll(Example<S> example) {
        return blackListRepository.findAll(example);
    }

    @Override
    public <S extends BlackList> List<S> findAll(Example<S> example, Sort sort) {
        return blackListRepository.findAll(example, sort);
    }

    @Override
    public <S extends BlackList> S save(S entity) {
        return blackListRepository.save(entity);
    }

    @Override
    public void deleteById(String s) {
        blackListRepository.deleteById(s);
    }

    @Override
    public void delete(BlackList entity) {
        blackListRepository.delete(entity);
    }

    @Override
    public List<BlackList> findAll(Sort sort) {
        return blackListRepository.findAll(sort);
    }

    @Override
    public Page<BlackList> findAll(Pageable pageable) {
        return blackListRepository.findAll(pageable);
    }

    @Override
    public <S extends BlackList> Optional<S> findOne(Example<S> example) {
        return blackListRepository.findOne(example);
    }

    @Override
    public <S extends BlackList> Page<S> findAll(Example<S> example, Pageable pageable) {
        return blackListRepository.findAll(example, pageable);
    }

    @Override
    public <S extends BlackList> long count(Example<S> example) {
        return blackListRepository.count(example);
    }

    @Override
    public <S extends BlackList> boolean exists(Example<S> example) {
        return blackListRepository.exists(example);
    }

    @Override
    public <S extends BlackList, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return blackListRepository.findBy(example, queryFunction);
    }

    public ActionResult addBlackList(String userId, BlackListDto blackListDto){
        ActionResult actionResult = new ActionResult();
        try {

        }catch (Exception e) {
            actionResult.setErrorCode(ErrorCodeEnum.FAIL_ADD_BLACKLIST);
            return actionResult;
        }

        return actionResult;
    }
}
