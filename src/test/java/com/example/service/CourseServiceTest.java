package com.example.service;

import com.example.model.Course;
import com.example.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course activeCourse()   { return new Course(1L, "Math",    3, true);  }
    private Course inactiveCourse() { return new Course(2L, "History", 2, false); }

    // -----------------------------------------------------------------------
    // findActiveCourses
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("findActiveCourses()")
    class FindActiveCoursesTests {

        @Test
        @DisplayName("Returns only active courses from repository")
        void returnsOnlyActiveCourses() {
            when(courseRepository.findAll())
                    .thenReturn(List.of(activeCourse(), inactiveCourse()));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).isActive()).isTrue();
            assertThat(result.get(0).getName()).isEqualTo("Math");
        }

        @Test
        @DisplayName("Returns empty list when no active courses exist")
        void returnsEmptyListWhenNoActiveCourses() {
            when(courseRepository.findAll()).thenReturn(List.of(inactiveCourse()));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Returns empty list when repository is empty")
        void returnsEmptyListWhenRepositoryEmpty() {
            when(courseRepository.findAll()).thenReturn(List.of());

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Returns all courses when all are active")
        void returnsAllCoursesWhenAllActive() {
            when(courseRepository.findAll())
                    .thenReturn(List.of(
                            new Course(1L, "Math", 3, true),
                            new Course(2L, "Physics", 4, true)
                    ));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).hasSize(2);
        }
    }

    // -----------------------------------------------------------------------
    // findById
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("findById(Long id)")
    class FindByIdTests {

        @Test
        @DisplayName("Returns course when it exists")
        void returnsCourseWhenFound() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse()));

            Course result = courseService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Math");
        }

        @Test
        @DisplayName("Throws RuntimeException when course not found")
        void throwsRuntimeExceptionWhenNotFound() {
            when(courseRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.findById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");
        }
    }

    // -----------------------------------------------------------------------
    // createCourse
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("createCourse(String name, int credits)")
    class CreateCourseTests {

        @Test
        @DisplayName("Successfully creates a course with valid input")
        void createsCourseSucessfully() {
            when(courseRepository.save(any(Course.class)))
                    .thenReturn(new Course(1L, "Math", 3, true));

            Course result = courseService.createCourse("Math", 3);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Math");
            assertThat(result.getCredits()).isEqualTo(3);
            assertThat(result.isActive()).isTrue();
            verify(courseRepository).save(any(Course.class));
        }

        @Test
        @DisplayName("Saved course always has active=true")
        void newCourseIsAlwaysActive() {
            when(courseRepository.save(any(Course.class)))
                    .thenAnswer(inv -> {
                        Course arg = inv.getArgument(0);
                        return new Course(10L, arg.getName(), arg.getCredits(), arg.isActive());
                    });

            Course result = courseService.createCourse("Java", 5);

            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when name is null")
        void throwsWhenNameIsNull() {
            assertThatThrownBy(() -> courseService.createCourse(null, 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course name is required");

            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when name is blank")
        void throwsWhenNameIsBlank() {
            assertThatThrownBy(() -> courseService.createCourse("   ", 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course name is required");

            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when name is empty string")
        void throwsWhenNameIsEmpty() {
            assertThatThrownBy(() -> courseService.createCourse("", 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course name is required");

            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when credits are zero")
        void throwsWhenCreditsAreZero() {
            assertThatThrownBy(() -> courseService.createCourse("Math", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Credits must be positive");

            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when credits are negative")
        void throwsWhenCreditsAreNegative() {
            assertThatThrownBy(() -> courseService.createCourse("Math", -5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Credits must be positive");

            verifyNoInteractions(courseRepository);
        }

        @Test
        @DisplayName("Repository save is called exactly once on success")
        void repositorySaveCalledOnce() {
            when(courseRepository.save(any(Course.class)))
                    .thenReturn(new Course(1L, "Physics", 4, true));

            courseService.createCourse("Physics", 4);

            verify(courseRepository, times(1)).save(any(Course.class));
        }
    }

    // -----------------------------------------------------------------------
    // deleteCourse
    // -----------------------------------------------------------------------
    @Nested
    @DisplayName("deleteCourse(Long id)")
    class DeleteCourseTests {

        @Test
        @DisplayName("Successfully deletes an existing course")
        void deletesExistingCourse() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse()));

            courseService.deleteCourse(1L);

            verify(courseRepository).deleteById(1L);
        }

        @Test
        @DisplayName("deleteById is called with the course's own ID")
        void deleteByIdCalledWithCorrectId() {
            Course course = new Course(42L, "Art", 1, true);
            when(courseRepository.findById(42L)).thenReturn(Optional.of(course));

            courseService.deleteCourse(42L);

            verify(courseRepository).deleteById(42L);
        }

        @Test
        @DisplayName("Throws RuntimeException when course to delete does not exist")
        void throwsWhenCourseNotFound() {
            when(courseRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.deleteCourse(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");

            verify(courseRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deleteById is never called when course is not found")
        void deleteByIdNeverCalledOnMissingCourse() {
            when(courseRepository.findById(5L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.deleteCourse(5L))
                    .isInstanceOf(RuntimeException.class);

            verify(courseRepository, never()).deleteById(any());
        }
    }
}