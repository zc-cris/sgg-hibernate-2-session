package com.zc.cris.pojo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSpring {

	@BeforeEach
	public void init() {
		System.out.println("init");
	}
	
	@AfterEach
	public void destroy() {
		System.out.println("destroy");
	}
	
	@Test
	void test() {
		System.out.println("test");
	}

}
