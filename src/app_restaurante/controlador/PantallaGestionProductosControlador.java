package app_restaurante.controlador;

import app_restaurante.dao.ProductoCartaDAO;
import app_restaurante.modelo.CategoriaCarta;
import app_restaurante.modelo.ProductoCarta;
import app_restaurante.modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaGestionProductosControlador implements Initializable {

    // ELEMENTOS FXML
    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private Label labelError;

    // Tabla y Columnas (Tipadas con ProductoCarta)
    @FXML
    private TableView<ProductoCarta> tablaGestionDeProductos;
    @FXML
    private TableColumn<ProductoCarta, String> columnaNombre;
    @FXML
    private TableColumn<ProductoCarta, Double> columnaPrecio;
    @FXML
    private TableColumn<ProductoCarta, CategoriaCarta> columnaCategoriaProducto;

    // Campos
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldPrecio;
    @FXML
    private ComboBox<CategoriaCarta> comboboxCategoriaCarta;

    // Botones
    @FXML
    private Button botonGuardar;
    @FXML
    private Button botonActualizar;
    @FXML
    private Button botonLimpiar;
    @FXML
    private Button botonEliminar;

    // Variables esenciales
    private ProductoCartaDAO productoCartaDAO;
    private Usuario administradorLogueado;
    private ProductoCarta productoSeleccionado;
    private ObservableList<ProductoCarta> listaProductosObservable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();

        // 1. Instanciamos DAO
        productoCartaDAO = new ProductoCartaDAO();

        // 2. Cargamos ComboBox con el Enum CategoriaCarta
        comboboxCategoriaCarta.setItems(FXCollections.observableArrayList(CategoriaCarta.values()));

        // 3. Configuramos la tabla
        configurarTabla();

        // 4. Cargamos datos
        cargarProductos();

        // 5. Limpiamos inicio
        limpiarCampos(null);
    }

    // Cogemos el usuairo logueado
    public void setUsuarioLogueado(Usuario usuario) {
        this.administradorLogueado = usuario;
    }

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));
        Parent root = loader.load();

        PantallaPrincipalControlador controlador = loader.getController();
        if (administradorLogueado != null) {
            controlador.setUsuarioActual(administradorLogueado);
        }

        Scene escena = new Scene(root);
        Stage stage = (Stage) iconoHome.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");
    }

    // --- LÓGICA DE TABLA ---
    private void configurarTabla() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // Listener de selección
        tablaGestionDeProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });
    }

    private void cargarProductos() {
        List<ProductoCarta> lista = productoCartaDAO.obtenerProductos();
        listaProductosObservable = FXCollections.observableArrayList(lista);
        tablaGestionDeProductos.setItems(listaProductosObservable);
        tablaGestionDeProductos.refresh();
    }

    private void cargarDatosEnFormulario(ProductoCarta prod) {
        limpiarError();
        productoSeleccionado = prod;

        textFieldNombre.setText(prod.getNombre());
        textFieldPrecio.setText(String.valueOf(prod.getPrecio()));
        comboboxCategoriaCarta.setValue(prod.getCategoria());

        configurarBotones(false, true, true, true); // Ocultar Guardar
    }

    // OPERACIONES CRUD 
    @FXML
    private void guardarProductoCarta(MouseEvent event) {
        limpiarError();
        if (!validarCampos()) return;

        try {
            double precio = Double.parseDouble(textFieldPrecio.getText().replace(",", "."));

            ProductoCarta nuevo = new ProductoCarta(
                textFieldNombre.getText().trim(),
                precio,
                comboboxCategoriaCarta.getValue()
            );

            productoCartaDAO.guardarProducto(nuevo);
            
            mostrarMensajeExito("Producto añadido a la carta.");
            cargarProductos();
            limpiarCampos(null);

        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido.");
        }
    }

    @FXML
    private void actualizarProductoCarta(MouseEvent event) {
        limpiarError();
        if (productoSeleccionado == null) return;
        if (!validarCampos()) return;

        try {
            double precio = Double.parseDouble(textFieldPrecio.getText().replace(",", "."));

            productoSeleccionado.setNombre(textFieldNombre.getText().trim());
            productoSeleccionado.setPrecio(precio);
            productoSeleccionado.setCategoria(comboboxCategoriaCarta.getValue());

            productoCartaDAO.actualizarProducto(productoSeleccionado);
            
            mostrarMensajeExito("Producto de la carta actualizado.");
            cargarProductos();
            limpiarCampos(null);

        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido.");
        }
    }

    @FXML
    private void eliminarProductoCarta(MouseEvent event) {
        limpiarError();
        if (productoSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar de la Carta");
        alert.setHeaderText("¿Eliminar " + productoSeleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productoCartaDAO.eliminarProducto(productoSeleccionado);
            
            cargarProductos();
            limpiarCampos(null);
        }
    }

    @FXML
    private void limpiarCampos(MouseEvent event) {
        limpiarError();
        textFieldNombre.clear();
        textFieldPrecio.clear();
        comboboxCategoriaCarta.setValue(null);

        productoSeleccionado = null;
        tablaGestionDeProductos.getSelectionModel().clearSelection();
        configurarBotones(true, false, false, true);
    }

    // Métodos adicionales
    private boolean validarCampos() {
        if (textFieldNombre.getText().trim().isEmpty() || 
            textFieldPrecio.getText().trim().isEmpty() ||
            comboboxCategoriaCarta.getValue() == null) {
            
            mostrarError("Por favor, rellena todos los campos.");
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        labelError.setText(mensaje);
        labelError.setVisible(true);
    }

    private void limpiarError() {
        labelError.setText("");
        labelError.setVisible(false);
    }
    
    private void mostrarMensajeExito(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void configurarBotones(boolean guardar, boolean actualizar, boolean eliminar, boolean limpiar) {
        botonGuardar.setVisible(guardar);   botonGuardar.setManaged(guardar);
        botonActualizar.setVisible(actualizar); botonActualizar.setManaged(actualizar);
        botonEliminar.setVisible(eliminar); botonEliminar.setManaged(eliminar);
        botonLimpiar.setVisible(limpiar);
    }

    public void inicializarFechayHora(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime dateTimeActual = LocalDateTime.now();
            labelHora.setText(formatoHora.format(dateTimeActual));
            labelFecha.setText(formatoFecha.format(dateTimeActual));
        }));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }
}