/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.modelo;

import com.lablibre.excepciones.DevolucionInvalidaException;

public class Libro extends Material {

    public Libro(String codigo, String nombre) {
        super(codigo, nombre);
    }

    @Override
    public String getTipo() {
        return "LIBRO";
    }

    @Override
    public int getPlazoMaximoDias() {
        return 15;
    }

    @Override
    public double calcularMultaBase(
            long diasRetraso,
            int componentesFaltantes
    ) throws DevolucionInvalidaException {

        if (diasRetraso < 0) {
            throw new DevolucionInvalidaException(
                    "Los dias de retraso no pueden ser negativos.");
        }

        return diasRetraso * 1000.0;
    }
    @Override
public double calcularSeguro() {
    return 0;
}
}
