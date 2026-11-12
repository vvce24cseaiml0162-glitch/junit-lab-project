package com.lab.tests;

import com.lab.application.MainApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Main Application Entrypoint Test")
@Tag("integration")
public class MainApplicationTest {

    @Test
    @DisplayName("Should execute main entrypoint demonstration without throwing any exception")
    void testMainMethodExecution() {
        assertDoesNotThrow(() -> MainApplication.main(new String[]{}));
    }
}
