/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.persistencia;

import com.lablibre.modelo.*;
import com.lablibre.patron.*;

import com.lablibre.excepciones.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PrestamoDAO {

    private final MaterialDAO materialDAO = new MaterialDAO();

    // ==========================================
    // REGISTRAR PRESTAMO
    // ==========================================

    public void guardar(Prestamo prestamo) throws SQLException {

        String sql = """
            INSERT INTO prestamos (
                material_id, documento, fecha_prestamo,
                fecha_limite, fecha_devolucion,
                componentes_faltantes, politica_multa,
                seguro, multa_base, multa_final
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int materialId = obtenerIdMaterial(
                    con, prestamo.getMaterial().getCodigo());

            ps.setInt(1, materialId);
            ps.setString(2, prestamo.getDocumento());
            ps.setString(3, prestamo.getFechaPrestamo().toString());
            ps.setString(4, prestamo.getFechaLimite().toString());

            if (prestamo.getFechaDevolucion() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5,
                        prestamo.getFechaDevolucion().toString());
            }

            ps.setInt(6, prestamo.getComponentesFaltantes());
            ps.setString(7, prestamo.getPolitica());
            ps.setDouble(8, prestamo.getSeguro());
            ps.setDouble(9, prestamo.getMultaBase());
            ps.setDouble(10, prestamo.getMultaFinal());

            ps.executeUpdate();
        }
    }

    // ==========================================
    // LISTAR TODOS LOS PRESTAMOS
    // ==========================================

    public List<Prestamo> listar() throws SQLException {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql = """
            SELECT p.*, m.codigo
            FROM prestamos p
            INNER JOIN materiales m
            ON p.material_id = m.id
            ORDER BY p.id
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(reconstruir(rs));
            }
        }

        return prestamos;
    }

    // ==========================================
    // BUSCAR PRESTAMO POR ID
    // ==========================================

    public Prestamo buscarPorId(int id) throws SQLException {

        String sql = """
            SELECT p.*, m.codigo
            FROM prestamos p
            INNER JOIN materiales m
            ON p.material_id = m.id
            WHERE p.id = ?
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return reconstruir(rs);
                }
            }
        }

        return null;
    }

    // ==========================================
    // ACTUALIZAR PRESTAMO
    // ==========================================

    public void actualizar(Prestamo prestamo) throws SQLException {

        String sql = """
            UPDATE prestamos SET
                material_id = ?,
                documento = ?,
                fecha_prestamo = ?,
                fecha_limite = ?,
                fecha_devolucion = ?,
                componentes_faltantes = ?,
                politica_multa = ?,
                seguro = ?,
                multa_base = ?,
                multa_final = ?
            WHERE id = ?
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int materialId = obtenerIdMaterial(
                    con, prestamo.getMaterial().getCodigo());

            ps.setInt(1, materialId);
            ps.setString(2, prestamo.getDocumento());
            ps.setString(3, prestamo.getFechaPrestamo().toString());
            ps.setString(4, prestamo.getFechaLimite().toString());

            if (prestamo.getFechaDevolucion() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5,
                        prestamo.getFechaDevolucion().toString());
            }

            ps.setInt(6, prestamo.getComponentesFaltantes());
            ps.setString(7, prestamo.getPolitica());
            ps.setDouble(8, prestamo.getSeguro());
            ps.setDouble(9, prestamo.getMultaBase());
            ps.setDouble(10, prestamo.getMultaFinal());
            ps.setInt(11, prestamo.getId());

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontro el prestamo.");
            }
        }
    }

    // ==========================================
    // ELIMINAR PRESTAMO
    // ==========================================

    public void eliminar(int id) throws SQLException {

        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontro el prestamo.");
            }
        }
    }

    // ==========================================
    // PRESTAMOS VENCIDOS SIN DEVOLVER
    // ==========================================

    public List<Prestamo> consultarVencidos(
            LocalDate fechaConsulta) throws SQLException {

        List<Prestamo> vencidos = new ArrayList<>();

        String sql = """
            SELECT p.*, m.codigo
            FROM prestamos p
            INNER JOIN materiales m
            ON p.material_id = m.id
            WHERE p.fecha_devolucion IS NULL
            AND p.fecha_limite < ?
            ORDER BY p.fecha_limite
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaConsulta.toString());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    vencidos.add(reconstruir(rs));
                }
            }
        }

        return vencidos;
    }

    // ==========================================
    // TOTAL DE MULTAS POR TIPO DE MATERIAL
    // ==========================================

    public Map<String, Double> totalMultasPorTipo()
            throws SQLException {

        Map<String, Double> resultados = new LinkedHashMap<>();

        String sql = """
            SELECT m.tipo, COALESCE(SUM(p.multa_final), 0) AS total
            FROM materiales m
            LEFT JOIN prestamos p ON m.id = p.material_id
            WHERE p.fecha_devolucion IS NOT NULL
            GROUP BY m.tipo
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultados.put(
                        rs.getString("tipo"),
                        rs.getDouble("total")
                );
            }
        }

        return resultados;
    }

    // ==========================================
    // OBTENER ID DEL MATERIAL
    // ==========================================

    private int obtenerIdMaterial(
            Connection con,
            String codigo) throws SQLException {

        String sql =
                "SELECT id FROM materiales WHERE codigo = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new SQLException(
                "El material no existe en la base de datos.");
    }

    // ==========================================
    // RECONSTRUIR PRESTAMO
    // ==========================================

    private Prestamo reconstruir(ResultSet rs)
            throws SQLException {

        try {
            Material material = materialDAO.buscarPorCodigo(
                    rs.getString("codigo"));

            if (material == null) {
                throw new SQLException(
                        "No se encontro el material del prestamo.");
            }

            LocalDate fechaPrestamo =
                    LocalDate.parse(rs.getString("fecha_prestamo"));

            LocalDate fechaLimite =
                    LocalDate.parse(rs.getString("fecha_limite"));

            Prestamo prestamo = new Prestamo(
                    material,
                    rs.getString("documento"),
                    fechaPrestamo,
                    fechaLimite
            );

            prestamo.setId(rs.getInt("id"));

            // Recuperar politica Strategy
            prestamo.setPolitica(
                    crearPolitica(rs.getString("politica_multa"))
            );

            // Recuperar devolucion si existe
            String fechaDevolucion =
                    rs.getString("fecha_devolucion");

            if (fechaDevolucion != null) {

                prestamo.devolver(
                        LocalDate.parse(fechaDevolucion),
                        rs.getInt("componentes_faltantes")
                );
            }

            return prestamo;

        } catch (PlazoExcedidoException |
                 DevolucionInvalidaException |
                 IllegalArgumentException e) {

            throw new SQLException(
                    "Error reconstruyendo el prestamo: "
                    + e.getMessage(), e);
        }
    }

    // ==========================================
    // RECUPERAR POLITICA STRATEGY
    // ==========================================

    private PoliticaMulta crearPolitica(String nombre) {

        return switch (nombre) {

            case "DOCENTE" -> new MultaDocente();

            case "EXAMENES" -> new MultaExamenes();

            default -> new MultaNormal();
        };
    }
}
