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
