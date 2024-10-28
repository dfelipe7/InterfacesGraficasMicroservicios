package vistasSesion;


import javax.swing.*;
import java.awt.*;

public class LoginFrame {

    public static void main(String[] args) {

//        seleccionarLookAndField();

        SwingUtilities.invokeLater(() -> {
            VtnPrincipalLogin login = new VtnPrincipalLogin();
            login.setVisible(true);
        });
    }

//    private static void seleccionarLookAndField() {
//        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
//            if ("Nimbus".equals(laf.getName()))
//                try {
//                UIManager.setLookAndFeel(laf.getClassName());
//            } catch (Exception ex) {
//            }
//        }
//    }
}
