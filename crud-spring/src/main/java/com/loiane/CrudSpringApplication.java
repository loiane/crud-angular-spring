package com.loiane;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.loiane.enums.Category;
import com.loiane.enums.Status;
import com.loiane.model.Course;
import com.loiane.model.Lesson;
import com.loiane.repository.CourseRepository;

@SpringBootApplication
public class CrudSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudSpringApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner initDatabase(CourseRepository courseRepository) {
		return args -> extracted(courseRepository);
	}

	private void extracted(CourseRepository courseRepository) {
		courseRepository.deleteAll();
		for (int i = 1; i < 20; i++) {
			Course c = new Course();
			c.setName("Course " + i);
			c.setCategory(Category.FRONT_END);
			c.setStatus(Status.ACTIVE);

			Set<Lesson> lessons = new HashSet<>();
			Lesson lesson = new Lesson();
			lesson.setName("Lesson " + i);
			lesson.setYoutubeUrl("abcdefgh123");
			lesson.setCourse(c);
			lessons.add(lesson);
			c.setLessons(lessons);

			courseRepository.save(c);
		}
	}

}
