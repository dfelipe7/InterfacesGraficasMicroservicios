package vistasAutor;


import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import javax.swing.table.TableCellRenderer;

public class ArticleFrame extends JFrame {

    private JTextField titleField;
    private JTextArea abstractField;
    private JTextField keywordsField;
    private JComboBox<String> conferenceComboBox;
    private ArticleService articleService;
    private String autorId;
    private String selectedFile;
    private JTable articleTable;
    private DefaultTableModel tableModel;
    private JTextField fileNameField;  // Cuadro de texto para mostrar el nombre del archivo

    public ArticleFrame(String autorId) {
        this.autorId = autorId;
        articleService = new ArticleService();
        setTitle("Gestión de Artículos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de entrada
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Resumen:"), gbc);
        gbc.gridx = 1;
        abstractField = new JTextArea(5, 20);
        inputPanel.add(new JScrollPane(abstractField), gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Palabras Clave:"), gbc);
        gbc.gridx = 1;
        keywordsField = new JTextField(20);
        inputPanel.add(keywordsField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Seleccionar Conferencia:"), gbc);
        gbc.gridx = 1;
        conferenceComboBox = new JComboBox<>();
        inputPanel.add(conferenceComboBox, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        JButton fileButton = new JButton("Seleccionar Archivo");
        fileButton.addActionListener(e -> selectFile());
        inputPanel.add(fileButton, gbc);

        // Cuadro de texto deshabilitado para mostrar el nombre del archivo
        gbc.gridx = 1;
        fileNameField = new JTextField(20);
        fileNameField.setEditable(false);  // Hacer el campo no editable
        inputPanel.add(fileNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        JButton createButton = new JButton("Crear Artículo");
        createButton.addActionListener(e -> createArticle());
        inputPanel.add(createButton, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton loadConferenceButton = new JButton("Cargar Conferencias");
        loadConferenceButton.addActionListener(e -> loadConferences());
        inputPanel.add(loadConferenceButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Crear tabla para listar artículos
        String[] columnNames = {"ID", "Título", "Resumen", "Palabras Clave", "Archivo", "Autor ID", "PDF"};
        tableModel = new DefaultTableModel(columnNames, 0);
        articleTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(articleTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones de acción
        JPanel actionPanel = new JPanel();
        JButton listArticlesButton = new JButton("Listar Artículos");
        listArticlesButton.addActionListener(e -> loadArticles());
        actionPanel.add(listArticlesButton);

        JButton updateButton = new JButton("Actualizar Artículo");
        updateButton.addActionListener(e -> updateArticle());
        actionPanel.add(updateButton);

        JButton deleteButton = new JButton("Eliminar Artículo");
        deleteButton.addActionListener(e -> deleteArticle());
        actionPanel.add(deleteButton);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Documentos PDF", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
            fileNameField.setText(selectedFile);  // Mostrar el nombre del archivo en el cuadro de texto
            JOptionPane.showMessageDialog(this, "Archivo seleccionado: " + selectedFile);
        }
    }

    private void createArticle() {
        try {
            String title = titleField.getText().trim();
            String abstractText = abstractField.getText().trim();
            String keywords = keywordsField.getText().trim();

            if (title.isEmpty() || abstractText.isEmpty() || keywords.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
                return;
            }

            String selectedConference = (String) conferenceComboBox.getSelectedItem();
            if (selectedConference == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona una conferencia para asociar el artículo.");
                return;
            }

            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un archivo PDF para subir.");
                return;
            }

            String selectedConferenceId = selectedConference.split(" - ")[0];
            String result = articleService.createArticle(title, abstractText, keywords, selectedConferenceId, autorId, selectedFile);
            JOptionPane.showMessageDialog(this, result);
            clearFields();
            loadArticles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al crear el artículo: " + e.getMessage());
        }
    }

    private void loadConferences() {
        try {
            String[][] conferences = articleService.getConferences();
            conferenceComboBox.removeAllItems();

            // Cargar cada conferencia en el JComboBox una por una
            for (String[] conference : conferences) {
                String conferenceEntry = conference[0] + " - " + conference[1];
                conferenceComboBox.addItem(conferenceEntry);
                // break; 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadArticles() {
        try {
            String[][] articles = articleService.getArticles();
            tableModel.setRowCount(0);

            for (String[] article : articles) {
                Object[] rowData = {
                    article[0],
                    article[1],
                    article[2],
                    article[3],
                    article[4],
                    article[5],
                    "Abrir PDF" // Texto del botón en la última columna
                };
                tableModel.addRow(rowData);
            }

            // Agregar un botón en la columna de "Abrir PDF"
            articleTable.getColumnModel().getColumn(6).setCellRenderer((TableCellRenderer) new ButtonRenderer());
            articleTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los artículos: " + e.getMessage());
        }
    }

    // Renderer para mostrar el botón en la celda
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setText("Abrir PDF");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    // Editor para manejar el clic del botón
    class ButtonEditor extends DefaultCellEditor {

        private String pdfPath;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            JButton button = new JButton("Abrir PDF");
            button.addActionListener(e -> openPdf(pdfPath)); // Llama al método de abrir PDF
            this.editorComponent = button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            pdfPath = (String) table.getModel().getValueAt(row, 4); // Obtiene la ruta del PDF de la columna 4
            return (Component) editorComponent;
        }

        @Override
        public Object getCellEditorValue() {
            return pdfPath;
        }
    }

    private void fillFieldsWithSelectedArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow != -1) {
            titleField.setText((String) tableModel.getValueAt(selectedRow, 1));
            abstractField.setText((String) tableModel.getValueAt(selectedRow, 2));
            keywordsField.setText((String) tableModel.getValueAt(selectedRow, 3));
            fileNameField.setText((String) tableModel.getValueAt(selectedRow, 4));
        }
    }

    private void updateArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un artículo para actualizar.");
            return;
        }

        try {
            String articleId = (String) tableModel.getValueAt(selectedRow, 0);
            String title = titleField.getText().trim();
            String abstractText = abstractField.getText().trim();
            String keywords = keywordsField.getText().trim();
            String pdfFilePath = selectedFile;

            if (title.isEmpty() || abstractText.isEmpty() || keywords.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
                return;
            }

            String result = articleService.updateArticle(Long.parseLong(articleId), title, abstractText, keywords, pdfFilePath, autorId);
            JOptionPane.showMessageDialog(this, result);
            loadArticles();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el artículo: " + e.getMessage());
        }
    }

    private void deleteArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un artículo para eliminar.");
            return;
        }

        try {
            String articleId = (String) tableModel.getValueAt(selectedRow, 0);
            String result = articleService.deleteArticle(Long.parseLong(articleId), autorId);
            JOptionPane.showMessageDialog(this, result);
            loadArticles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el artículo: " + e.getMessage());
        }
    }

    private void openPdf(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(this, "El archivo no existe: " + pdfPath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir el PDF: " + e.getMessage());
        }
    }

    private void clearFields() {
        titleField.setText("");
        abstractField.setText("");
        keywordsField.setText("");
        conferenceComboBox.setSelectedIndex(-1);
        fileNameField.setText("");
        selectedFile = null;
    }

}
