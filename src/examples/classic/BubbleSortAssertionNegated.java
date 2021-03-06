package classic;


public class BubbleSortAssertionNegated {
	
	static int N = 30;
	
	void sort(int[] a) {
		int i, j, t;
		for (j = 0; j < N - 1; j++) {
			for (i = 0; i < N - j - 1; i++) {
				if (a[i] > a[i + 1]) {
					t = a[i];
					a[i] = a[i + 1];
					a[i + 1] = t;
				}
			}
		}
	}
	
	boolean test(int[] a) {
		int i;
		sort(a);
		for (i = 0; i < N -1; i++)
			if (a[i] > a[i + 1]) {
				return true;
			}
		return false;
	}

	public static void main(String[] args) {
		int[] a = new int[N];
		BubbleSortAssertionNegated e = new BubbleSortAssertionNegated();
		// a will be made symbolic by SPF
		assert (e.test(a)==true);
	}
}
