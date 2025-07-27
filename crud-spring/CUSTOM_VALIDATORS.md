# Custom Validators Implementation Summary

This document summarizes the custom validators implemented to improve data integrity in the Spring Boot application.

## Implemented Custom Validators

### 1. YouTube URL Validator (`@ValidYouTubeUrl`)

**Purpose**: Validates YouTube video IDs and URLs to ensure they follow the correct format.

**Features**:
- Validates 11-character YouTube video ID format
- Accepts direct video IDs (e.g., "dQw4w9WgXcQ")
- Extracts and validates video IDs from full YouTube URLs
- Supports multiple URL formats:
  - `https://youtu.be/VIDEO_ID`
  - `https://www.youtube.com/watch?v=VIDEO_ID`
  - Direct video ID strings

**Applied to**:
- `Lesson.youtubeUrl` field
- `LessonDTO.youtubeUrl` field

**Usage**:
```java
@ValidYouTubeUrl
private String youtubeUrl;
```

### 2. Unique Course Name Validator (`@UniqueCourseNameValidation`)

**Purpose**: Ensures course names are unique among active courses in the database.

**Features**:
- Checks for duplicate course names in active status
- Works at the class level for CourseRequestDTO
- Provides specific error messages with the conflicting name
- Uses constructor injection for better testability

**Applied to**:
- `CourseRequestDTO` class level

**Usage**:
```java
@UniqueCourseNameValidation
public record CourseRequestDTO(...) {}
```

### 3. Valid Course Name Validator (`@ValidCourseName`)

**Purpose**: Validates course names for professional content and appropriate formatting.

**Features**:
- Checks for valid character patterns (letters, numbers, common punctuation)
- Prevents excessive special characters
- Detects excessive spacing
- Filters out inappropriate/test content (e.g., "test", "dummy", "lorem")
- Ensures meaningful content (not just numbers)
- Provides specific error messages for different validation failures

**Applied to**:
- `Course.name` field
- `CourseRequestDTO.name` field

**Usage**:
```java
@ValidCourseName
private String name;
```

### 4. Valid Lesson Collection Validator (`@ValidLessonCollection`)

**Purpose**: Ensures lesson collections contain at least one valid lesson with meaningful content.

**Features**:
- Validates that the collection is not empty
- Checks that at least one lesson has valid name and YouTube URL
- Ensures lesson names are at least 5 characters
- Ensures YouTube URLs are at least 10 characters
- Provides meaningful error messages

**Applied to**:
- `CourseRequestDTO.lessons` field

**Usage**:
```java
@ValidLessonCollection
private List<LessonDTO> lessons;
```

## Improved Error Handling

### Enhanced Exception Handler

The `ApplicationControllerAdvice` has been updated to provide better error responses:

```java
@ExceptionHandler(ConstraintViolationException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
    // Returns structured error response with field-specific messages
}
```

**Response Format**:
```json
{
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Course name contains inappropriate content or formatting"
    },
    {
      "field": "lessons[0].youtubeUrl",
      "message": "Invalid YouTube URL format. Must be a valid YouTube video ID (11 characters)"
    }
  ]
}
```

## Service Layer Improvements

### Simplified Course Service

Removed duplicate validation logic from `CourseService.create()` method since validation is now handled by annotations:

**Before**:
```java
public CourseDTO create(@Valid CourseRequestDTO courseRequestDTO) {
    courseRepository.findByName(courseRequestDTO.name()).stream()
            .filter(c -> c.getStatus().equals(Status.ACTIVE))
            .findAny().ifPresent(c -> {
                throw new BusinessException("A course with name " + courseRequestDTO.name() + " already exists.");
            });
    // ... rest of method
}
```

**After**:
```java
public CourseDTO create(@Valid CourseRequestDTO courseRequestDTO) {
    Course course = courseMapper.toModel(courseRequestDTO);
    course.setStatus(Status.ACTIVE);
    return courseMapper.toDTO(courseRepository.save(course));
}
```

## Testing

### YouTube URL Validator Test

Created comprehensive test for the YouTube URL validator:
- Tests valid YouTube video IDs
- Tests URL extraction from various YouTube URL formats
- Tests rejection of invalid formats
- Tests null value handling

## Benefits of Custom Validators

1. **Data Integrity**: Ensures all data meets business requirements before persistence
2. **Centralized Validation**: Validation logic is in one place and reusable
3. **Better User Experience**: Provides specific, actionable error messages
4. **Separation of Concerns**: Business logic validation is separate from service logic
5. **Consistency**: Same validation rules apply across all layers (Entity, DTO)
6. **Testability**: Validators can be tested independently

## Usage Examples

### Valid Data Examples:
```java
// Valid course request
CourseRequestDTO validCourse = new CourseRequestDTO(
    "Spring Boot Fundamentals",           // Professional name
    "Back-end",                          // Valid category
    List.of(new LessonDTO(              // Valid lessons
        0,
        "Introduction to Spring Boot",
        "dQw4w9WgXcQ"                   // Valid YouTube ID
    ))
);
```

### Invalid Data Examples:
```java
// Invalid course request
CourseRequestDTO invalidCourse = new CourseRequestDTO(
    "test",                             // ❌ Contains inappropriate word
    "Invalid",                          // ❌ Invalid category
    List.of(new LessonDTO(             // ❌ Invalid lesson
        0,
        "abc",                          // ❌ Too short
        "invalid"                       // ❌ Invalid YouTube URL
    ))
);
```

This implementation provides robust data validation while maintaining clean, maintainable code structure.
