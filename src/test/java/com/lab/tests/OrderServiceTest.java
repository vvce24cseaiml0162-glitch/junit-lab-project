package com.lab.tests;

import com.lab.exceptions.InvalidDiscountCodeException;
import com.lab.exceptions.OrderProcessingException;
import com.lab.models.Order;
import com.lab.models.OrderItem;
import com.lab.services.OrderService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Processing & Billing Test Suite")
@Tag("unit")
public class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @AfterEach
    void tearDown() {
        orderService.clearAll();
    }

    @Test
    @DisplayName("Should create order and accumulate item subtotals accurately")
    void testCreateOrderAndAddItems() {
        Order order = orderService.createOrder("ORD-101", "buyer@vvce.ac.in");
        orderService.addItemToOrder("ORD-101", new OrderItem("I1", "Lab Notebook", 50.0, 3));
        orderService.addItemToOrder("ORD-101", new OrderItem("I2", "Scientific Pen", 20.0, 2));

        assertEquals(2, order.getItems().size());
        assertEquals(190.0, order.calculateSubtotal(), 0.01);
    }

    @ParameterizedTest(name = "Coupon {0} on subtotal {1} should give discount {2}")
    @CsvSource({
            "STUDENT10, 1000.0, 100.0",
            "STUDENT10, 500.0,  50.0",
            "FLAT50,    200.0,  50.0",
            "FLAT50,    100.0,  50.0",
            "FESTIVE25, 1000.0, 250.0",
            "FESTIVE25, 600.0,  150.0"
    })
    @DisplayName("Should apply valid promo coupons and calculate accurate discount amounts")
    void testValidPromoCoupons(String couponCode, double itemPrice, double expectedDiscount) {
        String orderId = "ORD-" + couponCode;
        orderService.createOrder(orderId, "promo@vvce.ac.in");
        orderService.addItemToOrder(orderId, new OrderItem("ITM", "Study Material", itemPrice, 1));

        double actualDiscount = orderService.applyDiscount(orderId, couponCode);
        assertEquals(expectedDiscount, actualDiscount, 0.01);
    }

    @Test
    @DisplayName("Should reject invalid or unrecognized discount codes")
    void testInvalidDiscountCode() {
        orderService.createOrder("ORD-BAD", "user@vvce.ac.in");
        orderService.addItemToOrder("ORD-BAD", new OrderItem("ITM", "Book", 500.0, 1));

        assertThrows(InvalidDiscountCodeException.class, () ->
                orderService.applyDiscount("ORD-BAD", "BOGUS99")
        );
    }

    @Test
    @DisplayName("Should enforce minimum order value for threshold discounts")
    void testCouponMinimumThresholdViolation() {
        orderService.createOrder("ORD-LOW", "user@vvce.ac.in");
        orderService.addItemToOrder("ORD-LOW", new OrderItem("PEN", "Pen", 40.0, 1));

        // FLAT50 requires min 100
        assertThrows(InvalidDiscountCodeException.class, () ->
                orderService.applyDiscount("ORD-LOW", "FLAT50")
        );
    }

    @Test
    @DisplayName("Should correctly calculate grand total including tax percentage")
    void testFinalTotalCalculationWithTax() {
        orderService.createOrder("ORD-TAX", "user@vvce.ac.in");
        orderService.addItemToOrder("ORD-TAX", new OrderItem("LAPTOP", "Laptop", 1000.0, 1));
        orderService.applyDiscount("ORD-TAX", "STUDENT10"); // Discount = 100, Net = 900

        // 18% GST on 900 = 162 => Total = 1062.0
        double finalTotal = orderService.calculateFinalTotal("ORD-TAX", 18.0);
        assertEquals(1062.0, finalTotal, 0.01);
    }

    @Test
    @DisplayName("Should successfully complete checkout and lock order against additions")
    void testSuccessfulCheckout() {
        orderService.createOrder("ORD-FIN", "fin@vvce.ac.in");
        orderService.addItemToOrder("ORD-FIN", new OrderItem("ITEM", "Item", 100.0, 1));

        boolean checkedOut = orderService.checkout("ORD-FIN");
        assertTrue(checkedOut);

        Order order = orderService.getOrder("ORD-FIN");
        assertEquals(Order.Status.COMPLETED, order.getStatus());

        // Subsequent additions must fail
        assertThrows(OrderProcessingException.class, () ->
                orderService.addItemToOrder("ORD-FIN", new OrderItem("EXTRA", "Extra", 50.0, 1))
        );
    }
}
