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

	private final String hostName = System.getenv("HOSTNAME");


	public RiskController(AgencyServiceClient client) {
		this.agencySvc = client;
	}

	@RequestMapping("/")
	public String ribbonPing() {
		LOGGER.info("Ribbon ping");
		return this.hostName+" from risk-service";
	}


	@RequestMapping("/greeting")
	public String getGreeting() {
		String res = this.agencySvc.getName();
		LOGGER.info(String.format("Hello from %s!", res));
		return String.format("Hello from %s!", res);
	}
}
