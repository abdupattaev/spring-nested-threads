# spring-nested-threads


Notes:
Threads


ExecutorService

Runtime.getRuntime().availableProcessors(); -  returns the number of cores available in a machine

Threads waiting for the operation to finish

4 type of thread pool are available:

1. FixedThreadPool - it has fixed number of threads, keep submitting task to the queue, executes one after another
2. CashedThreadPool - queue is replaced by synchronous queue, can hold only 1 task, every time when new task is submitted, it will check existing threads if they are free, it will give the task to the free thread or if all threads are busy, it will create a new Thread and and asks to execute it.

They have ability to kill these idle threads, if there are no tasks available, thread pool size starts shrinking.

1.  ScheduledThreadPool
    1. ScheduledExecutorService
        1. service.schedule(new Task(), 10, SECONDS);   - task to run after 10 second delay
        2. service.scheduleAtFixedRate(new Task(), 15, 10, SECONDS); - task to run repeatedly every 10 seconds, first it will wait for 15 seconds, after that every 10 seconds triggers the task
        3. scheduleWithFixedDelay();  - task to run repeatedly 10 seconds after previous task completes, first it will wait for 15 seconds
2. SingleThreadedExecutor - service.schedule - size of the pool is only 1. if task throws exception, it will recreate the thread, tasks are not stopped, ensure tasks run sequentially






