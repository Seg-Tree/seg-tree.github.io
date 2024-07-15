javac Hello.java 命令搞出来的Hello.class这个文件，其实是一个对象。这个对象的类型是Class。Class这个类，是一种“类的类”。

Class类的对象——Hello.class包含了Hello这个类所有成员变量的名称、类型；所有成员函数的名称、返回类型、参数名称及类型、和他们的汇编代码（适用于jvm这种机器的汇编代码）。

也就是说，对于一个运行中的java程序，所有代码（函数/方法）都存在于某个Class类型的对象中。

运行的Hello程序，其main函数等代码存在于Hello.class这个对象中，而其间接调用的属于String类的方法，存在于String类的类的对象（让我们称之为String.class）中。
Hello.class文件中存放有string，system等对象的路径。
这些对象会在程序启动时加载进内存。

![image](https://github.com/Seg-Tree/seg-tree.github.io_1/blob/main/pen/reflection/java_class.png)

---

反射（reflection）是一个相对于正射的概念。

正射是指在java编程时，已经知道了使用的类的名称、方法等信息，即已经拥有了该类的.class文件，将类实例化成对象后再使用。我们平时使用的都是正射。

反射则是指在编程时，尚未拥有该类的.class文件，需要在运行中获得Class对象后，再使用其中的成员变量、方法等。

需要注意，在编译程序javac ReflectionTest.java时，需要加入参数-Xlint:unchecked。javac会认为我们调用各种函数时没有检查类型，十分不安全，而把这些警告当成错误处理。加入这个参数以继续编译。

运行结果：

![image](https://github.com/Seg-Tree/seg-tree.github.io_1/blob/main/pen/reflection/reflection_test.png)
