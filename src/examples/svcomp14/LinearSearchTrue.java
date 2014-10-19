package svcomp14;

public class LinearSearchTrue {
	
	public static final int SIZE = 100;

	int linear_search(int[] a, int n, int q) {
		int j = 0;
		while (j < n && a[j] != q) {
			j++;
		}
		if (j < SIZE)
			return 1;
		else
			return 0;
	}

	public static void main(String[] args) {
		int[] a = new int[SIZE];
		a[SIZE / 2] = 3;
		LinearSearchTrue o = new LinearSearchTrue();
		assert o.linear_search(a, SIZE, 3) == 1;
	}
}
