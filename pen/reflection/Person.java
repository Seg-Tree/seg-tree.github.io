import java.io.Serializable;  
  
public class Person implements Serializable {  
    private String name;  
    private int age;
    public Person(){
        this.name = "default name";
        this.age = -1;
    }
    public Person(String name, int age){
        this.name = name;
        this.age = age;
    }
    public void AgeInc(){
        System.out.println(name + "'s age increased by 1");
        age++;
    }
    public void AgeInc(int inc){
        System.out.println(name + "'s age increased from " + age + " to " + (age+inc));
        age+=inc;
    }
    @Override
    public String toString(){
        return "Person{" + "name='" + name + '\'' + ", age=" + age + "}";
    }
}