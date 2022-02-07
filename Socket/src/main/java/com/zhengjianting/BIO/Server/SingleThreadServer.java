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
