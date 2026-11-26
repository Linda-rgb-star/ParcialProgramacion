/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Conexion {

    private static final String RUTA_BD =
            "database/lablibre.db";

    private static final String URL =
            "jdbc:sqlite:" + RUTA_BD;

    public static Connection conectar() throws SQLException {

        try {
            Files.createDirectories(Paths.get("database"));

            Connection conexion =
                    DriverManager.getConnection(URL);

            try (Statement st = conexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }

            return conexion;

        } catch (Exception e) {
            throw new SQLException(
                    "Error al conectar con SQLite: "
                    + e.getMessage(), e);
        }
    }

    public static void inicializarBD() throws SQLException {

        String[] sentencias = {

            "CREATE TABLE IF NOT EXISTS materiales (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "codigo TEXT NOT NULL UNIQUE," +
            "nombre TEXT NOT NULL," +
            "tipo TEXT NOT NULL CHECK(tipo IN " +
            "('LIBRO','PORTATIL','KIT'))," +
            "valor_comercial REAL," +
            "componentes INTEGER," +
            "CHECK ((tipo='LIBRO' AND valor_comercial IS NULL " +
            "AND componentes IS NULL) OR " +
            "(tipo='PORTATIL' AND valor_comercial > 0 " +
            "AND componentes IS NULL) OR " +
            "(tipo='KIT' AND componentes BETWEEN 1 AND 50 " +
            "AND valor_comercial IS NULL)))",

            "CREATE TABLE IF NOT EXISTS prestamos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "material_id INTEGER NOT NULL," +
            "documento TEXT NOT NULL," +
            "fecha_prestamo TEXT NOT NULL," +
            "fecha_limite TEXT NOT NULL," +
            "fecha_devolucion TEXT," +
            "componentes_faltantes INTEGER DEFAULT 0," +
            "politica_multa TEXT NOT NULL DEFAULT 'NORMAL' " +
            "CHECK(politica_multa IN " +
            "('NORMAL','DOCENTE','EXAMENES'))," +
            "seguro REAL NOT NULL DEFAULT 0," +
            "multa_base REAL NOT NULL DEFAULT 0," +
            "multa_final REAL NOT NULL DEFAULT 0," +
            "FOREIGN KEY(material_id) REFERENCES materiales(id) " +
            "ON DELETE RESTRICT," +
            "CHECK(fecha_limite >= fecha_prestamo)," +
            "CHECK(fecha_devolucion IS NULL OR " +
            "fecha_devolucion >= fecha_prestamo)," +
            "CHECK(componentes_faltantes >= 0))",

            "CREATE UNIQUE INDEX IF NOT EXISTS " +
            "idx_material_prestamo_activo " +
            "ON prestamos(material_id) " +
            "WHERE fecha_devolucion IS NULL",

            "CREATE INDEX IF NOT EXISTS idx_prestamos_fecha " +
            "ON prestamos(fecha_limite)",

            "CREATE INDEX IF NOT EXISTS idx_prestamos_documento " +
            "ON prestamos(documento)"
        };

        try (Connection conexion = conectar();
             Statement st = conexion.createStatement()) {

            for (String sql : sentencias) {
                st.execute(sql);
            }

            System.out.println(
                    "Tablas de LabLibre creadas correctamente.");
        }
    }

    public static void verificarConexion() throws SQLException {

        try (Connection conexion = conectar()) {

            System.out.println(
                    "Conexion con SQLite exitosa.");
        }
    }
}
