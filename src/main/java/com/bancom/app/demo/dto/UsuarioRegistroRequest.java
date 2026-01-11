package com.bancom.app.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRegistroRequest {
    
    @NotBlank(message = "El celular es obligatorio")
    //@Pattern(regexp = "^[0-9]{10}$", message = "El celular debe tener exactamente 10 dígitos")
    private String cellphone;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastname;
    
    @NotBlank(message = "El nickname es obligatorio")
    @Size(min = 3, max = 50, message = "El nickname debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "El nickname solo puede contener letras, números, guiones y guiones bajos")
    private String nickname;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    public UsuarioRegistroRequest() {
    }

    public UsuarioRegistroRequest(String cellphone, String name, String lastname, String nickname, String password) {
        this.cellphone = cellphone;
        this.name = name;
        this.lastname = lastname;
        this.nickname = nickname;
        this.password = password;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
