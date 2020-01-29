package com.ekocbiyik.multithreadqueue.thread;

import org.springframework.stereotype.Controller;

import java.util.concurrent.BlockingDeque;

/**
 * ekocbiyik on 28.01.2020
 */
@Controller
public class CorrelationThread implements Runnable {

    private BlockingDeque<String> ipfixLinesQueue;
    private BlockingDeque<String> correlatedLinesQueue;

    @Override
    public void run() {
        try {
            int counter = 0;
            System.out.println("CorrelationThread: " + Thread.currentThread().getName() + " is started!");
            String ipfixLine;
            while (!(ipfixLine = ipfixLinesQueue.take()).isEmpty()) {
                correlatedLinesQueue.add(ipfixLine + "_correlated!");
                ++counter;
            }
            System.out.println("CorrelationThread: " + Thread.currentThread().getName() + " is finished with total: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIpfixLinesQueue(BlockingDeque<String> ipfixLinesQueue) {
        this.ipfixLinesQueue = ipfixLinesQueue;
    }

    public void setCorrelatedLinesQueue(BlockingDeque<String> correlatedLinesQueue) {
        this.correlatedLinesQueue = correlatedLinesQueue;
    }
}
