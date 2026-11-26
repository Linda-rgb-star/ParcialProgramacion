/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.modelo;

import com.lablibre.excepciones.*;
import com.lablibre.patron.PoliticaMulta;
import com.lablibre.patron.MultaNormal;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {

    private int id;
    private Material material;
    private String documento;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaDevolucion;
    private int componentesFaltantes;

    private PoliticaMulta politica;
    private double seguro;
    private double multaBase;
    private double multaFinal;

    public Prestamo(
            Material material,
            String documento,
            LocalDate fechaPrestamo,
            LocalDate fechaLimite
    ) throws PlazoExcedidoException {

        if (material == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un material.");
        }

        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio.");
        }

        if (fechaPrestamo == null || fechaLimite == null
                || fechaLimite.isBefore(fechaPrestamo)) {
            throw new IllegalArgumentException(
                    "Las fechas del prestamo no son validas.");
        }

        long dias = ChronoUnit.DAYS.between(
                fechaPrestamo, fechaLimite);

        if (dias > material.getPlazoMaximoDias()) {
            throw new PlazoExcedidoException(
                    "El plazo maximo para "
                    + material.getTipo() + " es de "
                    + material.getPlazoMaximoDias() + " dias.");
        }

        this.material = material;
        this.documento = documento.trim();
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.componentesFaltantes = 0;

        this.politica = new MultaNormal();

        this.seguro = material.calcularSeguro();
        this.multaBase = 0;
        this.multaFinal = 0;
    }

    public void devolver(
            LocalDate fechaDevolucion,
            int componentesFaltantes
    ) throws DevolucionInvalidaException {

        if (fechaDevolucion == null ||
                fechaDevolucion.isBefore(fechaPrestamo)) {

            throw new DevolucionInvalidaException(
                    "La fecha de devolucion no es valida.");
        }

        if (this.fechaDevolucion != null) {
            throw new DevolucionInvalidaException(
                    "Este prestamo ya fue devuelto.");
        }

        long diasRetraso = Math.max(
                0,
                ChronoUnit.DAYS.between(
                        fechaLimite, fechaDevolucion)
        );

        this.multaBase = material.calcularMultaBase(
                diasRetraso,
                componentesFaltantes
        );

        this.multaFinal = politica.aplicar(multaBase);

        this.fechaDevolucion = fechaDevolucion;
        this.componentesFaltantes = componentesFaltantes;
    }

    public void setPolitica(PoliticaMulta politica) {

        if (politica == null) {
            throw new IllegalArgumentException(
                    "La politica no puede ser nula.");
        }

        this.politica = politica;

        if (fechaDevolucion != null) {
            this.multaFinal = politica.aplicar(multaBase);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public int getComponentesFaltantes() {
        return componentesFaltantes;
    }

    public String getPolitica() {
        return politica.getNombre();
    }

    public double getSeguro() {
        return seguro;
    }

    public double getMultaBase() {
        return multaBase;
    }

    public double getMultaFinal() {
        return multaFinal;
    }

    public boolean estaDevuelto() {
        return fechaDevolucion != null;
    }

    public long calcularDiasRetraso(LocalDate fechaConsulta) {

        if (fechaConsulta == null) {
            throw new IllegalArgumentException(
                    "La fecha de consulta es obligatoria.");
        }

        LocalDate fechaCalculo = fechaDevolucion != null
                ? fechaDevolucion
                : fechaConsulta;

        return Math.max(
                0,
                ChronoUnit.DAYS.between(
                        fechaLimite, fechaCalculo)
        );
    }
}
