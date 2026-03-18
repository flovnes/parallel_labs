public class Main {
    private static int[] arr;
    private static int globalMin = Integer.MAX_VALUE;
    private static int globalMinIndex = -1;

    private static synchronized void updateGlobalMin(int localMin, int localIndex) {
        if (localMin < globalMin) {
            globalMin = localMin;
            globalMinIndex = localIndex;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int dim = 1000000;
        int threadNum = 4;
        arr = new int[dim];

        for (int i = 0; i < dim; i++) arr[i] = (int) (Math.random() * 10000);
        arr[(int) (Math.random() * dim)] = -999;

        Thread[] threads = new Thread[threadNum];
        int chunkSize = dim / threadNum;

        for (int i = 0; i < threadNum; i++) {
            final int start = i * chunkSize;
            final int end = (i == threadNum - 1) ? dim : (i + 1) * chunkSize;

            threads[i] = new Thread(() -> {
                int localMin = Integer.MAX_VALUE;
                int localIndex = -1;
                for (int j = start; j < end; j++) {
                    if (arr[j] < localMin) {
                        localMin = arr[j];
                        localIndex = j;
                    }
                }
                updateGlobalMin(localMin, localIndex);
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();

        System.out.println("Min: " + globalMin + " at index: " + globalMinIndex);
    }
}