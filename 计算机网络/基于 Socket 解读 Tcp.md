## 协议栈

网络设计者以分层的方式组织协议以及实现这些协议的网络硬件和软件。

<div align="center"> <img width="50%" src="https://api.zhengjianting.com/picture?name=protocolStack"/> </div> <br>

1. **因特网协议栈**

   - 应用层（application layer）：应用层是网络应用程序及它们的应用层协议存留的地方，位于应用层的信息分组称为报文（message）。
   - 传输层（transport layer）：传输层在应用程序端点之间传送应用层报文，为进程提供通用数据传输服务。在因特网中，有两种传输协议，即 TCP 和 UDP，利用其中任一个都能传输应用层报文。TCP 提供面向连接、可靠的数据传输服务，并提供拥塞控制机制；UDP 则提供无连接的数据传输服务，不提供不必要的服务，没有可靠性、流量控制、拥塞控制。TCP 和 UDP 分组均称为报文段（segment），也有人将 UDP 分组称为用户数据报。
   - 网络层（network layer）：网络层负责将数据报（datagram）的网络层分组从一台主机移动到另一台主机，网络层包括著名的网际协议 IP。
   - 链路层（data link layer）：网络层针对的是主机之间的数据传输服务，而主机之间可以有很多链路，链路层协议就是为同一链路的主机提供数据传输服务，数据链路层把网络层传下来的分组封装成帧（frame）。
   - 物理层（physical layer）：链路层的任务是将整个帧从一个网络元素移动到邻近的网络元素，而物理层的任务是将该帧中的一个个比特从一个节点移动到下一个节点。

2. **OSI**

   20 世纪 70 年代后期，国际标准化组织（ISO）提出计算机网络围绕 7 层来组织，称为开放系统互连（OSI）模型，OSI 在因特网协议栈的基础上附加了两层，表示层和会话层：

   - 表示层（presentation layer）：作用是使通信的应用程序能够解释交换数据的含义，这些服务包括数据压缩和数据加密以及数据描述（这使得应用程序不必担心在各台计算机中表示 / 存储的内部格式不同）。
   - 会话层（session layer）：提供了数据交换的定界和同步功能，包括了建立检查点和恢复方案的方法。

   五层因特网协议栈没有表示层和会话层，而是将这些功能留给应用程序开发者处理。

3. **TCP / IP**

   它只有四层，相当于五层协议栈中链路层和物理层合并为网络接口层（network access / link layer）。

   TCP / IP 体系结构不严格遵循 OSI 分层概念，应用层可能会直接使用 IP 层或者网络接口层。

   <div align="center"> <img width="50%" src="https://api.zhengjianting.com/picture?name=tcpIP"/> </div> <br>

4. **数据在各层之间的传递过程**

   在向下的过程中，需要添加下层协议所需要的首部或者尾部，而在向上的过程中不断拆开首部和尾部。

   路由器只有下面三层协议，因为路由器位于网络核心中，不需要为进程或者应用程序提供服务，因此也就不需要传输层和应用层。

## 套接字（Socket）

### 进程通信

当多个进程运行在相同的主机上时，它们使用进程间通信机制（由操作系统确定）相互通信。这里仅关注运行在不同主机（操作系统可能不一致）上的进程间通信，在两个不同主机上的进程，通过计算机网络交换报文（message）而相互通信。

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=processCommunication"/> </div> <br>

两个位于不同主机的进程通过套接字通信，**套接字是同一台主机内应用层和传输层之间的接口**，由于套接字是建立网络应用程序的可编程接口，因此套接字也称为应用程序和网络之间的**应用程序编程接口**（Application Programming Interface，API）。应用程序开发者可以控制套接字在应用层的一切，但对该套接字的传输层几乎没有控制权，仅限于：

- 选择传输层协议
- 设定几个传输层参数，如最大缓存和最大报文段长度等

### 套接字标识符

一个进程（作为网络应用的一部分）有一个或多个套接字，它相当于从网络向进程传递数据和从进程向网络传递数据的门户。由于在任一时刻，主机上可能有不止一个套接字，所以每个套接字都有唯一的标识符。标识符的格式取决于它是 TCP 还是 UDP 套接字：

