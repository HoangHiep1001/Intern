package main;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;

import com.google.common.cache.LoadingCache;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Test {
    static LoadingCache<Integer, Integer> cache;

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void init() throws InterruptedException {
        Runnable run1 = new Runnable() {
            @Override
            public void run() {
                get("spark/getData", (request, response) -> {
                    String id = request.queryParams("n");
                    return getStudentUsingGuava(Integer.parseInt(id));
                });
            }
        };
        Runnable run2 = new Runnable() {
            @Override
            public void run() {
                Runnable task1 = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cache) {
                            read();
                        }
                    }
                };
                Runnable task2 = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cache) {
                            read();
                        }
                    }
                };
                Runnable task3 = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cache) {
                            read();
                        }
                    }
                };
                Runnable task4 = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cache) {
                            read();
                        }
                    }
                };
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                executorService.submit(task1);
                executorService.submit(task2);
                executorService.submit(task3);
                executorService.submit(task4);
                executorService.shutdown();

            }
        };
        Thread t1 = new Thread(run1);
        Thread t2 = new Thread(run2);
        t1.start();
        t1.sleep(2000);
        t2.start();

    }

    public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
        Options opt = new OptionsBuilder().include(Test.class.getSimpleName())
                .forks(0)
                .build();
        new Runner(opt).run();
    }

    public static String creatUrl() {
        int n = new Random().nextInt(20);
        return "http://localhost:4567/spark/getData?n=" + n;
    }

    private static Integer getStudentUsingGuava(Integer i) throws ExecutionException {
        int rs = 0;
        cache = LoadCache.getLoadingCache();
        cache.invalidateAll();
        if (cache.asMap().get(i) != null) {
            cache.asMap().put(i, i * i);
            rs = cache.get(i);
        } else {
            rs = cache.get(i);
        }
        System.out.println("Cache size:" + cache.size() + " Value:" + cache.get(i));
        return rs;
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
}
