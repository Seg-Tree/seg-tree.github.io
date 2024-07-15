反射：

javac Hello.java 命令搞出来的Hello.class这个文件，其实是一个对象。这个对象的类型是Class。Class这个类，是一种“类的类”。

Class类的对象——Hello.class包含了Hello这个类所有成员变量的名称、类型；所有成员函数的名称、返回类型、参数名称及类型、和他们的汇编代码（适用于jvm这种机器的汇编代码）

也就是说，对于一个运行中的java程序，所有代码（函数/方法）都存在于某个Class类型的对象中。
运行的Hello程序，其main函数等代码存在于Hello.class这个对象中，而其间接调用的属于String类的方法，存在于String类的类的对象（让我们称之为String.class）中。
Hello.class文件中存放有string，system等对象的路径。
这些对象会在程序启动时加载进内存。

