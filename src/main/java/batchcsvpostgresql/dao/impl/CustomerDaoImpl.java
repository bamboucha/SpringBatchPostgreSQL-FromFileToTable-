package batchcsvpostgresql.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import batchcsvpostgresql.dao.CustomerDao;
import batchcsvpostgresql.model.Customer;

@Repository
public class CustomerDaoImpl extends JdbcDaoSupport implements CustomerDao {
Logger log = LoggerFactory.getLogger(CustomerDaoImpl.class);
	
	@Autowired
	DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}

	@Override
	public void insert(List<? extends Customer> Customers) {

		String sql = "INSERT INTO customer (id, first_name, last_name) VALUES (?, ?, ?)"
				+ " where (first_name) = ?";
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Customer customer = Customers.get(i);
				ps.setLong(1, customer.getId());
				ps.setString(2, customer.getFirstName());
				ps.setString(3, customer.getLastName());
				ps.setString(4, "JACK");
			}
			public int getBatchSize() {
				return Customers.size();
			}
		});			
	}


	@Override
	public List<Customer> loadAllCustomers() {
		String sql = "SELECT * FROM customer";
		List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);

		List<Customer> result = new ArrayList<Customer>();
		for (Map<String, Object> row : rows) {
			Customer customer = new Customer();
			customer.setId((Long) row.get("id"));
			customer.setFirstName((String) row.get("first_name"));
			customer.setLastName((String) row.get("last_name"));
			result.add(customer);
		}

		return result;
	}

	@Override
	public void update() {
		String sqlUpdate = "Update customer set (first_name) = ? where (last_name) = ?";
		try {
			PreparedStatement pss = dataSource.getConnection().prepareStatement(sqlUpdate);
			pss.setString(1, "JACK");
			pss.setString(2, "FIRAS");
			pss.executeUpdate();
			pss.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("***********************");
		}
		
		
	}
}
