package com.frank.demo.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frank.demo.entity.Agency;
import com.frank.demo.repository.AgencyRepository;

@RestController
public class Controller {
	@Autowired
	private AgencyRepository agencyRepository;

	private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

	private final String hostName = System.getenv("HOSTNAME");

	@RequestMapping("/")
	public String ribbonPing() {
		LOG.info("Ribbon ping");
		return this.hostName;
	}

	@RequestMapping("/name")
	public String getName() {
		LOG.info(String.format("This response is from host '%s' ", this.hostName));
		return this.hostName;
	}

	@GetMapping("/{id}")
	public Agency findById(@PathVariable Long id) {
		Optional<Agency> findOne = this.agencyRepository.findById(id);
		return findOne.get();
	}
}
