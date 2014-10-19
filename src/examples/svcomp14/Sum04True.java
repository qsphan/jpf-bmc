package svcomp14;

public class Sum04True {

	public static final int SIZE = 1000;
	
	void test(int a){
	  int i, sn=0;
	  for(i=1; i<=SIZE; i++) {
	    sn = sn + a;
	  }
	  assert(sn==SIZE*a || sn == 0);
	}
	
	public static void main(String[] args)
	{
		Sum04True o = new Sum04True();
		o.test(2);
	}
}