package sudoku;

import java.lang.*; 
import java.util.*;

abstract class AbstractSudoku implements SudokuBase {
   
   /**
     * total node count
     */
   protected int nodeCount;
   
   /**
     * Current Assignment
     */
   protected List<List<Variable>> assignment;
   
   /**
     * Current Unassigned Vars
     */
   protected List<Variable> unassignedVars;
   
   /**
    * @return      return results as a list of unassigned vars
    */
   protected abstract List<Variable> getUnassignedVars();
   
      /**
    * @param vars unassigned vars to be sorted
    */
   protected abstract void sortUnassignedVars(List<Variable> vars);
   
     /**
    * @param var the variable want to be assigned
    * @param vars the results after calling getUnassignedVars(), the list of unassigned vars
    * @param domain values want to be ordered
    */
   protected abstract List<Integer> orderDomainValues(final Variable var, final List<Variable> vars);
   
   
   /**
    * @param var the variable to be assigned
    * @param val the value to be assigned to var
    * @return      return if the coming assignment is consistennt if assign a val to a var
    */
   protected abstract boolean consistencyCheck(Variable var, int val);
   
   /**
    * @param vars the results after calling getUnassignedVars(), the list of unassigned vars
    * @return      return if forward checking finds an unassigned var with empty domain
    */
   protected abstract boolean inference(List<Variable> vars);
   
}