import java.util.concurrent.CountDownLatch;

public class Main {

    static class Result {

        int min = Integer.MAX_VALUE;
        int index = -1;

        public synchronized void update(int val, int idx) {
            if (val < min) {
                min = val;
                index = idx;
            }
        }
    }

    static class Worker implements Runnable {

        private final int[] arr;
        private final int start, end;
        private final Result res;
        private final CountDownLatch latch;

        Worker(
            int[] arr,
            int start,
            int end,
            Result res,
            CountDownLatch latch
        ) {
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.res = res;
            this.latch = latch;
        }

        @Override
        public void run() {
            int min = Integer.MAX_VALUE;
            int index = -1;
            for (int i = start; i < end; i++) {
                if (arr[i] < min) {
                    min = arr[i];
                    index = i;
                }
            }
            res.update(min, index);
            latch.countDown();
        }
    }

    public void run() throws InterruptedException {
        int arraySize = 1000000000;
        int threadCount = 12;
        int[] arr = new int[arraySize];
        Result res = new Result();
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < arraySize; i++) arr[i] = i;
        arr[arraySize / 2] = -4;

        int chunk = arraySize / threadCount;
        for (int i = 0; i < threadCount; i++) {
            int l = i * chunk;
            int r = (i == threadCount - 1) ? arraySize : (i + 1) * chunk;
            new Thread(new Worker(arr, l, r, res, latch)).start();
        }

        latch.await();
        System.out.println("min: " + res.min + ", index: " + res.index);
    }

    public static void main(String[] args) throws InterruptedException {
        new Main().run();
    }
}
