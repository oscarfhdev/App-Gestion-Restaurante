package app_restaurante.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class PantallaCartaControlador implements Initializable {

    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) {
    }
    
}
