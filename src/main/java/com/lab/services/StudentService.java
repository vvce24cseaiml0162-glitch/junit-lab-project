package com.lab.services;

import com.lab.exceptions.DuplicateStudentException;
import com.lab.exceptions.InvalidStudentDataException;
import com.lab.exceptions.StudentNotFoundException;
import com.lab.models.Student;
import com.lab.utilities.MathUtils;
import com.lab.utilities.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentService {

    private final Map<String, Student> studentDatabase = new HashMap<>();
    private final Map<String, AtomicInteger> departmentCounters = new HashMap<>();

    public synchronized String generateNextUsn(String department, int year) {
        if (department == null || department.trim().isEmpty()) {
            throw new InvalidStudentDataException("Department code cannot be empty");
        }
        String dept = department.trim().toUpperCase();
        departmentCounters.putIfAbsent(dept, new AtomicInteger(0));
        int count = departmentCounters.get(dept).incrementAndGet();
        return String.format("4VV%02d%s%03d", year % 100, dept, count);
    }

    public synchronized Student registerStudent(String name, String email, String phone, String department) {
        if (name == null || name.trim().length() < 2) {
            throw new InvalidStudentDataException("Student name must be at least 2 characters");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new InvalidStudentDataException("Invalid student email address: " + email);
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            throw new InvalidStudentDataException("Invalid student phone number: " + phone);
        }

        // Check duplicate email
        for (Student s : studentDatabase.values()) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                throw new DuplicateStudentException("A student with email " + email + " already exists");
            }
        }

        String usn = generateNextUsn(department, 24);
        Student student = new Student(usn, name.trim(), email.trim(), phone.trim(), department.trim().toUpperCase());
        studentDatabase.put(usn, student);
        return student;
    }

    public Student getStudent(String usn) {
        if (usn == null || !studentDatabase.containsKey(usn.toUpperCase())) {
            throw new StudentNotFoundException("Student with USN " + usn + " not found");
        }
        return studentDatabase.get(usn.toUpperCase());
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(studentDatabase.values()));
    }

    public Student updateStudent(String usn, String newName, String newPhone) {
        Student student = getStudent(usn);
        if (newName != null && !newName.trim().isEmpty()) {
            if (newName.trim().length() < 2) {
                throw new InvalidStudentDataException("Student name must be at least 2 characters");
            }
            student.setName(newName.trim());
        }
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            if (!ValidationUtils.isValidPhone(newPhone)) {
                throw new InvalidStudentDataException("Invalid phone number format: " + newPhone);
            }
            student.setPhone(newPhone.trim());
        }
        return student;
    }

    public boolean deleteStudent(String usn) {
        Student student = getStudent(usn);
        return studentDatabase.remove(student.getUsn()) != null;
    }

    public void recordMarks(String usn, String subject, double marks) {
        Student student = getStudent(usn);
        student.addMarks(subject, marks);
    }

    public char assignGrade(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        if (percentage >= 90) return 'S';
        if (percentage >= 80) return 'A';
        if (percentage >= 70) return 'B';
        if (percentage >= 60) return 'C';
        if (percentage >= 50) return 'D';
        if (percentage >= 40) return 'E';
        return 'F';
    }

    public double calculateCgpa(String usn) {
        Student student = getStudent(usn);
        double avg = student.calculateAverageMarks();
        return MathUtils.round(avg / 9.5, 2);
    }

    public void clearAll() {
        studentDatabase.clear();
        departmentCounters.clear();
    }
}
