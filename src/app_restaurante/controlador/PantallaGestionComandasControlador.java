
package app_restaurante.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


public class PantallaGestionComandasControlador implements Initializable {

    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private Pane paneContenedorMesas;
    @FXML
    private Button botonCobrarComandasIndividuales;
    @FXML
    private TextField textFieldNumeroDeMesas;
    @FXML
    private Button botonConfigurarMesas;
    @FXML
    private Button botonHistorialCuentasPagadas;
    @FXML
    private Label labelNumeroMesasLibres;
    @FXML
    private Label labelNumeroMesasOcupadas;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) {
    }

    @FXML
    private void cobrarComandaIndependiente(MouseEvent event) {
    }

    @FXML
    private void configurarMesas(MouseEvent event) {
    }
    
}