- UDP 套接字：由一个二元组（目的 IP 地址，目的端口号）全面标识
- TCP 套接字：由一个四元组（源 IP 地址，源端口号，目的 IP 地址，目的端口号）全面标识

### TCP 套接字编程

当我们通过浏览器访问一个 URL 时，将域名解析为服务器 IP 地址后，先要生成一个客户端 TCP 套接字，和服务器进行三次握手建立连接，再向服务器发送 HTTP 报文。

<div align="center"> <img width="50%" src="https://api.zhengjianting.com/picture?name=tcpSocket"/> </div> <br>

服务器的欢迎套接字即 ServerSocket，以 HTTP 为例，ServerSocket 对应于服务器的 80 端口，在经过三次握手建立 TCP 连接后，ServerSocket.accept() 会为该连接新建一个连接套接字进行数据传输，该连接套接字由（客户端 IP 地址，客户端套接字端口号，服务器 IP 地址，80）标识。

注意，具有不同 IP 地址或端口号的客户端套接字在进行三次握手建立 TCP 连接时，在服务器与之通信的都是欢迎套接字，当然，建立连接后，由于客户端 IP 地址或客户端口号不同，在服务器与之通信的是不同的连接套接字。

## BIO 通信方式

为了使 WireShark 抓包时不受其它因素影响，用 Java 编写一个最简单的阻塞式 I / O，服务器端为单线程的 Client - Server 应用程序，一个客户端对应一个线程，利用 CountDownLatch 同时发送请求，此处只为了抓包，客户端数量设置为 1.

### 客户端代码

```java
package com.zhengjianting.BIO.Client;

import java.util.concurrent.CountDownLatch;

public class SocketClientDaemon {
    public static void main(String[] args) throws InterruptedException {
        int clientNumber = 1;
        CountDownLatch countDownLatch = new CountDownLatch(clientNumber);

        for (int i = 0; i < clientNumber; i++, countDownLatch.countDown()) {
            SocketClientRequestThread client = new SocketClientRequestThread(i, countDownLatch);
            new Thread(client).start();
        }

        // countDownLatch == 0, 所有 SocketClientRequestThread 线程同时启动, SocketClientDaemon 进入等待状态
        synchronized (SocketClientDaemon.class) {
            SocketClientDaemon.class.wait();
        }
    }
}
```

```java
package com.zhengjianting.BIO.Client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class SocketClientRequestThread implements Runnable {
    // 日志
    static {
        BasicConfigurator.configure();
    }
    private static final Logger LOGGER = Logger.getLogger(SocketClientRequestThread.class);

    // 客户请求线程编号
    private int clientIndex;
    private CountDownLatch countDownLatch;

    public SocketClientRequestThread(int clientIndex, CountDownLatch countDownLatch) {
        this.clientIndex = clientIndex;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        Socket socket = null;
        InputStream clientResponse = null;
        OutputStream clientRequest = null;
        try {
            socket = new Socket("localhost", 10526);
            clientRequest = socket.getOutputStream();
            clientResponse = socket.getInputStream();

            // 等待 countDownLatch == 0, 所有 SocketClientRequestThread 同时发送请求
            this.countDownLatch.await();

            // 发生请求信息
            clientRequest.write(("这是第 " + this.clientIndex + " 个客户端的请求").getBytes(StandardCharsets.UTF_8));
            clientRequest.flush();

            // 等待, 直到服务器返回信息
            SocketClientRequestThread.LOGGER.info("第 " + this.clientIndex + " 个客户端的请求发送完成, 等待服务器返回信息");
            int realLength, maxLength = 1024;
            String message = "";
            byte[] bytes = new byte[maxLength];
            /**
             * InputStream.read(): This method blocks until input data is available, end of file is detected, or an exception is thrown.
             * 客户端线程执行到这里, 会阻塞在此, 直到服务器返回信息 (注意: 前提是服务器端 in, out 都不能 close)
             */
            while ((realLength = clientResponse.read(bytes, 0, maxLength)) != -1) {
                message += new String(bytes, 0, realLength);
            }
            SocketClientRequestThread.LOGGER.info("第 " + this.clientIndex + " 个客户端接收到来自服务器的信息: " + message);
        } catch (Exception e) {
            SocketClientRequestThread.LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (clientRequest != null) {
                    clientRequest.close();
                }
                if (clientResponse != null) {
                    clientResponse.close();
                }
            } catch (IOException e) {
                SocketClientRequestThread.LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
```

