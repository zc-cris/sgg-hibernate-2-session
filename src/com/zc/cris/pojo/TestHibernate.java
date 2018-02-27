package com.zc.cris.pojo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHibernate {
	
	
	private SessionFactory sessionFactory = null;
	private Session session = null;
	private Transaction transaction = null;

	/**
	 * 
	 * @MethodName: init
	 * @Description: TODO (执行每次@Test 方法前需要执行的方法)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
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
	
	/**
	 * 
	 * @MethodName: destroy
	 * @Description: TODO (执行每次@Test 方法后需要执行的方法)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
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
	
	/**
	 * 
	 * @MethodName: testClear
	 * @Description: TODO (清理session中的缓存)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@Test
	public void testClear() {
		News news = this.session.get(News.class, 1);
		
		this.session.clear();
		//执行这个方法的时候还是发送了sql语句去进行查询
		News news2 = this.session.get(News.class, 1);
	}
	
	
	/**
	 * 
	 * @MethodName: testRefresh
	 * @Description: TODO (refresh方法会强制发送select语句，以使session缓存中的对象状态和数据同数据库表中的记录保持一致！)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@Test
	public void testRefresh() {
		News news = this.session.get(News.class, 1);
		System.err.println(news);
		
		//即使是在当前连接mysql事务级别为REPEATABLE-READ的情况下，也可以正常执行（不需要在hibernate的配置文件中进行事务的配置）
		this.session.refresh(news);
		System.out.println(news);
	}
	
	
	/**
	 * 
	 * @MethodName: testSessionCatch
	 * @Description: TODO (测试session的缓存)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@Test
	public void testSessionCatch() {
		//实际上由于 session 的缓存（一级缓存）作用，这里只查询了一次数据库，第二次查询的数据(如果和第一次相同)，那么是从 session 缓存中获取的
		//对应的控制台打印的sql语句：Hibernate: 
							//    select
							//        news0_.ID as ID1_0_0_,
							//        news0_.TITLE as TITLE2_0_0_,
							//        news0_.AUTHOR as AUTHOR3_0_0_,
							//        news0_.DATE as DATE4_0_0_ 
							//    from
							//        NEWS news0_ 
							//    where
							//        news0_.ID=?
							//	News [id=1, title=java, author=james, date=2018-02-27 09:39:30.0]
							//	News [id=1, title=java, author=james, date=2018-02-27 09:39:30.0]
		News news = this.session.get(News.class, 1);
		System.out.println(news);
		News news2 = this.session.get(News.class, 1);
		System.out.println(news2);
	}
	
	/**
	 * 
	 * @MethodName: testSessionFlush2
	 * @Description: TODO (当数据库主键生成策略为native的时候，调用session的save方法就会立即执行sql语句)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	public void testSessionFlush2() {
		this.session.save(new News("oracle", "老铁", new Date()));
	}
	
	/**
	 * flush：使数据表中的数据和 session 缓存中的对象的状态保持一致，为了保持一致，可能会发送对应的 sql 语句（在不一致的情况下）
	 * 1. 在Transaction 的commit方法中：先调用session的flush方法，再提交事务
	 * 2. 调用flush方法可能会发送sql语句，但是不会提交事务
	 * 3. 注意：在未提交事务或显示的调用session.flush（）方法之前，也有可能会进行flush操作（发送sql语句）
	 * 	- 执行HQL或者QBC查询，会先进行flush操作，以得到数据表的最新记录  
	 *  - 若数据库的id是由底层数据库使用自增的方式生成的，则在调用save方法的时候，就会立即发送insert语句并执行
	 *  	因为调用save方法后，需要保证数据库中要有这个保存对象的记录id
	 */
	@Test
	public void testSessionFlush() {
		
		News news = this.session.get(News.class, 1);
		news.setAuthor("Oracle");
		
//		session.flush();
		
		// QBC 查询(不建议使用了）
		News news2 = (News) this.session.createCriteria(News.class).uniqueResult();
		System.out.println(news2);
		
	}
	
	/**
	 * 
	 * @MethodName: testSave
	 * @Description: TODO (通过session向数据库插入数据)
	 * @Return Type: void
	 * @Author: zc-cris
	 */
	@Test
	public void testSave() {
		this.session.save(new News("java", "james", new Date()));
		
	}

}
