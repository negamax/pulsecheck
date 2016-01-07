package com.force.guard;

import com.force.guard.checkers.HttpConnectionChecker;
import com.force.guard.checkers.JSErrorsChecker;
import com.force.guard.checkers.SSLCertChecker;
import com.force.guard.services.ErrorReporter;
import com.force.guard.services.HouseKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.logging.Logger;


@SpringBootApplication
@ComponentScan
public class AliveApplication implements CommandLineRunner {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(AliveApplication.class, args);
    }

    @Value("${run.only.frontend}")
    private boolean runOnlyFrontendServices;

    @Autowired
    private JSErrorsChecker jsErrorsChecker;

    @Autowired
    private SSLCertChecker sslCertChecker;

    @Autowired
    private HttpConnectionChecker httpConnectionChecker;

    @Autowired
    private HouseKeeper houseKeeper;

    @Autowired
    private ErrorReporter errorReporter;

    @Override
    public void run(String... strings) throws Exception {

        logger.info("Starting application");

        if(!runOnlyFrontendServices) {
            new Thread(jsErrorsChecker).start();
            new Thread(sslCertChecker).start();
            new Thread(httpConnectionChecker).start();
            new Thread(houseKeeper).start();
        }

        new Thread(errorReporter).start();
    }
}
