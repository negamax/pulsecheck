package com.force.guard;

import com.force.guard.checkers.HttpConnectionChecker;
import com.force.guard.checkers.JSErrorsChecker;
import com.force.guard.checkers.SSLCertChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class AliveApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(AliveApplication.class, args);
    }

    @Autowired
    private JSErrorsChecker jsErrorsChecker;

    @Autowired
    private SSLCertChecker sslCertChecker;

    @Autowired
    private HttpConnectionChecker httpConnectionChecker;

    @Override
    public void run(String... strings) throws Exception {
        new Thread(jsErrorsChecker).start();
        new Thread(sslCertChecker).start();
        new Thread(httpConnectionChecker).start();
    }
}
