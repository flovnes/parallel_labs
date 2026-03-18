import java.util.Random;

public class Main {

    private static final int DIM = 1000000;
    private static final int THREAD_NUM = 4;
    private static final int[] arr = new int[DIM];

    static class Result {

        int min = Integer.MAX_VALUE;
        int index = -1;

        public synchronized void update(int localMin, int localIndex) {
            if (localMin < this.min) {
                this.min = localMin;
                this.index = localIndex;
            }
        }
    }

    static class MinFinder implements Runnable {

        private final int start;
        private final int end;
        private final Result result;

        public MinFinder(int start, int end, Result result) {
            this.start = start;
            this.end = end;
            this.result = result;
        }

        @Override
        public void run() {
            int localMin = Integer.MAX_VALUE;
            int localIndex = -1;

            for (int i = start; i < end; i++) {
                if (arr[i] < localMin) {
                    localMin = arr[i];
                    localIndex = i;
                }
            }

            result.update(localMin, localIndex);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        initArray();
        Result finalResult = new Result();
        Thread[] threads = new Thread[THREAD_NUM];

        int chunkSize = DIM / THREAD_NUM;

        for (int i = 0; i < THREAD_NUM; i++) {
            int start = i * chunkSize;
            int end = (i == THREAD_NUM - 1) ? DIM : (i + 1) * chunkSize;

            threads[i] = new Thread(new MinFinder(start, end, finalResult));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println(
            "value: " + finalResult.min + ", index -> " + finalResult.index
        );
    }

    private static void initArray() {
        Random rand = new Random();
        for (int i = 0; i < DIM; i++) {
            arr[i] = rand.nextInt(2000000);
        }
        int randomIdx = rand.nextInt(DIM);
        arr[randomIdx] = -4747;
    }
}
