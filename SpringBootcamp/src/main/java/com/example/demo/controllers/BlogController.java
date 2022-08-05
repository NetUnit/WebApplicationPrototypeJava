package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;

/**
 * Returns the array with all blog posts for our db
 * <p>
 * blogMain - always renders HTML page with an array of * articles items
 * @param posts - ...
 */
@Controller
public class BlogController {
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/blog")
    public String blogMain(Model model) throws IllegalAccessException {
         // Iterable is an object reflected from our db
         Iterable<Post> posts = postRepository.findAll();
         // System.out.println(posts);
         // add posts object in order to be rendered in a template
         model.addAttribute("posts", posts);
         // System.out.println(posts.getClass().getSimpleName());

         // instantiate new object instance
         Post object = new Post();

         // Load all fields in the class (private included)
         Field[] attributes =  object.getClass().getDeclaredFields();

         for (Field field : attributes) {
             // Dynamically read Attribute Name
             System.out.println("ATTRIBUTE NAME: " + field.getName());

             // gets object field-value
             field.setAccessible(true);
             Object objectValue = field.get(object);
             System.out.println("ATTRIBUTE Value: " + objectValue);
         }

          // single object
          // Object obj = postRepository.findById(1);
          // Field[] obj_fields = obj.getClass().getDeclaredFields();

          // for (Field ff: obj_fields) {
          //     ff.setAccessible(true);
          //    System.out.println("ATTRIBUTE Value: " + ff.get(obj).toString());
          // }
         return "blog-main";
    }
    @GetMapping("/blog/add")
    public String blogAdd(Model model) {
        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String blogAddForm(
            @RequestParam String title,
            @RequestParam String anons,
            @RequestParam String full_text,
            Model model)
    {
        System.out.println(String.format(
                "These are form @params: title %s | anons %s | full_text %s", title, anons, full_text)
        );
        Post form = new Post(title, anons, full_text);

        System.out.println(form);
        postRepository.save(form);
        return "redirect:/blog";
    }
}
