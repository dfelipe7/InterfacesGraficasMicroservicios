/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Unicauca
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;
    private LoginFrame loginFrame; // Para regresar a la ventana de inicio de sesión

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame; // Guardar la referencia de la ventana de login
        setTitle("Registro de Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear un JPanel para los campos de entrada
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Nombre:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Correo:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Rol:"));
        String[] roles = {"Organizador", "Autor", "Evaluador"};
        roleComboBox = new JComboBox<>(roles);
        panel.add(roleComboBox);

        // Botón de registro
        registerButton = new JButton("Registrarse");
        registerButton.addActionListener(e -> register());
        panel.add(registerButton);

        // Botón de regreso
        backButton = new JButton("Volver");
        backButton.addActionListener(e -> goBackToLogin());
        panel.add(backButton);

        // Aplicar borde al panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Añadir el panel al JFrame
        add(panel, BorderLayout.CENTER);
    }

    private void register() {
    // Captura de los datos del formulario
    String name = nameField.getText();
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());
    String role = (String) roleComboBox.getSelectedItem();

    // Crea una instancia de UserService
    UserService userService = new UserService();

    try {
        // Llama al método de registro de UserService
        String response = userService.registerUser(name, email, password, role);

        // Muestra el resultado del registro
        JOptionPane.showMessageDialog(this, response);
        
        // Aquí puedes agregar lógica adicional si el registro es exitoso, como cerrar la ventana de registro o limpiar campos

    } catch (Exception e) {
        // Muestra un mensaje de error en caso de que algo falle
        JOptionPane.showMessageDialog(this, "Error al registrarse: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void goBackToLogin() {
        this.dispose(); // Cerrar ventana de registro
        loginFrame.setVisible(true); // Mostrar ventana de inicio de sesión
    }
}
