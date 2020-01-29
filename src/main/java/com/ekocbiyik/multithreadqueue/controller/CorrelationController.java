package com.ekocbiyik.multithreadqueue.controller;

import com.ekocbiyik.multithreadqueue.thread.CorrelationThread;
import com.ekocbiyik.multithreadqueue.thread.FileWriterThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ekocbiyik on 28.01.2020
 */

@Controller
public class CorrelationController {

    @Autowired
    private ApplicationContext applicationContext;

    public void startCorrelation() throws IOException {

        long start = System.currentTimeMillis();

        String firstFilePath = "/tmp/multithread/first/";
        String lastFilePath = "/tmp/multithread/last/";
        String firstFilename = "firstFile.csv";
        String lastFilename = "lastFile.csv";

        Files.createDirectories(Paths.get(firstFilePath));
        Files.createDirectories(Paths.get(lastFilePath));
        generateFirstFile(firstFilePath + firstFilename);

        int totalCoreCount = Runtime.getRuntime().availableProcessors();
        int correlationCoreCount = totalCoreCount - 1;
        int fileWriterCoreCount = 1;

        BlockingDeque<String> fileWriterQueue = new LinkedBlockingDeque<>();
        ExecutorService fileWriteExecutor = Executors.newFixedThreadPool(fileWriterCoreCount);
        FileWriterThread fileWriterThread = applicationContext.getBean(FileWriterThread.class);
        fileWriterThread.setCorrelatedLinesQueue(fileWriterQueue);
        fileWriterThread.setWriter(new BufferedWriter(new FileWriter(new File(lastFilePath + lastFilename))));
        fileWriterThread.setCorrelatedFile(lastFilePath + lastFilename);
        fileWriteExecutor.execute(fileWriterThread);

        //..
        ExecutorService correlationExecutor = Executors.newFixedThreadPool(correlationCoreCount);
        BlockingDeque<String> correlationQueue = new LinkedBlockingDeque<>();
        for (int i = 0; i < correlationCoreCount; i++) {
            CorrelationThread correlationThread = applicationContext.getBean(CorrelationThread.class);
            correlationThread.setIpfixLinesQueue(correlationQueue);
            correlationThread.setCorrelatedLinesQueue(fileWriterQueue);
            correlationExecutor.execute(correlationThread);
        }

        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(firstFilePath + firstFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                correlationQueue.put(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < correlationCoreCount; i++) correlationQueue.add("");
        }

        correlationExecutor.shutdown();
        while (!correlationExecutor.isTerminated()) ;
        System.out.println("correlationExecutor finished...");

        for (int i = 0; i < fileWriterCoreCount; i++) fileWriterQueue.add("");
        fileWriteExecutor.shutdown();
        while (!fileWriteExecutor.isTerminated()) ;
        System.out.println("fileWriteExecutor finished...");

        System.out.println("Total time execution: " + TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - start)) + " s");
    }

    private void generateFirstFile(String fullPath) throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            lines.add("line_" + i);
        }
        Files.write(Paths.get(fullPath), (Iterable<String>) lines::iterator);
    }
}
