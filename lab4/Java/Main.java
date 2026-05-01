import java.util.Random;
import java.util.concurrent.Semaphore;

class Table {

    private final Semaphore[] forks = new Semaphore[5];
    private final Semaphore waiter = new Semaphore(2);

    public Table() {
        for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1);
    }

    public void getForks(int left, int right, int id)
        throws InterruptedException {
        waiter.acquire();
        forks[right].acquire();
        forks[left].acquire();
    }

    public void putForks(int left, int right) {
        forks[left].release();
        forks[right].release();
        waiter.release();
    }
}

class Philosopher extends Thread {

    private final int id, left, right;
    private final Table table;
    private final Random rand = new Random();

    public Philosopher(int id, Table table) {
        this.id = id;
        this.table = table;
        this.right = id;
        this.left = (id + 1) % 5;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("Філософ " + id + " думає");
                Thread.sleep(rand.nextInt(100) + 50);
                table.getForks(left, right, id);
                System.out.println("Філософ " + id + " жує");
                Thread.sleep(rand.nextInt(100) + 50);
                table.putForks(left, right);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Table table = new Table();
        Philosopher[] philosophers = new Philosopher[5];
        CountdownLatch latch = new CountdownLatch(5);

        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(i, table);
            philosophers[i].start();
        }

        latch.await();
    }
}
