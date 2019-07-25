package main;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;

public class LoadCache {
    public static String s;
    private static LoadingCache<Integer, Integer> cache;
    static {
        cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(30, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer id) throws Exception {
                        return getdata(id);

                    }
                });
        cache.invalidateAll();

    }

    public static LoadingCache<Integer, Integer> getLoadingCache() {
        return cache;
    }

    public static int getdata(Integer id) {
        return id*id;
    }
}