### 服务器端代码

```java
package com.zhengjianting.BIO.Server;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SingleThreadServer {
    // 日志
    static {
        BasicConfigurator.configure();
    }
    private static final Logger LOGGER = Logger.getLogger(SingleThreadServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10526);
        try {
            while (true) {
                /**
                 * ServerSocket.accept(): 为此连接新建一个 socket
                 *  Listens for a connection to be made to this socket and accepts it.
                 *  The method blocks until a connection is made.
                 */
                Socket socket = serverSocket.accept();

                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int sourcePort = socket.getPort();
                int maxLength = 2048;
                byte[] bytes = new byte[maxLength];

                // 读取请求信息, InputStream.read() 会被阻塞, 直到客户端请求信息准备好
                int realLength = in.read(bytes, 0, maxLength);
                String message = new String(bytes, 0, realLength);

                SingleThreadServer.LOGGER.info("服务器收到来自客户端 " + sourcePort + " 端口的请求信息: " + message);
                out.write(("回发客户端 " + sourcePort + " 端口的响应信息!").getBytes(StandardCharsets.UTF_8));

                in.close();
                out.close();
                socket.close();
            }
        } catch (Exception e) {
            SingleThreadServer.LOGGER.error(e.getMessage(), e);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}
```

### 运行结果

<div align="center"> <img width="95%" src="https://api.zhengjianting.com/picture?name=runSingleThreadServer"/> </div> <br>

<div align="center"> <img width="95%" src="https://api.zhengjianting.com/picture?name=runSocketClientDaemon"/> </div> <br>

## TCP 报文段结构

在运行上述代码并使用 WireShark 抓包之前，先概述一下 TCP 相关知识。

TCP 报文段由 20 字节的首部字段（若选项字段为空，通常如此）和一个数据字段组成：

<div align="center"> <img width="60%" src="https://api.zhengjianting.com/picture?name=tcpSegment"/> </div> <br>

- 源端口号，目的端口号：用于多路复用 / 分解来自或送到上层应用的数据。
- 序号（sequence number）：序号建立在传送的字节流之上，而不是传送的报文段序列之上。例如，序号为 100，报文段大小为 200 字节，那么下一个报文段的序号为 300.
- 确认号（acknowledgment number）：期望收到的下一个报文段的序号。例如，A 向 B 发送一个序号为 526，数据大小为 100 字节的报文段，那么 B 向 A 发送的回答报文的确认号为 626.
- CWR（拥塞窗口缩减），ECE（明确拥塞通告回显）：用于网络辅助拥塞控制。
- URG：指示报文段里存在被发送端的上层实体置为“紧急”的数据。
- 紧急数据指针（urgent data pointer）：紧急数据的最后一个字节由该 16 bits 的字段指出。
- ACK：指示该报文段是有效的，TCP 规定，在连接建立后所有传送的报文段都必须把 ACK 置 1.
- PSH：指示接收方应当立即将数据交给上层。
- SYN：在三次握手时同步序号。当 SYN = 1，ACK = 0 时表示这是一个请求连接报文段。若对方同意连接，则响应报文段中 SYN = 1，ACK = 1.
- FIN：用于释放连接，常见于四次挥手阶段，当 FIN = 1 时表示发送方的数据已发送完毕，要求释放连接。
- 接收窗口（receive window）：用于流量控制，表示接收方愿意接受的字节数。

## TCP 三次握手

<div align="center"> <img width="75%" src="https://api.zhengjianting.com/picture?name=threeWayHandshake"/> </div> <br>

假设 A 为客户端，B 为服务器端：

- 首先 B 处于 LISTEN（监听）状态，等待客户的连接请求。
- A 向 B 发送连接请求报文，SYN = 1，ACK = 0，并选择一个初始的序号 x。
- B 收到连接请求报文，如果同意建立连接，则向 A 发送连接确认报文，SYN = 1，ACK = 1，确认号 ack = x + 1，同时也选择一个初始的序号 y。
- A 收到 B 的连接确认报文后，还要向 B 发出确认（可以携带数据），确认号 ack = y + 1，序号为 x + 1.
- B 收到 A 的确认后，连接建立。

