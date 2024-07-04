堆题有时会出现无法正常结束程序的情况，导致无法调用exit()函数，打不了house of apple。这时可以使用puts()在libc中的一个got表，利用任意写修改    ，从而控制程序流程。

例题：[强网杯2023 warmup23](https://files.cnblogs.com/files/blogs/709433/warmup23.7z?t=1715926264&download=true "强网杯2023 warmup23")

add时有一个强制off by null，无edit。有沙箱，需要打orw。需要用到[[原创]从PWN题NULL_FXCK中学到的glibc知识](https://bbs.kanxue.com/thread-273746.htm "[原创]从PWN题NULL_FXCK中学到的glibc知识")的堆风水手法。自整理的ppt：[off-by-null](https://files.cnblogs.com/files/blogs/709433/off-by-null.pptx?t=1715929811&download=true "off-by-null")

通过这种方法达成任意写之后，会发现程序无法正常退出，无法进入house of apple的调用链。ymnh学长提供了一种方法：在puts()中，会使用到一系列的got表，用于strlen，abs等等功能，其中一个便是\*ABS\*@got.plt，他在2.35-0ubuntu3.5_amd64中的偏移是0x2190a8。把堆块分配到这里，在后面写一堆exit()(或者是ogg也行，如果其他题的话)，即可
