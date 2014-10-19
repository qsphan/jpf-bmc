package uk.ac.qmul.bmc.concur;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class WorkerThreadSolvingCubes implements Callable<Integer>{

	protected int id;
	protected AtomicBoolean valid;
	protected Context ctx;
	protected ArrayList<BoolExpr> lstOfExpr;
	
	public WorkerThreadSolvingCubes(int id1, AtomicBoolean valid1, Context ctx1, ArrayList<BoolExpr> lstOfExpr1){
		id = id1;
		valid = valid1;
		ctx = ctx1;
		lstOfExpr = lstOfExpr1;
	}
	
	@Override
	public Integer call() {
		//TODO: modify this 3 lines for multiple errors
		
		try {
			
			Solver solver = ctx.MkSolver("QF_LIA");
			// Solver solver = ctx.MkSolver();
			for(BoolExpr expr: lstOfExpr){
				solver.Push();
				solver.Assert(expr);
				if (solver.Check() == Status.UNSATISFIABLE){
					// System.out.println("\nThread " + id + ": No error found");
				}
				else{
					System.out.println("\nThread " + id + ": Error found");
					Model m = solver.Model();
					// System.out.println("PCs are: " + expr.toString());
					System.out.println("Model is" + m);
					if(valid.get() == true){
						valid.set(false);
						// finishedTime.set(System.currentTimeMillis());
					}
					
					solver.Dispose();
					ctx.Dispose();
					
					return id;
				}
				solver.Pop();
			}
			
			solver.Dispose();
			
		} catch (Z3Exception ex) {
			ex.printStackTrace();
		}
		ctx.Dispose();
		return id; //return for void, seems stupid
	}

}
