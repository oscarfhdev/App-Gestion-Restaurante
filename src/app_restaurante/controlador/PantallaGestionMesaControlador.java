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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class PantallaGestionMesaControlador implements Initializable {

    @FXML
    private Label labelTituloMesa;
    @FXML
    private ImageView iconoHome;
    @FXML
    private TableView<?> tablaProductosDisponibles;
    @FXML
    private TableColumn<?, ?> columnaNombreProductoDisponible;
    @FXML
    private Button botonCobrarMesaCompleta;
    @FXML
    private Button botonCobrarMesaPorSeparado;
    @FXML
    private Button botonAnadirProducto;
    @FXML
    private ComboBox<?> comboboxCategoriaProductosDisponibles;
    @FXML
    private Button botonGuardar;
    @FXML
    private TableView<?> tablaProductosMesa;
    @FXML
    private TableColumn<?, ?> columnaNombre;
    @FXML
    private TableColumn<?, ?> columnaPrecio;
    @FXML
    private TableColumn<?, ?> columnaCantidad;
    @FXML
    private Button botonEliminarProducto;
    @FXML
    private Label labelTotalAPagar;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    


    @FXML
    private void cobrarMesaCompleta(MouseEvent event) {
    }

    @FXML
    private void cobrarMesaPorSeparado(MouseEvent event) {
    }

    @FXML
    private void anadirProductoAMesa(MouseEvent event) {
    }
    
    @FXML
    private void eliminarProductoAMesa(MouseEvent event) {
    }

    @FXML
    private void guardarCambiosYCerrar(MouseEvent event) {
    }

    @FXML
    private void irAGestiondeComandas(MouseEvent event) {
    }
    
}
