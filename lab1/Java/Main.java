public class Main {
    public static void main(String[] args) {
        int numberOfThreads = 3;
        int stepSize = 4;

        Worker[] workers = new Worker[numberOfThreads];
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            workers[i] = new Worker(i + 1, stepSize);
            
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        Thread managerThread = new Thread(() -> {
            for (int i = 0; i < numberOfThreads; i++) {
                sleep(2000);
                workers[i].stop();
                System.out.println("Stopping thread " + (i + 1));
            }
        });
        
        managerThread.start();
        System.out.println("All threads started, waiting.");
    }

    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}