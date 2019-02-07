package batchcsvpostgresql.step;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import batchcsvpostgresql.dao.CustomerDao;
import batchcsvpostgresql.model.Customer;


public class Listener extends JobExecutionListenerSupport implements JobExecutionListener{
	private static final Logger log = LoggerFactory.getLogger(Listener.class);

	private final CustomerDao customerDao;

	public Listener(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("Finish Job! Check the results");
			customerDao.update();
			
			List<Customer> customers = customerDao.loadAllCustomers();
			for (Customer customer : customers) {
				log.info("Found <" + customer + "> in the database.");
			}
		}
		
		else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			log.error("Warning Job has failed");
		}
		
		
		
	}
	
/*	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			jdbcTemplate.query("SELECT first_name, last_name FROM people",
				(rs, row) -> new Person(
					rs.getString(1),
					rs.getString(2))
			).forEach(person -> log.info("Found <" + person + "> in the database."));
		}
	}*/
}
