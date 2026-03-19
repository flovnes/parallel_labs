    static class Worker implements Runnable {
        private final int id;
        private final int step;
        private volatile boolean canStop = false;

        public Worker(int id, int step) {
            this.id = id;
            this.step = step;
        }

        public void stop() => canStop = true;

        @Override
        public void run() {
            long sum = 0, count = 0, currentValue = 0;

            while (!canStop) {
                sum += currentValue;
                count++;
                currentValue += step;
            }

            System.out.println("thread " + id + " stopped, elements: " + count + ", sum: " + sum);
        }
    }
