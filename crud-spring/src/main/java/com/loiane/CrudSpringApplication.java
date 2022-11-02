package com.loiane;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.loiane.enums.Category;
import com.loiane.enums.Status;
import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

@SpringBootApplication
public class CrudSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudSpringApplication.class, args);
	}

	// @Bean
	// CommandLineRunner initDatabase(CourseRepository courseRepository) {
	// return args -> extracted(courseRepository);
	// }

	// private void extracted(CourseRepository courseRepository) {
	// courseRepository.deleteAll();
	// for (int i = 1; i < 20; i++) {
	// Course c = new Course();
	// c.setName("Curso " + i);
	// c.setCategory(Category.FRONT_END);
	// c.setStatus(Status.ACTIVE);
	// courseRepository.save(c);
	// }
	// }

}
