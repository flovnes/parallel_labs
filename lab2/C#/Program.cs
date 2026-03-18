using System;
using System.Threading;

namespace ThreadMinSharp
{
    class Program
    {
        private static readonly int dim = 1000000;
        private static readonly int threadCount = 4;
        private readonly int[] arr = new int[dim];

        private int globalMin = int.MaxValue;
        private int globalMinIndex = -1;
        private int finishedThreads = 0;

        private readonly object lockerForMin = new object();
        private readonly object lockerForCount = new object();

        static void Main(string[] args)
        {
            Program program = new Program();
            program.InitArr();

            program.RunParallelMin();

            Console.WriteLine($"value: {program.globalMin}, index -> {program.globalMinIndex}");

            Console.ReadKey();
        }

        private void InitArr()
        {
            Random rand = new Random();
            for (int i = 0; i < dim; i++)
            {
                arr[i] = rand.Next(1, 1000000);
            }

            int randomIndex = rand.Next(0, dim);
            arr[randomIndex] = -474;
        }

        public void RunParallelMin()
        {
            int chunkSize = dim / threadCount;

            for (int i = 0; i < threadCount; i++)
            {
                int start = i * chunkSize;
                int end = (i == threadCount - 1) ? dim : (i + 1) * chunkSize;

                Thread t = new Thread(WorkerStep);
                t.Start(new Bound(start, end));
            }

            lock (lockerForCount)
            {
                while (finishedThreads < threadCount)
                {
                    Monitor.Wait(lockerForCount);
                }
            }
        }

        private void WorkerStep(object param)
        {
            if (param is Bound b)
            {
                int localMin = int.MaxValue;
                int localIndex = -1;

                for (int i = b.StartIndex; i < b.FinishIndex; i++)
                {
                    if (arr[i] < localMin)
                    {
                        localMin = arr[i];
                        localIndex = i;
                    }
                }

                lock (lockerForMin)
                {
                    if (localMin < globalMin)
                    {
                        globalMin = localMin;
                        globalMinIndex = localIndex;
                    }
                }

                lock (lockerForCount)
                {
                    finishedThreads++;
                    Monitor.Pulse(lockerForCount);
                }
            }
        }

        class Bound
        {
            public int StartIndex { get; }
            public int FinishIndex { get; }
            public Bound(int start, int finish)
            {
                StartIndex = start;
                FinishIndex = finish;
            }
        }
    }
}
