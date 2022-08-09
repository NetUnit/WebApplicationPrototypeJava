package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

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
             // System.out.println("ATTRIBUTE NAME: " + field.getName());

             // gets object field-value
             field.setAccessible(true);
             Object objectValue = field.get(object);
             // System.out.println("ATTRIBUTE Value: " + objectValue);
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
            HttpServletRequest request,
            Model model) throws IOException {
        // System.out.println(String.format(
        //        "These are form @params: title %s | anons %s | full_text %s", title, anons, full_text)
        // );
        // form is an object outlined by our Model
        // that is being saved into the db
        Post form = new Post(title, anons, full_text);
        postRepository.save(form);

        // Check POST results of Create blog object
        String method = String.format("request method: %s", request.getMethod());
        String url = String.format("%s", request.getRequestURL());
        int status = getHttpRequestStatus(url);
        // basic POST result output
        System.out.println(String.format("%s | request url: %s | request status: %d", method, url, status));

        // Check full POST request result
        String request_data = httpServletRequestToString(request);
        System.out.println(request_data);

        return "redirect:/blog";
    }

    // in order to make this url dynamic with dynamic url_pattern
    // @PathVariable decorator is needed which takes dynamic id parameter
    @GetMapping("/blog/{id}")
    public String blogDetailed(
            Model model,
            @PathVariable(value = "id") Integer id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // existsById - searches if such id presented in the db
        if(!postRepository.existsById(id)) {
            // this will raise 404 status
            raise404(request, response);
            //return "redirect:/blog";
        }

        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> post_attrs = new ArrayList<>();
        post.ifPresent(post_attrs::add);
        System.out.println(post_attrs);
        model.addAttribute("post_attrs", post_attrs);
        return "blog-detail";
    }

    // update functionality
    // @GetMapping decorator aims to use POST/GET methods along with each other
    @GetMapping("/blog/{id}/update")
    public String blogUpdateForm(
            Model model,
            @PathVariable(value = "id") Integer id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if(!postRepository.existsById(id)) {
            // this will raise 404 status
            raise404(request, response);
        }

        // get post obj from db
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> post_attrs = new ArrayList<>();
        post.ifPresent(post_attrs::add);
        model.addAttribute("post_attrs", post_attrs);
        return "blog-update";
    }
    @PostMapping("/blog/{id}/update")
    public String blogUpdateForm(
            Model model,
            @RequestParam String title,
            @RequestParam String anons,
            @RequestParam String full_text,
            @PathVariable(value = "id") Integer id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Post upd_post = postRepository.findById(id).orElseThrow();
        // through setter methods we assign new object attributes
        upd_post.setTitle(title);
        upd_post.setAnons(anons);
        upd_post.setFull_text(full_text);
        // check obtained result of posted data
         System.out.println(String.format(
                "This is posted data: post_obj - %s | title: %s | anons: %s | full_text: %s ",
                upd_post, title, anons, full_text)
         );
        postRepository.save(upd_post);
        return "redirect:/blog/{id}";
    }

    @GetMapping("/blog/{id}/delete")
    public String blogDelete(Model model, @PathVariable(value = "id") Integer id) {
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> post_del_attrs = new ArrayList<>();
        post.ifPresent(post_del_attrs::add);
        model.addAttribute("post_del", post_del_attrs);
        return "blog-delete";
    }

    @PostMapping("/blog/{id}/delete")
    public String blogPostDelete(Model model, @PathVariable(value = "id") Integer id) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/blog";
    }

    private String httpServletRequestToString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append("Request Method = [" + request.getMethod() + "], ");
        sb.append("Request URL Path = [" + request.getRequestURL() + "], ");

        String headers =
                Collections.list(request.getHeaderNames()).stream()
                        .map(headerName -> headerName + " : " + Collections.list(request.getHeaders(headerName)) )
                        .collect(Collectors.joining(", "));

        if (headers.isEmpty()) {
            sb.append("Request headers: NONE,");
        } else {
            sb.append("Request headers: ["+headers+"],");
        }

        String parameters =
                Collections.list(request.getParameterNames()).stream()
                        .map(p -> p + " : " + Arrays.asList( request.getParameterValues(p)) )
                        .collect(Collectors.joining(", "));

        if (parameters.isEmpty()) {
            sb.append("Request parameters: NONE.");
        } else {
            sb.append("Request parameters: [" + parameters + "].");
        }
        return sb.toString();
    }
    private int getHttpRequestStatus(String request_url) throws IOException {
        URL url = new URL(request_url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.connect();

        int code = connection.getResponseCode();
        return code;
    }
    public void raise404(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