**三次握手的原因**

第三次握手是为了防止失效的连接请求到达服务器，让服务器错误打开连接。

客户端发送的连接请求如果在网络中滞留，那么就会隔很长一段时间才能收到服务器端发回的连接确认。客户端等待一个超时重传时间之后，就会重新请求连接。但是这个滞留的连接请求最后还是会到达服务器，如果不进行三次握手，那么服务器就会打开两个连接。如果有第三次握手，客户端会忽略服务器之后发送的对滞留连接请求的连接确认，不进行第三次握手，因此就不会再次打开连接。

## TCP 四次挥手

<div align="center"> <img width="75%" src="https://api.zhengjianting.com/picture?name=fourWayHandshake"/> </div> <br>

以下描述不讨论序号和确认号，因为序号和确认号的规则比较简单。并且不讨论 ACK，因为 ACK 连接建立之后都为 1.

- A 发送连接释放报文，FIN = 1.
- B 收到之后发出确认，此时 TCP 属于半关闭状态，B 能向 A 发送数据但是 A 不能向 B 发送数据。
- 当 B 将剩余数据传输完毕后，不再需要连接时，发送连接释放报文，FIN = 1.
- A 收到后发出确认，进入 TIME-WAIT 状态，等待 2 MSL（2 Maximum Segment Lifetime，两个最大报文段生命周期）后释放连接。
- B 收到 A 的确认后释放连接。

**四次挥手的原因**

客户端发送了 FIN 连接释放报文之后，服务器收到了这个报文，就进入了 CLOSE-WAIT 状态。这个状态是为了让服务器端发送还未传送完毕的数据，传送完毕之后，服务器会发送 FIN 连接释放报文。

**TIME_WAIT**  

客户端接收到服务器端的 FIN 报文后进入此状态，此时并不是直接进入 CLOSED 状态，还需要等待一个时间计时器设置的时间 2 MSL。这么做有两个理由：

- 确保最后一个确认报文能够到达。如果 B 没收到 A 发送来的确认报文，那么就会重新发送连接释放请求报文，A 等待一段时间就是为了处理这种情况的发生。
- 等待一段时间是为了让本连接持续时间内所产生的所有报文都从网络中消失，使得下一个新的连接不会出现旧的连接请求报文。

**注意**

客户端或服务器端均可主动发起挥手动作，在 socket 编程中，任何一方执行 `socket.close()`操作即可产生挥手操作，在上述代码中挥手操作便是由服务器端发起的，在下面 WireShark 抓包结果中可以看到。

## WireShark 抓包

运行上述 BIO 通信例子，监听本地回环网卡，抓包结果如下：

<div align="center"> <img width="95%" src="https://api.zhengjianting.com/picture?name=tcpWiresharkOverview"/> </div> <br>

显然，前三个报文段对应于 TCP 三次握手，最后四个报文段对应于 TCP 四次挥手（由服务器端发起）。其中客户端套接字端口为 61863，服务器端的欢迎套接字（ServerSocket）端口为 10526，三次握手时，服务器端与客户端套接字通信的是欢迎套接字，其余时刻与之对应的是 ServerSocket.accept() 新建的由四元组全面标识的连接套接字，虽然它们在 WireShark 中显示的服务器端口均为 10526.

### 三次握手

**第一次握手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=firstHandshake"/> </div> <br>

**第二次握手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=secondHandshake"/> </div> <br>

**第三次握手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=thirdHandshake"/> </div> <br>

### 四次挥手

**第一次挥手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=firstWave"/> </div> <br>

**第二次挥手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=secondWave"/> </div> <br>

**第三次挥手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=thirdWave"/> </div> <br>

**第四次挥手**

<div align="center"> <img width="85%" src="https://api.zhengjianting.com/picture?name=fourthWave"/> </div> <br>

## 参考资料

计算机网络 - 自顶向下方法

[系统间通信技术](https://blog.csdn.net/yinwenjie/category_9264552_3.html)

[CS-Notes](https://github.com/CyC2018/CS-Notes)

