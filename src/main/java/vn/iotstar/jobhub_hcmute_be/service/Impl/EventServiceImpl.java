package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Event;
import vn.iotstar.jobhub_hcmute_be.enums.ErrorCodeEnum;
import vn.iotstar.jobhub_hcmute_be.model.ActionResult;
import vn.iotstar.jobhub_hcmute_be.repository.EventRepository;
import vn.iotstar.jobhub_hcmute_be.service.EventService;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Override
    public ActionResult getAllEvent(Pageable pageable){
        ActionResult actionResult = new ActionResult();
        Page<Event> pageEvent = eventRepository.findAll(pageable);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("events", pageEvent.getContent());
        map.put("admin", pageEvent.getContent().get(0).getAdmin().getFullName());
        actionResult.setData(map);
        actionResult.setErrorCode(ErrorCodeEnum.OK);
        return actionResult;
    }
}
