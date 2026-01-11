package com.bancom.app.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostCreacionRequest {
    
    @NotBlank(message = "El texto del post es obligatorio")
    @Size(min = 1, max = 500, message = "El texto del post debe tener entre 1 y 500 caracteres")
    private String text;

    public PostCreacionRequest() {
    }

    public PostCreacionRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
