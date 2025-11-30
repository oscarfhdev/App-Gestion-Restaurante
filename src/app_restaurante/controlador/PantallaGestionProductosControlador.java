package app_restaurante.controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class PantallaGestionProductosControlador implements Initializable {

    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private TableView<?> tablaGestionDeUsuarios;
    @FXML
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TableColumn<?, ?> columnaCategoria;
    @FXML
    private TableColumn<?, ?> columnaStockActual;
    @FXML
    private TableColumn<?, ?> columnaStockMinimo;
    @FXML
    private TableColumn<?, ?> columnaUnidad;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textStockActual;
    @FXML
    private TextField textFieldStockMinimo;
    @FXML
    private Button botonGuardar;
    @FXML
    private Button botonActualizar;
    @FXML
    private Button botonLimpiar1;
    @FXML
    private Button botonEliminar;
    @FXML
    private Label labelError;
    @FXML
    private ComboBox<?> comboboxCategoria;
    @FXML
    private ComboBox<?> comboboxUnidadMedida;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) {
    }

    @FXML
    private void guardarCampos(MouseEvent event) {
    }

    @FXML
    private void actualizarUsuario(MouseEvent event) {
    }

    @FXML
    private void limpiarCampos(MouseEvent event) {
    }

    @FXML
    private void eliminarUsuario(MouseEvent event) {
    }
    
}
