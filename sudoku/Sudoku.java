package sudoku;

import java.lang.*; 
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;

public final class Sudoku extends AbstractSudoku {
    public Sudoku(int dataSet, int mode) throws IOException, URISyntaxException {
        this.mode = mode;
        nodeCount = 0;
        assignment = new ArrayList<List<Variable>>();
        unassignedVars = new ArrayList<Variable>();
        System.out.println("reading from /sudoku/dataSet/" + String.valueOf(dataSet));
        URI uri = this.getClass().getResource("/sudoku/dataSet/" + String.valueOf(dataSet)).toURI();
        List<String> lines = Files.readAllLines(Paths.get(uri), Charset.defaultCharset());
        int r = 0;
        //System.out.println("");
        for (String rLine : lines) {
            List<Variable> row = new ArrayList<Variable>();
            int c = 0;
            //System.out.println("Coordinates:");
            for (String val : rLine.split(",")) {
                Variable var = new Variable (r, c);
                int n = 0;
                //System.out.print("(" + r + ", " +  c + ") ");

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
            //System.out.println("");
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
    
    public boolean backtrack() {
        List<Variable> curUnassigned = getUnassignedVars();
        sortUnassignedVars(curUnassigned);
        
    	//  System.out.println(" ");
    	//  for (Variable var : curUnassigned) System.out.print("(" + var.x + ", " +  var.y + " - " +  var.val  + ") "); 
    	Variable var = null;  
        if (curUnassigned.size() == 0) return true;
        if (mode == 3) {
            var = curUnassigned.remove(0); 
        } else {
            var = curUnassigned.remove(random.nextInt(curUnassigned.size()));
        }
        this.nodeCount++;
        List<Integer> domain = orderDomainValues(var, curUnassigned);
    	  
        for (int value : domain) {
            boolean consistencyCheckResult = consistencyCheck (var, value);
            if (consistencyCheckResult) {
                var.setVal(value);
                boolean inferenceResult = this.mode == 1 || inference(var);
                /*
                if (!inferenceResult) {
                    System.out.println("Check " + var.x + " " + var.y + " value " + value);
                    print();
                    printDomain();
                }*/
                if ( inferenceResult && backtrack()) return true;
            }
            var.setVal(0); //set var back
            if (this.mode != 1 && consistencyCheckResult) inferenceReset(var);
        }

        curUnassigned.add(var);
        return false;
    }
 
    protected void printDomain() {
        System.out.println("Current Assignment:");
		for (List<Variable> r : assignment) {
			for (Variable var : r) {
			  System.out.print("(" + var.x + ", " +  var.y + " - " +  var.val  + ") ");
			  for (int d : var.domain) System.out.print(d);
			}
			System.out.println("");
		}
		if (this.unassignedVars != null) System.out.println("(Unassigned:" + this.unassignedVars.size() + ")");
    }
    
    protected List<Variable> getUnassignedVars() {
        if (mode == 3) Collections.shuffle(this.unassignedVars, random);
        return this.unassignedVars;
    }
    
    protected int countConstrantingLevel (Variable var) {
        // Same Row
        List<Variable> sameRow = assignment.get(var.x);
        
        // Same Column
        List<Variable> sameColumn = new ArrayList<Variable>();
        for (List<Variable> row : assignment) {
            sameColumn.add(row.get(var.y));
        }
        // Same Box
        List<Variable> sameBox = new ArrayList<Variable>();
        for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) sameBox.add(assignment.get(3* (var.x/3) + u).get(3* (var.y/3) + v));
        
        int constraintingLevel = 0;
        
        for (Variable v : sameRow) {
			if (v.val == 0) {
		      constraintingLevel++;
		    } 
		}
		for (Variable v : sameColumn) {
			if (v.val == 0) {
		      constraintingLevel++;
		    } 
		}
		for (Variable v : sameBox) {
			if (v.val == 0) {
		      constraintingLevel++;
		    } 
		}
		if (var.val == 0) constraintingLevel = constraintingLevel - 3;
		return constraintingLevel;
    }
    
    protected List<Integer> orderDomainValues(final Variable var, final List<Variable> vars){
        List<Integer> newDomain = new ArrayList<Integer> (var.domain);
        Collections.shuffle(newDomain, random);
        if (mode != 3) {
            return newDomain;
        } 
        else {
            // Same Row
            final List<Variable> sameRow = assignment.get(var.x);
            
            // Same Column
            final List<Variable> sameColumn = new ArrayList<Variable>();
            for (List<Variable> row : assignment) {
                sameColumn.add(row.get(var.y));
            }
            // Same Box
            final List<Variable> sameBox = new ArrayList<Variable>();
            for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) sameBox.add(assignment.get(3* (var.x/3) + u).get(3* (var.y/3) + v));
            
	        Collections.sort(newDomain, new Comparator<Integer>() {
                public int compare(Integer v1, Integer v2) {
                        
                    int constraintingLevel = 0;
                    
                    for (Variable v : sameRow) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v1) != -1) {
            		      constraintingLevel++;
            		    } 
            		}
            		for (Variable v : sameColumn) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v1) != -1) {
            		      constraintingLevel++;
            		    } 
            		}
            		for (Variable v : sameBox) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v1) != -1) {
            		      constraintingLevel++;
            		    } 
            		} 
            		
                    int c1 = constraintingLevel;
     
                    constraintingLevel = 0;
                    
                    for (Variable v : sameRow) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v2) != -1) {
            		      constraintingLevel++;
            		    } 
            		}
            		for (Variable v : sameColumn) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v2) != -1) {
            		      constraintingLevel++;
            		    } 
            		}
            		for (Variable v : sameBox) {
            			if (v.val == 0 && v != var && v.domain.indexOf(v2) != -1) {
            		      constraintingLevel++;
            		    } 
            		} 
                    int c2 = constraintingLevel;
                    
                    if (c1 < c2) return 1;
                    if (c1 > c2) return -1;
                    
                    return 0;
                }
	        });
	        return newDomain;
        }
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

    protected void sortUnassignedVars(List<Variable> vars){
        if (mode != 3) {
            return;
        } 
        else {
	        Collections.sort(vars, new Comparator<Variable>() {
                public int compare(Variable v1, Variable v2) {
                    int d1 = v1.domain.size();
                    int d2 = v2.domain.size();
                    if (d1 < d2) return -1;
                    if (d1 > d2) return 1;
                    
                    int c1 = countConstrantingLevel(v1);
                    int c2 = countConstrantingLevel(v2);
                    
                    if (c1 < c2) return -1;
                    if (c1 > c2) return 1;
                    
                    return 0;
                }
	        });
        }
        
        return;
    }
    
    protected void pushDomain(List<Variable> vars, Variable self){
         //System.out.println("pu");
        for (Variable var : vars) {
            var.domainHist.push(new ArrayList<Integer>(var.domain));
        }
    }
    
    protected void popDomain(List<Variable> vars, Variable self){
        // Reset each domain
        for (Variable var : vars) {
            if (!(var.domainHist.size() > 0)) {
                System.out.println("Er!");
                for(int i = 1; i <= 9; i++) var.domain.add(i);
                continue;
            }
            var.domain = var.domainHist.pop();
        }
    } 
    
    protected void inference(List<Variable> vars, Variable self){
        for (Variable var : vars) {
            if (var.val == 0) continue;
			for (Variable varToBeInferenced : vars) {
			    if (!varToBeInferenced.equals(var)) {
			        int valToBeRemoved = var.val;
			        try {
			            int index = varToBeInferenced.domain.indexOf(valToBeRemoved);
			            if (index > -1) varToBeInferenced.domain.remove(index);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			    }
			}
		}
    }

    protected boolean inference(Variable var) {
        // Same Row
        List<Variable> sameRow = assignment.get(var.x);
        
        // Same Column
        List<Variable> sameColumn = new ArrayList<Variable>();
        for (List<Variable> row : assignment) {
            sameColumn.add(row.get(var.y));
        }
        // Same Box
        List<Variable> sameBox = new ArrayList<Variable>();
        for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) sameBox.add(assignment.get(3* (var.x/3) + u).get(3* (var.y/3) + v));
        
        pushDomain(sameRow, var);
        pushDomain(sameColumn, var);
        pushDomain(sameBox, var);
         
        inference(sameRow, var);
        inference(sameColumn, var);
        inference(sameBox, var);
		
	    return checkDomain(sameRow) && checkDomain(sameColumn) && checkDomain(sameBox);
    }
    
    protected void inferenceReset(Variable var) {
        // Same Row
        List<Variable> sameRow = assignment.get(var.x);
        
        // Same Column
        List<Variable> sameColumn = new ArrayList<Variable>();
        for (List<Variable> row : assignment) {
            sameColumn.add(row.get(var.y));
        }
        // Same Box
        List<Variable> sameBox = new ArrayList<Variable>();
        for (int u = 0; u < 3; u++) for (int v = 0; v < 3; v++) sameBox.add(assignment.get(3* (var.x/3) + u).get(3* (var.y/3) + v));
        
        popDomain(sameRow, var);
        popDomain(sameColumn, var);
        popDomain(sameBox, var);
    }
    
    protected boolean checkDomain(List<Variable> vars){
        for (Variable var : vars) {
            if (var.val != 0) continue;
			if (var.domain.size() < 1) return false; 
		}
		return true;
    }
}