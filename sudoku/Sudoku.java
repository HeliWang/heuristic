package sudoku;

import java.lang.*; 
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;

public class Sudoku  {
    
    /**
     * total node count
     */
    protected int nodeCount;
   
    /**
     * Current Assignment
     */
    protected List<List<Variable>> assignment;
    protected List<Variable> unassignedVars;
    protected Random random = new Random(System.nanoTime());
   
    public Sudoku(int dataSet) throws IOException, URISyntaxException {
        
        nodeCount = 0;
        assignment = new ArrayList<List<Variable>>();
        unassignedVars = new ArrayList<Variable>();
        System.out.println("reading from /sudoku/dataSet/" + String.valueOf(dataSet));
        URI uri = this.getClass().getResource("/sudoku/dataSet/" + String.valueOf(dataSet)).toURI();
        List<String> lines = Files.readAllLines(Paths.get(uri), Charset.defaultCharset());
        int r = 0;
        System.out.println("");
        for (String rLine : lines) {
            List<Variable> row = new ArrayList<Variable>();
            int c = 0;
            System.out.println("Coordinates:");
            for (String val : rLine.split(",")) {
                Variable var = new Variable (r, c);
                int n = 0;
                System.out.print("(" + r + ", " +  c + ") ");

                try {
                    n = Integer.parseInt(val); 
                } catch (Exception e) {
                    
                }
                if (n > 0 && n < 10) var.setVal(n);
                else unassignedVars.add(var);
                
                row.add(var);
                c++;
            }
            r++;
            System.out.println("");
            assignment.add(row);
        }
    }
    
    public void print() {
        System.out.println("Current Assignment:");
		for (List<Variable> r : assignment) {
			for (Variable var : r) System.out.print(var.getVal() + " ");
			System.out.println("");
		}
		if (this.unassignedVars != null) System.out.println("(Unassigned:" + this.unassignedVars.size() + ")");
    }
    
    public int nodeCount() {
        return this.nodeCount;
    }
    
    boolean backtrack() {
        List<Variable> curUnassigned = getUnassignedVars();
        if (curUnassigned.size() == 0) return true;
        Variable var = curUnassigned.remove(0);
        this.nodeCount++;
        List<Integer> domain = orderDomainValues(var, curUnassigned);
        for (int value : domain) {
            if (consistencyCheck (var, value)) {
                var.setVal(value);
                if (backtrack()) return true;
            }
            var.setVal(0); //set var back
        }
        curUnassigned.add(0,var);
        return false;
    }

    protected List<Variable> getUnassignedVars() {
        Collections.shuffle(this.unassignedVars, random);
        return this.unassignedVars;
        /*
         List<Variable> newlist = new ArrayList<Variable> (this.unassignedVars);
        Collections.shuffle(newlist, random);
        return newlist;
        */
    }
    
    protected List<Integer> orderDomainValues(final Variable var, final List<Variable> vars){
        /*List<Integer> newDomain = new ArrayList<Integer> (var.domain);
        Collections.shuffle(newDomain, random);
        return newDomain;*/
        Collections.shuffle(var.domain, random);
        return var.domain;
    }
    
    protected boolean consistencyCheck(Variable var, int val) {
        // Same Row
        Set <Integer> hashsetRow = new HashSet<Integer>();
        hashsetRow.add(val);
        List<Variable> sameRow = assignment.get(var.x);
		for (Variable vari : sameRow) {
		    if (hashsetRow.contains(vari.val) && vari.val > 0) return false; else hashsetRow.add(vari.val);
		} 
		
        // Same Column
        Set <Integer> hashsetColumn = new HashSet<Integer>();
        hashsetColumn.add(val);
        List<Variable> sameColumn = new ArrayList<Variable>();
        for (List<Variable> row : assignment) {
            sameColumn.add(row.get(var.y));
        }
		for (Variable vari : sameColumn) if (hashsetColumn.contains(vari.val) && vari.val > 0) return false; else hashsetColumn.add(vari.val);
		
        // Same Box
        Set <Integer> hashsetBox = new HashSet<Integer>();
        hashsetBox.add(val);
        List<Variable> sameBox = new ArrayList<Variable>();
        for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) sameBox.add(assignment.get(3* (var.x/3) + u).get(3* (var.y/3) + v));
        //System.out.println("Same Box:");
		for (Variable vari : sameBox) {
            //System.out.print("(" + vari.x + ", " +  vari.y + ") ");
		    if (hashsetBox.contains(vari.val) && vari.val > 0) return false; else hashsetBox.add(vari.val);
		}
			 
	    return true;
    }
	

}