import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class Storage {
    private final Queue<String> items = new LinkedList<>();
    private final Semaphore full, empty;
    private final Object locker = new Object();

    public Storage(int size) {
        full = new Semaphore(size);
        empty = new Semaphore(0);
    }

    public void put(String item, int id) throws InterruptedException {
        full.acquire();
        synchronized (locker) {
            items.add(item);
            System.out.println("Producer " + id + " added: " + item);
        }
        empty.release();
    }

    public String get(int id) throws InterruptedException {
        empty.acquire();
        String item;
        synchronized (locker) {
            item = items.poll();
            System.out.println("Consumer " + id + " took: " + item);
        }
        full.release();
        return item;
    }
}

public class Main {
    public static void main(String[] args) {
        int totalItems = 20, storageSize = 5;
        int prodCount = 2, consCount = 2;
        Storage storage = new Storage(storageSize);
        int perThread = totalItems / prodCount;

        for (int i = 1; i <= prodCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < perThread; j++) storage.put("item " + j, id);
                } catch (InterruptedException e) {}
            }).start();
        }

        for (int i = 1; i <= consCount; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < perThread; j++) storage.get(id);
                } catch (InterruptedException e) {}
            }).start();
        }
    }
}