/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.modelo;

import com.lablibre.interfaces.Asegurable;
import com.lablibre.excepciones.DevolucionInvalidaException;

public class KitElectronica extends Material implements Asegurable {

    private int componentes;

    public KitElectronica(
            String codigo,
            String nombre,
            int componentes) {

        super(codigo, nombre);
        setComponentes(componentes);
    }

    public int getComponentes() {
        return componentes;
    }

    public void setComponentes(int componentes) {

        if (componentes < 1 || componentes > 50) {
            throw new IllegalArgumentException(
                    "El kit debe tener entre 1 y 50 componentes.");
        }

        this.componentes = componentes;
    }

    @Override
    public String getTipo() {
        return "KIT";
    }

    @Override
    public int getPlazoMaximoDias() {
        return 7;
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

        if (componentesFaltantes < 0 ||
                componentesFaltantes > componentes) {

            throw new DevolucionInvalidaException(
                    "Los componentes faltantes no son validos.");
        }

        return (diasRetraso * 3000.0)
                + (componentesFaltantes * 5000.0);
    }

    @Override
    public double calcularSeguro() {
        return 2000.0;
    }
}
