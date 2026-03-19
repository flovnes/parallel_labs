public class Main {

    public static void main() throws InterruptedException {
        double[] durations = { 4.0, 4.0, 7.0, 4.0, 7.0, 4.0, 7.0, 4.0 };
        int threadCount = durations.length;

        Worker[] workers = new Worker[threadCount];
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            workers[i] = new Worker(i + 1, 2);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        Thread managerThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            boolean[] stopped = new boolean[threadCount];
            int stoppedCount = 0;

            while (stoppedCount < threadCount) {
                double elapsed = (System.currentTimeMillis() - start) / 1000.0;
                for (int i = 0; i < threadCount; i++) {
                    if (!stopped[i] && elapsed >= durations[i]) {
                        workers[i].stop();
                        stopped[i] = true;
                        stoppedCount++;
                        System.out.println(
                            "stopped thread " +
                                (i + 1) +
                                " at " +
                                elapsed +
                                " sec"
                        );
                    }
                }
            }
        });

        managerThread.start();
        managerThread.join();
    }
}
