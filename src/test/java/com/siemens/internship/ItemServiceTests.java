package com.siemens.internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testFindAllItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "Item1", "Description1", "NEW", "item1@example.com"));
        items.add(new Item(2L, "Item2", "Description2", "NEW", "item2@example.com"));

        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAll();

        assertEquals(2, result.size(), "Should return all items");
        assertEquals("Item1", result.get(0).getName());
        assertEquals("Item2", result.get(1).getName());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testFindItemById() {
        Item item = new Item(1L, "Item1", "Description1", "NEW", "item1@example.com");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findById(1L);

        assertTrue(result.isPresent(), "Item should be present");
        assertEquals("Item1", result.get().getName());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindItemByIdNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.findById(999L);

        assertFalse(result.isPresent(), "Item should not be present");
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    public void testFindItemByIdWithNull() {
        Optional<Item> result = itemService.findById(null);

        assertFalse(result.isPresent(), "Item should not be present for null ID");
        verify(itemRepository, times(0)).findById(null);
    }

    @Test
    public void testSaveItem() {
        Item item = new Item(1L, "Item1", "Description1", "NEW", "item1@example.com");
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.save(item);

        assertEquals(item, result, "The saved item should be the same as the input item");
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    public void testSaveNullItem() {
        Item result = itemService.save(null);

        assertNull(result, "Saving a null item should return null");
        verify(itemRepository, times(0)).save(null);
    }

    @Test
    public void testDeleteItemById() {
        doNothing().when(itemRepository).deleteById(1L);

        itemService.deleteById(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteItemByIdNotFound() {
        doNothing().when(itemRepository).deleteById(999L);

        itemService.deleteById(999L);

        verify(itemRepository, times(1)).deleteById(999L);
    }

    @Test
    public void testProcessItemsAsyncWithEmptyList() throws ExecutionException, InterruptedException {
        when(itemRepository.findAllIds()).thenReturn(List.of());

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        assertEquals(0, processedItems.size(), "Should process no items if list is empty");
        verify(itemRepository, times(1)).findAllIds();
        verify(itemRepository, times(0)).findById(anyLong());
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    public void testProcessItemsAsyncWithMissingItems() throws ExecutionException, InterruptedException {
        List<Long> itemIds = List.of(1L, 2L, 3L);
        Item item1 = new Item(1L, "Item1", "Description1", "NEW", "item1@example.com");

        when(itemRepository.findAllIds()).thenReturn(itemIds);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        assertEquals(1, processedItems.size(), "Should only process existing items");
        assertEquals("PROCESSED", processedItems.get(0).getStatus());
        verify(itemRepository, times(1)).findAllIds();
        verify(itemRepository, times(3)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }
}