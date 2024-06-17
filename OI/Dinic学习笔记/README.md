和EK一样的建图。
`bfs()`判是否还有增广路并将图分层，`dfs()`找增广路。

------------
有关分层图：
- $dpt_i$表示$s$点到$i$点需要经过的路径条数。
- 找出所有的弧，满足
1.$dpt_u+1=dpt_v$
2.顺着该弧往下走，保证经过的边都满足条件1，能够达到$t$

- 这些弧组成的图一定有一些增广路，如果$dpt_t\ne INF$。
- `dfs()`跑一遍该图，即一个DAG，最差复杂度为$O(NM)$。
- 可以证明只需要分$N$层（[证明，待填坑](http://www.youtube.com/watch%3Fv%3DuM06jHdIC70 "证明，待填坑")）
- 故Dinic复杂度为$O(N^2M)$
------------
有关`dfs()`：
```cpp
ll dfs(const int &u,const ll &low){
	if(u==t)
		return low;
	ll tlow=0,used=0;
	for(int i=cur[u]; i && used<low; i=edg[i].nxt){
		cur[u]=i;
		const int v=edg[i].v;
		const ll w=edg[i].w;
		if(w && dpt[v]==dpt[u]+1){
			if(tlow=dfs(v,min(low-used,w))){
				used+=tlow;
				edg[i].w-=tlow;
				edg[i^1].w+=tlow;
			}
		}
	}
	return used;
}
```
- 用$used$储存从这个点往后到$t$最大能输去多少流量
- 用$low$储存$s$走到这能带来多少流量。到$t$回去的就是$low$，而该$low$，即上一个点的$tlow$，是要$\le low-used$的。以此类推，自己手模一下，会发现不会出现问题，因此`for()`判断中的$used<low$也可以改成$used\ne low$。
- 前向弧优化，$cur_u$表示$u$已经跑到$cur_u$这条弧了，而前面的那些弧都已经被跑过没必要再跑了。减少了很多无用的判断，加速效果明显。
- 感觉分层图除了固定时间复杂度之外最重要的一个作用是使图成为一个DAG，而上述优化好像都需要保证是DAG才能使用。
------------
Dinic还是有需要再进一步理解。
最终代码：
```cpp
#include<iostream>
#include<cstring>
#include<cstdio>
using namespace std;
typedef long long ll;
const int N=205,M=5005,iINF=0x3fffffff;
const ll lINF=0x3fffffffffffffff;
inline ll read(){
	char c=getchar();ll in=0;
	while(c<48 || c>57)c=getchar();
	while(c>47 && c<58)in=in*10+c-48,c=getchar();
	return in;
}

struct EDG{
	int v,nxt;
	ll w;
}edg[M<<1];
int head[N],cur[N],tot_edg=1;
inline void add_edg(const int &u,const int &v,const ll &w){
	edg[++tot_edg]=(EDG){v,head[u],w};
	head[u]=tot_edg;
}

int n,s,t,dpt[N];

int q[N],hd,tl;
bool vis[N];
inline bool bfs(){
	for(int i=1; i<=n; i++){
		cur[i]=head[i];
		dpt[i]=iINF;
		vis[i]=0;
	}
	hd=tl=0;
	q[++tl]=s;
	dpt[s]=0;
	vis[s]=1;
	while(hd<tl){
		const int u=q[++hd];
		for(int i=head[u]; i; i=edg[i].nxt){
			const int v=edg[i].v;
			if(dpt[v]>dpt[u]+1 && edg[i].w){
				dpt[v]=dpt[u]+1;
				if(!vis[v]){
					q[++tl]=v;
					vis[v]=1;
				}
			}
		}
	}
	if(dpt[t]!=iINF)return 1;
	return 0;
}

ll dfs(const int &u,const ll &low){
	if(u==t)
		return low;
	ll tlow=0,used=0;
	for(int i=cur[u]; i && used!=low; i=edg[i].nxt){
		cur[u]=i;
		const int v=edg[i].v;
		const ll w=edg[i].w;
		if(w && dpt[v]==dpt[u]+1){
			if(tlow=dfs(v,min(low-used,w))){
				used+=tlow;
				edg[i].w-=tlow;
				edg[i^1].w+=tlow;
			}
		}
	}
	return used;
}

int main(){
	n=read();
	int m=read();
	s=read();
	t=read();
	while(m--){
		const int u=read(),v=read();
		ll w=read();
		add_edg(u,v,w);
		add_edg(v,u,0);
	}
	ll ans=0;
	while(bfs())
		ans+=dfs(s,lINF);
	cout<<ans<<endl;
	return 0;
}
```
