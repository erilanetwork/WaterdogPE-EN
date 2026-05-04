package dev.waterdog.waterdogpe.scheduler;

public interface WaterdogScheduler {

    <T extends Runnable> TaskHandler<T> scheduleTask(T task, boolean async);

    <T extends Runnable> TaskHandler<T> scheduleAsync(T task);

    <T extends Runnable> TaskHandler<T> scheduleDelayed(T task, int delay);

    <T extends Runnable> TaskHandler<T> scheduleDelayed(T task, int delay, boolean async);

    <T extends Runnable> TaskHandler<T> scheduleRepeating(T task, int period);

    <T extends Runnable> TaskHandler<T> scheduleRepeating(T task, int period, boolean async);

    <T extends Runnable> TaskHandler<T> scheduleDelayedRepeating(T task, int delay, int period);

    <T extends Runnable> TaskHandler<T> scheduleDelayedRepeating(T task, int delay, int period, boolean async);

    void cancelTask(int taskId);
}
