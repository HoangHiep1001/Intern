package main;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.google.common.cache.LoadingCache;

public class App {
    static LoadingCache<Integer, Integer> cache;
    public void init() {
        get("prime/getData", (request, response) -> {
            String id = request.queryParams("n");
            return getStudentUsingGuava(Integer.parseInt(id));
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable run1 = new Runnable() {
            @Override
            public void run() {
                get("spark/getData", (request, response) -> {
                    String id = request.queryParams("n");
                    return getStudentUsingGuava(Integer.parseInt(id));
                });
            }
        };
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                while (true){
                    read();
                }
            }
        };
        Thread t1 = new Thread(run1);
        Thread t2 = new Thread(task1);
        t1.start();
        t1.sleep(2000);
        t2.start();
    }
    public static String creatUrl() {
        int n = new Random().nextInt(2000);
        return "http://localhost:4567/spark/getData?n=" + n;
    }
    private static void read() {
        URL url = null;
        try {
            url = new URL(creatUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(20000);
            connection.connect();
            InputStream response = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Integer getStudentUsingGuava(Integer i) throws ExecutionException {
        int rs = 0;
        cache = LoadCache.getLoadingCache();
        if (cache.asMap().get(i) != null) {
            cache.asMap().put(i, i * i);
            rs = cache.get(i);
        } else {
            rs = cache.get(i);
        }
        System.out.println("Cache size:" + cache.size() + " Value:" + cache.get(i));
        return rs;
    }
}
