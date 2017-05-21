package sudoku;

import java.lang.*; 

interface SudokuBase {
   boolean backtrack();
   
   // number of nodes expanded (average and standard deviation of the 50 runs). 
   // A node is expanded when it is removed from the queue and its children are added to the queue 
   // (the children are not expanded until they are removed from the queue).
   int nodeCount();
   
   void print();
}