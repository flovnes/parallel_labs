class Worker implements Runnable {

    private final int id;
    private final int step;

    private volatile boolean canStop = false;

    private long sum = 0;
    private long count = 0;

    public Worker(int id, int step) {
        this.id = id;
        this.step = step;
    }

    public void stop() {
        this.canStop = true;
    }

    @Override
    public void run() {
        long currentValue = 0;

        while (!canStop) {
            sum += currentValue;
            count++;
            currentValue += step;
        }

        System.out.printf(
            "thread %d stopped, count: %d, sum: %d%n",
            id,
            count,
            sum
        );
    }
}
