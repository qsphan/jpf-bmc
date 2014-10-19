package uk.ac.qmul.bmc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.VM;

public abstract class BmcListener extends PropertyListenerAdapter {

	Config conf;
	protected boolean hasBug = false;
	int count = 0;

	public BmcListener(Config config, JPF jpf) {

		jpf.getReporter().getPublishers().clear();
		conf = config;

	}
	
	public void propertyViolated(Search search) {

		// System.out.println(">>> Potential error: " +
		// search.getLastError().getDetails());
		hasBug = true;

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
			count++;
			
			addPC(pc);
		}
	}
	
	
	public void searchFinished(Search search) {
		System.out.println("\n*******BOUNDED MODEL CHECKING******\n");
		System.out.println("The number of error paths is: " + count+ "\n");
		
		doBMC();
		
		System.out.println("***********************************");
	}

	protected abstract void addPC(PathCondition pc);
	
	protected abstract void doBMC();
}
