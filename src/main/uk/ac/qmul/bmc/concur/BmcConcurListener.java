package uk.ac.qmul.bmc.concur;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BmcConcurListener extends PropertyListenerAdapter{
	
	protected int batch = 4096; // number of PCs to encode
	
	protected boolean hasBug = false;
	protected int counter;
	protected int totalPC;
	protected int pcCounter;

	protected AtomicBoolean valid;
	
	// ExecutorService threadPool = Executors.newCachedThreadPool();
	final ExecutorService threadPool = Executors.newFixedThreadPool(32);
	final ExecutorCompletionService<Integer> service = new ExecutorCompletionService<Integer>(
			threadPool);

	public BmcConcurListener(Config config, JPF jpf) {
		counter = 0;
		totalPC = 0;
		pcCounter = 0;

		// batch = config.getInt("bmc.batch", 10);
		batch = config.getInt("bmc.batch", 200);
		
		valid = new AtomicBoolean();		
		valid.set(true);
		
		jpf.getReporter().getPublishers().clear();
	}

	@Override
	public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
		// if (valid.get() == false && multipleError == false) {
		if (valid.get() == false) {
			// TODO: stop the search
			// vm.getJPF().getSearch().terminate();
			vm.terminateProcess(currentThread);
		}
	}
	
	@Override
	public void propertyViolated(Search search) {

		if (hasBug == false) {
			hasBug = true;
		}
		// System.out.println(search.getLastError().getDetails());
		VM vm = search.getVM();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator)
				&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			
			totalPC++;
			
			processPC(pc);
		}
	}
	
	protected abstract void processPC(PathCondition pc);
	
	protected abstract void submitLastPCs();
	
	public void searchFinished(Search search)   {
		// handle the rest
		if(pcCounter > 0){
			submitLastPCs();
			counter++;
		}

		// if Z3 is not called. Everything is simple
		if(counter == 0) 
		{
			if (hasBug){
				System.out.println("\nVERIFICATION FAILED by JPF");
			}
			else{
				System.out.println("\nVERIFICATION SUCCESSFUL by JPF");
			}
			return;
		}
		
		// once we have submitted all jobs to the thread pool, it should be shutdown
		threadPool.shutdown();
		//*
		try {
			while (!threadPool.isTerminated()) {
				// do something
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//*/
		System.out.println("Threads finished\n");
		System.out.println("There are total " + totalPC + " error paths. Sent to " + counter + " workers\n");
		
		if(valid.get()==true){
			System.out.println("VERIFICATION SUCCESSFUL BY Z3");
		}
		else
			System.out.println("VERIFICATION FAILED BY Z3");
	}

}