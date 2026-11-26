/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.patron;

public class GestorMultas {

    private PoliticaMulta politica;

    public GestorMultas(PoliticaMulta politica) {
        setPolitica(politica);
    }

    public void setPolitica(PoliticaMulta politica) {

        if (politica == null) {
            throw new IllegalArgumentException(
                    "La politica de multa no puede ser nula.");
        }

        this.politica = politica;
    }

    public double calcularMultaFinal(double multaBase) {

        if (multaBase < 0) {
            throw new IllegalArgumentException(
                    "La multa base no puede ser negativa.");
        }

        return politica.aplicar(multaBase);
    }

    public String getPoliticaActual() {
        return politica.getNombre();
    }
}
