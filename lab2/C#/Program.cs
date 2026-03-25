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
                if (val < Min) { Min = val; Index = idx; }
        }
    }

    class Worker(int[] arr, int start, int end, Result res)
    {
        public void FindMin()
        {
            // Random randGen = new();
            int min = int.MaxValue;
            int index = -1;

            for (int i = start; i < end; i++)
                if (arr[i] < min) { min = arr[i]; index = i; }
            res.Update(min, index);
        }
    }

    class App
    {
        private readonly int arraySize = 1000000000;
        private readonly int threadCount = 4;
        private readonly int[] arr;
        private readonly Result res;

        public App()
        {
            arr = new int[arraySize];
            res = new Result();
        }

        public void Run()
        {
            for (int i = 0; i < arraySize; i++) arr[i] = i;
            arr[arraySize / 2] = -4;

            Thread[] threads = new Thread[threadCount];
            int chunk = arraySize / threadCount;

            for (int i = 0; i < threadCount; i++)
            {
                int s = i * chunk;
                int e = (i == threadCount - 1) ? arraySize : (i + 1) * chunk;
                threads[i] = new Thread(new Worker(arr, s, e, res).FindMin);
                threads[i].Start();
            }

            foreach (var t in threads) t.Join();
            Console.WriteLine($"min: {res.Min}, index: {res.Index}");
        }
    }

    class Program
    {
        static void Main() => new App().Run();
    }
}
