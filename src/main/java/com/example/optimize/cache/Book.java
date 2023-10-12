package com.example.optimize.cache;

import lombok.Data;

@Data
public class Book {
    private String name;

    private int s;

    public Book() {
    }

    public Book(String name, int s) {
        this.name = name;
        this.s = s;
    }
}
