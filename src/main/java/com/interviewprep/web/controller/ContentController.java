package com.interviewprep.web.controller;

import com.interviewprep.web.content.ContentProvider;
import com.interviewprep.web.model.Topic;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API serving interview prep content as JSON.
 * The frontend (HTML/JS) calls these endpoints.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ContentController {

    @GetMapping("/sections")
    public List<String> getSections() {
        return ContentProvider.getSectionNames();
    }

    @GetMapping("/topics/{section}")
    public List<Topic> getTopics(@PathVariable String section) {
        return ContentProvider.getTopicsForSection(section);
    }

    @GetMapping("/all")
    public Map<String, List<Topic>> getAll() {
        return ContentProvider.getSections();
    }
}
