public class Pix{
public static void Pi(int X){
int I;
int J;
int COUNTER;
int MODRESULT;
I = 1;
while(I<=X){
COUNTER = 0;
J = 1;
while(J<=I){
MODRESULT = I%J;
if(MODRESULT==0){
COUNTER = COUNTER+1;
}
J = J+1;
}
if(I==X){
if(COUNTER==2){
System.out.print("X");}
}
if(COUNTER==2){
System.out.print("I,");}
I = I+1;
}
}
public static void main(String[] args){
Pi();
}
}
