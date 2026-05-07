namespace Lab2
{
    class Result
    {
        public int Min { get; private set; } = int.MaxValue;
        public int Index { get; private set; } = -1;
        private readonly object locker = new();

        public void Update(int val, int idx)
        {
            lock (locker)
            {
                if (val < Min)
                {
                    Min = val;
                    Index = idx;
                }
            }
        }
    }

    class Worker(int[] arr, int start, int end, Result res, CountdownEvent latch)
    {
        public void FindMin()
        {
            int localMin = int.MaxValue;
            int localIndex = -1;

            for (int i = start; i < end; i++)
            {
                if (arr[i] < localMin)
                {
                    localMin = arr[i];
                    localIndex = i;
                }
            }
            res.Update(localMin, localIndex);
            latch.Signal();
        }
    }

    class App
    {
        private readonly int arraySize = 1_000_000_000;
        private readonly int threadCount = 4;
        private readonly int[] arr;
        private readonly Result res;
        private readonly CountdownEvent latch;

        public App()
        {
            arr = new int[arraySize];
            res = new Result();
            latch = new CountdownEvent(threadCount);
        }

        public void Run()
        {
            for (int i = 0; i < arraySize; i++) arr[i] = i;
            arr[arraySize / 2] = -4;

            int chunk = arraySize / threadCount;

            for (int i = 0; i < threadCount; i++)
            {
                int s = i * chunk;
                int e = (i == threadCount - 1) ? arraySize : (i + 1) * chunk;

                Worker worker = new(arr, s, e, res, latch);
                Thread thread = new(worker.FindMin);
                thread.Start();
            }

            latch.Wait();

            Console.WriteLine($"Min value found: {res.Min} at index: {res.Index}");
        }
    }

    class Program
    {
        static void Main() => new App().Run();
    }
}
