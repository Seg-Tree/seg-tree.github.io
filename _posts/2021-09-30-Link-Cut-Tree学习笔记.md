---
layout: post
title: Task Item List
tags: [to-do list]
---

[P3690 【模板】动态树（Link Cut Tree）](https://www.luogu.com.cn/problem/P3690)

基本概念其他博客里都有，就不重复了。
- 本质维护一个文艺splay森林。
- 每一个splay中的点集合在**实树**中构成一条链
- 其中splay保证以每个节点在实树中的$dpt$为$key$,是一棵bst。
- 一棵splay的根也会有一个$fa$，但那个$fa$却不认它这个$son$
- 虚边，即根与外界连的那条边，在实树上存在于该splay最左边（即$key$最小，$dpt$最浅）的那个点与上一条中的$fa$之间。
- 实边，即一棵splay内部的边，与实树的边其实并没有关系。
- 个人认为过于强调这两种边与 轻重链剖分 中的轻边与重边的关系并不恰当

*实树：题目里给出的，真实存在的那一棵树。

有关文艺splay部分，参见[P3391 【模板】文艺平衡树](https://www.luogu.com.cn/problem/P3391)，即带翻转的splay

------------

```access(x)```：最基础的操作，使 $x$所属实树的根到$x$ 这条链形成一个splay，splay的根是 $x$所属实树的根。

从$x$开始往上操作

```while(x) ```中逐步解析：

- ```splay(x);```

~~显然，~~ 将$x$旋为所属splay的根。

- ```heavy_link(son,x,1);```

若是第一次循环，转换$x$与其它 实树中在他下面的其他乱七八糟的点的实边，为虚边。即，从今以后，$x$与那个点就不在同一个splay中了

在$x$与$son$之间连一条重边，即从今以后$son$，即上一次循环中的$x$，通过$fa$爬上来了当前的$x$（由于上次的$x$一定是之前那个splay的根，故用$fa$爬一定爬的是实树中的$fa$），与$x$是在同一个splay中了。由于$son$的$dpt$一定比$x$要大，故这样连一定是合法的。

- ```pushup(x);```

都连边了肯定要```pushup(x)```的嘛

- ```son=x;```

- ```x=tr[x].fa;```

往上爬

```cpp
inline void access(int x){
	int son=0;
	while(x){
		splay(x);
		heavy_link(son,x,1);
		pushup(x);
		son=x;
		x=tr[x].fa;
	}
}
```

以上，循环中每次一个一个```heavy_link```，把$x$到根的所有点都加到同一个splay中，即可达成```access(x)```的目的了。

------------

```makeroot(x)```使$x$成为其所在实树的根

想象我们的实树，如何让$x$取代$root$成为根？

（自己脑补动图）

考虑改变splay中每个节点的$dpt$。由于我们的splay中不是真的存了每个节点的$dpt$，而只是通过splay内部的边，以及各splay之间的关系来间接体现$dpt$。

因此在搞清楚之前我们先来```access(x)```吧！

- ```access(x);```

~~就是他那个功能~~

容易发现，现在需要改变的splay内相对$dpt$的只有自$root$至$x$路径上的所有的点，其他splay中的点的$dpt$显然会随着其根所连虚边另一端的点的$dpt$的改变而做出正确改变，因此不需要我们去管。

那么考虑那条链上节点的$dpt$应该怎么改变呢？

（再自己脑补标上$dpt$的动图）

没错，只需要对整个splay，即整条链，做一个```reverse(x)```就可以了

- ```reverse(x);```

原理与作用参见[P3391 【模板】文艺平衡树](https://www.luogu.com.cn/problem/P3391)。

- ```pushup(x);```

日常```pushup(x)```

```cpp
inline void makeroot(const int &x){
		access(x);
		reverse(x);
		pushup(x);
	}
```
以上，```makeroot(x)```。

------------

```findroot(x)```,找到$x$所属实树的根。

------------

```split(x，y)```，使$x$成为所属实树的根，使$y$到$x$的链在同一以$y$为根splay中。通俗地讲，将$x$与$y$拉成一条链。LCT得以进行链上操作的基础。

------------

```link(x,y)```，在$x$，$y$之间连一条实际的，实树上的边。

------------

```cut(x,y)```，切断$x$,$y$之间实际的，实树上的边。

------------

```modify(x,val)```，修改$x$节点的$val$为$val$（迷惑）。

------------

有关链上询问，只需```split(x，y)```，然后$x$到$y$路径上的信息都由代表这条链的splay的根$y$储存了。
