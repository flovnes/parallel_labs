namespace ParallelLab1
{
    class Worker(int id, int step)
    {
        private readonly int id = id;
        private readonly int step = step;
        private volatile bool shouldStop = false;

        public void Stop() => shouldStop = true;

        public void Calculate() {
            long sum = 0;
            long count = 0;
            long currentValue = 0;

            while (!shouldStop) {
                sum += currentValue;
                count++;
                currentValue += step;
                Thread.Sleep(50);
            }

            Console.WriteLine($"Thread {id} stopped. Step count = {count}, Sum = {sum}");
        }
    }

    class Program {
        static void Main() {
            int numberOfThreads = 3;
            int stepSize = 4;

            Thread[] threds = new Thread[numberOfThreads];
            Worker[] workers = new Worker[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                workers[i] = new Worker(i + 1, stepSize);
                threds[i] = new Thread(workers[i].Calculate);
                threds[i].Start();
            }

            Thread managerThread = new(() => {
                for (int i = 0; i < numberOfThreads; i++) {
                    Thread.Sleep(2000); 
                    workers[i].Stop();
                    Console.WriteLine($"\n{(i+1)*2000} ms passed, stopping thread {i + 1}");
                }
            });
            
            managerThread.Start();

            Console.WriteLine("All threads started, waiting.");
        }
    }
}