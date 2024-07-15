public class Hello{
    public static void main(String[] args){
        int[] a = new int[5];
        for(int i=0; i<5; i++)
            a[i]=i;
        Arrprint(a);
    }
    public static void Arrprint(int[] a){
        for(int i=0; i<10; i++)
            System.out.print(a[i]);
    }
}