package com.lab.tests;

import com.lab.utilities.MathUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mathematical Utilities Test Suite")
@Tag("math")
public class MathUtilsTest {

    @ParameterizedTest(name = "Number {0} is prime")
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 97})
    @DisplayName("Should correctly identify prime numbers")
    void testPrimeNumbers(int number) {
        assertTrue(MathUtils.isPrime(number), number + " should be identified as prime");
    }

    @ParameterizedTest(name = "Number {0} is not prime")
    @ValueSource(ints = {-10, -1, 0, 1, 4, 6, 8, 9, 10, 15, 25, 100})
    @DisplayName("Should correctly identify non-prime numbers")
    void testNonPrimeNumbers(int number) {
        assertFalse(MathUtils.isPrime(number), number + " should not be identified as prime");
    }

    @ParameterizedTest(name = "{0}! should equal {1}")
    @CsvSource({
            "0, 1",
            "1, 1",
            "3, 6",
            "5, 120",
            "7, 5040",
            "10, 3628800"
    })
    @DisplayName("Should calculate accurate factorials for integers")
    void testFactorialCalculation(int input, long expectedFactorial) {
        assertEquals(expectedFactorial, MathUtils.factorial(input));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when factorial input is negative")
    void testNegativeFactorialException() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.factorial(-5));
    }

    @Test
    @DisplayName("Should calculate percentage accurately with decimal rounding")
    void testCalculatePercentage() {
        assertEquals(85.50, MathUtils.calculatePercentage(171.0, 200.0), 0.01);
        assertEquals(100.0, MathUtils.calculatePercentage(50.0, 50.0), 0.01);
        assertEquals(0.0, MathUtils.calculatePercentage(0.0, 100.0), 0.01);
    }

    @Test
    @DisplayName("Should reject invalid percentage boundaries")
    void testInvalidPercentageBoundaries() {
        assertThrows(IllegalArgumentException.class, () -> MathUtils.calculatePercentage(110.0, 100.0));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.calculatePercentage(-5.0, 100.0));
        assertThrows(IllegalArgumentException.class, () -> MathUtils.calculatePercentage(50.0, 0.0));
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("Should execute intensive prime calculation within timeout limit")
    void testPrimePerformanceWithinTimeout() {
        for (int i = 1; i <= 5000; i++) {
            MathUtils.isPrime(i);
        }
    }
}
