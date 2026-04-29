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
        int prodCount = 5, consCount = 2;
        Storage storage = new Storage(storageSize);

        int prodBase = totalItems / prodCount;
        int prodExtra = totalItems % prodCount;

        for (int i = 0; i < prodCount; i++) {
            final int id = i + 1;
            final int count = prodBase + (i < prodExtra ? 1 : 0);
            new Thread(() -> {
                try {
                    for (int j = 0; j < count; j++) storage.put("item " + j, id);
                } catch (InterruptedException e) {}
            }).start();
        }

        int consBase = totalItems / consCount;
        int consExtra = totalItems % consCount;

        for (int i = 0; i < consCount; i++) {
            final int id = i + 1;
            final int count = consBase + (i < consExtra ? 1 : 0);
            new Thread(() -> {
                try {
                    for (int j = 0; j < count; j++) storage.get(id);
                } catch (InterruptedException e) {}
            }).start();
        }
    }
}