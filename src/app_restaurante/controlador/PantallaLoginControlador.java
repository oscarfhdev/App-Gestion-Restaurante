package app_restaurante.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


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
    private void iniciarSesion(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));

        Parent root = loader.load();
        Scene escena = new Scene(root);
        
        // Obtener el Stage actual desde el botón
        Stage stage = (Stage) botonIniciarSesion.getScene().getWindow();

        // Reemplazar la escena actual
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");  
    }
    
}
