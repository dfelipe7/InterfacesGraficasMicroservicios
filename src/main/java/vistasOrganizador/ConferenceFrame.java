package vistasOrganizador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Unicauca
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser; // Importar la clase JDateChooser
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import vistasSesion.VtnPrincipalLogin;

public class ConferenceFrame extends JFrame {

    private JTextField nameField;
    private JTextField locationField;
    private JDateChooser startDateChooser; // Usar JDateChooser en lugar de JTextField
    private JDateChooser endDateChooser;   // Usar JDateChooser en lugar de JTextField
    private JTextField topicsField;
    private JTable conferenceTable;
    private DefaultTableModel tableModel;
    private ConferenceService conferenceService;
    private String organizerId;
    private Long selectedConferenceId;  // Variable para almacenar la conferencia seleccionada

    public ConferenceFrame(String organizerId) {
        this.organizerId = organizerId;
        conferenceService = new ConferenceService(); // Instancia de ConferenceService
        setTitle("Gestión de Conferencias");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel para los campos de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2, 10, 10));

        // Campos de entrada
        inputPanel.add(new JLabel("Nombre:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Ubicación:"));
        locationField = new JTextField();
        inputPanel.add(locationField);

        inputPanel.add(new JLabel("Fecha de Inicio:"));
        startDateChooser = new JDateChooser(); // Inicializar el JDateChooser
        inputPanel.add(startDateChooser);

        inputPanel.add(new JLabel("Fecha de Fin:"));
        endDateChooser = new JDateChooser(); // Inicializar el JDateChooser
        inputPanel.add(endDateChooser);

        inputPanel.add(new JLabel("Temas:"));
        topicsField = new JTextField();
        inputPanel.add(topicsField);

        // Botón para crear la conferencia
        JButton createButton = new JButton("Crear Conferencia");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createConference();
            }
        });
        inputPanel.add(createButton);

        // Botón para listar conferencias
        JButton listButton = new JButton("Listar Conferencias");
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listConferences();
            }
        });
        inputPanel.add(listButton);

        // Botón para actualizar la conferencia
        JButton updateButton = new JButton("Actualizar Conferencia");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConference();
            }
        });
        inputPanel.add(updateButton);

        // Botón para eliminar la conferencia
        JButton deleteButton = new JButton("Eliminar Conferencia");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteConference();
            }
        });
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // Modelo y tabla para listar conferencias
        String[] columnNames = {"ID", "Nombre", "Ubicación", "Fecha de Inicio", "Fecha de Fin", "Temas"};
        tableModel = new DefaultTableModel(columnNames, 0);
        conferenceTable = new JTable(tableModel);
        conferenceTable.setFillsViewportHeight(true);

        // Añadir JScrollPane para la tabla
        JScrollPane scrollPane = new JScrollPane(conferenceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Añadir evento de selección de fila para cargar los datos en los campos de texto
        conferenceTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = conferenceTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                locationField.setText(tableModel.getValueAt(selectedRow, 2).toString());

                // Obtener las fechas del modelo de la tabla y establecerlas en los JDateChooser
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajustar formato según tu implementación
                try {
                    startDateChooser.setDate(sdf.parse(tableModel.getValueAt(selectedRow, 3).toString()));
                    endDateChooser.setDate(sdf.parse(tableModel.getValueAt(selectedRow, 4).toString()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                topicsField.setText(tableModel.getValueAt(selectedRow, 5).toString());

                // Obtener el ID seleccionado de la tabla y convertirlo a Long
                String selectedIdString = tableModel.getValueAt(selectedRow, 0).toString();
                selectedConferenceId = Long.parseLong(selectedIdString);
            }
        });

        // Botón de Cerrar Sesión
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.addActionListener(new LogoutButtonListener()); // Evento de cierre de sesión
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Establecer un borde
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Conferencias"));
    }

    private void createConference() {
        try {
            String name = nameField.getText();
            String location = locationField.getText();
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDateChooser.getDate()); // Obtener la fecha como String
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDateChooser.getDate()); // Obtener la fecha como String
            String topics = topicsField.getText();

            String result = conferenceService.createConference(name, location, startDate, endDate, topics, organizerId);
            JOptionPane.showMessageDialog(this, result);
            clearFields(); // Limpiar campos después de crear
            listConferences(); // Actualizar la tabla
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void listConferences() {
        try {
            String[][] conferences = conferenceService.getConferences();
            tableModel.setRowCount(0);  // Limpiar la tabla
            for (String[] conference : conferences) {
                tableModel.addRow(conference);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateConference() {
        try {
            if (selectedConferenceId == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona una conferencia para actualizar.");
                return;
            }
            String name = nameField.getText();
            String location = locationField.getText();
            String startDate = new SimpleDateFormat("yyyy-MM-dd").format(startDateChooser.getDate()); // Obtener la fecha como String
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(endDateChooser.getDate()); // Obtener la fecha como String
            String topics = topicsField.getText();

            String result = conferenceService.updateConference(selectedConferenceId, name, location, startDate, endDate, topics, organizerId);
            JOptionPane.showMessageDialog(this, result);
            clearFields(); // Limpiar campos después de actualizar
            listConferences(); // Actualizar la tabla después de la actualización
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteConference() {
        try {
            if (selectedConferenceId == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona una conferencia para eliminar.");
                return;
            }

            String result = conferenceService.deleteConference(selectedConferenceId, organizerId);
            JOptionPane.showMessageDialog(this, result);
            clearFields(); // Limpiar campos después de eliminar
            listConferences(); // Actualizar la tabla después de la eliminación
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        locationField.setText("");
        startDateChooser.setDate(null); // Limpiar el JDateChooser
        endDateChooser.setDate(null);   // Limpiar el JDateChooser
        topicsField.setText("");
        selectedConferenceId = null;  // Limpiar el ID de la conferencia seleccionada
    }
    // Evento de Cerrar Sesión

    private class LogoutButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(ConferenceFrame.this, "Sesión Cerrada");
            dispose(); // Cierra esta ventana

            // Abre la ventana de inicio de sesión (reemplaza con tu ventana de inicio)
            VtnPrincipalLogin login = new VtnPrincipalLogin();
            login.setVisible(true);
        }
    }
}
