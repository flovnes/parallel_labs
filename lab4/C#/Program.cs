using System;
using System.Threading;

namespace Lab4
{
    class Table
    {
        private readonly Semaphore[] forks = new Semaphore[5];

        public Table()
        {
            for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1, 1);
        }

        public void GetForks(int left, int right, int id)
        {
            if (id == 4) {
                forks[left].WaitOne();
                forks[right].WaitOne();
            } else {
                forks[right].WaitOne();
                forks[left].WaitOne();
            }
        }

        public void PutForks(int left, int right)
        {
            forks[left].Release();
            forks[right].Release();
        }
    }

    class Philosopher(int id, Table table)
    {
        private readonly int left = (id + 1) % 5;
        private readonly int right = id;
        private readonly Random rand = new();

        public void Run()
        {
            for (int i = 0; i < 5; i++)
            {
                Console.WriteLine($"Філософ {id} думає");
                Thread.Sleep(rand.Next(50, 150));
                table.GetForks(left, right, id);
                Console.WriteLine($"Філософ {id} жує");
                Thread.Sleep(rand.Next(50, 150));
                table.PutForks(left, right);
            }
        }
    }

    class Program
    {
        static void Main()
        {
            Table table = new Table();
            for (int i = 0; i < 5; i++)
            {
                int id = i;
                new Thread(new Philosopher(id, table).Run).Start();
            }
        }
    }
}