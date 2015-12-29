package com.force.guard;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.force.guard.aws.data.Sites;
import com.force.guard.checkers.JSErrorsChecker;
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
    private Sites sites;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private JSErrorsChecker jsErrorsChecker;

    @Override
    public void run(String... strings) throws Exception {
        new Thread(jsErrorsChecker).start();
    }
}
