package com.hyman.springboot;

import com.hyman.springboot.config.MongoCustomerRepository;
import com.hyman.springboot.entity.Customer;
import com.hyman.springboot.entity.SampleJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author huaimin
 * @date 2019/10/31
 */
@SpringBootApplication
public class SpringbootReactiveApplication implements CommandLineRunner {

    @Autowired
    private MongoCustomerRepository repository;

    @Override
    public void run(String... args) throws Exception {
        this.repository.deleteAll();

        // save a couple of customers
        Customer customer = new Customer();
        customer.setFirstName("Alice");
        customer.setLastName("Smith");
        this.repository.save(customer);
        customer = new Customer();
        customer.setFirstName("Bob");
        customer.setLastName("Smith");
        this.repository.save(customer);

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Customer item : this.repository.findAll()) {
            System.out.println(item);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(this.repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer item : this.repository.findByLastName("Smith")) {
            System.out.println(item);
        }
    }


    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SampleJob.class).withIdentity("sampleJob").usingJobData("name", "World").storeDurably()
                .build();
    }

    /**
     * 简单触发器
     * @return
     */
    @Bean
    public Trigger sampleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2)
                .repeatForever();

        return TriggerBuilder.newTrigger().forJob(sampleJobDetail()).withIdentity("sampleTrigger")
                .withSchedule(scheduleBuilder).build();
    }

    /**
     * http://www.bejson.com/othertools/cron/
     * @return
     */
    @Bean
    public Trigger cronJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 * * * * ?");

        return TriggerBuilder.newTrigger().forJob(sampleJobDetail()).withIdentity("sampleTrigger")
                .withSchedule(scheduleBuilder).build();
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringbootReactiveApplication.class, args);
    }

}
