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
            int totalItems = 40, storageSize = 5;
            int prodCount = 4, consCount = 4;
            
            Storage storage = new(storageSize);
            int itemsPerThread = totalItems / prodCount;
            int extraItems = totalItems % prodCount;

            for (int i = 1; i <= prodCount; i++)
            {
                int threadId = i;
                int count = itemsPerThread + (i == prodCount - 1 ? extraItems : 0);
                new Thread(() => {
                    for (int j = 0; j < count; j++) 
                        storage.Put($"item {j}", threadId);
                }).Start();
            }

            for (int i = 1; i <= consCount; i++)
            {
                int threadId = i;
                new Thread(() => {
                    for (int j = 0; j < itemsPerThread; j++) 
                        storage.Get(threadId);
                }).Start();
            }
        }
    }
}