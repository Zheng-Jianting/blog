package com.zhengjianting.BIO.Client;

import java.util.concurrent.CountDownLatch;

public class SocketClientDaemon {
    public static void main(String[] args) throws InterruptedException {
        int clientNumber = 10;
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
