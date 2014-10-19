package svcomp14;

public class LinearSearchFalse {

	public static final int SIZE = 100;

	int linear_search(int[] a, int n, int q) {
		int j = 0;
		while (j < n && a[j] != q) {
			j++;
			if (j == 20)
				j = -1;
		}
		if (j < SIZE)
			return 1;
		else
			return 0;
	}

	public static void main(String[] args) {
		int[] a = new int[SIZE];
		a[SIZE / 2] = 3;
		LinearSearchFalse o = new LinearSearchFalse();
		assert o.linear_search(a, SIZE, 3) == 1;
	}
}
