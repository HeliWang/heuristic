package sudoku;

import java.io.*;  
import static java.util.concurrent.TimeUnit.*;

public class Main {

    public static void main(String[] args) {
        
        Console c = System.console();    
        System.out.println("Enter the data set number (1 easy, 2 medium, 3 hard, 4 evil): ");    
        
        int n = 0;
        
        do {
            try {
                n = Integer.parseInt(c.readLine()); 
            } catch (Exception e) {
                System.out.println("Invalid Input. Try Again");  
                continue;
            }
        } while (!(n >= 1 && n <= 4)); 
        // Datasets are from CS486 Piazza
       
        Sudoku instance = null;
        
        try {
           instance = new Sudoku(n);
        } catch (Exception e) {
           System.out.println("Failed to load dataSet Num." + n);
           e.printStackTrace();
        }
        
        instance.print();
        
        long startTime = System.nanoTime();
        //code
        if (instance.backtrack()) System.out.println("Find a solution!"); else System.out.println("No solution!");
        instance.print();
        
        long endTime = System.nanoTime();
        System.out.println("Took "+ (endTime - startTime) + " ns or " + NANOSECONDS.toMillis((endTime - startTime)) + " ms or " + NANOSECONDS.toSeconds((endTime - startTime)) + " s" + " Going through" + instance.nodeCount());
    }

}