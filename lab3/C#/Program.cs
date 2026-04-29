using System;
using System.Collections.Generic;
using System.Threading;

namespace Lab3
{
    class Storage(int size)
    {
        private readonly Queue<string> items = new();
        private readonly Semaphore full = new(size, size);
        private readonly Semaphore empty = new(0, size);
        private readonly object locker = new();

        public void Put(string item, int id)
        {
            full.WaitOne();
            lock (locker)
            {
                items.Enqueue(item);
                Console.WriteLine($"Producer {id} added: {item}");
            }
            empty.Release();
        }

        public string Get(int id)
        {
            empty.WaitOne();
            string item;
            lock (locker)
            {
                item = items.Dequeue();
                Console.WriteLine($"Consumer {id} took: {item}");
            }
            full.Release();
            return item;
        }
    }

    class Program
    {
        static void Main()
        {
            int totalItems = 5, storageSize = 1;
            int prodCount = 3, consCount = 3;
            
            Storage storage = new(storageSize);

            int prodBase = totalItems / prodCount;
            int prodExtra = totalItems % prodCount;

            for (int i = 0; i < prodCount; i++)
            {
                int threadId = i + 1;
                int count = prodBase + (i < prodExtra ? 1 : 0);
                new Thread(() => {
                    for (int j = 0; j < count; j++) 
                        storage.Put($"item {j}", threadId);
                }).Start();
            }

            int consBase = totalItems / consCount;
            int consExtra = totalItems % consCount;

            for (int i = 0; i < consCount; i++)
            {
                int threadId = i + 1;
                int count = consBase + (i < consExtra ? 1 : 0);
                new Thread(() => {
                    for (int j = 0; j < count; j++) 
                        storage.Get(threadId);
                }).Start();
            }
        }
    }
}