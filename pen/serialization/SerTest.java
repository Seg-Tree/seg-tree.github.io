// package src;  
import java.io.*;
import java.net.*;
import java.util.*;

public class SerTest {  
    public static void serialize(Object obj,String Filename) throws IOException{  
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Filename));  
        oos.writeObject(obj);
        System.out.println("Object serialized to file " + Filename);
    }
    public static Object unserialize(String Filename) throws IOException, ClassNotFoundException{  
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Filename));  
        Object obj = ois.readObject();
        System.out.println("Object unserialized from file " + Filename);
        return obj;
    }
    public static void main(String[] args) throws Exception{  
        int[] score = new int[0x100];
        int tot = 8;
        String Filename = "ser.bin";
        for(int i=0; i<tot; i++)
            score[i] = i*2;
        Person p1 = new Person("AAAA",0x10,score,tot);
        System.out.println(p1);
        serialize(p1,Filename);
        Person p2 = (Person)unserialize(Filename);
        System.out.println(p2);
    }
}