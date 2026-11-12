package com.lab.services;

import com.lab.exceptions.InvalidDiscountCodeException;
import com.lab.exceptions.OrderProcessingException;
import com.lab.models.Order;
import com.lab.models.OrderItem;
import com.lab.utilities.MathUtils;
import com.lab.utilities.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class OrderService {

    private final Map<String, Order> orders = new HashMap<>();

    public Order createOrder(String orderId, String customerEmail) {
        if (!ValidationUtils.isValidEmail(customerEmail)) {
            throw new OrderProcessingException("Invalid customer email: " + customerEmail);
        }
        if (orders.containsKey(orderId)) {
            throw new OrderProcessingException("Order with ID " + orderId + " already exists");
        }
        Order order = new Order(orderId, customerEmail);
        orders.put(orderId, order);
        return order;
    }

    public Order getOrder(String orderId) {
        if (orderId == null || !orders.containsKey(orderId)) {
            throw new OrderProcessingException("Order not found: " + orderId);
        }
        return orders.get(orderId);
    }

    public void addItemToOrder(String orderId, OrderItem item) {
        Order order = getOrder(orderId);
        if (order.getStatus() == Order.Status.COMPLETED) {
            throw new OrderProcessingException("Cannot add items to completed order");
        }
        order.addItem(item);
    }

    public double applyDiscount(String orderId, String discountCode) {
        Order order = getOrder(orderId);
        double subtotal = order.calculateSubtotal();

        if (subtotal <= 0) {
            throw new OrderProcessingException("Cannot apply discount code to empty order");
        }
        if (discountCode == null || discountCode.trim().isEmpty()) {
            throw new InvalidDiscountCodeException("Discount code cannot be empty");
        }

        String code = discountCode.trim().toUpperCase();
        double discount = 0.0;

        switch (code) {
            case "STUDENT10":
                discount = subtotal * 0.10;
                break;
            case "FLAT50":
                if (subtotal < 100.0) {
                    throw new InvalidDiscountCodeException("FLAT50 requires a minimum order subtotal of 100");
                }
                discount = 50.0;
                break;
            case "FESTIVE25":
                if (subtotal < 500.0) {
                    throw new InvalidDiscountCodeException("FESTIVE25 requires a minimum order subtotal of 500");
                }
                discount = subtotal * 0.25;
                break;
            default:
                throw new InvalidDiscountCodeException("Invalid discount coupon code: " + discountCode);
        }

        order.setDiscountCode(code);
        order.setDiscountAmount(MathUtils.round(discount, 2));
        order.setStatus(Order.Status.DISCOUNT_APPLIED);
        return order.getDiscountAmount();
    }

    public double calculateFinalTotal(String orderId, double taxPercentage) {
        Order order = getOrder(orderId);
        double subtotal = order.calculateSubtotal();
        if (subtotal <= 0) {
            throw new OrderProcessingException("Cannot calculate total for empty order");
        }
        if (taxPercentage < 0 || taxPercentage > 50) {
            throw new IllegalArgumentException("Tax percentage must be between 0 and 50%");
        }

        double discountedSubtotal = subtotal - order.getDiscountAmount();
        double taxAmount = discountedSubtotal * (taxPercentage / 100.0);
        return MathUtils.round(discountedSubtotal + taxAmount, 2);
    }

    public boolean checkout(String orderId) {
        Order order = getOrder(orderId);
        if (order.getItems().isEmpty()) {
            throw new OrderProcessingException("Cannot checkout order with zero items");
        }
        order.setStatus(Order.Status.COMPLETED);
        return true;
    }

    public void clearAll() {
        orders.clear();
    }
}
