package ProgrammStart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    public static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                new StartProgramm().ProgrammStart();
            }
        });
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void setExecutor(ScheduledExecutorService executor) {
        Main.executor = executor;
    }
}