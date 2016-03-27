package com.nicaiya.diywidget.util;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhengjie on 16/3/22.
 */
public class ThreadPoolManager {

    private static AbsThreadPool generalPool;
    private static AbsThreadPool imageDecodingPool;
    private static AbsThreadPool networkThreadPool;

    public static ThreadPoolExecutor getGeneralExecutor() {
        if (generalPool == null) {
            generalPool = new GeneralThreadPool();
        }
        return generalPool.getThreadPoolExecutor();
    }

    public static ThreadPoolExecutor getImageDecodingExecutor() {
        if (imageDecodingPool == null) {
            imageDecodingPool = new ImageDecodingThreadPool();
        }
        return imageDecodingPool.getThreadPoolExecutor();
    }

    public static ThreadPoolExecutor getNetworkExecutor() {
        if (networkThreadPool == null) {
            networkThreadPool = new NetworkThreadPool();
        }
        return networkThreadPool.getThreadPoolExecutor();
    }

    private static abstract class AbsThreadPool {

        private ThreadPoolExecutor threadPool;

        abstract int getCorePoolSize();

        abstract int getMaxPoolSize();

        abstract String getThreadNamePrefix();

        public final ThreadPoolExecutor getThreadPoolExecutor() {
            try {
                if (threadPool == null) {
                    int i = getCorePoolSize();
                    int j = getMaxPoolSize();
                    ThreadFactory threadFactory = new ThreadFactory() {
                        private final AtomicInteger count = new AtomicInteger(1);

                        public Thread newThread(@NonNull Runnable runnable) {
                            return new Thread(runnable, getThreadNamePrefix() + "#" + this.count.getAndIncrement());
                        }
                    };
                    LinkedBlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<>();
                    threadPool = new ThreadPoolExecutor(i, j, Long.MAX_VALUE, TimeUnit.NANOSECONDS, linkedBlockingDeque, threadFactory);
                }
                return threadPool;
            } finally {
            }
        }
    }

    private static class GeneralThreadPool extends ThreadPoolManager.AbsThreadPool {
        private static final int CORE_THREAD_SIZE = 5;
        private static final int MAX_THREAD_SIZE = 5;
        private static final String NAME = "general";

        private GeneralThreadPool() {
            super();
        }

        int getCorePoolSize() {
            return CORE_THREAD_SIZE;
        }

        int getMaxPoolSize() {
            return MAX_THREAD_SIZE;
        }

        String getThreadNamePrefix() {
            return NAME;
        }
    }

    private static class ImageDecodingThreadPool extends ThreadPoolManager.AbsThreadPool {
        private static final int CORE_THREAD_SIZE = 5;
        private static final int MAX_THREAD_SIZE = 5;
        private static final String NAME = "ImageDecoding";

        private ImageDecodingThreadPool() {
            super();
        }

        int getCorePoolSize() {
            return CORE_THREAD_SIZE;
        }

        int getMaxPoolSize() {
            return MAX_THREAD_SIZE;
        }

        String getThreadNamePrefix() {
            return NAME;
        }
    }

    private static class NetworkThreadPool extends ThreadPoolManager.AbsThreadPool {
        private static final int CORE_THREAD_SIZE = 5;
        private static final int MAX_THREAD_SIZE = 5;
        private static final String NAME = "Network";

        private NetworkThreadPool() {
            super();
        }

        int getCorePoolSize() {
            return CORE_THREAD_SIZE;
        }

        int getMaxPoolSize() {
            return MAX_THREAD_SIZE;
        }

        String getThreadNamePrefix() {
            return NAME;
        }
    }

}
