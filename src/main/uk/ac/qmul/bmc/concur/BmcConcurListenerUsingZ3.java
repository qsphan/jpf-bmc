package uk.ac.qmul.bmc.concur;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import qs.phan.spf.Z3ExprConverter;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

public class BmcConcurListenerUsingZ3 extends BmcConcurListener{
	
	private Hashtable<String, Expr> setOfSymVals;
	
	BoolExpr expr = null;
	Context ctx;

	public BmcConcurListenerUsingZ3(Config config, JPF jpf) {
		
		super(config,jpf);
		
		batch = config.getInt("bmc.batch", 10);
		// batch = config.getInt("bmc.batch", 200);
		
		setOfSymVals = new Hashtable<String, Expr>();
		
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		try {
			ctx = new Context(cfg);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void resetPCs(){
		expr = null;
		pcCounter = 0;
		setOfSymVals = new Hashtable<String, Expr>();
		
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		try {
			ctx = new Context(cfg);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void processPC(PathCondition pc) {
		// store paths
		
		// Z3Converter converter = new Z3Converter(setOfSymVals, ctx);
		
		Z3ExprConverter converter = new Z3ExprConverter(setOfSymVals, ctx);
		
		BoolExpr tmp;
		try {
			tmp = converter.convertPC(pc);

			if (expr == null)
				expr = tmp;
			else
				expr = ctx.MkOr(new BoolExpr[] { expr, tmp });
			pcCounter++;
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		if (pcCounter >= batch) {
			// OK, enough PC, delegate to handler
			Callable<Integer> handler = new WorkerThreadUsingZ3(counter, valid, ctx,
					expr);
			service.submit(handler);
			counter++;
			// reset
			resetPCs();
		}
	}

	@Override
	protected void submitLastPCs() {
		Callable<Integer> handler = new WorkerThreadUsingZ3(counter, valid, ctx, expr);
		service.submit(handler);
	}
}