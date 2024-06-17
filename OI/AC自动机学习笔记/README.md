某种意义上，在字典树上做KMP。

$fail_i$表示在字典树上第$i$个结点上，发现接下来无可匹配了以后，需要跳到哪个结点。[洛谷日报2018#44 [水手hwy]强势图解AC自动机](https://www.luogu.com.cn/blog/3383669u/qiang-shi-tu-xie-ac-zi-dong-ji "洛谷日报2018#44 [水手hwy]强势图解AC自动机")里的动图很不错，对$fail$指针相关解释得很清晰。

艹感觉当时跟洛谷日报学得太顺利没过啥坎没啥好提到的，这篇就当洛谷日报指路帖好了。
```cpp
#include<iostream>
#include<cstdio>
#define N 1000050
using namespace std;
inline int read(){
	char c=getchar();int in=0;
	while(c<48 || 57<c)c=getchar();
	while(47<c && c<58)in=in*10+c-48,c=getchar();
	return in;
}

struct AC{
	struct NODE{
		int fail,end;
		int s[27];
	}t[N];
	int tot_node;
	inline void insert(const int *in){
		int k=0;
		for(int i=0; in[i]; k=t[k].s[in[i]],i++)
			if(!t[k].s[in[i]])
				t[k].s[in[i]]=++tot_node;
		t[k].end++;
//		cout<<k<<" "<<t[k].end<<endl;
	}
	
	int q[N],hd,tl=-1;
	inline void build(){
		for(int i=1; i<27; i++)
			if(t[0].s[i])
				q[++tl]=t[0].s[i];
		while(hd<=tl){
			const int k=q[hd++];
			for(int i=1; i<27; i++){
				if(t[k].s[i])
					t[q[++tl]=t[k].s[i]].fail=t[t[k].fail].s[i];
				else
					t[k].s[i]=t[t[k].fail].s[i];
			}
		}
	}
	inline int query_dif(const int *s){
		int k=0,ret=0;
		for(int i=0; s[i]; i++){
			k=t[k].s[s[i]];
			for(int p=k; p && ~t[p].end; p=t[p].fail){
				ret+=t[p].end;
//				cout<<s[i]<<" "<<i<<" "<<k<<" "<<ret<<endl;
				t[p].end=-1;
			}
		}
		return ret;
	}
}ac;

int in[N];
int main(){
//	freopen("P3808_1.in","r",stdin);
	int n=read();
	while(n--){
		char c=getchar();int len=0;
		while(c<97 || c>122)c=getchar();
		while(c>96 && c<123)in[len++]=c-96,c=getchar();
		in[len]=0;
		ac.insert(in);
	}
	ac.build();
	char c=getchar();int len=0;
	while(c<97 || c>122)c=getchar();
	while(c>96 && c<123)in[len++]=c-96,c=getchar();
	in[len]=0;
	cout<<ac.query_dif(in)<<endl;
}
```
另外正如日报中提到的，`build()`时千万不要直接入队0号空结点，而是一个个将其子结点入队。

其他两个模板打得太自闭，额外处理的数据太烦，就没打了。
