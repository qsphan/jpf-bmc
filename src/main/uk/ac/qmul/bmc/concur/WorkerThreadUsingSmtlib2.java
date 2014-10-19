package uk.ac.qmul.bmc.concur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import qs.phan.smtlib2.SymbolicVariable;

public class WorkerThreadUsingSmtlib2 implements Callable<Integer>{
	
	private ArrayList<String> lstOfPaths;
	private HashSet<SymbolicVariable> setOfSymVals;
	private Hashtable<String,String> impVars;
	private int id;
	private AtomicBoolean valid;
	String z3;
	
	public WorkerThreadUsingSmtlib2 (int id1, AtomicBoolean valid1){
		id = id1;
		valid = valid1;
	}
	
	public void setPC(ArrayList<String> paths, HashSet<SymbolicVariable> vals, Hashtable<String,String> imp){
		lstOfPaths = paths;
		setOfSymVals = vals;
		impVars = imp;
	}
	
	public void setZ3(String path){
		z3 = path;
	}
	
	@Override
	public Integer call() {
		//TODO: modify this 3 lines for multiple errors
		if(valid.get() == false){
			return id;
		}
		
		genBMCtoFile();
		verify();
		return id; //return for void, seems stupid
	}
	
	private String getFileName(){
		return "build/tmp/result" + id + ".smt2";
	}
	
	public void genBMCtoFile(){	
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(getFileName()));
			// bw.write(";*************************************************\n");
			// bw.write("; This formula is the program and the negation of the specification\n");
			// bw.write("; if Z3 returns SAT, it means the program violates the specification\n");
			
			// declare variables
			for(SymbolicVariable sv : setOfSymVals){
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
			//	map boolean abstraction		
			Iterator<Map.Entry<String, String>> iter = impVars.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				bw.write("(define-fun " + value + " () Bool (" + key + ") )\n");
			}
			// assertion
			bw.write("(assert (or \n");
			for(String pc : lstOfPaths){
		    	bw.write(pc + "\n");	
		    }
			bw.write("))\n");
			bw.write("(check-sat)\n(get-model)\n");
			// bw.write(";*************************************************\n");
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

	private void verify() {
		boolean bValid = false;
		try {
			String line;
			Process p = Runtime.getRuntime().exec(z3 + " " + getFileName());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			
			/*
			// output z3 result
			System.out.println("\n>>> Begin Z3 output\n");
			while ((line = input.readLine()) != null) {
				System.out.println(line);
				if (line.indexOf("unsat") != -1) {
					// valid.set(true);
					// break if no need output
				}
			}
			System.out.println("\n>>> End Z3 output\n");
			//*/
			
			// output z3 result
			String str= "\nResult of thread " + id;
			str += "\n>>> Begin Z3 output\n";
			while ((line = input.readLine()) != null) {
				str = str + line + "\n";
				if (line.indexOf("unsat") != -1) {
					bValid = true;
					break;
					// break if no need output
				}
			}
			str += "\n>>> End Z3 output\n";
			System.out.println(str);
			
			// clean up
			input.close();		
			// cleanUp();
			
		} catch (Exception err) {
			System.out.println("Error in thread " + id);
			err.printStackTrace();
		}
		if(bValid == false && (valid.get() == true)){
			valid.set(false);
		}
	}	
	
	protected void cleanUp() {
		// clean up
		File file = new File(getFileName());
		if (!file.delete()) {
			System.out.println("Delete operation is failed.");
		}
	}
}
