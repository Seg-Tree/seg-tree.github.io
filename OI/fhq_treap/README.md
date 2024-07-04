[P6136 【模板】普通平衡树（数据加强版）](http://https://www.luogu.com.cn/problem/P6136 "P6136 【模板】普通平衡树（数据加强版）")
是一棵treap，即以$val$为关键词是一棵bst，以$rnd$为关键词是一个堆。采用`split()`与`merge()`而非`rotate()`维护，达到“非旋”的效果，使速度变快，码量变小，又称“非旋treap”。

------------

定义
```
struct Node{
	int val,siz,rnd;
	int ls,rs;
}t[N];
inline int NewNode(const int val){
	t[++TotNode]={val,1,rand(),0,0};return TotNode;
}
```
没啥好说的，与最基础的treap相同。

------------

`split(const int &val,int k,int &x,int &y)`：
以$val$为关键字，将以$k$为根的树分裂为两棵，一棵上面所有值都$<val$，另一棵上所有值都$>val$。较小一棵的根填充到$x$上，较大一棵的根填充到$y$上。
```
if(!k){return x=y=0,void();}
```
若$k=0$，则当前已无树可以分裂，则上一层伸下来的寻求填充的地址$x$与$y$都应被填充为$0$。
```
if(t[k].val<val)
	split(val,t[x=k].rs,t[k].rs,y);
else
	split(val,t[y=k].ls,x,t[k].ls);
```
抉择$k$及$k$的左/右子树应该被填充到哪里。若`t[k].val<val`，则$k$的左子树一定都$<val$，接下来只需分裂$k$的右子树。因此，将$k$的右子树分为两棵，一棵在$x$（此时数值上已经等于$k$了）的右子树上，一棵在仍然代填充的地址$y$上。
若`t[k].val>=val`同理。
```
PushUp(k);
```
原树$k$已经不存在，此处之$k$指的是已完成赋值的$x$或$y$。以$x$为例。显然，相比原树现在许多子树的$siz$已发生了变化，因此需要更新。此处只更新$x$是因为相比原树$k$，右子树的一部分被拿走了；$y$在本层没有发生变化。

## 注意：$k$不能使用const int &来定义，因为地址传来传去会乱掉

------------

`int merge(const int &x,const int &y)`：
将树$x$与$y$合并为一棵树，其根作为返回值。
```
if(!x || !y)return x|y;
```
若$x$为空或$y$为空，则$x$或$y$即为他们合并后的样子。
```
if(t[x].rnd<t[y].rnd){
	t[x].rs=merge(t[x].rs,y);
	PushUp(x);
	return x;
} else {
	t[y].ls=merge(x,t[y].ls);
	PushUp(y);
	return y;
}
```
根据$rnd$进行合并。因为已知$x$中的值全部都小于$y$中的值，故可以如此不管其$val$而合并。

------------

```
inline void insert(const int &val){
	int x=0,y=0;
	split(val,root,x,y);
	root=merge(merge(x,NewNode(val)),y);
}
```
裂开，将新结点与$x$合并，再将合并后的树与$y$合并，将最后合并出来的树根放在$root$上。
```
inline void delet(const int &val){
	int x=0,y=0,z=0,tmp;
	split(val,root,x,z);
	split(val+1,z,z,y);
	root=merge(merge(x,merge(t[z].ls,t[z].rs)),y);
}
```
以$val$为关键字将树裂为所有值全部小于$val$的$x$，与大于等于$val$的$y$两部分。
以$val+1$为关键字将$y$裂为所有值全部等于$val$的$z$，与大于$val$的$y$两部分。
将$z$的左儿子与右二子合并，使其根节点迷失。合并之后的树与$x$合并，再与$y$合并。
```
inline int GetRank(const int &val){
	int k=root,ret=1;
	while(k){
		if(t[k].val<val)
			ret+=t[t[k].ls].siz+1,k=t[k].rs;
		else k=t[k].ls;
	}
	return ret;
}
inline int GetVal(int rak){
	int k=root;
	while(1){
		if(t[t[k].ls].siz+1==rak)return t[k].val;
		if(t[t[k].ls].siz>=rak)k=t[k].ls;
		else rak-=t[t[k].ls].siz+1,k=t[k].rs;
	}
	return -1;
}
```
与一般的`GetRank()`与`GetVal()`一样。不使用常用而好写的`split()`开再找，原因是循环比递归快。
```
inline int GetPre(const int &val){
	int x=0,y=0;
	split(val,root,x,y);
	int k=x;
	while(t[k].rs)k=t[k].rs;
	int ret=t[k].val;
	root=merge(x,y);
	return ret;
}
inline int GetNext(const int &val){
	int x=0,y=0;
	split(val+1,root,x,y);
	int k=y;
	while(t[k].ls)k=t[k].ls;
	int ret=t[k].val;
	root=merge(x,y);
	return ret;
}
```
前驱：将$val$裂到$y$上，使之成为$y$上最小的元素，$x$上有着所有比$val$小的元素。因此在$x$上最大的元素即为$val$的前驱。
后继同理。

------------

完整代码：
```
class FHQ_Treap{
    private:
    struct Node{
        int val,siz,rnd;
        int ls,rs;
    }t[N];
    int TotNode,root;
    inline void PushUp(const int &k){t[k].siz=t[t[k].ls].siz+t[t[k].rs].siz+1;}
    inline int NewNode(const int val){t[++TotNode]={val,1,rand(),0,0};return TotNode;}
    void print(const int &k){
	    if(!k)return;
	    cout<<"ls: "<<t[t[k].ls].val<<" rs: "<<t[t[k].rs].val<<" ## "<<k<<endl;
	    cout<<"size= "<<t[k].siz<<" val= "<<t[k].val<<endl;
	    print(t[k].ls);
	    print(t[k].rs);
    }
    void split(const int &val,int k,int &x,int &y){
        if(!k){return x=y=0,void();}
        else
        {if(t[k].val<val)
            split(val,t[x=k].rs,t[k].rs,y);
        else
            split(val,t[y=k].ls,x,t[k].ls);
        PushUp(k);}
    }
    int merge(const int &x,const int &y){
        if(!x || !y)
            return x|y;
        if(t[x].rnd<t[y].rnd){
            t[x].rs=merge(t[x].rs,y);
            PushUp(x);
            return x;
        } else {
            t[y].ls=merge(x,t[y].ls);
            PushUp(y);
            return y;
        }
    }
    public:
    inline void DeBug(){print(root);}
    inline void insert(const int &val){
        int x=0,y=0;
        split(val,root,x,y);
        root=merge(merge(x,NewNode(val)),y);
    }
    inline void delet(const int &val){
        int x=0,y=0,z=0,tmp;
        split(val,root,x,z);
        split(val+1,z,z,y);
        root=merge(merge(x,merge(t[z].ls,t[z].rs)),y);
    }
    inline int GetRank(const int &val){
        int k=root,ret=1;
        while(k){
            if(t[k].val<val)
                ret+=t[t[k].ls].siz+1,k=t[k].rs;
            else k=t[k].ls;
        }
        return ret;
    }
    inline int GetVal(int rak){
        int k=root;
        while(1){
            if(t[t[k].ls].siz+1==rak)return t[k].val;
            if(t[t[k].ls].siz>=rak)k=t[k].ls;
            else rak-=t[t[k].ls].siz+1,k=t[k].rs;
        }
        return -1;
    }
    inline int GetPre(const int &val){
        int x=0,y=0;
        split(val,root,x,y);
        int k=x;
        while(t[k].rs)k=t[k].rs;
        int ret=t[k].val;
        root=merge(x,y);
        return ret;
    }
    inline int GetNext(const int &val){
        int x=0,y=0;
        split(val+1,root,x,y);
        int k=y;
        while(t[k].ls)k=t[k].ls;
        int ret=t[k].val;
        root=merge(x,y);
        return ret;
    }
}T;
