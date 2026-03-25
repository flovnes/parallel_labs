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

        Worker(int[] arr, int start, int end, Result res) {
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.res = res;
        }

        @Override
        public void run() {
            int min = Integer.MAX_VALUE;
            int index = -1;
            // java.util.Random randGen = new java.util.Random();

            for (int i = start; i < end; i++) {
                if (arr[i] < min) {
                    min = arr[i];
                    index = i;
                }
            }
            res.update(min, index);
        }
    }

    public void run() throws InterruptedException {
        int arraySize = 1000000000;
        int threadCount = 12;
        int[] arr = new int[arraySize];
        Result res = new Result();

        for (int i = 0; i < arraySize; i++) arr[i] = i;
        arr[arraySize / 2] = -4;

        Thread[] threads = new Thread[threadCount];
        int chunk = arraySize / threadCount;

        for (int i = 0; i < threadCount; i++) {
            int l = i * chunk;
            int r = (i == threadCount - 1) ? arraySize : (i + 1) * chunk;
            threads[i] = new Thread(new Worker(arr, l, r, res));
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        System.out.println("min: " + res.min + ", index: " + res.index);
    }

    public static void main() throws InterruptedException {
        new Main().run();
    }
}
