/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.modelo;

import com.lablibre.excepciones.DevolucionInvalidaException;

public abstract class Material {

    private String codigo;
    private String nombre;

    public Material(String codigo, String nombre) {
        setCodigo(codigo);
        setNombre(nombre);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El codigo no puede estar vacio.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    public abstract String getTipo();

    public abstract int getPlazoMaximoDias();

    public abstract double calcularMultaBase(
            long diasRetraso,
            int componentesFaltantes
    ) throws DevolucionInvalidaException;
    public abstract double calcularSeguro();
}
