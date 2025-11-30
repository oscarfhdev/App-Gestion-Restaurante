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
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TextField textFieldNombre;
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
    private TableView<?> tablaGestionDeProductos;
    @FXML
    private TableColumn<?, ?> columnaPrecio;
    @FXML
    private TableColumn<?, ?> columnaCategoriaProducto;
    @FXML
    private TextField textFieldPrecio;
    @FXML
    private ComboBox<?> comboboxCategoriaProducto;


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
