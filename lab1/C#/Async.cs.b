namespace Lab1
{
    class Worker(int id, int step)
    {
        public volatile bool canStop = false;

        public void Calculate()
        {
            long sum = 0, count = 0, currentValue = 0;

            while (!canStop)
            {
                sum += currentValue;
                count++;
                currentValue += step;
            }

            Console.WriteLine($"thread {id} dead. count: {count}, sum: {sum}");
        }
    }

    class Program
    {
        static async Task Main()
        {
            double[] durations = [4.0, 7.0, 4.0, 5.0];

            var workerTasks = durations.Select((dur, index) => RunWorkerAsync(index + 1, dur)).ToArray();

            await Task.WhenAll(workerTasks);
        }

        static async Task RunWorkerAsync(int id, double durationSeconds)
        {
            var worker = new Worker(id, 2);

            // threads[i].Start()
            Task calculateTask = Task.Run(() => worker.Calculate());

            // sleep loop
            await Task.Delay(TimeSpan.FromSeconds(durationSeconds));

            worker.canStop = true;
            Console.WriteLine($"stopping worker {id} after {durationSeconds}s");

            await calculateTask;
        }
    }
}
