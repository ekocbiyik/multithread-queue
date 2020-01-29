package com.ekocbiyik.multithreadqueue;

import com.ekocbiyik.multithreadqueue.controller.CorrelationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationStarterTests {

    @Autowired
    private CorrelationController correlationController;

    @Test
    void contextLoads() throws Exception {
        correlationController.startCorrelation();
    }

}
