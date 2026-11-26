/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre;

import com.lablibre.persistencia.Conexion;
import com.lablibre.vista.VentanaPrincipal;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try {

            Conexion.inicializarBD();

            SwingUtilities.invokeLater(() -> {

                VentanaPrincipal ventana =
                        new VentanaPrincipal();

                ventana.setVisible(true);
            });

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar LabLibre:\n"
                    + e.getMessage(),
                    "Error de conexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
