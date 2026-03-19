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
            long startTime = System.currentTimeMillis();
            boolean[] stopped = new boolean[threadCount];
            int stoppedCount = 0;

            while (stoppedCount < threadCount) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                double elapsedSeconds = elapsedMillis / 1000.0;

                for (int i = 0; i < threadCount; i++) {
                    if (!stopped[i] && elapsedSeconds >= durations[i]) {
                        workers[i].stop();
                        stopped[i] = true;
                        stoppedCount++;
                        System.out.printf(
                            "stopping thread %d at %.2f sec%n",
                            (i + 1),
                            elapsedSeconds
                        );
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        managerThread.start();
        managerThread.join();

        for (Thread t : threads) {
            t.join();
        }
    }
}
