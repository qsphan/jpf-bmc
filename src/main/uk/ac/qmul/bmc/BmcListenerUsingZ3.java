package uk.ac.qmul.bmc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.Hashtable;

import qs.phan.z3.Z3Converter;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class BmcListenerUsingZ3 extends BmcListener {

	Context ctx;
	BoolExpr expr = null;
	private Hashtable<String, Expr> setOfSymVars;
	
	public BmcListenerUsingZ3(Config config, JPF jpf) {
		super(config, jpf);
		
		setOfSymVars = new Hashtable<String, Expr>();
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		try {
			ctx = new Context(cfg);
		} catch (Z3Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void addPC(PathCondition pc) {
		Z3Converter converter = new Z3Converter(setOfSymVars, ctx);
		BoolExpr tmp;
		try {
			
			tmp = converter.convertPC(pc);
			
			/* Start using Corina's code
			ProblemGeneral pb = new ProblemZ3New(ctx);
			pb = PCParser.parse(pc,pb);
			Solver solver = ((ProblemZ3New)pb).getSolver();
			tmp = ctx.MkAnd(solver.Assertions());
			// End using Corina's code //*/
			
			if(expr == null)
				expr = tmp;
			else
				expr = ctx.MkOr(new BoolExpr[] { expr, tmp });	
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doBMC() {
		try {
			if (expr == null) {
				if (!hasBug)
					System.out.println("\nVERIFICATION SUCCESSFUL by JPF");
				else
					System.out.println("\nVERIFICATION FAILED by JPF");
				return;
			}
			
			// System.out.println(expr.toString());
			
			//TODO: detect logic by symbolic variables
			Solver solver = ctx.MkSolver("QF_LIA");
			
			solver.Assert(expr);
			if (solver.Check() == Status.UNSATISFIABLE)
				System.out.println("\nVERIFICATION SUCCESSFUL by Z3");
			else{
				System.out.println("\nVERIFICATION FAILED by Z3\n");
				Model m = solver.Model();
				System.out.println("Model is " + m);
			}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

}
