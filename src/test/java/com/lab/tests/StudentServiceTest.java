package com.lab.tests;

import com.lab.exceptions.DuplicateStudentException;
import com.lab.exceptions.InvalidStudentDataException;
import com.lab.exceptions.StudentNotFoundException;
import com.lab.models.Student;
import com.lab.services.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Service & USN Management Test Suite")
@Tag("unit")
public class StudentServiceTest {

    private StudentService studentService;

    @BeforeAll
    static void initTestSuite() {
        System.out.println(">>> Starting StudentServiceTest execution...");
    }

    @AfterAll
    static void tearDownTestSuite() {
        System.out.println("<<< Completed StudentServiceTest execution.");
    }

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
    }

    @AfterEach
    void tearDown() {
        studentService.clearAll();
    }

    @Nested
    @DisplayName("Student Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register a new student with auto-generated USN")
        void testSuccessfulRegistration() {
            Student student = studentService.registerStudent(
                    "Pruthvik V", "pruthvik@vvce.ac.in", "9876543210", "CI"
            );

            assertNotNull(student, "Registered student should not be null");
            assertEquals("4VV24CI001", student.getUsn(), "First student USN should be 4VV24CI001");
            assertEquals("Pruthvik V", student.getName());
            assertEquals("pruthvik@vvce.ac.in", student.getEmail());
            assertEquals("9876543210", student.getPhone());
            assertEquals("CI", student.getDepartment());
        }

        @Test
        @DisplayName("Should generate sequential, unique USNs for multiple registrations in same department")
        void testSequentialUsnGeneration() {
            Student s1 = studentService.registerStudent("Student One", "s1@vvce.ac.in", "9876543211", "CI");
            Student s2 = studentService.registerStudent("Student Two", "s2@vvce.ac.in", "9876543212", "CI");

            assertNotEquals(s1.getUsn(), s2.getUsn(), "USNs must be unique");
            assertEquals("4VV24CI001", s1.getUsn());
            assertEquals("4VV24CI002", s2.getUsn());
        }

        @Test
        @DisplayName("Should throw DuplicateStudentException when registering existing email")
        void testDuplicateEmailRegistration() {
            studentService.registerStudent("Student Alpha", "alpha@vvce.ac.in", "9876543213", "CI");

            DuplicateStudentException ex = assertThrows(DuplicateStudentException.class, () ->
                    studentService.registerStudent("Duplicate Person", "alpha@vvce.ac.in", "9876543214", "CI")
            );

            assertTrue(ex.getMessage().contains("already exists"), "Exception message should indicate duplication");
        }

        @ParameterizedTest(name = "Invalid email: {0} should throw InvalidStudentDataException")
        @ValueSource(strings = {"invalid-email", "missingat.com", "@domain.com", "plainaddress"})
        @DisplayName("Should reject invalid email formats during student registration")
        void testInvalidEmailRegistration(String invalidEmail) {
            assertThrows(InvalidStudentDataException.class, () ->
                    studentService.registerStudent("Invalid Email Stu", invalidEmail, "9876543215", "CI")
            );
        }

        @ParameterizedTest(name = "Invalid phone: {0} should throw InvalidStudentDataException")
        @ValueSource(strings = {"12345", "abcdefghij", "5987654321", "98765432100"})
        @DisplayName("Should reject invalid phone numbers during student registration")
        void testInvalidPhoneRegistration(String invalidPhone) {
            assertThrows(InvalidStudentDataException.class, () ->
                    studentService.registerStudent("Invalid Phone Stu", "phone@vvce.ac.in", invalidPhone, "CI")
            );
        }
    }

    @Nested
    @DisplayName("Student Operations & CRUD Tests")
    class OperationsTests {

        @Test
        @DisplayName("Should retrieve an existing student by USN")
        void testGetStudentSuccess() {
            Student registered = studentService.registerStudent("Lookup Student", "lookup@vvce.ac.in", "9876543216", "CS");
            Student found = studentService.getStudent(registered.getUsn());

            assertNotNull(found);
            assertEquals(registered.getUsn(), found.getUsn());
            assertEquals("Lookup Student", found.getName());
        }

        @Test
        @DisplayName("Should throw StudentNotFoundException when searching non-existent USN")
        void testGetNonExistentStudent() {
            assertThrows(StudentNotFoundException.class, () ->
                    studentService.getStudent("4VV24CI999")
            );
        }

        @Test
        @DisplayName("Should update student details successfully")
        void testUpdateStudentSuccess() {
            Student student = studentService.registerStudent("Original Name", "orig@vvce.ac.in", "9876543217", "CI");
            Student updated = studentService.updateStudent(student.getUsn(), "Updated Name", "9876543218");

            assertEquals("Updated Name", updated.getName());
            assertEquals("9876543218", updated.getPhone());
        }

        @Test
        @DisplayName("Should delete student and ensure subsequent search fails")
        void testDeleteStudent() {
            Student student = studentService.registerStudent("To Delete", "delete@vvce.ac.in", "9876543219", "CI");
            boolean deleted = studentService.deleteStudent(student.getUsn());

            assertTrue(deleted, "Delete operation should return true");
            assertThrows(StudentNotFoundException.class, () -> studentService.getStudent(student.getUsn()));
        }
    }

    @Nested
    @DisplayName("Academic Evaluation & Grading Tests")
    class AcademicTests {

        @Test
        @DisplayName("Should record marks and compute average score accurately")
        void testRecordMarksAndAverage() {
            Student student = studentService.registerStudent("Academic Stu", "acad@vvce.ac.in", "9876543220", "CI");
            studentService.recordMarks(student.getUsn(), "Maths", 85.0);
            studentService.recordMarks(student.getUsn(), "Data Structures", 95.0);

            assertAll("Academic Metrics Verification",
                    () -> assertEquals(2, student.getMarks().size()),
                    () -> assertEquals(90.0, student.calculateAverageMarks(), 0.01),
                    () -> assertEquals(9.47, studentService.calculateCgpa(student.getUsn()), 0.01)
            );
        }

        @ParameterizedTest(name = "Percentage {0} should yield Grade {1}")
        @CsvSource({
                "95.0, S",
                "90.0, S",
                "85.0, A",
                "80.0, A",
                "75.0, B",
                "70.0, B",
                "65.0, C",
                "60.0, C",
                "55.0, D",
                "50.0, D",
                "45.0, E",
                "40.0, E",
                "35.0, F",
                "0.0,  F"
        })
        @DisplayName("Should correctly map percentage marks to VTU university letter grades")
        void testGradeMapping(double percentage, char expectedGrade) {
            char actualGrade = studentService.assignGrade(percentage);
            assertEquals(expectedGrade, actualGrade);
        }

        @Test
        @DisplayName("Should reject invalid marks boundary (<0 or >100)")
        void testInvalidMarksBoundary() {
            Student student = studentService.registerStudent("Boundary Stu", "bound@vvce.ac.in", "9876543221", "CI");

            assertThrows(IllegalArgumentException.class, () ->
                    studentService.recordMarks(student.getUsn(), "Maths", -5.0)
            );
            assertThrows(IllegalArgumentException.class, () ->
                    studentService.recordMarks(student.getUsn(), "Maths", 105.0)
            );
        }
    }
}
