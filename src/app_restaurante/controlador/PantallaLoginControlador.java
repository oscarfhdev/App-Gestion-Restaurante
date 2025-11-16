package app_restaurante.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


public class PantallaLoginControlador implements Initializable {

    @FXML
    private Button botonIniciarSesion;
    @FXML
    private Label labelErrorLogin;
    @FXML
    private TextField textFieldUsuario;
    @FXML
    private PasswordField textFieldContrasena;

 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void cerrarApp(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void iniciarSesion(MouseEvent event) {
    }
    
}
