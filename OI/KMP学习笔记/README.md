$kmp_i$定义：

定义一个字符串$s$的border为$S$的一个非$S$本身的字符串$T$，有$T$既是$S$的前缀又是$S$的后缀。
如：
$$S = abcdzzzabcd$$
当然我们要的是最长border，于是有
$$T = abcd$$
对于$s$，找到其每个前缀$S'$的最长border $T'$。

$kmp_i$指$S$的第$i$个前缀$S_{1,i}$的border为$S_{1,kmp_i}$。

请严格按照定义做题，否则会出锅。

由$kmp$数组的性质，可以发现它可以用于字符串匹配。在$A$中寻找$B$出现的位置次数等信息。

$$A = acbbacaacbaacbaac$$
$$B = acbaac$$

手模匹配思路，有下列代码。
```cpp
	for(int i=1,j=0; i<=la; i++){
		while(a[i]!=b[j+1] && j)j=kmp[j];
		if(a[i]==b[j+1])j++;
		if(j==lb)cout<<i-lb+1<<endl,j=kmp[j];
	}
```
再考虑用自匹配来求$kmp_i$。
```cpp
	for(int i=2,j=0; i<=lb; i++){
		while(b[i]!=b[j+1] && j)j=kmp[j];
		if(b[i]==b[j+1])j++;
		kmp[i]=j;
	}
```
（感觉好像没啥好说的，这种板子盯着代码样例手模一遍基本就懂了。）
