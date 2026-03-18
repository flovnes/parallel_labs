namespace Lab2
{
    class Program
    {
        static int[] arr;
        static int minVal = int.MaxValue;
        static int minIndex = -1;
        static readonly object locker = new object();

        static void Main(string[] args)
        {
            int dim = 1000000;
            int threadNum = 4;
            arr = new int[dim];

            Random rand = new Random();
            for (int i = 0; i < dim; i++) arr[i] = rand.Next(1, 10000);
            int targetIndex = rand.Next(0, dim);
            // int targetIndex = 4;
            arr[targetIndex] = -4747;

            Thread[] threads = new Thread[threadNum];
            int chunkSize = dim / threadNum;

            for (int i = 0; i < threadNum; i++)
            {
                int start = i * chunkSize;
                int end = (i == threadNum - 1) ? dim : (i + 1) * chunkSize;

                threads[i] = new Thread(() => FindMin(start, end));
                threads[i].Start();
            }

            foreach (var t in threads) t.Join();

            Console.WriteLine($"Global Min: {minVal} at Index: {minIndex}");
        }

        static void FindMin(int start, int end)
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

            lock (locker)
            {
                if (localMin < minVal)
                {
                    minVal = localMin;
                    minIndex = localIndex;
                }
            }
        }
    }
}