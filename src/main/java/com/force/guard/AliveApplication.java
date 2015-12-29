package com.force.guard;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.force.guard.aws.data.Sites;
import com.force.guard.aws.data.models.JSError;
import com.force.guard.checkers.JSErrorsChecker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

//        ListTablesResult lstResult = amazonDynamoDB.listTables();
//
////        GetItemResult items = amazonDynamoDB.getItem(new GetItemRequest().withTableName("sites"));
//
//        for(String name : lstResult.getTableNames()) {
//            System.out.println(name);
//        }
//        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
//
////        Site site = new Site();
////
////        site.setName("work!");
////
////        mapper.save(site);
//
//        List<Site> sites = mapper.scan(Site.class, new DynamoDBScanExpression());
//
//        for(Site site : sites) {
//            System.out.println(site.getName());
//        }
//
//
////        Table  table = dynamoDB.getTable("sites");
////
////        for(Item item : table.)
//
////        DynamoDBMapper mapper = new DynamoDBMapper(this.dynamoDB);
    }
}
