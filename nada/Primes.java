public class PRIMES{
	static int I;
	static int J;
	static int COUNTER;
	static int MODRESULT;
	public static void main(String[] args){
		I = 1;
		while(I<=100){
			COUNTER = 0;
			J = 1;
			while(J<=I){
				MODRESULT = I%J;
				if(MODRESULT==0){
					COUNTER = COUNTER+1;
				}
				J = J+1;
			}
			if(I==100){
				System.out.print("0");
			}
			else if(COUNTER==2){
				System.out.print("1,");
			}
			else{
				System.out.print("0,");
			}
			I = I+1;
		}
	}
}
