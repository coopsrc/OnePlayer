package com.coopsrc.oneplayer.core;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-29 13:18
 */
public final class OneExecutors {

    private static final int NETWORK_THREAD_COUNT = 4;

    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    private static final class Holder {
        private static final OneExecutors INSTANCE = new OneExecutors();
    }

    public static OneExecutors getInstance() {
        return Holder.INSTANCE;
    }

    private OneExecutors() {
        this.mDiskIO = Executors.newSingleThreadExecutor();
        this.mNetworkIO = Executors.newFixedThreadPool(NETWORK_THREAD_COUNT);
        this.mMainThread = new MainThreadExecutor();
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }


    private class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NotNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
