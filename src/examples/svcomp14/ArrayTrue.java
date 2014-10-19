package svcomp14;

public class ArrayTrue {

	public static final int SIZE = 10;

	void test(int[] array, int menor) {

		for (int j = 0; j < SIZE; j++) {
			if (array[j] <= menor)
				menor = array[j];
		}

		assert (array[0] >= menor);
	}
	
	public static void main(String[] args)
	{
		int menor = 0; 
		int[] array = new int[SIZE];
		ArrayTrue af = new ArrayTrue();
		af.test(array, menor);
	}
}
