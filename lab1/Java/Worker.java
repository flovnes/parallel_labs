public class Worker implements Runnable {
    private final int id;
    private final int step;
    private volatile boolean canStop = false;

    public Worker(int id, int step) {
        this.id = id;
        this.step = step;
    }

    public void stop() {
        this.canStop = true;
    }

    @Override
    public void run() {
        long sum = 0;
        long count = 0;
        long currentValue = 0;

        while (!canStop) {
            sum += currentValue;
            count++;
            currentValue += step;
            sleep(50);
        }

        System.out.println("Thread " + id + " stopped. Step count = " + count + ", Sum = " + sum);
    }

    private void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}