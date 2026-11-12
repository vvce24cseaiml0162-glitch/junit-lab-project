package com.lab.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Student {
    private final String usn;
    private String name;
    private String email;
    private String phone;
    private String department;
    private final Map<String, Double> marks;

    public Student(String usn, String name, String email, String phone, String department) {
        this.usn = usn;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.marks = new HashMap<>();
    }

    public String getUsn() {
        return usn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void addMarks(String subject, double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        marks.put(subject, score);
    }

    public Map<String, Double> getMarks() {
        return Collections.unmodifiableMap(marks);
    }

    public double calculateAverageMarks() {
        if (marks.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double score : marks.values()) {
            sum += score;
        }
        return sum / marks.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(usn, student.usn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usn);
    }

    @Override
    public String toString() {
        return "Student{" +
                "usn='" + usn + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", department='" + department + '\'' +
                ", averageMarks=" + calculateAverageMarks() +
                '}';
    }
}
