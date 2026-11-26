/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 
package com.lablibre.persistencia;

import com.lablibre.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    // REGISTRAR MATERIAL
    public void guardar(Material material) throws SQLException {

        String sql = """
            INSERT INTO materiales
            (codigo, nombre, tipo, valor_comercial, componentes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, material.getCodigo());
            ps.setString(2, material.getNombre());
            ps.setString(3, material.getTipo());

            if (material instanceof Portatil p) {
                ps.setDouble(4, p.getValorComercial());
                ps.setNull(5, Types.INTEGER);

            } else if (material instanceof KitElectronica k) {
                ps.setNull(4, Types.REAL);
                ps.setInt(5, k.getComponentes());

            } else {
                ps.setNull(4, Types.REAL);
                ps.setNull(5, Types.INTEGER);
            }

            ps.executeUpdate();
        }
    }

    // LISTAR TODOS LOS MATERIALES
    public List<Material> listar() throws SQLException {

        List<Material> materiales = new ArrayList<>();

        String sql = "SELECT * FROM materiales ORDER BY id";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                materiales.add(reconstruir(rs));
            }
        }

        return materiales;
    }

    // BUSCAR MATERIAL POR CODIGO
    public Material buscarPorCodigo(String codigo)
            throws SQLException {

        String sql =
                "SELECT * FROM materiales WHERE codigo = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return reconstruir(rs);
                }
            }
        }

        return null;
    }

    // ACTUALIZAR MATERIAL
    public void actualizar(
            String codigoOriginal,
            Material material
    ) throws SQLException {

        String sql = """
            UPDATE materiales
            SET codigo = ?, nombre = ?, tipo = ?,
                valor_comercial = ?, componentes = ?
            WHERE codigo = ?
        """;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, material.getCodigo());
            ps.setString(2, material.getNombre());
            ps.setString(3, material.getTipo());

            if (material instanceof Portatil p) {
                ps.setDouble(4, p.getValorComercial());
                ps.setNull(5, Types.INTEGER);

            } else if (material instanceof KitElectronica k) {
                ps.setNull(4, Types.REAL);
                ps.setInt(5, k.getComponentes());

            } else {
                ps.setNull(4, Types.REAL);
                ps.setNull(5, Types.INTEGER);
            }

            ps.setString(6, codigoOriginal.toUpperCase());

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontro el material.");
            }
        }
    }

    // ELIMINAR MATERIAL
    public void eliminar(String codigo) throws SQLException {

        String sql = "DELETE FROM materiales WHERE codigo = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo.toUpperCase());

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontro el material.");
            }
        }
    }

    // RECONSTRUIR MATERIAL CON SU TIPO CORRECTO
    private Material reconstruir(ResultSet rs)
            throws SQLException {

        String tipo = rs.getString("tipo");
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");

        return switch (tipo) {

            case "LIBRO" ->
                new Libro(codigo, nombre);

            case "PORTATIL" ->
                new Portatil(
                    codigo,
                    nombre,
                    rs.getDouble("valor_comercial")
                );

            case "KIT" ->
                new KitElectronica(
                    codigo,
                    nombre,
                    rs.getInt("componentes")
                );

            default ->
                throw new SQLException(
                    "Tipo de material desconocido: " + tipo);
        };
    }
}
