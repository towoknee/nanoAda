public class Foo{
static int r;
public static int Bar(int X){
X = 2;
return X;
}
public static void Hello(int Y){
;
}
public static void main(String[] args){
Bar(r);
System.out.println(r);
}
}
