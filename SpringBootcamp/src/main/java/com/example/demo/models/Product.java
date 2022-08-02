package com.example.demo.models;

import javax.persistence.*;

@Entity
@Table(name = "Product")
public class Product {
    // Id is annotation of unique id(pk) identifier
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;
    private String content;
    private Integer price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}