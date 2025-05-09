package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllItems() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "Item1", "Description1", "NEW", "item1@example.com"));
        items.add(new Item(2L, "Item2", "Description2", "NEW", "item2@example.com"));

        when(itemService.findAll()).thenReturn(items);

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item1"))
                .andExpect(jsonPath("$[1].name").value("Item2"));
    }

    @Test
    public void testGetItemById() throws Exception {
        Item item = new Item(1L, "Item1", "Description1", "NEW", "item1@example.com");

        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item1"));
    }

    @Test
    public void testGetItemByIdNotFound() throws Exception {
        when(itemService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateItem() throws Exception {
        Item item = new Item(null, "New Item", "New Description", "NEW", "new@example.com");
        Item savedItem = new Item(1L, "New Item", "New Description", "NEW", "new@example.com");

        when(itemService.save(any(Item.class))).thenReturn(savedItem);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    public void testCreateItemInvalidEmail() throws Exception {
        Item item = new Item(null, "New Item", "New Description", "NEW", "invalid-email");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteItem() throws Exception {
        when(itemService.findById(1L)).thenReturn(Optional.of(new Item(1L, "Test Item", "Test Description", "NEW", "test@example.com")));
        doNothing().when(itemService).deleteById(1L);

        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteItemNotFound() throws Exception {
        when(itemService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItem() throws Exception {
        Item existingItem = new Item(1L, "Old Item", "Old Description", "NEW", "old@example.com");
        Item updatedItem = new Item(1L, "Updated Item", "Updated Description", "PROCESSED", "updated@example.com");

        when(itemService.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemService.save(any(Item.class))).thenReturn(updatedItem);

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    public void testUpdateItemNotFound() throws Exception {
        Item updatedItem = new Item(999L, "Nonexistent Item", "Description", "PROCESSED", "updated@example.com");

        when(itemService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isNotFound());
    }

}
