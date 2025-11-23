package app_restaurante.controlador;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


public class PantallaGestionUsuariosControlador implements Initializable {

    @FXML
    private Label labelFechaHora;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
    }    

    
    public void inicializarFechayHora(){
    // Formato de fecha y hora
    DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Evento que se ejecuta cada segundo
    Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        LocalDateTime dateTimeActual = LocalDateTime.now();
        labelFechaHora.setText(formatoFecha.format(dateTimeActual) + " " + formatoHora.format(dateTimeActual));
    }));
    reloj.setCycleCount(Timeline.INDEFINITE);
    reloj.play();
    }
 
}
