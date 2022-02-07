## 协议栈

网络设计者以分层的方式组织协议以及实现这些协议的网络硬件和软件。

![image-20220207183529034](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207183529034.png)

1. ### 因特网协议栈

   - 应用层（application layer）：应用层是网络应用程序及它们的应用层协议存留的地方，位于应用层的信息分组称为报文（message）。
   - 传输层（transport layer）：传输层在应用程序端点之间传送应用层报文，为进程提供通用数据传输服务。在因特网中，有两种传输协议，即 TCP 和 UDP，利用其中任一个都能传输应用层报文。TCP 提供面向连接、可靠的数据传输服务，并提供拥塞控制机制；UDP 则提供无连接的数据传输服务，不提供不必要的服务，没有可靠性、流量控制、拥塞控制。TCP 和 UDP 分组均称为报文段（segment），也有人将 UDP 分组称为用户数据报。
   - 网络层（network layer）：网络层负责将数据报（datagram）的网络层分组从一台主机移动到另一台主机，网络层包括著名的网际协议 IP。
   - 链路层（data link layer）：网络层针对的是主机之间的数据传输服务，而主机之间可以有很多链路，链路层协议就是为同一链路的主机提供数据传输服务，数据链路层把网络层传下来的分组封装成帧（frame）。
   - 物理层（physical layer）：链路层的任务是将整个帧从一个网络元素移动到邻近的网络元素，而物理层的任务是将该帧中的一个个比特从一个节点移动到下一个节点。

2. ### OSI

   20 世纪 70 年代后期，国际标准化组织（ISO）提出计算机网络围绕 7 层来组织，称为开放系统互连（OSI）模型，OSI 在因特网协议栈的基础上附加了两层，表示层和会话层：

   - 表示层（presentation layer）：作用是使通信的应用程序能够解释交换数据的含义，这些服务包括数据压缩和数据加密以及数据描述（这使得应用程序不必担心在各台计算机中表示 / 存储的内部格式不同）。
   - 会话层（session layer）：提供了数据交换的定界和同步功能，包括了建立检查点和恢复方案的方法。

   五层因特网协议栈没有表示层和会话层，而是将这些功能留给应用程序开发者处理。

3. ### TCP / IP

   它只有四层，相当于五层因特网协议栈中链路层和物理层合并为网络接口层（network access / link layer）。

   TCP / IP 体系结构不严格遵循 OSI 分层概念，应用层可能会直接使用 IP 层或者网络接口层。

   ![image-20220207190826197](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207190826197.png)

4. 数据在各层之间的传递过程

   在向下的过程中，需要添加下层协议所需要的首部或者尾部，而在向上的过程中不断拆开首部和尾部。

   路由器只有下面三层协议，因为路由器位于网络核心中，不需要为进程或者应用程序提供服务，因此也就不需要传输层和应用层。

## 套接字（Socket）

### 进程通信

当多个进程运行在相同的主机上时，它们使用进程间通信机制（由操作系统确定）相互通信。这里仅关注运行在不同主机（操作系统可能不一致）上的进程间通信，在两个不同主机上的进程，通过计算机网络交换报文（message）而相互通信。

![image-20220207193010047](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207193010047.png)

两个位于不同主机的进程通过套接字通信，**套接字是同一台主机内应用层和传输层之间的接口**，由于套接字是建立网络应用程序的可编程接口，因此套接字也称为应用程序和网络之间的**应用程序编程接口**（Application Programming Interface，API）。应用程序开发者可以控制套接字在应用层的一切，但对该套接字的传输层几乎没有控制权，仅限于：

- 选择传输层协议
- 设定几个传输层参数，如最大缓存和最大报文段长度等

### 套接字标识符

一个进程（作为网络应用的一部分）有一个或多个套接字，它相当于从网络向进程传递数据和从进程向网络传递数据的门户。由于在任一时刻，主机上可能有不止一个套接字，所以每个套接字都有唯一的标识符。标识符的格式取决于它是 TCP 还是 UDP 套接字：

- UDP 套接字：由一个二元组（目的 IP 地址，目的端口号）全面标识
- TCP 套接字：由一个四元组（源 IP 地址，源端口号，目的 IP 地址，目的端口号）全面标识

### TCP 套接字编程

当我们通过浏览器访问一个 URL 时，将域名解析为服务器 IP 地址后，先要生成一个客户端 TCP 套接字，和服务器进行三次握手建立连接，再向服务器发送 HTTP 报文。

![image-20220207212455812](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207212455812.png)

服务器的欢迎套接字即 ServerSocket，以 HTTP 为例，ServerSocket 对应于服务器的 80 端口，在经过三次握手建立 TCP 连接后，ServerSocket.accept() 会为该连接新建一个连接套接字进行数据传输，该连接套接字由（客户端 IP 地址，客户端套接字端口号，服务器 IP 地址，80）标识。

注意，具有不同 IP 地址或端口号的客户端套接字在进行三次握手建立 TCP 连接时，在服务器与之通信的都是欢迎套接字，当然，建立连接后，由于客户端 IP 地址或客户端口号不同，在服务器与之通信的是不同的连接套接字。

## BIO 通信方式

为了使 WireShark 抓包时不受其它因素影响，用 Java 编写一个最简单的阻塞式 I / O，服务器端单线程的 CS 应用程序，一个客户端对应一个线程，利用 CountDownLatch 同时发送请求，此处只为了抓包，客户端数量设置为 1.

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

![image-20220207215519275](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207215519275.png)

![image-20220207215553784](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207215553784.png)

## WireShark 抓包

运行上述例子，监听本地回环网卡，抓包结果如下：

![image-20220207215807986](C:\Users\zjt\AppData\Roaming\Typora\typora-user-images\image-20220207215807986.png)

显然，前三个报文段对应于 TCP 三次握手，最后四个报文段对应于 TCP 四次挥手。其中客户端套接字端口为 61863，服务器端的欢迎套接字（ServerSocket）端口为 10526，三次握手时，服务器端与客户端套接字通信的是欢迎套接字，其余时刻与之对应的是 ServerSocket.accept() 新建的由四元组全面标识的连接套接字，虽然它们在 WireShark 中显示的服务器端口均为 10526.

### TCP 报文

### TCP 三次握手

### TCP 四次挥手

