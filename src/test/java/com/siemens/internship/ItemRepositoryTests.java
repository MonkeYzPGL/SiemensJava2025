package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    public void testSaveItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setStatus("NEW");
        item.setEmail("test@example.com");

        Item savedItem = itemRepository.save(item);

        assertNotNull(savedItem.getId(), "The saved item should have an ID");
        assertEquals("Test Item", savedItem.getName(), "The saved item should have the correct name");
    }

    @Test
    public void testFindItemById() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setStatus("NEW");
        item.setEmail("test@example.com");

        Item savedItem = itemRepository.save(item);
        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());

        assertTrue(foundItem.isPresent(), "Item should be found by ID");
        assertEquals(savedItem.getName(), foundItem.get().getName(), "The found item should have the correct name");
    }

    @Test
    public void testFindAllItems() {
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setStatus("NEW");
        item1.setEmail("item1@example.com");

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setStatus("NEW");
        item2.setEmail("item2@example.com");

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAll();

        assertEquals(2, items.size(), "Should return all saved items");
    }

    @Test
    public void testDeleteItemById() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setStatus("NEW");
        item.setEmail("test@example.com");

        Item savedItem = itemRepository.save(item);
        itemRepository.deleteById(savedItem.getId());

        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());
        assertFalse(foundItem.isPresent(), "Item should be deleted");
    }

    @Test
    public void testFindAllIds() {
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setStatus("NEW");
        item1.setEmail("item1@example.com");

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setStatus("NEW");
        item2.setEmail("item2@example.com");

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Long> ids = itemRepository.findAllIds();

        assertEquals(2, ids.size(), "Should return all item IDs");
        assertTrue(ids.contains(item1.getId()), "Should contain the ID of the first item");
        assertTrue(ids.contains(item2.getId()), "Should contain the ID of the second item");
    }
}
