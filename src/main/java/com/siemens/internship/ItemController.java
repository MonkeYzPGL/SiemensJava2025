package com.siemens.internship;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            //Return 400 (BAD Request) if validation fails
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //Return 201 otherwise
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        //I modified from NO_Content to NOT_Found if the id doesn't exist in our db
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            //I changed from the CREATED status which is used for "CREATE" to OK which is suitable for UPDATE
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        } else {
            //If the user is not found we need to return 404(NOT_FOUND)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        //Check if the item exists before we try to delete it
        if (itemService.findById(id).isPresent()) {
            itemService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  //204(successfully deleted)
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  //404(item not found)
        }
    }

    @GetMapping("/process")
    public ResponseEntity<CompletableFuture<List<Item>>> processItems() {
        return new ResponseEntity<>(itemService.processItemsAsync(), HttpStatus.OK);
    }
}
