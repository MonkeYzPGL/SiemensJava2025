package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InternshipApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "The application context should not be null");
	}

	@Test
	void itemControllerLoads() {
		assertTrue(applicationContext.containsBean("itemController"), "ItemController should be loaded");
	}

	@Test
	void itemServiceLoads() {
		assertTrue(applicationContext.containsBean("itemService"), "ItemService should be loaded");
	}

	@Test
	void itemRepositoryLoads() {
		assertTrue(applicationContext.containsBean("itemRepository"), "ItemRepository should be loaded");
	}
}
