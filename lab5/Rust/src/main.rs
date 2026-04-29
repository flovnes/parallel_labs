use rayon::prelude::*;
use std::time::Instant;

const ROWS: usize = 10000;
const COLS: usize = 10000;

fn main() {
    let matrix: Vec<Vec<i32>> = (0..ROWS).into_par_iter().map(|i| {
        (0..COLS).map(|j| (i + j + 1) as i32).collect()
    }).collect();
    
    let mut matrix = matrix;
    matrix[5][0] = -1000000;

    let start_sum = Instant::now();
    
    let total_sum: i64 = matrix.iter()
        .map(|row| row.iter().map(|&x| x as i64).sum::<i64>())
        .sum();
        
    println!("[Sum] Result: {} | Time: {:?}", total_sum, start_sum.elapsed());

    let start_min = Instant::now();

    let (min_row_index, min_row_sum) = matrix.iter()
        .enumerate()
        .map(|(i, row)| {
            let row_sum: i64 = row.iter().map(|&x| x as i64).sum();
            (i, row_sum)
        })
        .min_by_key(|&(_, sum)| sum)
        .unwrap();

    println!("[MinRow] Row: {} | Value: {} | Time: {:?}", min_row_index, min_row_sum, start_min.elapsed());
}
