package batchcsvpostgresql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import batchcsvpostgresql.controller.WebController;
import batchcsvpostgresql.dao.impl.CustomerDaoImpl;
import batchcsvpostgresql.step.Listener;
import batchcsvpostgresql.step.Processor;
import batchcsvpostgresql.step.Reader;
import batchcsvpostgresql.step.Writer;

@SpringBootApplication
public class SpringBatchCsvPostgreSqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchCsvPostgreSqlApplication.class, args);
	}
}
