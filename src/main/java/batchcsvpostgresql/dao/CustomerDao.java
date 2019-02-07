package batchcsvpostgresql.dao;

import java.util.List;

import batchcsvpostgresql.model.Customer;

public interface CustomerDao {
	public void insert(List<? extends Customer> customers);
	List<Customer> loadAllCustomers();
	void update();
}
