package com.lab.application;

import com.lab.models.BankAccount;
import com.lab.models.OrderItem;
import com.lab.models.Student;
import com.lab.services.BankAccountService;
import com.lab.services.OrderService;
import com.lab.services.StudentService;
import com.lab.utilities.MathUtils;
import com.lab.utilities.ValidationUtils;

public class MainApplication {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("     END-TO-END JUNIT LAB PROJECT DEMONSTRATION & EXECUTION    ");
        System.out.println("===============================================================");

        // 1. Student Service Demonstration
        System.out.println("\n[1] Demonstrating Student Management & USN Generation:");
        StudentService studentService = new StudentService();
        Student s1 = studentService.registerStudent("Pruthvik V", "pruthvik@vvce.ac.in", "9876543210", "CI");
        studentService.recordMarks(s1.getUsn(), "Software Testing", 94.0);
        studentService.recordMarks(s1.getUsn(), "DevOps & CI/CD", 91.5);
        studentService.recordMarks(s1.getUsn(), "Automated QA", 89.0);

        System.out.println("   Registered Student: " + s1.getName());
        System.out.println("   Auto-generated USN: " + s1.getUsn());
        System.out.println("   Average Score: " + s1.calculateAverageMarks() + "%");
        System.out.println("   Assigned Grade: " + studentService.assignGrade(s1.calculateAverageMarks()));
        System.out.println("   Calculated CGPA: " + studentService.calculateCgpa(s1.getUsn()));

        // 2. Banking Service Demonstration
        System.out.println("\n[2] Demonstrating Banking Transaction Service:");
        BankAccountService bankService = new BankAccountService();
        BankAccount acc1 = bankService.createAccount("Pruthvik V", 10000.0);
        BankAccount acc2 = bankService.createAccount("Lab Operations", 5000.0);
        System.out.println("   Created Account 1: " + acc1.getAccountNumber() + " | Balance: INR " + acc1.getBalance());
        System.out.println("   Created Account 2: " + acc2.getAccountNumber() + " | Balance: INR " + acc2.getBalance());

        bankService.deposit(acc1.getAccountNumber(), 2500.0);
        bankService.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 1500.0);
        System.out.println("   Post Transfer Balance [Acc1]: INR " + acc1.getBalance());
        System.out.println("   Post Transfer Balance [Acc2]: INR " + acc2.getBalance());

        // 3. Order & Billing Service Demonstration
        System.out.println("\n[3] Demonstrating Order Processing & Billing Service:");
        OrderService orderService = new OrderService();
        orderService.createOrder("ORD-2026-001", "student@vvce.ac.in");
        orderService.addItemToOrder("ORD-2026-001", new OrderItem("ITEM-1", "Lab Manual & Guide", 250.0, 2));
        orderService.addItemToOrder("ORD-2026-001", new OrderItem("ITEM-2", "Scientific Calculator", 850.0, 1));
        double discount = orderService.applyDiscount("ORD-2026-001", "STUDENT10");
        double total = orderService.calculateFinalTotal("ORD-2026-001", 18.0);
        orderService.checkout("ORD-2026-001");
        System.out.println("   Order ORD-2026-001 Discount Applied: INR " + discount);
        System.out.println("   Order Final Total with 18% GST: INR " + total);

        // 4. Utilities Demonstration
        System.out.println("\n[4] Demonstrating Validation & Math Utilities:");
        System.out.println("   Validating USN '4VV24CI001': " + ValidationUtils.isValidUSN("4VV24CI001"));
        System.out.println("   Validating Email 'test@vvce.ac.in': " + ValidationUtils.isValidEmail("test@vvce.ac.in"));
        System.out.println("   Is 97 prime? " + MathUtils.isPrime(97));
        System.out.println("   Factorial of 5: " + MathUtils.factorial(5));

        System.out.println("\n===============================================================");
        System.out.println("        APPLICATION INITIALIZED & READY FOR JUNIT SUITE        ");
        System.out.println("===============================================================");
    }
}
