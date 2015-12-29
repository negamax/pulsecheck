package com.force.guard;

import com.force.guard.checkers.JSErrorsChecker;
import com.force.guard.checkers.SSLCertChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

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

    @Override
    public void run(String... strings) throws Exception {
        new Thread(jsErrorsChecker).start();
        new Thread(sslCertChecker).start();
    }
}
