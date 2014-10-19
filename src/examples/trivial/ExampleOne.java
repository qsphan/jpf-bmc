package trivial;

public class ExampleOne {
	
	public void run(int x, int y) {
		if(x > 5){
			if ((x + 7) + 2 * x + 3 < 3)
				assert false;
		}
	}

	public static void main(String[] args) {
		ExampleOne num = new ExampleOne();
		num.run(1,1);
	}
}