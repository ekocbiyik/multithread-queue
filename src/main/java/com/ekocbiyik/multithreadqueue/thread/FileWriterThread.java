package com.ekocbiyik.multithreadqueue.thread;

import org.springframework.stereotype.Controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;

/**
 * ekocbiyik on 28.01.2020
 */
@Controller
public class FileWriterThread implements Runnable {

    private BlockingDeque<String> correlatedLinesQueue;
    private BufferedWriter writer;
    private String correlatedFile;

    @Override
    public void run() {
        try {
            int counter = 0;
            System.out.println("FileWriterThread: " + Thread.currentThread().getName() + " is started!");
            String correlatedLine;
            while (!(correlatedLine = correlatedLinesQueue.take()).isEmpty()) {
                writer.append(correlatedLine + System.lineSeparator());
                ++counter;
            }
            System.out.println("FileWriterThread: " + Thread.currentThread().getName() + " is finished with total: " + counter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCorrelatedLinesQueue(BlockingDeque<String> correlatedLinesQueue) {
        this.correlatedLinesQueue = correlatedLinesQueue;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public void setCorrelatedFile(String correlatedFile) {
        this.correlatedFile = correlatedFile;
    }
}
