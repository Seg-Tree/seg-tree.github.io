javac Hello.java

将java源代码编译为Hello.class二进制文件

java Hello

使用jvm虚拟机运行Hello.class

char类型占2字节，使用Unicode表示。

java的数组本质是C中的指针，定义一个数组（指针）后还需用new（malloc）为他分配空间。

数组越界有保护。将数组传进函数之后再越界访问仍会报错。可能是底层搞了一些什么保护。

Arrays类：类似vector

构造函数，析构函数，继承之类的概念估计类似C++

java中的类无多继承，这比C++好理解一点

抽象类：无法直接创建出来。这个类里面有些函数还没有实现，需要子孙类去实现

接口：里面只定义了一堆未实现的函数。由一个实现类来实现这些函数

一个实现类可以继承多个接口
```
public interface UserService{
	//定义了一堆函数
}
public
public class UserServiceImpl implements UserService{
	//实现了接口中定义的所有函数
}
```
