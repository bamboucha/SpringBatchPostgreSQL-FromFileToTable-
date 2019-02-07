package batchcsvpostgresql.step;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import batchcsvpostgresql.model.CustomerDTO;

@Configuration
public class DatabaseToXmlFileJobConfig {
	
	@Autowired
	DataSource dataSource;
 
    private static final String QUERY_FIND_CUSTOMERS =
            "SELECT " +
                "id, " +
                "first_name, " +
                "last_name " +
            "FROM customer " +
            "ORDER BY id ASC";
 
    @Bean
    ItemReader<CustomerDTO> databaseXmlItemReader(DataSource dataSource) {
        JdbcCursorItemReader<CustomerDTO> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(QUERY_FIND_CUSTOMERS);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(CustomerDTO.class));
 
        return databaseReader;
    }
}