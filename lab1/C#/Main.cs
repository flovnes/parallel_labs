using System.Diagnostics;

namespace Lab1
{
    class Worker(int id, int step)
    {
        private volatile bool canStop = false;

        public void Stop() => canStop = true;

        public void Calculate()
        {
            long sum = 0;
            long count = 0;
            long currentValue = 0;

            while (!canStop)
            {
                sum += currentValue;
                count++;
                currentValue += step;
            }

            Console.WriteLine($"thread {id} stopped, elements: {count}, sum: {sum}");
        }
    }

    class Program
    {
        static void Main()
        {
            double[] durations = [4.0, 4.0, 7.0, 4.0, 7.0, 4.0, 7.0, 4.0];
            int threadCount = durations.Length;

            Worker[] workers = new Worker[threadCount];
            Thread[] threads = new Thread[threadCount];

            for (int i = 0; i < threadCount; i++)
            {
                workers[i] = new Worker(i + 1, 2);
                threads[i] = new Thread(workers[i].Calculate);
                threads[i].Start();
            }

            Thread managerThread = new(() =>
            {
                Stopwatch sw = Stopwatch.StartNew();
                bool[] stopped = new bool[threadCount];
                int stoppedCount = 0;

                while (stoppedCount < threadCount)
                {
                    for (int i = 0; i < threadCount; i++)
                    {
                        if (!stopped[i] && sw.Elapsed.TotalSeconds >= durations[i])
                        {
                            workers[i].Stop();
                            stopped[i] = true;
                            stoppedCount++;
                            Console.WriteLine($"stopped thread {i + 1} at {sw.Elapsed.TotalSeconds} sec");
                        }
                    }
                }
            });

            managerThread.Start();
            managerThread.Join();
        }
    }
}
