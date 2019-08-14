package com.spj.easychat.server;

import org.apache.ibatis.executor.Executor;

import java.util.concurrent.*;

public class ChatEventLoop {
    private static final ExecutorService service= new ThreadPoolExecutor(10, 10, 1000, TimeUnit.MILLISECONDS
            , new LinkedBlockingDeque<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.getQueue().size() >= 100){
                return;
            }else {
                executor.execute(r);
            }
        }
    });

    public static void executor(Runnable runnable){
        service.execute(runnable);
    }


}
