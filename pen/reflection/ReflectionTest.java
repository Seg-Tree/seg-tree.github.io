import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionTest{
    public static void main(String[] args) throws Exception{
        Person person = new Person("aaaa",0x10);
        Class c = person.getClass();

        Constructor pConstructor1 = c.getConstructor();
        Constructor pConstructor2 = c.getConstructor(String.class,int.class);
        Person p1 = (Person)pConstructor1.newInstance();
        Person p2 = (Person)pConstructor2.newInstance("person2",0x20);
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);

        Field[] fs = c.getDeclaredFields();
        //getFields: no private fields
        //getDeclaredFields: all
        for(Field f:fs){
            System.out.println(f);
        }
        Field f = c.getDeclaredField("age");
        f.setAccessible(true);
        //private field have to be set accessible
        f.set(p1,0x10);
        System.out.println("p1: " + p1);

        Method[] ms = c.getDeclaredMethods();
        for(Method m:ms){
            System.out.println(m);
        }
        Method m1 = c.getMethod("AgeInc");
        Method m2 = c.getMethod("AgeInc", int.class);
        m1.invoke(p1);
        m2.invoke(p2,0x10);
    }
}
