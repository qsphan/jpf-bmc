

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

public class TestBMC {

	public TestBMC() {
	}

	boolean test() {
		String[] args = new String[0];
		Config conf = JPF.createConfig(args);
		conf.setProperty("site", "${jpf-bmc}../site.properties");
		conf.setProperty("classpath", "${jpf-bmc}/build/main;${jpf-bmc}/build/examples;${jpf-symbc}/build/examples");
		
		// conf.setProperty("search.multiple_errors", "false");
		// conf.setProperty("z3", "/home/qsp/Programs/z3/bin/z3");
		conf.setProperty("z3", "/homes/qsp30/Programs/z3/bin/z3");
		
		/*
		conf.setProperty("target", "trivial.ExampleOne");
		conf.setProperty("symbolic.method", "trivial.ExampleOne.run(sym#sym)");
		//*/			
		
		/*
		conf.setProperty("target", "classic.BubbleSortAssertionNegated");
		conf.setProperty("symbolic.method", "classic.BubbleSortAssertionNegated.test(sym)");
		//*/	
		
		//*
		conf.setProperty("target", "classic.BubbleSort");
		conf.setProperty("symbolic.method", "classic.BubbleSort.test(sym)");
		//*/
		
		/* multi-threaded program
		conf.setProperty("target", "FlapController");
		conf.setProperty("symbolic.method", "FlapController.startThreads(sym#sym#sym)");
		//*/
		
		/*
		conf.setProperty("target", "classic.BellmanFord");
		conf.setProperty("symbolic.method", "classic.BellmanFord.bellmanFord(sym#sym#sym)");
		//*/		

		/*
		conf.setProperty("target", "rbt.TreeMap");
		conf.setProperty("search.depth_limit", "50");
		conf.setProperty("symbolic.method", "rbt.TreeMap.genTreeS2_1(sym#sym),rbt.TreeMap.put(sym#con),TreeMap.remove(sym),TreeMap.get(sym)");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.SumArrayTrue");
		conf.setProperty("symbolic.method", "svcomp14.SumArrayTrue.sum(sym#sym#sym)");
		//*/	
		
		/*
		conf.setProperty("target", "svcomp14.SumArrayFalse");
		conf.setProperty("symbolic.method", "svcomp14.SumArrayFalse.sum(sym#sym#sym)");
		//*/		
		
		/*
		conf.setProperty("target", "svcomp14.ArrayFalse");
		conf.setProperty("symbolic.method", "svcomp14.ArrayFalse.test(sym#sym)");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.ArrayTrue");
		conf.setProperty("symbolic.method", "svcomp14.ArrayTrue.test(sym#sym)");
		//*/	
		
		/*
		conf.setProperty("target", "svcomp14.Sum04False");
		conf.setProperty("symbolic.method", "svcomp14.Sum04False.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.Sum04True");
		conf.setProperty("symbolic.method", "svcomp14.Sum04True.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.EurekaTrue");
		conf.setProperty("symbolic.method", "svcomp14.Eureka.test()");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.EurekaFalse");
		conf.setProperty("symbolic.method", "svcomp14.EurekaFalse.test(sym#sym#sym#sym)");
		//*/		
		
		/*
		conf.setProperty("target", "svcomp14.CountUpDown");
		conf.setProperty("symbolic.method", "svcomp14.CountUpDown.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.LinearSearchFalse");
		conf.setProperty("symbolic.method", "svcomp14.LinearSearchFalse.linear_search(sym#con#con)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.LinearSearchTrue");
		conf.setProperty("symbolic.method", "svcomp14.LinearSearchTrue.linear_search(sym#con#con)");
		//*/			
		
		/* StateMachine new
		conf.setProperty("symbolic.maxint","4");
		conf.setProperty("symbolic.minint","0");
		conf.setProperty("classpath", "${jpf-bmc}/build/main;${jpf-home}/UMLStateMachines/bin");
		conf.setProperty("target", "MerArbiter.MerArbiter");
		conf.setProperty("symbolic.method", "MerArbiter.MerArbiter.setUser1Input(sym#sym),MerArbiter.MerArbiter.setUser2Input(sym#sym)");
		//*/
		
		conf.setProperty("symbolic.dp", "no_solver");
		
		//*
		// conf.setProperty("listener", "gov.nasa.jpf.symbc.SymbolicListenerClean");
		// conf.setProperty("search.multiple_errors", "false");
		//*/
		
		// conf.setProperty("listener", "uk.ac.qmul.bmc.BmcListenerUsingSmtlib2");
		// conf.setProperty("listener", "uk.ac.qmul.bmc.BmcListenerUsingZ3");
		// conf.setProperty("listener", "uk.ac.qmul.bmc.concur.BmcConcurListenerUsingZ3");
		// conf.setProperty("listener", "uk.ac.qmul.bmc.concur.BmcConcurListenerSolvingCubes");
		conf.setProperty("listener", "uk.ac.qmul.bmc.concur.BmcConcurListenerUsingSmtlib2");
	 
		JPF jpf = new JPF(conf);
		// remove all publisher of JPF
		
		// BMCListener bmcl = new BMCListener(conf,jpf);
		// jpf.addPropertyListener(bmcl);

		boolean violate = true;
		try {
			jpf.run();
			violate = jpf.foundErrors();
		} catch (JPFConfigException cx) {
			System.out.println("JPFConfigException: ");
			cx.printStackTrace();
		} catch (JPFException jx) {
			System.out.println("JPFException: ");
			jx.printStackTrace();
		}
		return !violate;
	}
	

