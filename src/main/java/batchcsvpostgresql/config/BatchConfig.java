package batchcsvpostgresql.config;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import batchcsvpostgresql.dao.CustomerDao;
import batchcsvpostgresql.model.Customer;
import batchcsvpostgresql.model.CustomerDTO;
import batchcsvpostgresql.step.Listener;
import batchcsvpostgresql.step.Processor;
import batchcsvpostgresql.step.Reader;
import batchcsvpostgresql.step.Writer;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	final String QUERY_FIND_CUSTOMERS =  "SELECT " +
            "id, " +
            "first_name, " +
            "last_name " +
        "FROM customer " +
        "ORDER BY id ASC";

	private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public CustomerDao customerDao;

	@Autowired
	JobLauncher jobLauncher;

	@Bean
	public Job job() {

	final Job jobToBeReturnedInsert =  jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).listener(new Listener(customerDao))
				.flow(step1()).end().build();

	 final Job jobToBeReturnedUpdate = jobBuilderFactory
				.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(new Listener(customerDao))
				.flow(step1()).end().build();

		//Si je veux éxécuter je passe par le job launcher 
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(jobToBeReturnedInsert, jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			e.printStackTrace();
		} catch (JobRestartException e) {
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}
		
		/*try {
			jobLauncher.run(jobToBeReturnedUpdate, jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			e.printStackTrace();
		} catch (JobRestartException e) {
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}*/


		List<Customer> customers = customerDao.loadAllCustomers();
		for (Customer customer : customers) {
			log.info("Found <" + customer + "> in the database.");
		}
		
		return jobToBeReturnedInsert;
}

	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(2)
				.reader(Reader.reader("customer-data.csv"))
				.processor(new Processor()).writer(new Writer(customerDao)).build();
	}



}
