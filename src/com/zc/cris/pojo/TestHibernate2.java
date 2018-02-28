package com.zc.cris.pojo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.Work;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHibernate2 {
	
	private SessionFactory sessionFactory = null;
	private Session session = null;
	private Transaction transaction = null;

	/*
	 * 测试News对象的派生属性
	 */
	@Test
	void testFormulaProperty() {
		News news = this.session.get(News.class, 4);
		System.out.println(news.getDesc());
	}
	
	/*
	 * 对象映射文件中的字段的update属性设置为false，对应的属性无法修改到数据库中
	 */
	@Test
	void testPropertyUpdate() {
		News news = this.session.get(News.class, 4);
		news.setAuthor("保罗");
	}
	
	/*
	 * 测试通过hibernate的配置文件来修改主键生成策略
	 */
	@Test
	void testIdGenerator() throws InterruptedException {
		News news = new News("bb", "cirs", new Date());
		this.session.save(news);
		
		Thread.sleep(5000);
	}
	
	/*
	 * 动态更新当前session缓存对象已经改变了的属性对应的记录
	 * (假如缓存对象只有name属性发生了改变，那么更新语句只会更新name属性对应的那个记录）
	 */
	@Test
	void testDynamicUpdate() {
		News news = this.session.get(News.class, 3);
		news.setAuthor("cris");
	}
	
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
	 * @Description: TODO (执行每次@Test 方法后需要执行的方法注解)
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
	
	/*
	 * 通过调用原生的Connection对象来调用数据库的存储过程
	 */
	@Test
	void testDoWork() {
		this.session.doWork(new Work() {
			
			@Override
			public void execute(Connection arg0) throws SQLException {
				System.out.println(arg0);	//com.mysql.jdbc.JDBC4Connection@33a053d
				//调用存储过程（同jdbc方式调用存储过程）
				//还可以进行批量操作（使用原生的connection）
			}
		});
	}
	
	/*
	 * evict:从session缓存中把指定的持久化对象移除
	 */
	@Test
	void testEvict() {
		News news = this.session.get(News.class, 3);
		News news2 = this.session.get(News.class, 4);
		
		news.setAuthor("123");
		news2.setAuthor("123");
		this.session.evict(news);	//无法发送update语句进行更新news对象对应的数据
	}
	
	/*
	 * delete：执行删除操作，只要OID和数据表中的一条记录对应（无论是游离对象还是持久化对象），就会准备执行delete操作，将数据库中对应的记录删除掉
	 * 		      如果OID在数据表中没有对应的记录，那么抛出异常
	 * 注意：
	 * 	可以通过设置hibernate 配置文件的hibernate.use_identifier_rollback为true，使得被删除的对象的OID设置为null
	 */
	@Test
	void testDelete() {
		News news = this.session.get(News.class, 2);
		this.session.delete(news);		//准备执行删除数据表记录操作，等到事务提交的时候才进行真正的删除操作
		System.out.println(news);	//news对象依然存在，并且这条语句优先delete语句执行
		
	}
	
	
	/*
	 * 注意：
	 * 1. 若OID不为空，但是数据表中没有这个OID对应的记录，那么会抛出异常
	 * 2. 若OID为null，本来应该是游离对象，但是如果OID等于id的unsaved-value属性值的对象，也会被认为是游离对象(了解）
	 */
	@Test
	void testSaveOrUpdate() {
		News news = new News("qq", "jj", new Date());
//		news.setId(111);
		this.session.saveOrUpdate(news);
	}
	
	
	/*
	 * update:
	 * 1. 若更新一个持久化对象，不需要显示的调用session的update方法，因为在调用Transaction的commit方法之前就已经执行了
	 * session的flush方法来确保session缓存中的数据和数据库中的记录一致
	 * 2. 若更新一个游离的对象，就需要显示的调用session的update方法，可以把一个游离的对象变为持久化对象
	 * 
	 * 注意：
	 * 1. 无论要更新的游离状态的对象和数据表的记录是否一致，都会发送update语句（因为新开的session并不能确定游离状态的对象和
	 * 		数据库中的对应的记录是否一致
	 * 		那么如何才能让session的update方法不盲目的发送update语句呢？在.hbm.xml文件的class节点设置select-before-update=true
	 * 		（默认为false），但是通常不要设置这个属性，因为效率较低，一般都是使用hibernate的触发器才可能使用这个属性
	 * 2. 若数据表中没有对应的记录，但还是调用了update方法，那么会抛出异常
	 * 
	 * 3. 当update方法关联一个游离的对象时,如果在session的缓存中已经有了相同OID的对象，那么调用update方法将会抛出异常，
	 * 因为在session的缓存中是不允许有两个OID相同的对象！
	 */
	@Test
	void testUpdate() {
		News news = this.session.get(News.class, 1);
		this.transaction.commit();
		this.session.close();
		
//		news.setId(1000);
		
		//之前的session已经被关闭，存储在session缓存中的对象成为了游离状态，此时新开的session缓存中没有之前查询的这个处于
		//游离状态的对象，如果想要通过新开的session对象将游离状态的对象持久化，就需要手工调用session的update方法了
		this.session = this.sessionFactory.openSession();
		this.transaction = this.session.beginTransaction();
		News news2 = this.session.get(News.class, 1);
		//news.setAuthor("老二爹");
		this.session.update(news);
	}
	
	
	/*
	 * get vs load
	 * 
	 * 1. 执行get方法会立即加载对象（立即检索）
	 * 		执行load方法，若不使用该对象，则不会立即执行查询操作，而是返回一个代理对象（延迟加载） 
	 * 
	 * 2. load方法可能会抛出懒加载异常 lazyInitiallizationException 异常:需要初始化代理对象之前已经关闭了session
	 * 
	 * 3. 若数据表中没有对应的记录，并且session没有关闭的情况下
	 * get返回null
	 * load方法如果不使用对象的任何属性，则没有问题；若需要初始化代理对象时，就抛出异常
	 */
	@Test
	void testLoad() {
		News news = this.session.load(News.class, 10);
		System.out.println(news.getClass().getName());
//		this.session.close();
		System.out.println(news);
	}
	
	@Test
	void testGet() {
		News news = this.session.get(News.class, 10);
//		this.session.close();
		System.out.println(news);
	}
	
	
	/*
	 * persist方法：也可以保存数据，执行insert语句
	 * 
	 * 和save方法的区别：
	 * 在调用persist方法之前，若对象已经有id主键了，将抛出异常并不会执行insert语句
	 */
	@Test
	void testPersist() {
		News news = new News("cc", "张三", new Date());
		System.out.println(news);
//		news.setId(1001);
		this.session.persist(news);;
	}
	
	
	/*
	 * save方法详解：
	 * 1. 使一个临时对象变为持久化对象
	 * 2. 为对象分配id（使用数据库自己的主键生成策略）
	 * 3. 在flush缓存时会发送一条insert语句
	 * 4. 在save方法之前设置的id可以设置，但是无效
	 * 5. 持久化后的对象的id是不可以改变的！
	 */
	@Test
	public void testSave() {
		
		News news = new News("aa", "张三", new Date());
		System.out.println(news);
		news.setId(1001);
		this.session.save(news);
		news.setId(222);
		System.out.println(news);
	}
	

}
