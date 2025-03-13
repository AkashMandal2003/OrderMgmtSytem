package com.jocata.oms.config;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
public class EhCacheConfig {

    @Bean
    public CacheManager ehCacheManager() {

        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        CacheManager cacheManager =  provider.getCacheManager();
        MutableConfiguration<Object, Object> cacheConfig = new MutableConfiguration<>()
                .setStoreByValue(false)
                .setStatisticsEnabled(true);
        cacheManager.createCache("products", cacheConfig);
        return cacheManager;

    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager(CacheManager cm) {
        return new JCacheCacheManager(cm);
    }

}

