import java.util.Random;
import java.util.concurrent.Semaphore;

class Table {
    private final Semaphore[] forks = new Semaphore[5];

    public Table() {
        for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1);
    }

    public void getForks(int left, int right, int id) throws InterruptedException {
        if (id == 4) {
            forks[left].acquire();
            forks[right].acquire();
        } else {
            forks[right].acquire();
            forks[left].acquire();
        }
    }

    public void putForks(int left, int right) {
        forks[left].release();
        forks[right].release();
    }
}

class Philosopher extends Thread {
    private final int id, left, right;
    private final Table table;
    private final Random rand = new Random();

    public Philosopher(int id, Table table) {
        this.id = id; this.table = table;
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
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
}

public class Main {
    public static void main() {
        Table table = new Table();
        for (int i = 0; i < 5; i++) new Philosopher(i, table).start();
    }
}