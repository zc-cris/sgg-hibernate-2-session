package com.zc.cris.pojo.component;


public class Emp {
	private int id;
	private String name;
	
	//工资类
	private Salary salary;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Salary getSalary() {
		return salary;
	}
	public void setSalary(Salary salary) {
		this.salary = salary;
	}
	
	@Override
	public String toString() {
		return "Emp [id=" + id + ", name=" + name + ", salary=" + salary + "]";
	}
	public Emp(int id, String name, Salary salary) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	public Emp() {
		super();
		
	}
	public Emp(String name, Salary salary) {
		super();
		this.name = name;
		this.salary = salary;
	}
	
	
}
