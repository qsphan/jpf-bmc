package svcomp14;

public class Sum04False {

	public static final int SIZE = 1000;
	
	void test(int a){
	  int i, sn=0;
	  for(i=1; i<=SIZE; i++) {
	    if (i<1000)
	    sn = sn + a;
	  }
	  assert(sn==SIZE*a || sn == 0);
	}
	
	public static void main(String[] args)
	{
		Sum04False o = new Sum04False();
		o.test(2);
	}
}
