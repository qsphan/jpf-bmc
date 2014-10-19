package uk.ac.qmul.bmc.concur;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import qs.phan.smtlib2.SymbolicVariable;
import qs.phan.spf.Smtlib2Converter;

public class BmcConcurListenerUsingSmtlib2 extends BmcConcurListener {

	private String z3;
	private ArrayList<String> lstOfPaths;
	private HashSet<SymbolicVariable> setOfSymVals;
	Hashtable<String,String> impVars;

	public BmcConcurListenerUsingSmtlib2(Config config, JPF jpf) {
		super(config,jpf);
		
		z3 = config.getProperty("z3");
		// z3 = "/homes/qsp30/Programs/z3/bin/z3";
		
		lstOfPaths = new ArrayList<String>();
		setOfSymVals = new HashSet<SymbolicVariable>();
		impVars = new Hashtable<String,String>();

	}

	@Override
	protected void processPC(PathCondition pc) {
		
		// SMTLIB2Converter converter = new SMTLIB2Converter(setOfSymVals,impVars);
		Smtlib2Converter converter = new Smtlib2Converter(setOfSymVals,impVars);
		String prefixPC = converter.convertPC(pc);
		lstOfPaths.add(prefixPC);
		pcCounter++;
		if (pcCounter >= batch) {
			// OK, enough PC, delegate to handler
			Callable<Integer> handler = new WorkerThreadUsingSmtlib2(counter, valid);
			((WorkerThreadUsingSmtlib2) handler).setZ3(z3);
			((WorkerThreadUsingSmtlib2) handler).setPC(lstOfPaths, setOfSymVals,impVars);
			service.submit(handler);
			counter++;
			// reset
			lstOfPaths = new ArrayList<String>();
			setOfSymVals = new HashSet<SymbolicVariable>();
			pcCounter = 0;
		}
		
	}

	@Override
	protected void submitLastPCs() {
		Callable<Integer> handler = new WorkerThreadUsingSmtlib2(counter, valid);
		((WorkerThreadUsingSmtlib2)handler).setZ3(z3);
		((WorkerThreadUsingSmtlib2)handler).setPC(lstOfPaths,setOfSymVals,impVars);
		service.submit(handler);
	}
}
