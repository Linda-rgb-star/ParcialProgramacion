/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.patron;

public class MultaDocente implements PoliticaMulta {

    @Override
    public double aplicar(double multaBase) {
        return multaBase * 0.50;
    }

    @Override
    public String getNombre() {
        return "DOCENTE";
    }
}
