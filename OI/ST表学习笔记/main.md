$nlog(n)$初始化，$O(1)$在线查询，简短高效的RMQ，虽然简单但用得少~~Seg_Tree赛高！~~，所以要写一下。

以后要多用用，又短又快，还可以学四毛子。

```cpp
for(int i=1,res=0; i<=N; i++)
	log[i]=(res+=(1<<res+1<=i));
```
初始化，这样就能$O(1)$求$log(x)$了。

```cpp
for(int i=1; i<=n; i++)
	ST[0][i]=read();
for(int i=1; i<=log[n]; i++)
	for(int j=1; j<n; j++)
		ST[i][j]=max(ST[i-1][j],ST[i-1][j+(1<<i-1)]);
```

初始化ST表。$ST_{i,j}$表示该序列在$[j,j+2^i]$上的最值。显然$ST_{0,j}=a_j$。

又显然$[j,j+2^{(i-1)}]\bigcup[j+2^{(i-1)},j+2^{(i-1)}+2^{(i-1)}]=[j,j+2^i]$，故$ST_{i,j}$可以这样更新。

```cpp
max(ST[log[r-l]][l],ST[log[r-l]][r-(1<<log[r-l])+1]))
```
数学方法易证，$l+2^{\lfloor log_2(r-l+1)\rfloor}>=r-2^{\lfloor log_2(r-l+1)\rfloor}+1$

因此$[l,l+2^{\lfloor log_2(r-l+1)\rfloor}]\bigcup [r-2^{\lfloor log_2(r-l+1)\rfloor}+1,r]=[l,r]$,故该序列在$[l,r]$上的最值可以这样求。
