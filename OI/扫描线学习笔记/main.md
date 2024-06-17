[P5490 【模板】扫描线](https://www.luogu.com.cn/problem/P5490)

建立正常的，数学学科中的直角坐标系，将读入数据全部简化为一根一根的竖线。

```cpp
for(int i=0; i<n; i++){
	X[i<<1]=read();
	int y1=read();
	X[i<<1|1]=read();
	int y2=read();
	line[i<<1]=(Scan_Line){X[i<<1],X[i<<1|1],y1,1};
	line[i<<1|1]=(Scan_Line){X[i<<1],X[i<<1|1],y2,-1};
}
```
排序并去重，没错我们想要的就只有直角坐标系中呈现的有一些X值处有一条无限长的竖线的情景。

```cpp
	n*=2;
	sort(line,line+n);
	sort(X,X+n);
	int m=unique(X,X+n)-X-1;
```

现在想象所有矩形并起来的那个图形

显然，扫描线在自下往上扫的时候只需要考虑**当前正在被计算的X区间有多长**，再将这个数乘上到下一条横线的高度即可。

在拿到一条开始横线的时候，我们需要把这条横线所涵盖的X区间的开始层数（重叠层数）给加上一，拿到结束横线同理。

随后统计当前整条扫描线上有多长的X区间是应被计算的，即有多大的X区间的开始层数是$>0$的。
```cpp
for(int i=0; i<n; i++){
	T.x=lower_bound(X,X+m,line[i].l)-X+1;
	T.y=lower_bound(X,X+m,line[i].r)-X;
	T.val=line[i].type;
	T.maintain(1,1,m);
	ans+=(long long)T.tr[1].len*(line[i+1].h-line[i].h);
}
```
（某种意义上，X[ ]数组可以算是一个离散化）

用线段树维护这个问题。
```cpp
struct NODE{
	int len;
	int sum;
}tr[N<<3];
int x,y,val;
inline void pushup(int k,int l,int r){
	if(tr[k].sum)
		tr[k].len=X[r]-X[l-1];
	else
		tr[k].len=tr[k<<1].len+tr[k<<1|1].len;
}
void maintain(int k,int l,int r){
	if(x<=l && r<=y){
		tr[k].sum+=val;
		pushup(k,l,r);
		return;
	}
	int ls=k<<1,rs=k<<1|1,mid=l+r>>1;
	if(x<=mid)
		maintain(ls,l,mid);
	if(mid<y)
		maintain(rs,mid+1,r);
	pushup(k,l,r);
}
```
（$sum$表示该区间的开始层数，$len$表示当前区间内有多大的X区间有$sum>0$）

总代码

```cpp
#include<iostream>
#include<cstdio>
#include<algorithm>
#define N 200500
using namespace std;
int read(){
	char c=getchar();int in=0;
	while(c<48 || c>57)
		c=getchar();
	while(c>47 && c<58)
		in=in*10+c-48,c=getchar();
	return in;
}
struct Scan_Line{
	int l,r;
	int h;
	int type;
	bool operator <(Scan_Line _a){return h<_a.h;}
}line[N];
int X[N];
struct Segment_Tree{
	struct NODE{
		int len;
		int sum;
	}tr[N<<3];
	int x,y,val;
	inline void pushup(int k,int l,int r){
		if(tr[k].sum)
			tr[k].len=X[r]-X[l-1];
		else
			tr[k].len=tr[k<<1].len+tr[k<<1|1].len;
	}
	void maintain(int k,int l,int r){
//		cout<<k<<" "<<l<<" "<<r<<endl;
		if(x<=l && r<=y){
			tr[k].sum+=val;
			pushup(k,l,r);
//			cout<<k<<" "<<l<<" "<<r<<" "<<tr[k].sum<<" "<<tr[k].len<<endl;
			return;
		}
		int ls=k<<1,rs=k<<1|1,mid=l+r>>1;
		if(x<=mid)
			maintain(ls,l,mid);
		if(mid<y)
			maintain(rs,mid+1,r);
		pushup(k,l,r);
	}
}T;

int main(){
	int n=read();
	for(int i=0; i<n; i++){
		X[i<<1]=read();
		int y1=read();
		X[i<<1|1]=read();
		int y2=read();
		line[i<<1]=(Scan_Line){X[i<<1],X[i<<1|1],y1,1};
		line[i<<1|1]=(Scan_Line){X[i<<1],X[i<<1|1],y2,-1};
	}
	n*=2;
	sort(line,line+n);
	sort(X,X+n);
	int m=unique(X,X+n)-X-1;
	long long ans=0;
	n--;
	for(int i=0; i<n; i++){
		T.x=lower_bound(X,X+m,line[i].l)-X+1;
		T.y=lower_bound(X,X+m,line[i].r)-X;
		T.val=line[i].type;
		T.maintain(1,1,m);
		ans+=(long long)T.tr[1].len*(line[i+1].h-line[i].h);
//		cout<<T.tr[1].len<<" "<<ans<<endl;
	}
	cout<<ans<<endl;
	return 0;
	
}
```
