/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.modelo;

import com.lablibre.interfaces.Asegurable;
import com.lablibre.excepciones.DevolucionInvalidaException;

public class Portatil extends Material implements Asegurable {

    private double valorComercial;

    public Portatil(
            String codigo,
            String nombre,
            double valorComercial) {

        super(codigo, nombre);
        setValorComercial(valorComercial);
    }

    public double getValorComercial() {
        return valorComercial;
    }

    public void setValorComercial(double valorComercial) {

        if (valorComercial <= 0) {
            throw new IllegalArgumentException(
                    "El valor comercial debe ser mayor que cero.");
        }

        this.valorComercial = valorComercial;
    }

    @Override
    public String getTipo() {
        return "PORTATIL";
    }

    @Override
    public int getPlazoMaximoDias() {
        return 3;
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

        return diasRetraso * 10000.0;
    }

    @Override
    public double calcularSeguro() {
        return valorComercial * 0.02;
    }
}
