package sudoku;

import java.io.*;  
import java.lang.*; 
import java.lang.Runnable;
import java.lang.Thread;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;
import static java.util.concurrent.TimeUnit.*;

class SharedObject {
    public static volatile List<Long> timeRecords = new ArrayList<Long>();
    public static volatile List<Long> nodeRecords = new ArrayList<Long>();
}

class Experiment {
   int ID;
   int n;
   int mode;
   
   public Experiment(int oID, int on, int mode) {
      this.ID = oID;
      this.n = on;
      this.mode = mode;
   }
   
   public void run() {
      System.out.println("Running " +  ID + " on data set" + n + " mode " + mode);  
        Sudoku instance = null;
        
        try {
           instance = new Sudoku(n, mode);
        } catch (Exception e) {
           System.out.println("Failed to load dataSet Num." + n);
           e.printStackTrace();
        }
         //     instance.print();
        long startTime = System.nanoTime();
        //code
        if (instance.backtrack()) System.out.println("Runner " +  ID + " :) Find a solution!"); else System.out.println("Runner " +  ID + " - !!!! No solution !!!!");
        
        long endTime = System.nanoTime();
        
        System.out.println("Took "+ (endTime - startTime) + " ns or " + NANOSECONDS.toMillis((endTime - startTime)) + " ms or " + NANOSECONDS.toSeconds((endTime - startTime)) + " s" + " Going through nodes " + instance.nodeCount());
        
        List<Long> timeRecords = SharedObject.timeRecords;
        List<Long> nodeRecords = SharedObject.nodeRecords;
         
        timeRecords.add(NANOSECONDS.toMillis((endTime - startTime)));
        nodeRecords.add((long) instance.nodeCount());

        // Get average and std Time
        Long totalTime = 0l;
        for(Long recond : timeRecords) totalTime += recond;
        double averageTime = totalTime/timeRecords.size();
		double stdTime = 0d;
		for(Long recond : timeRecords) stdTime += Math.pow(recond - averageTime, 2);
		stdTime = stdTime / timeRecords.size();
		stdTime = Math.sqrt(stdTime);
		
        System.out.println("Time - In average: "+ timeRecords.size() + " 's exp in avg " + averageTime + " and in std " + stdTime);

        // Get average and std Node
        Long totalNode = 0l;
        for(Long recond : nodeRecords) totalNode += recond;
        double averageNode = totalNode/nodeRecords.size();
		double stdNode = 0d;
		for(Long recond : nodeRecords) stdNode += Math.pow(recond - averageNode, 2);
		stdNode = stdNode / nodeRecords.size();
		stdNode = Math.sqrt(stdNode);
		
        System.out.println("Nodes - In average: "+ nodeRecords.size() + " 's exp in avg " + averageNode + " and in std " + stdNode);

        //instance.print();
   }

}
    
class ExperimentThread implements Runnable {
   Thread experimentThread;
   int ID;
   int n;
   int mode;
  
   
   public ExperimentThread(int oID, int on, int mode) {
      this.ID = oID;
      this.n = on;
      this.mode = mode;
   }
   
   public void run() {
      System.out.println("Running " +  ID + " on data set" + n  + " mode " + mode);
      try {
        Sudoku instance = null;
        try {
           instance = new Sudoku(n, mode);
        } catch (Exception e) {
           System.out.println("Failed to load dataSet Num." + n);
           e.printStackTrace();
        }
              
        long startTime = System.nanoTime();
        //code
        if (instance.backtrack()) System.out.println("Runner " +  ID + " Find a solution!"); else System.out.println("Runner " +  ID + " Find NO solution!");
        
        long endTime = System.nanoTime();
        
        System.out.println("Took "+ (endTime - startTime) + " ns or " + NANOSECONDS.toMillis((endTime - startTime)) + " ms or " + NANOSECONDS.toSeconds((endTime - startTime)) + " s" + " Going through nodes " + instance.nodeCount());
        
        List<Long> timeRecords = SharedObject.timeRecords;
        List<Long> nodeRecords = SharedObject.nodeRecords;
        timeRecords.add(NANOSECONDS.toMillis((endTime - startTime)));
        nodeRecords.add((long) instance.nodeCount());

        // Get average and std Time
        Long totalTime = 0l;
        for(Long recond : timeRecords) totalTime += recond;
        double averageTime = totalTime/timeRecords.size();
		double stdTime = 0d;
		for(Long recond : timeRecords) stdTime += Math.pow(recond - averageTime, 2);
		stdTime = stdTime / timeRecords.size();
		stdTime = Math.sqrt(stdTime);
		
        System.out.println("Time - In average: "+ timeRecords.size() + " 's exp in avg " + averageTime + " and in std " + stdTime);

        // Get average and std Node
        Long totalNode = 0l;
        for(Long recond : nodeRecords) totalNode += recond;
        double averageNode = totalNode/nodeRecords.size();
		double stdNode = 0d;
		for(Long recond : nodeRecords) stdNode += Math.pow(recond - averageNode, 2);
		stdNode = stdNode / nodeRecords.size();
		stdNode = Math.sqrt(stdNode);
		
        System.out.println("Nodes - In average: "+ nodeRecords.size() + " 's exp in avg " + averageNode + " and in std " + stdNode);

        //instance.print();

      } catch (Exception e) {
         System.out.println("Thread " +  ID + " got interrupted.");
      }
   }
   
   public void start () {
        if (experimentThread != null) return;
        experimentThread = new Thread(this, "Thread" + ID);
        experimentThread.start();
   }
}
    
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

        System.out.println("Enter Mode (1 B, 2 B+FC, 3 B+FC+H): ");    
        
        int m = 0;
        
        do {
            try {
                m = Integer.parseInt(c.readLine()); 
            } catch (Exception e) {
                System.out.println("Invalid Input. Try Again");  
                continue;
            }
        } while (!(m >= 1 && m <= 3)); 

        System.out.println("Enter Repeat Times: (1-100)");    
        
        int r = 0;
        
        do {
            try {
                r = Integer.parseInt(c.readLine()); 
            } catch (Exception e) {
                System.out.println("Invalid Input. Try Again");  
                continue;
            }
        } while (!(r >= 1 && r <= 100)); 
        
        
        System.out.println("Enter Node Limits: (10000-2,147,483,647 recommend 125260053)");    
        
        int d = 0;
        
        do {
            try {
                d = Integer.parseInt(c.readLine()); 
            } catch (Exception e) {
                System.out.println("Invalid Input. Try Again");  
                continue;
            }
        } while (!(d >= 1)); 

        System.out.println("MultiThreading? (1 = no, 2 = yes)");    
        
        int o = 0;
        
        do {
            try {
                o = Integer.parseInt(c.readLine()); 
            } catch (Exception e) {
                System.out.println("Invalid Input. Try Again");  
                continue;
            }
        } while (!(o >= 1)); 
        
        for (int rep = 1; rep <= r; rep++){
            if (o == 2) {
                ExperimentThread e = new ExperimentThread(rep, n, m);
                e.start();
            } else {
                Experiment e = new Experiment(rep, n, m);
                e.run();
            }
        }
    }
}