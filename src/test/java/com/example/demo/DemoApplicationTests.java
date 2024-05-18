package com.example.demo;

import com.example.demo.services.DroneService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private DroneService droneService;
	@Test
	void contextLoads() {
		Assertions.assertNotNull(droneService);
	}

}
