package com.frank.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frank.demo.AgencyServiceClient;

@RestController
public class RiskController {
	private static final Logger LOGGER = LoggerFactory.getLogger(RiskController.class);
	private final AgencyServiceClient agencySvc;

	public RiskController(AgencyServiceClient client) {
		this.agencySvc = client;
	}

	@RequestMapping("/greeting")
	public String getGreeting() {
		String res = this.agencySvc.getName();
		LOGGER.info(String.format("Hello from %s!", res));
		return String.format("Hello from %s!", res);
	}
}
