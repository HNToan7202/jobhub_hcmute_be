package vn.iotstar.jobhub_hcmute_be.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;


public class CustomCacheManager extends SimpleCacheManager {

    @Override
    public Cache getCache(@NotNull String name) {
        return new CustomCache(super.getCache(name));
    }
}
