package com.loiane.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.loiane.course.dto.CourseDTO;
import com.loiane.course.dto.CoursePageDTO;
import com.loiane.course.dto.CourseRequestDTO;
import com.loiane.course.dto.mapper.CourseMapper;
import com.loiane.course.enums.Status;
import com.loiane.exception.BusinessException;
import com.loiane.exception.RecordNotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Service
@Validated
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public CoursePageDTO findAll(@PositiveOrZero int page, @Positive @Max(1000) int pageSize) {
        Page<CourseDTO> coursePage = courseRepository.findAll(PageRequest.of(page, pageSize))
                .map(courseMapper::toDTO);
        return new CoursePageDTO(coursePage.getContent(), coursePage.getTotalElements(),
                coursePage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> findByName(@NotNull @NotBlank String name) {
        return courseRepository.findByNameContainingIgnoreCase(name).stream()
                .map(courseMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDTO findById(@Positive @NotNull Long id) {
        return courseRepository.findById(id).map(courseMapper::toDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    @Transactional
    public CourseDTO create(@Valid CourseRequestDTO courseRequestDTO) {
        validateUniqueName(courseRequestDTO.name(), null);
        Course course = courseMapper.toModel(courseRequestDTO);
        course.setStatus(Status.ACTIVE);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    @Transactional
    public CourseDTO update(@Positive @NotNull Long id, @Valid CourseRequestDTO courseRequestDTO) {
        return courseRepository.findById(id).map(actual -> {
            validateUniqueName(courseRequestDTO.name(), id);
            actual.setName(courseRequestDTO.name());
            actual.setCategory(courseMapper.convertCategoryValue(courseRequestDTO.category()));
            mergeLessonsForUpdate(actual, courseRequestDTO);
            return courseMapper.toDTO(courseRepository.save(actual));
        })
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    /**
     * A course name must be unique across all courses, including soft-deleted
     * ones, mirroring the unique constraint on the name column. When updating,
     * the course being updated may keep its own name.
     */
    private void validateUniqueName(String name, Long currentId) {
        boolean duplicateExists = courseRepository.findByNameIgnoringRestriction(name).stream()
                .anyMatch(course -> !course.getId().equals(currentId));
        if (duplicateExists) {
            throw new BusinessException("A course with name '" + name + "' already exists");
        }
    }

    private void mergeLessonsForUpdate(Course updatedCourse, CourseRequestDTO courseRequestDTO) {

        // remove the lessons that are no longer present in the request
        List<Lesson> lessonsToRemove = updatedCourse.getLessons().stream()
                .filter(lesson -> courseRequestDTO.lessons().stream()
                        .noneMatch(lessonDto -> lessonDto._id() != 0 && lessonDto._id() == lesson.getId()))
                .toList();
        lessonsToRemove.forEach(updatedCourse::removeLesson);

        // update the lessons that match an existing id; anything else (id 0 or
        // an id that does not belong to this course) is added as a new lesson
        courseRequestDTO.lessons().forEach(lessonDto -> updatedCourse.getLessons().stream()
                .filter(lesson -> lessonDto._id() != 0 && lesson.getId() == lessonDto._id())
                .findAny()
                .ifPresentOrElse(lesson -> {
                    lesson.setName(lessonDto.name());
                    lesson.setYoutubeUrl(lessonDto.youtubeUrl());
                }, () -> updatedCourse.addLesson(courseMapper.convertLessonDTOToLesson(lessonDto))));
    }

    @Transactional
    public void delete(@Positive @NotNull Long id) {
        courseRepository.delete(courseRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
