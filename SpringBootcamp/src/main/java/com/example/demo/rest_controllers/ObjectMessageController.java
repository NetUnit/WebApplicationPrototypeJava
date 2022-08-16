package com.example.demo.rest_controllers;


import com.example.demo.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

@RestController
// all requests started with message will be redirected to this controller
@RequestMapping("message")
public class ObjectMessageController {

    private int counter = 4;
    private List<Map<String, String>> messages = new ArrayList<>() {{
        add(new HashMap<String, String>() {{
            put("id", "1");
            put("text", "First message");
        }});
        add(new HashMap<String, String>() {{
            put("id", "2");
            put("text", "Second message");
        }});
        add(new HashMap<String, String>() {{
            put("id", "3");
            put("text", "Third message");
        }});
    }};

    @GetMapping
    public List<Map<String, String>> list() {
        return messages;
    }

    @GetMapping("{id}")
    public Map<String, String> getOne(@PathVariable String id) {
        return messages.stream().filter(message -> message.get("id").equals(id)).findFirst()
                .orElseThrow(NotFoundException::new);
        // put a logic here - serialize object to json


    }

    @PostMapping
    public Map<String, String> create(@RequestBody Map<String, String> message) {
        message.put("id", valueOf(counter++));
        messages.add(message);
        return message;
    }

    @PutMapping("{id}")
    public Map<String, String> update(@RequestBody Map<String, String> message, @PathVariable String id) {
        Map<String, String> messageFromDb = getOne(id);
        messageFromDb.putAll(message);
        messageFromDb.put("id", id);
        return messageFromDb;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Map<String, String> message = getOne(id);
        messages.remove(message);
    }
}