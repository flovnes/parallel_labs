#include <iostream>
#include <vector>
#include <omp.h>
#include <iomanip>

using namespace std;

const int ROWS = 10000;
const int COLS = 10000;

class ParallelMatrixApp {
private:
    vector<vector<int>> matrix;

public:
    ParallelMatrixApp() {
        matrix.resize(ROWS, vector<int>(COLS));
    }

    void initMatrix() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                matrix[i][j] = i + j + 1;
            }
        }
        matrix[5][0] = -1000000; 
    }

    void calculateTotalSum(int num_threads) {
        long long total_sum = 0;
        double start = omp_get_wtime();

        #pragma omp parallel for reduction(+:total_sum) num_threads(num_threads)
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                total_sum += matrix[i][j];
            }
        }

        double end = omp_get_wtime();
        cout << "[Sum] Threads: " << num_threads << " | Result: " << total_sum 
             << " | Time: " << (end - start) << "s" << endl;
    }

    void findMinRowSum(int num_threads) {
        long long global_min_sum = 9223372036854775807LL;
        int min_row_index = -1;
        double start = omp_get_wtime();

        #pragma omp parallel num_threads(num_threads)
        {
            long long local_min_sum = 9223372036854775807LL;
            int local_min_index = -1;

            #pragma omp for
            for (int i = 0; i < ROWS; i++) {
                long long current_row_sum = 0;
                for (int j = 0; j < COLS; j++) {
                    current_row_sum += matrix[i][j];
                }

                if (current_row_sum < local_min_sum) {
                    local_min_sum = current_row_sum;
                    local_min_index = i;
                }
            }

            #pragma omp critical
            {
                if (local_min_sum < global_min_sum) {
                    global_min_sum = local_min_sum;
                    min_row_index = local_min_index;
                }
            }
        }

        double end = omp_get_wtime();
        cout << "[MinRow] Threads: " << num_threads << " | Row: " << min_row_index 
             << " | Value: " << global_min_sum << " | Time: " << (end - start) << "s" << endl;
    }

    void run() {
        initMatrix();

        omp_set_nested(1);

        double total_start = omp_get_wtime();

        #pragma omp parallel sections
        {
            #pragma omp section
            {
                calculateTotalSum(4);
            }

            #pragma omp section
            {
                findMinRowSum(4);
            }
        }

        double total_end = omp_get_wtime();
        cout << "Total execution time: " << (total_end - total_start) << "s" << endl;
    }
};

int main() {
    ParallelMatrixApp app;
    app.run();
    return 0;
}