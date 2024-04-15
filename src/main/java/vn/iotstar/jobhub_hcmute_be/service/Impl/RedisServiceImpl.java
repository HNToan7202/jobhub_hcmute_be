package vn.iotstar.jobhub_hcmute_be.service.Impl;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.iotstar.jobhub_hcmute_be.entity.Permission;
import vn.iotstar.jobhub_hcmute_be.entity.User;
import vn.iotstar.jobhub_hcmute_be.model.SessionModel;
import vn.iotstar.jobhub_hcmute_be.security.UserDetail;
import vn.iotstar.jobhub_hcmute_be.service.RedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Service
public class RedisServiceImpl  implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;
    private static final String REDIS_SESSION_PREFIX = "session:";
    private static final long SESSION_EXPIRATION_DAY = 1;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void saveSession(String token, UserDetail userDetail, Object password, Object username) {
        String key = REDIS_SESSION_PREFIX + token;
        SessionModel sessionModel = new SessionModel();
        sessionModel.setUsername(username);
        sessionModel.setPassword(password);
        sessionModel.setNameRole(userDetail.getUser().getRole().getName());
        List<String> permissionsName = new ArrayList<>();
        for (Permission permission : userDetail.getUser().getRole().getPermissions()) {
            permissionsName.add(permission.getName());
        }
        sessionModel.setPermissionsName(permissionsName);
        redisTemplate.opsForValue().set(key, sessionModel, SESSION_EXPIRATION_DAY, TimeUnit.DAYS);
    }
    public Set<String> getKeysWithPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }
    public SessionModel getSession(String token) {
        String key = token;
        return (SessionModel) redisTemplate.opsForValue().get(key);
    }

    public void deleteSession(String id) {
        String key = REDIS_SESSION_PREFIX + id;
        redisTemplate.delete(key);
    }

    //redisServiceImpl.set("user:1:name", "John Doe");
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    //redisServiceImpl.setTimeToLive("user:1:name", 7); // Expire the key after 7 days
    @Override
    public void setTimeToLive(String key, long timeToLive) {
        redisTemplate.expire(key, timeToLive, TimeUnit.DAYS);

    }
    //redisServiceImpl.hashSet("user:1", "name", "John Doe");
    @Override
    public void hashSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }
    //boolean exists = redisServiceImpl.hashExists("user:1", "name");
    @Override
    public boolean hashExists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }
    //boolean exists = redisServiceImpl.exists("user:1:name");

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    //Object value = redisServiceImpl.get("user:1:name");
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    //Map<String, Object> fields = redisServiceImpl.getFields("user:1"); // Get all fields of the key
    @Override
    public Map<String, Object> getFields(String key) {
        return hashOperations.entries(key);
    }
    //Object value = redisServiceImpl.hashGet("user:1", "name");
    @Override
    public Object hashGet(String key, String field) {
        return hashOperations.get(key, field);
    }
    //List<Object> values = redisServiceImpl.hashGetByFieldPrefix("user:1", "na");
    @Override
    public List<Object> hashGetByFieldPrefix(String key, String fieldPrefix) {
        List<Object> objects = new ArrayList<>();
        Map<String,Object> hashEntries = hashOperations.entries(key);
        for (Map.Entry<String, Object> entry : hashEntries.entrySet()) {
            if (entry.getKey().startsWith(fieldPrefix)) {//bắt đầu bằng fieldPrefix
                objects.add(entry.getValue());
            }
        }
        return objects;
    }
    //Set<String> fields = redisServiceImpl.getFieldPrefix("user:1");
    @Override
    public Set<String> getFieldPrefix(String key) {
        return hashOperations.entries(key).keySet();
    }
    //redisServiceImpl.delete("user:1");
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);

    }
    //redisServiceImpl.delete("user:1", "name");
    @Override
    public void delete(String key, String field) {
        hashOperations.delete(key, field);

    }
    //List<String> fieldsToDelete = Arrays.asList("name", "age", "email");
    //redisServiceImpl.delete("user:1", fieldsToDelete);
    @Override
    public void delete(String key, List<String> fields) {
        hashOperations.delete(key, fields.toArray());

    }
}
