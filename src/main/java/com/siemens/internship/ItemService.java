package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Queue<Item> processedItems = new ConcurrentLinkedQueue<>();

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        if (item == null) {
            return null;
        }
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     * <p>
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        //clear previous processed items to avoid duplicates
        processedItems.clear();

        List<Long> itemIds = itemRepository.findAllIds();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Long id : itemIds) {
            //Create a CompletableFuture for each item processing task
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    //Simulate processing delay
                    Thread.sleep(100);

                    //Retrieve item from the repo
                    Item item = itemRepository.findById(id).orElse(null);
                    if (item == null) {
                        return;
                    }

                    //Update item status and save
                    item.setStatus("PROCESSED");
                    itemRepository.save(item);

                    //Add to preprocessed items
                    processedItems.add(item);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); //Restore interupted status
                    throw new RuntimeException("Item processing interrupted", e);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing item with id:" + id, e);
                }
            }, executor);
            futures.add(future);
        }
        // We wait for all tasks to be completed before returning the processed items
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> List.copyOf(processedItems));
    }

}

