package com.nsalazar.quicktask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the QuickTask Spring Boot application.
 *
 * <p>This class bootstraps the Spring Boot application, enabling auto-configuration,
 * component scanning, and property support. It initializes the embedded web server
 * and configures all application beans.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li>Task management with CRUD operations</li>
 *   <li>Task list management for organizing tasks into groups</li>
 *   <li>RESTful API with pagination and sorting support</li>
 *   <li>Hexagonal architecture (domain, application, infrastructure layers)</li>
 * </ul>
 *
 * @author nsalazar
 */
@SpringBootApplication
public class QuicktaskApplication {

	/**
	 * Application entry point. Starts the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(QuicktaskApplication.class, args);
	}

}
