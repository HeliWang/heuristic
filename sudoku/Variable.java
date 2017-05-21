package sudoku;

import java.lang.*;
import java.util.*;

public class Variable {
	public int val;
	public List<Integer> domain;
	public int x;
	public int y;
	
	public Variable(int x, int y) {
        this.x = x;
        this.y = y;
        this.val = 0; // 0 == unasssigned
        domain = new ArrayList<Integer>();
        for(int i = 1; i <= 9; i++) domain.add(i);
	}
	
	public void setVal(int val) {
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
}