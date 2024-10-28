package vistasSesion;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Felipe Armero
 */
import vistasAutor.ArticleFrame;
import vistasOrganizador.ConferenceFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        setTitle("Inicio de Sesión");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Creamos un JPanel para los campos de entrada
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        // Campos de inicio de sesión
        panel.add(new JLabel("Correo:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Botón de inicio de sesión
        loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(e -> login());
        panel.add(loginButton);

        // Botón de registro
        registerButton = new JButton("Registrarse");
        registerButton.addActionListener(e -> openRegisterFrame());
        panel.add(registerButton);

        // Aplicar borde al panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Añadir el panel al JFrame
        add(panel, BorderLayout.CENTER);
    }

private void login() {
    // Capturamos de los datos del formulario
    String email = emailField.getText();
    String password = new String(passwordField.getPassword());

    // Crea una instancia de UserService
    UserService userService = new UserService();
try {
        String response = userService.login(email, password);
        
        // Verificar el rol del usuario y guardar el ID
        String role = userService.getUserRole(email); 
        String userId = userService.getUserId(email); 
        
        if ("Organizador".equalsIgnoreCase(role)) {
           
            ConferenceFrame conferenceFrame = new ConferenceFrame(userId);
            conferenceFrame.setVisible(true);
        }else if("Autor".equalsIgnoreCase(role)){
            ArticleFrame articleFrame = new ArticleFrame(userId);
            articleFrame.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(this, "No tienes permisos para acceder a las conferencias.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void openRegisterFrame() {
        new RegisterFrame(this).setVisible(true);
        this.setVisible(false); // Ocultar la ventana de login
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
