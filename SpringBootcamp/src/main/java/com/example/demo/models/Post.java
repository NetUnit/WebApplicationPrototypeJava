package com.example.demo.models;

import javax.persistence.*;

@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // public & private
    private String title, anons, full_text;
    private int views;

    public Integer getId() {
        return id;
    }

    public void setId(Integer Id) {
        this.id = id;
    }

    // after we've assigned paramaters in Controller(Blog)
    // init is obligatory

    // analogue of parameter=None
    // when empty object has been created
    public Post() {};

    public Post(String title, String anons, String full_text) {
        this.title = title;
        this.anons = anons;
        this.full_text = full_text;
    }
}
