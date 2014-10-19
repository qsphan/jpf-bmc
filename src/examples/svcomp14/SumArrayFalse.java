package svcomp14;

public class SumArrayFalse {
	
	static int M = 1000;
	
	public static void sum(int[] A, int [] B, int[] C ){
		int i;
		for(i=0;i<M;i++)
		        C[i]=A[i]+B[i]; 
		for(i=0;i<M;i++)
			assert(C[i]==A[i]-B[i]);
	}
	
	public static void main(String[] args)
	{
	  int A[], B[], C[];
	  A = new int[M];
	  B = new int[M];
	  C = new int[M];
	  // A, B,C will be made symbolic by SPF
	  sum(A,B,C);
	}
}
