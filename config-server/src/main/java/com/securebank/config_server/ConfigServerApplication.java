package com.securebank.config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer

public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}

/*		application.yml
      This is config-server's OWN config
      Only the config-server itself reads this
      Controls how config-server runs (port, eureka registration, etc.)

	  config-repo/application.yml
      This is SHARED config for ALL other microservices
	eureka-server, auth-service, customer-service etc. all read this
	Think of it as global settings inherited by every service


	eureka-server starts
      ↓
asks config-server for its config
      ↓
config-server looks in config-repo/
      ↓
returns config-repo/application.yml (shared)
     +
returns config-repo/eureka-server.yml (specific)
      ↓
eureka-server applies both
      */