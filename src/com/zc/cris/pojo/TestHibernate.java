package com.zc.cris.pojo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHibernate {
	
	//junit单元测试类里面的属性需要初始化为null
	private SessionFactory sessionFactory = null;
	private Session session = null;
	private Transaction transaction = null;

	@BeforeEach
	public void init() {
//		 //但在5.1.0版本汇总，hibernate则采用如下新方式获取：
//	    //1. 配置类型安全的准服务注册类，这是当前应用的单例对象，不作修改，所以声明为final
	    //在configure("cfg/hibernate.cfg.xml")方法中，如果不指定资源路径，默认在类路径下寻找名为hibernate.cfg.xml的文件
	    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("/hibernate.cfg.xml").build();
//	    //2. 根据服务注册类创建一个元数据资源集，同时构建元数据并生成该应用唯一（一般情况下）的一个session工厂
	    this.sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//		
//		//2. 创建一个Session对象
	    this.session = this.sessionFactory.openSession();
//		
//		//3. 开启事务
	    this.transaction = this.session.beginTransaction();
		
//		System.out.println("init");
	}
	
	@AfterEach
	public void destroy() {
		
//		//5. 提交事务
		this.transaction.commit();
//		
//		//6. 关闭session
		this.session.close();
//		
//		//7. 关闭sessionFactory对象
		this.sessionFactory.close();
		
//		System.out.println("destroy");
	}
	
	@Test
	public void test() {
		
		
		
	}


}