	public static void main(String[] args) {
		TestBMC bmc = new TestBMC();
		long time = System.currentTimeMillis();
		// bmc.test();
		bmc.testSPF();
		System.out.println("Total time: " + (System.currentTimeMillis() - time) + " ms");
	}
	
	public boolean testSPF(){
		String[] args = new String[0];
		Config conf = JPF.createConfig(args);
		conf.setProperty("site", "${jpf-bmc}../site.properties");
		conf.setProperty("classpath", "${jpf-bmc}/build/main;${jpf-bmc}/build/examples;${jpf-symbc}/build/examples");
	
		/*
		conf.setProperty("target", "trivial.ExampleOne");
		conf.setProperty("symbolic.method", "trivial.ExampleOne.run(sym#sym)");
		//*/			
		
		//*
		conf.setProperty("target", "classic.BubbleSortAssertionNegated");
		conf.setProperty("symbolic.method", "classic.BubbleSortAssertionNegated.test(sym)");
		//*/	
		
		/*
		conf.setProperty("target", "classic.BubbleSort");
		conf.setProperty("symbolic.method", "classic.BubbleSort.test(sym)");
		//*/
		
		/* multi-threaded program
		conf.setProperty("target", "FlapController");
		conf.setProperty("symbolic.method", "FlapController.startThreads(sym#sym#sym)");
		//*/
		
		/*
		conf.setProperty("target", "classic.BellmanFord");
		conf.setProperty("symbolic.method", "classic.BellmanFord.bellmanFord(sym#sym#sym)");
		//*/		

		/*
		conf.setProperty("target", "rbt.TreeMap");
		conf.setProperty("search.depth_limit", "50");
		conf.setProperty("symbolic.method", "rbt.TreeMap.genTreeS2_1(sym#sym),rbt.TreeMap.put(sym#con),TreeMap.remove(sym),TreeMap.get(sym)");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.SumArrayTrue");
		conf.setProperty("symbolic.method", "svcomp14.SumArrayTrue.sum(sym#sym#sym)");
		//*/	
		
		/*
		conf.setProperty("target", "svcomp14.SumArrayFalse");
		conf.setProperty("symbolic.method", "svcomp14.SumArrayFalse.sum(sym#sym#sym)");
		//*/		
		
		/*
		conf.setProperty("target", "svcomp14.ArrayFalse");
		conf.setProperty("symbolic.method", "svcomp14.ArrayFalse.test(sym#sym)");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.ArrayTrue");
		conf.setProperty("symbolic.method", "svcomp14.ArrayTrue.test(sym#sym)");
		//*/	
		
		/*
		conf.setProperty("target", "svcomp14.Sum04False");
		conf.setProperty("symbolic.method", "svcomp14.Sum04False.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.Sum04True");
		conf.setProperty("symbolic.method", "svcomp14.Sum04True.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.EurekaTrue");
		conf.setProperty("symbolic.method", "svcomp14.Eureka.test()");
		//*/	

		/*
		conf.setProperty("target", "svcomp14.EurekaFalse");
		conf.setProperty("symbolic.method", "svcomp14.EurekaFalse.test(sym#sym#sym#sym)");
		//*/		
		
		/*
		conf.setProperty("target", "svcomp14.CountUpDown");
		conf.setProperty("symbolic.method", "svcomp14.CountUpDown.test(sym)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.LinearSearchFalse");
		conf.setProperty("symbolic.method", "svcomp14.LinearSearchFalse.linear_search(sym#con#con)");
		//*/
		
		/*
		conf.setProperty("target", "svcomp14.LinearSearchTrue");
		conf.setProperty("symbolic.method", "svcomp14.LinearSearchTrue.linear_search(sym#con#con)");
		//*/		
		
		/*
		conf.setProperty("target", "umlStateMachines.merArbiter.MerArbiterSym");
		conf.setProperty("symbolic.method", "umlStateMachines.merArbiter.MerArbiterSym.setUser1Input(sym#sym),umlStateMachines.merArbiter.MerArbiterSym.setUser2Input(sym#sym),umlStateMachines.merArbiter.MerArbiterSym.flag(sym)");
		conf.setProperty("vm.storage.class","nil");
		//*/		
		
		/* StateMachine new
		conf.setProperty("symbolic.maxint","4");
		conf.setProperty("symbolic.minint","0");
		conf.setProperty("classpath", "${jpf-bmc}/build/main;${jpf-home}/UMLStateMachines/bin");
		conf.setProperty("target", "MerArbiter.MerArbiter");
		conf.setProperty("symbolic.method", "MerArbiter.MerArbiter.setUser1Input(sym#sym),MerArbiter.MerArbiter.setUser2Input(sym#sym)");
		//*/
		
		// conf.setProperty("symbolic.dp","cvc3");
		// conf.setProperty("symbolic.dp","yices");
		conf.setProperty("symbolic.dp","z3");
		conf.setProperty("search.multiple_errors", "false");
		// conf.setProperty("listener","gov.nasa.jpf.symbc.SymbolicListener");
	 
		JPF jpf = new JPF(conf);

		boolean violate = true;
		try {
			jpf.run();
			violate = jpf.foundErrors();
		} catch (JPFConfigException cx) {
			System.out.println("JPFConfigException: ");
			cx.printStackTrace();
		} catch (JPFException jx) {
			System.out.println("JPFException: ");
			jx.printStackTrace();
		}
		return !violate;
	}
}
