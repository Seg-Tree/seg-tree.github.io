import java.io.Serializable;  
  
public class Person implements Serializable {  
    private String name;  
    private int age;
    private int[] score;
    private int tot;
    public Person(){
        this.score = new int[0x100];
    }
    public Person(String name, int age, int[] score, int tot){
        this.name = name;
        this.age = age;
        this.score = new int[0x100];
        this.tot = tot;
        for(int i=0; i<tot; i++)
            this.score[i] = score[i];
    }
    @Override
    public String toString(){
        String ret = "Person{" + "name='" + name + '\'' + ", age=" + age + "}\n";
        for(int i=0; i<tot; i++)
            ret += "Num." + i + ": " + score[i] + "\n";
        return ret;
    }
}