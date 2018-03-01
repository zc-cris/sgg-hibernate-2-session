package com.zc.cris.pojo.component;


public class Salary {
	
	//月薪
	private Integer monthSalary;
	
	//年薪
	private Integer yearSalary;
	
	//对应映射文件component元素的parent节点（如果使用parent子节点，就必须在组件类即Salary类设置Emp类属性）
	private Emp emp;

	public Integer getMonthSalary() {
		return monthSalary;
	}

	public void setMonthSalary(Integer monthSalary) {
		this.monthSalary = monthSalary;
	}

	public Integer getYearSalary() {
		return yearSalary;
	}

	public void setYearSalary(Integer yearSalary) {
		this.yearSalary = yearSalary;
	}

	public Emp getEmp() {
		return emp;
	}

	public void setEmp(Emp emp) {
		this.emp = emp;
	}

	@Override
	public String toString() {
		return "Salary [monthSalary=" + monthSalary + ", yearSalary=" + yearSalary;
	}

	public Salary() {
		super();
		
	}

	public Salary(Integer monthSalary, Integer yearSalary, Emp emp) {
		super();
		this.monthSalary = monthSalary;
		this.yearSalary = yearSalary;
		this.emp = emp;
	}

	public Salary(Integer monthSalary, Integer yearSalary) {
		super();
		this.monthSalary = monthSalary;
		this.yearSalary = yearSalary;
	}

	
}
