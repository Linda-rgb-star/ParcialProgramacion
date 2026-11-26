/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.lablibre.vista;

import com.lablibre.modelo.*;
import com.lablibre.persistencia.MaterialDAO;
import com.lablibre.persistencia.PrestamoDAO;
import com.lablibre.patron.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    // ==========================================
    // DAO
    // ==========================================

    private final MaterialDAO materialDAO = new MaterialDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    // ==========================================
    // COMPONENTES DE MATERIALES
    // ==========================================

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtValor;
    private JTextField txtComponentes;

    private JComboBox<String> cmbTipo;

    private JTable tablaMateriales;
    private DefaultTableModel modeloTabla;

    private String codigoOriginal = null;

    // ==========================================
    // COMPONENTES DE PRESTAMOS
    // ==========================================

    private JComboBox<Material> cmbMaterial;
    private JTextField txtDocumento;
    private JTextField txtFechaPrestamo;
    private JTextField txtFechaLimite;
    private JTextField txtFechaDevolucion;
    private JTextField txtFaltantes;

    private JComboBox<String> cmbPolitica;

    private JTable tablaPrestamos;
    private DefaultTableModel modeloPrestamos;

    private int prestamoSeleccionadoId = -1;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public VentanaPrincipal() {

        setTitle("LabLibre - Gestion de laboratorio");
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));

        inicializarComponentes();

        cargarMateriales();
        cargarMaterialesCombo();
        cargarPrestamos();
    }

    // ==========================================
    // VENTANA PRINCIPAL
    // ==========================================

    private void inicializarComponentes() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));

        principal.setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "LABLIBRE - Sistema de prestamos de laboratorio");

        titulo.setFont(new Font("Arial", Font.BOLD, 23));

        principal.add(titulo, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();

        pestanas.addTab("Materiales", crearPanelMateriales());
        pestanas.addTab("Prestamos", crearPanelPrestamos());

        principal.add(pestanas, BorderLayout.CENTER);

        add(principal);
    }

    // ==========================================
    // PANEL DE MATERIALES
    // ==========================================

    private JPanel crearPanelMateriales() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());

        formulario.setPreferredSize(new Dimension(320, 500));

        formulario.setBorder(BorderFactory.createTitledBorder(
                "Informacion del material"));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbTipo = new JComboBox<>(
                new String[]{"LIBRO", "PORTATIL", "KIT"});

        txtCodigo = new JTextField(15);
        txtNombre = new JTextField(15);
        txtValor = new JTextField(15);
        txtComponentes = new JTextField(15);

        agregarCampo(formulario, gbc, 0,
                "Tipo:", cmbTipo);

        agregarCampo(formulario, gbc, 1,
                "Codigo:", txtCodigo);

        agregarCampo(formulario, gbc, 2,
                "Nombre:", txtNombre);

        agregarCampo(formulario, gbc, 3,
                "Valor comercial:", txtValor);

        agregarCampo(formulario, gbc, 4,
                "Numero de componentes:", txtComponentes);

        cmbTipo.addActionListener(e -> actualizarCampos());

        actualizarCampos();

        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new GridLayout(2, 2, 5, 5));

        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        formulario.add(botones, gbc);

        btnGuardar.addActionListener(e -> guardarMaterial());
        btnEditar.addActionListener(e -> editarMaterial());
        btnEliminar.addActionListener(e -> eliminarMaterial());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // TABLA

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "Codigo",
                    "Nombre",
                    "Tipo",
                    "Valor comercial",
                    "Componentes"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMateriales = new JTable(modeloTabla);

        tablaMateriales.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        tablaMateriales.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tablaMateriales);

        JPanel zonaTabla = new JPanel(new BorderLayout());

        zonaTabla.setBorder(BorderFactory.createTitledBorder(
                "Materiales registrados"));

        zonaTabla.add(scroll, BorderLayout.CENTER);

        tablaMateriales.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        cargarSeleccionTabla();
                    }
                });

        panel.add(formulario, BorderLayout.WEST);
        panel.add(zonaTabla, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // PANEL DE PRESTAMOS
    // ==========================================

    private JPanel crearPanelPrestamos() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());

        formulario.setPreferredSize(new Dimension(370, 500));

        formulario.setBorder(BorderFactory.createTitledBorder(
                "Gestion de prestamos"));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbMaterial = new JComboBox<>();

        cmbMaterial.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focus) {

                super.getListCellRendererComponent(
                        list, value, index, selected, focus);

                if (value instanceof Material m) {

                    setText(m.getCodigo() + " - "
                            + m.getNombre() + " ("
                            + m.getTipo() + ")");
                }

                return this;
            }
        });

        txtDocumento = new JTextField(15);
        txtFechaPrestamo = new JTextField(15);
        txtFechaLimite = new JTextField(15);
        txtFechaDevolucion = new JTextField(15);
        txtFaltantes = new JTextField("0", 15);

        cmbPolitica = new JComboBox<>(new String[]{
            "NORMAL",
            "DOCENTE",
            "EXAMENES"
        });

        agregarCampo(formulario, gbc, 0,
                "Material:", cmbMaterial);

        agregarCampo(formulario, gbc, 1,
                "Documento:", txtDocumento);

        agregarCampo(formulario, gbc, 2,
                "Fecha prestamo:", txtFechaPrestamo);

        agregarCampo(formulario, gbc, 3,
                "Fecha limite:", txtFechaLimite);

        agregarCampo(formulario, gbc, 4,
                "Politica de multa:", cmbPolitica);

        agregarCampo(formulario, gbc, 5,
                "Fecha devolucion:", txtFechaDevolucion);

        agregarCampo(formulario, gbc, 6,
                "Componentes faltantes:", txtFaltantes);

        JLabel ayuda = new JLabel(
                "Formato de fecha: AAAA-MM-DD");

        ayuda.setForeground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;

        formulario.add(ayuda, gbc);

        // BOTONES

        JButton btnRegistrar = new JButton("Registrar prestamo");
        JButton btnDevolver = new JButton("Devolver material");
        JButton btnCambiar = new JButton("Cambiar politica");
        JButton btnEliminar = new JButton("Eliminar prestamo");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new GridLayout(0, 2, 5, 5));

        botones.add(btnRegistrar);
        botones.add(btnDevolver);
        botones.add(btnCambiar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        gbc.gridy = 8;

        formulario.add(botones, gbc);

        btnRegistrar.addActionListener(
                e -> registrarPrestamo());

        btnDevolver.addActionListener(
                e -> registrarDevolucion());

        btnCambiar.addActionListener(
                e -> cambiarPolitica());

        btnEliminar.addActionListener(
                e -> eliminarPrestamo());

        btnLimpiar.addActionListener(
                e -> limpiarPrestamo());

        // TABLA

        modeloPrestamos = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Codigo",
                    "Documento",
                    "Prestamo",
                    "Limite",
                    "Devolucion",
                    "Seguro",
                    "Multa base",
                    "Multa final",
                    "Politica"
                }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloPrestamos);

        tablaPrestamos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        tablaPrestamos.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF);

        tablaPrestamos.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tablaPrestamos);

        JPanel zonaTabla = new JPanel(new BorderLayout());

        zonaTabla.setBorder(BorderFactory.createTitledBorder(
                "Prestamos registrados"));

        zonaTabla.add(scroll, BorderLayout.CENTER);

        tablaPrestamos.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        seleccionarPrestamo();
                    }
                });

        // CONSULTAS

        JPanel consultas = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnVencidos = new JButton("Consultar vencidos");
        JButton btnMultas = new JButton("Total multas por tipo");

        consultas.add(btnVencidos);
        consultas.add(btnMultas);

        btnVencidos.addActionListener(
                e -> consultarVencidos());

        btnMultas.addActionListener(
                e -> consultarMultas());

        zonaTabla.add(consultas, BorderLayout.SOUTH);

        panel.add(formulario, BorderLayout.WEST);
        panel.add(zonaTabla, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // METODO AUXILIAR PARA CAMPOS
    // ==========================================

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String etiqueta,
            JComponent componente) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;

        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;

        panel.add(componente, gbc);
    }

    // ==========================================
    // MATERIALES
    // ==========================================

    private void actualizarCampos() {

        if (cmbTipo == null) {
            return;
        }

        String tipo = cmbTipo.getSelectedItem().toString();

        txtValor.setEnabled(tipo.equals("PORTATIL"));
        txtComponentes.setEnabled(tipo.equals("KIT"));

        if (!tipo.equals("PORTATIL")) {
            txtValor.setText("");
        }

        if (!tipo.equals("KIT")) {
            txtComponentes.setText("");
        }
    }

    private Material crearMaterial() {

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "Complete el codigo y el nombre.");
        }

        String tipo = cmbTipo.getSelectedItem().toString();

        return switch (tipo) {

            case "LIBRO" ->
                new Libro(codigo, nombre);

            case "PORTATIL" -> {

                if (txtValor.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Ingrese el valor comercial.");
                }

                double valor = Double.parseDouble(
                        txtValor.getText().trim());

                yield new Portatil(codigo, nombre, valor);
            }

            case "KIT" -> {

                if (txtComponentes.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Ingrese el numero de componentes.");
                }

                int componentes = Integer.parseInt(
                        txtComponentes.getText().trim());

                yield new KitElectronica(
                        codigo, nombre, componentes);
            }

            default -> throw new IllegalArgumentException(
                    "Tipo de material no valido.");
        };
    }

    private void guardarMaterial() {

        try {

            Material material = crearMaterial();

            materialDAO.guardar(material);

            JOptionPane.showMessageDialog(this,
                    "Material registrado correctamente.");

            limpiarFormulario();
            cargarMateriales();
            cargarMaterialesCombo();

        } catch (NumberFormatException e) {

            mostrarError("Ingrese valores numericos validos.");

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void cargarMateriales() {

        try {

            modeloTabla.setRowCount(0);

            List<Material> materiales = materialDAO.listar();

            for (Material m : materiales) {

                String valor = "-";
                String componentes = "-";

                if (m instanceof Portatil p) {
                    valor = String.valueOf(p.getValorComercial());
                }

                if (m instanceof KitElectronica k) {
                    componentes = String.valueOf(
                            k.getComponentes());
                }

                modeloTabla.addRow(new Object[]{
                    m.getCodigo(),
                    m.getNombre(),
                    m.getTipo(),
                    valor,
                    componentes
                });
            }

        } catch (Exception e) {

            mostrarError("Error cargando materiales: "
                    + e.getMessage());
        }
    }

    private void cargarSeleccionTabla() {

        int fila = tablaMateriales.getSelectedRow();

        if (fila < 0) {
            return;
        }

        try {

            String codigo = modeloTabla.getValueAt(
                    fila, 0).toString();

            Material material =
                    materialDAO.buscarPorCodigo(codigo);

            if (material == null) {
                return;
            }

            codigoOriginal = codigo;

            cmbTipo.setSelectedItem(material.getTipo());

            txtCodigo.setText(material.getCodigo());
            txtNombre.setText(material.getNombre());

            txtValor.setText("");
            txtComponentes.setText("");

            if (material instanceof Portatil p) {
                txtValor.setText(
                        String.valueOf(p.getValorComercial()));
            }

            if (material instanceof KitElectronica k) {
                txtComponentes.setText(
                        String.valueOf(k.getComponentes()));
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void editarMaterial() {

        if (codigoOriginal == null) {
            mostrarError("Seleccione un material de la tabla.");
            return;
        }

        try {

            Material material = crearMaterial();

            materialDAO.actualizar(codigoOriginal, material);

            JOptionPane.showMessageDialog(this,
                    "Material actualizado correctamente.");

            limpiarFormulario();
            cargarMateriales();
            cargarMaterialesCombo();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void eliminarMaterial() {

        if (codigoOriginal == null) {
            mostrarError("Seleccione un material.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Esta seguro de eliminar este material?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            materialDAO.eliminar(codigoOriginal);

            JOptionPane.showMessageDialog(this,
                    "Material eliminado correctamente.");

            limpiarFormulario();
            cargarMateriales();
            cargarMaterialesCombo();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el material.\n"
                    + e.getMessage());
        }
    }

    private void limpiarFormulario() {

        codigoOriginal = null;

        txtCodigo.setText("");
        txtNombre.setText("");
        txtValor.setText("");
        txtComponentes.setText("");

        cmbTipo.setSelectedIndex(0);

        tablaMateriales.clearSelection();

        actualizarCampos();
    }

    // ==========================================
    // PRESTAMOS
    // ==========================================

    private void cargarMaterialesCombo() {

        try {

            cmbMaterial.removeAllItems();

            for (Material material : materialDAO.listar()) {
                cmbMaterial.addItem(material);
            }

        } catch (Exception e) {

            mostrarError("Error cargando materiales: "
                    + e.getMessage());
        }
    }

    private void registrarPrestamo() {

        try {

            Material material =
                    (Material) cmbMaterial.getSelectedItem();

            if (material == null) {
                throw new IllegalArgumentException(
                        "Primero registre un material.");
            }

            String documento = txtDocumento.getText().trim();

            LocalDate fechaPrestamo = LocalDate.parse(
                    txtFechaPrestamo.getText().trim());

            LocalDate fechaLimite = LocalDate.parse(
                    txtFechaLimite.getText().trim());

            Prestamo prestamo = new Prestamo(
                    material,
                    documento,
                    fechaPrestamo,
                    fechaLimite
            );

            prestamo.setPolitica(crearPoliticaSeleccionada());

            prestamoDAO.guardar(prestamo);

            JOptionPane.showMessageDialog(this,
                    "Prestamo registrado correctamente.\n"
                    + "Seguro: $" + prestamo.getSeguro());

            limpiarPrestamo();
            cargarPrestamos();

        } catch (Exception e) {

            mostrarError("No se pudo registrar el prestamo:\n"
                    + e.getMessage());
        }
    }

    private void registrarDevolucion() {

        if (prestamoSeleccionadoId == -1) {
            mostrarError("Seleccione un prestamo.");
            return;
        }

        try {

            Prestamo prestamo =
                    prestamoDAO.buscarPorId(prestamoSeleccionadoId);

            if (prestamo == null) {
                throw new IllegalArgumentException(
                        "No se encontro el prestamo.");
            }

            LocalDate fecha = LocalDate.parse(
                    txtFechaDevolucion.getText().trim());

            int faltantes = Integer.parseInt(
                    txtFaltantes.getText().trim());

            prestamo.setPolitica(crearPoliticaSeleccionada());

            prestamo.devolver(fecha, faltantes);

            prestamoDAO.actualizar(prestamo);

            JOptionPane.showMessageDialog(this,
                    "Devolucion registrada correctamente.\n"
                    + "Seguro: $" + prestamo.getSeguro()
                    + "\nMulta base: $" + prestamo.getMultaBase()
                    + "\nMulta final: $" + prestamo.getMultaFinal());

            limpiarPrestamo();
            cargarPrestamos();

        } catch (Exception e) {

            mostrarError("Error en la devolucion:\n"
                    + e.getMessage());
        }
    }

    private PoliticaMulta crearPoliticaSeleccionada() {

        String politica = cmbPolitica.getSelectedItem().toString();

        return switch (politica) {

            case "DOCENTE" -> new MultaDocente();

            case "EXAMENES" -> new MultaExamenes();

            default -> new MultaNormal();
        };
    }

    private void cambiarPolitica() {

        if (prestamoSeleccionadoId == -1) {
            mostrarError("Seleccione un prestamo.");
            return;
        }

        try {

            Prestamo prestamo =
                    prestamoDAO.buscarPorId(prestamoSeleccionadoId);

            if (prestamo == null) {
                throw new IllegalArgumentException(
                        "Prestamo no encontrado.");
            }

            prestamo.setPolitica(crearPoliticaSeleccionada());

            prestamoDAO.actualizar(prestamo);

            JOptionPane.showMessageDialog(this,
                    "Politica actualizada: "
                    + prestamo.getPolitica()
                    + "\nNueva multa final: $"
                    + prestamo.getMultaFinal());

            cargarPrestamos();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void cargarPrestamos() {

        try {

            modeloPrestamos.setRowCount(0);

            List<Prestamo> prestamos = prestamoDAO.listar();

            for (Prestamo p : prestamos) {

                modeloPrestamos.addRow(new Object[]{
                    p.getId(),
                    p.getMaterial().getCodigo(),
                    p.getDocumento(),
                    p.getFechaPrestamo(),
                    p.getFechaLimite(),
                    p.getFechaDevolucion() == null
                            ? "Sin devolver"
                            : p.getFechaDevolucion(),
                    p.getSeguro(),
                    p.getMultaBase(),
                    p.getMultaFinal(),
                    p.getPolitica()
                });
            }

        } catch (Exception e) {

            mostrarError("Error cargando prestamos: "
                    + e.getMessage());
        }
    }

    private void seleccionarPrestamo() {

        int fila = tablaPrestamos.getSelectedRow();

        if (fila < 0) {
            return;
        }

        try {

            prestamoSeleccionadoId = Integer.parseInt(
                    modeloPrestamos.getValueAt(fila, 0).toString());

            Prestamo p = prestamoDAO.buscarPorId(
                    prestamoSeleccionadoId);

            if (p == null) {
                return;
            }

            txtDocumento.setText(p.getDocumento());

            txtFechaPrestamo.setText(
                    p.getFechaPrestamo().toString());

            txtFechaLimite.setText(
                    p.getFechaLimite().toString());

            txtFechaDevolucion.setText(
                    p.getFechaDevolucion() == null
                            ? ""
                            : p.getFechaDevolucion().toString());

            txtFaltantes.setText(
                    String.valueOf(p.getComponentesFaltantes()));

            cmbPolitica.setSelectedItem(p.getPolitica());

            int indice = buscarIndiceMaterial(
                    p.getMaterial().getCodigo());

            if (indice >= 0) {
                cmbMaterial.setSelectedIndex(indice);
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private int buscarIndiceMaterial(String codigo) {

        for (int i = 0; i < cmbMaterial.getItemCount(); i++) {

            Material m = cmbMaterial.getItemAt(i);

            if (m.getCodigo().equals(codigo)) {
                return i;
            }
        }

        return -1;
    }

    private void eliminarPrestamo() {

        if (prestamoSeleccionadoId == -1) {
            mostrarError("Seleccione un prestamo.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar este prestamo?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            prestamoDAO.eliminar(prestamoSeleccionadoId);

            JOptionPane.showMessageDialog(this,
                    "Prestamo eliminado correctamente.");

            limpiarPrestamo();
            cargarPrestamos();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void limpiarPrestamo() {

        prestamoSeleccionadoId = -1;

        txtDocumento.setText("");
        txtFechaPrestamo.setText("");
        txtFechaLimite.setText("");
        txtFechaDevolucion.setText("");
        txtFaltantes.setText("0");

        cmbPolitica.setSelectedIndex(0);

        tablaPrestamos.clearSelection();
    }

    // ==========================================
    // CONSULTA 1: PRESTAMOS VENCIDOS
    // ==========================================

    private void consultarVencidos() {

        try {

            String entrada = JOptionPane.showInputDialog(
                    this,
                    "Ingrese la fecha de consulta (AAAA-MM-DD):",
                    LocalDate.now().toString());

            if (entrada == null) {
                return;
            }

            LocalDate fecha = LocalDate.parse(entrada.trim());

            List<Prestamo> vencidos =
                    prestamoDAO.consultarVencidos(fecha);

            StringBuilder resultado = new StringBuilder();

            resultado.append(
                    "PRESTAMOS VENCIDOS SIN DEVOLVER\n\n");

            for (Prestamo p : vencidos) {

                resultado.append("Material: ")
                        .append(p.getMaterial().getCodigo())
                        .append("\nDocumento: ")
                        .append(p.getDocumento())
                        .append("\nFecha limite: ")
                        .append(p.getFechaLimite())
                        .append("\nDias de retraso: ")
                        .append(p.calcularDiasRetraso(fecha))
                        .append("\n-------------------------\n");
            }

            if (vencidos.isEmpty()) {
                resultado.append(
                        "No existen prestamos vencidos.");
            }

            JTextArea area = new JTextArea(
                    resultado.toString(), 18, 45);

            area.setEditable(false);

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "Consulta de prestamos vencidos",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(
                    "Error en la consulta: " + e.getMessage());
        }
    }

    // ==========================================
    // CONSULTA 2: TOTAL MULTAS POR TIPO
    // ==========================================

    private void consultarMultas() {

        try {

            Map<String, Double> resultados =
                    prestamoDAO.totalMultasPorTipo();

            StringBuilder texto = new StringBuilder();

            texto.append("TOTAL DE MULTAS POR TIPO\n\n");

            for (Map.Entry<String, Double> entrada
                    : resultados.entrySet()) {

                texto.append(entrada.getKey())
                        .append(": $")
                        .append(String.format(
                                "%,.2f", entrada.getValue()))
                        .append("\n");
            }

            if (resultados.isEmpty()) {
                texto.append(
                        "Todavia no hay multas registradas.");
            }

            JOptionPane.showMessageDialog(
                    this,
                    texto.toString(),
                    "Consulta de multas",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(
                    "Error consultando multas: "
                    + e.getMessage());
        }
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
