package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTests {

    private Item item;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        item = new Item();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //Email tests
    @Test
    public void testValidEmail() {
        item.setEmail("valid.email@example.com");
        assertTrue(validate(item), "The email should be valid.");
    }

    @Test
    public void testInvalidEmail() {
        item.setEmail("invalid-email");
        assertFalse(validate(item), "The email should be invalid.");
    }

    @Test
    public void testBlankEmail() {
        item.setEmail("");
        assertFalse(validate(item), "The email should not be blank.");
    }

    @Test
    public void testNullEmail() {
        item.setEmail(null);
        assertFalse(validate(item), "The email should not be null.");
    }

    //Additional tests for item
    @Test
    public void testCreateItemWithValidEmail() {
        Item newItem = new Item();
        newItem.setEmail("another.email@example.com");
        assertTrue(validate(newItem), "A new item with a valid email should be valid.");
    }

    @Test
    public void testSetEmailAfterCreation() {
        item.setEmail("initial@example.com");
        assertTrue(validate(item), "The initial email should be valid.");
        item.setEmail("updated@example.com");
        assertTrue(validate(item), "The updated email should also be valid.");
    }

    @Test
    public void testItemsWithSameEmailAreDifferentObjects() {
        Item item1 = new Item();
        item1.setEmail("same@example.com");

        Item item2 = new Item();
        item2.setEmail("same@example.com");

        assertNotEquals(item1, item2, "Items with the same email should be different objects.");
    }

    private boolean validate(Item item) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        return violations.isEmpty();
    }
}