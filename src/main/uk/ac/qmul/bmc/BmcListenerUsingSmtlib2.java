package uk.ac.qmul.bmc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import qs.phan.smtlib2.SMTLIB2Converter;
import qs.phan.smtlib2.SymbolicVariable;

public class BmcListenerUsingSmtlib2 extends BmcListener {
	
	private ArrayList<String> lstOfPaths;
	private HashSet<SymbolicVariable> setOfSymVars;
	private Hashtable<String,String> impVars;

	public BmcListenerUsingSmtlib2(Config config, JPF jpf) {
		super(config,jpf);

		lstOfPaths = new ArrayList<String>();
		setOfSymVars = new HashSet<SymbolicVariable>();
		impVars = new Hashtable<String,String>();
	}
	
	protected boolean isValid() {

		boolean valid = false;

		try {
			String line;
			String z3 = conf.getProperty("z3");
			Process p = Runtime.getRuntime().exec(z3 + " build/tmp/result.smt2");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			// output z3 result
			System.out.println("\n>>> Begin Z3 output\n");
			while ((line = input.readLine()) != null) {
				System.out.println(line);
				if (line.indexOf("unsat") != -1) {
					valid = true;
					// break if no need output
				}
			}
			System.out.println("\n>>> End Z3 output\n");
			//
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}

		return valid;
	}
	
	public void genBMCtoFile(){	
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("build/tmp/result.smt2"));
			// bw.write(";*************************************************\n");
			// bw.write("; This formula is the program and the negation of the specification\n");
			// bw.write("; if Z3 returns SAT, it means the program violates the specification\n");
			bw.write("(set-option :produce-models true)\n");
			bw.write("(set-logic QF_LIA)\n\n");
			
			for(SymbolicVariable sv : setOfSymVars){
				switch(sv.getType()){
				case INT:
					bw.write("(declare-fun " + sv.getName() + " () Int)\n");
					break;
				case REAL:
					bw.write("(declare-fun " + sv.getName() + " () Real)\n");
					break;
				case REF:
					// don't know what is this
					break;
				case STRING:
					// don't know if Z3 support this?
					break;	
				default:
					throw new RuntimeException("Unhandled SymVarType: " + sv.getType());
				}
			}
			bw.write("\n");
			//	map boolean abstraction		
			Iterator<Map.Entry<String, String>> iter = impVars.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				bw.write("(define-fun " + value + " () Bool (" + key + ") )\n");
			}
			// assertion
			bw.write("\n(assert (or \n");
			for(String pc : lstOfPaths){
		    	bw.write(pc + "\n");	
		    }
			bw.write("))\n\n");
			bw.write("(check-sat)\n(get-model)\n");
			// bw.write(";*************************************************\n");
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	@Override
	protected void addPC(PathCondition pc) {
		//* 
		SMTLIB2Converter converter = new SMTLIB2Converter(setOfSymVars,impVars);
		String prefixPC = converter.convertPC(pc);
		//*/
		
		/*
		Smtlib2Converter converter = new Smtlib2Converter(setOfSymVars,impVars);
		String prefixPC = converter.convertPC(pc);
		//*/
		
		lstOfPaths.add(prefixPC);
	}

	@Override
	protected void doBMC() {
		if(!lstOfPaths.isEmpty()){
			genBMCtoFile();
		}
		else{
			if(!hasBug)
				System.out.println("\nVERIFICATION SUCCESSFUL by JPF");
			else
				System.out.println("\nVERIFICATION FAILED by JPF");
			return;
		}
		//*
		if (isValid())
			System.out.println("\nVERIFICATION SUCCESSFUL by Z3");
		else
			System.out.println("\nVERIFICATION FAILED by Z3");
		//*/	
	}

}
