package com.frank.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AgencyServiceClient {
	private final RestTemplate restTemplate;

	private static final Log log = LogFactory.getLog(AgencyServiceClient.class);

	public static final String FIND_A_BOSS_TASK = "find a new boss";

	public AgencyServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	@HystrixCommand(fallbackMethod = "getFallbackName", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
	})
	public String getName() {
		return this.restTemplate.getForObject(String.format("http://svc-agency-service/name"), String.class);
	}

	private String getFallbackName() {
		return "Fallback";
	}
}
