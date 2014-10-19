package svcomp14;

public class EurekaTrue {
	public static final int INFINITY = 899;
	public static final int nodecount = 3;
	public static final int edgecount = 5;

	int test(int[] Source, int[] Dest, int[] Weight, int[] distance) {

		int source = 0;
		int x, y;
		int i, j;

		for (i = 0; i < nodecount; i++) {
			if (i == source) {
				distance[i] = 0;
			} else {
				distance[i] = INFINITY;
			}
		}

		for (i = 0; i < nodecount; i++) {
			for (j = 0; j < edgecount; j++) {
				x = Dest[j];
				y = Source[j];
				if (distance[x] > distance[y] + Weight[j]) {
					distance[x] = distance[y] + Weight[j];
				}
			}
		}
		for (i = 0; i < edgecount; i++) {
			x = Dest[i];
			y = Source[i];
			if (distance[x] > distance[y] + Weight[i]) {
				return 0;
			}
		}

		for (i = 0; i < nodecount; i++) {
			assert (distance[i] >= 0);
		}
		
		return 0;
	}

	public static void main(String[] args) {
		EurekaFalse o = new EurekaFalse();
		int[] Source = new int [edgecount];
		int[] Dest = new int [edgecount];
		int[] Weight = new int [edgecount];
		int[] distance = new int[nodecount];
		o.test(Source, Dest, Weight, distance);
	}
}
