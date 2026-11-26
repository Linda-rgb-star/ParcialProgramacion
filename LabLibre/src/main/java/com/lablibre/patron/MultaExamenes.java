/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.patron;

public class MultaExamenes implements PoliticaMulta {

    @Override
    public double aplicar(double multaBase) {
        return Math.min(multaBase * 2, 100000);
    }

    @Override
    public String getNombre() {
        return "EXAMENES";
    }
}
