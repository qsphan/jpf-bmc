package svcomp14;

public class CountUpDown {

	public static final int SIZE = 1000;
	
	public void test(int x) {
		int y = 0;
		for (int i = 0; i < SIZE; i++) {
			x--;
			y++;
		}
		assert (y != (x - SIZE));
	}
	
	public static void main(String[] args)
	{
		CountUpDown o = new CountUpDown();
		o.test(5);
	}
}
