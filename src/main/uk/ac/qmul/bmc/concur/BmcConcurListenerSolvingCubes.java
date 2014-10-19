package uk.ac.qmul.bmc.concur;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import qs.phan.z3.Z3Converter;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

public class BmcConcurListenerSolvingCubes extends BmcConcurListener {
	
	protected Hashtable<String, Expr> setOfSymVals;
	protected ArrayList<BoolExpr> lstOfExpr;
	protected Context ctx;

	public BmcConcurListenerSolvingCubes(Config config, JPF jpf) {
		super(config, jpf);
		
		// batch = config.getInt("bmc.batch", 10);
		batch = config.getInt("bmc.batch", 200);
		
		setOfSymVals = new Hashtable<String, Expr>();
		lstOfExpr = new ArrayList<BoolExpr>();
		
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
		Z3Converter converter = new Z3Converter(setOfSymVals, ctx);
		BoolExpr tmp;
		try {
			tmp = converter.convertPC(pc);

			lstOfExpr.add(tmp);
			pcCounter++;
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		if (pcCounter >= batch) {
			// OK, enough PC, delegate to handler
			Callable<Integer> handler = new WorkerThreadSolvingCubes(counter, valid,
					ctx, lstOfExpr);
			service.submit(handler);
			counter++;
			// reset
			resetPCs();
		}
	}
	
	protected void resetPCs(){
		lstOfExpr = new ArrayList<BoolExpr>();
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
	protected void submitLastPCs() {
		Callable<Integer> handler = new WorkerThreadSolvingCubes(counter, valid, ctx, lstOfExpr);
		service.submit(handler);
	}

}
