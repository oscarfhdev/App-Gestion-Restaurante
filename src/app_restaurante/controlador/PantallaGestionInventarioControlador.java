package app_restaurante.controlador;

import app_restaurante.dao.InventarioDAO;
import app_restaurante.modelo.CategoriaInventario;
import app_restaurante.modelo.ProductoInventario;
import app_restaurante.modelo.UnidadMedida;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaGestionInventarioControlador implements Initializable {

    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private Label labelError;
    @FXML
    private TableView<ProductoInventario> tablaGestionDeInventario;
    @FXML
    private TableColumn<ProductoInventario, String> columnaNombre;
    @FXML
    private TableColumn<ProductoInventario, CategoriaInventario> columnaCategoriaInventario;
    @FXML
    private TableColumn<ProductoInventario, Double> columnaStockActual;
    @FXML
    private TableColumn<ProductoInventario, Double> columnaStockMinimo;
    @FXML
    private TableColumn<ProductoInventario, UnidadMedida> columnaUnidad;

    // Campos
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldStockActual;
    @FXML
    private TextField textFieldStockMinimo;
    @FXML
    private ComboBox<CategoriaInventario> comboboxCategoria;
    @FXML
    private ComboBox<UnidadMedida> comboboxUnidadMedida;

    // Botones
    @FXML
    private Button botonGuardar;
    @FXML
    private Button botonActualizar;
    @FXML
    private Button botonLimpiar1;
    @FXML
    private Button botonEliminar;

    // Variables de control
    private InventarioDAO inventarioDAO;
    private Usuario administradorLogueado;
    private ProductoInventario productoSeleccionado;
    private ObservableList<ProductoInventario> listaProductosObservable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();

        // 1. Instanciamos DAO
        inventarioDAO = new InventarioDAO();

        // 2. Cargamos los comoboxs con los Enums
        comboboxCategoria.setItems(FXCollections.observableArrayList(CategoriaInventario.values()));
        comboboxUnidadMedida.setItems(FXCollections.observableArrayList(UnidadMedida.values()));

        // 3. Configuramos la tabla
        configurarTabla();

        // 4. Cargamos los datos
        cargarProductos();

        // 5. Limpiamos al inicio
        limpiarCampos(null);
    }

    
    // ajustamos la sesion, se llama desde el otor lado
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
        columnaCategoriaInventario.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnaStockActual.setCellValueFactory(new PropertyValueFactory<>("cantidadActual"));
        columnaStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        columnaUnidad.setCellValueFactory(new PropertyValueFactory<>("unidadMedida"));

        // Listener de selección
        tablaGestionDeInventario.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });

        
        // Configuramos cómo debe comportarse cada fila de la tabla, esto es para la alerta de stock
        tablaGestionDeInventario.setRowFactory(tv -> new TableRow<ProductoInventario>() {
            @Override
            protected void updateItem(ProductoInventario item, boolean empty) {
                super.updateItem(item, empty); // Mantiene el comportamiento base

                // Quitamos el rojo siempre al principio porque la tabla recicla las filas al hacer scroll
                getStyleClass().remove("filaAlertaStock"); 

                // Solo entramos si la fila tiene un producto real y no está vacía
                if (item != null && !empty) {

                    // Preguntamos al producto si su cantidad es menor al mínimo
                    if (item.estaBajoStock()) {

                        // Si falta stock y la fila aún no tiene la clase roja...
                        if (!getStyleClass().contains("filaAlertaStock")) {
                            getStyleClass().add("filaAlertaStock"); // ...le añadimos el estilo rojo
                        }
                    }
                }
            }
        });
    }

    private void cargarProductos() {
        List<ProductoInventario> lista = inventarioDAO.obtenerProductos();
        listaProductosObservable = FXCollections.observableArrayList(lista);
        tablaGestionDeInventario.setItems(listaProductosObservable);
        tablaGestionDeInventario.refresh();
    }

    private void cargarDatosEnFormulario(ProductoInventario prod) {
        limpiarError();
        productoSeleccionado = prod;

        textFieldNombre.setText(prod.getNombre());
        textFieldStockActual.setText(String.valueOf(prod.getCantidadActual()));
        textFieldStockMinimo.setText(String.valueOf(prod.getStockMinimo()));
        comboboxCategoria.setValue(prod.getCategoria());
        comboboxUnidadMedida.setValue(prod.getUnidadMedida());

        configurarBotones(false, true, true, true);
    }

    
    // --- OPERACIONES CRUD ---
    @FXML
    private void guardarProductoInventario(MouseEvent event) {
        limpiarError();
        if (!validarCampos()) return;

        try {
            // Conversión de números (acepta comas y puntos)
            double stockActual = Double.parseDouble(textFieldStockActual.getText().replace(",", "."));
            double stockMinimo = Double.parseDouble(textFieldStockMinimo.getText().replace(",", "."));

            ProductoInventario nuevo = new ProductoInventario(
                textFieldNombre.getText().trim(),
                comboboxCategoria.getValue(),
                stockActual,
                comboboxUnidadMedida.getValue(),
                stockMinimo
            );

            inventarioDAO.guardarProducto(nuevo);
            
            mostrarMensajeExito("Producto añadido correctamente.");
            cargarProductos();
            limpiarCampos(null);

        } catch (NumberFormatException e) {
            mostrarError("El stock debe ser un número válido.");
        }
    }

    @FXML
    private void actualizarProductoInventario(MouseEvent event) {
        limpiarError();
        if (productoSeleccionado == null) return;
        if (!validarCampos()) return;

        try {
            double stockActual = Double.parseDouble(textFieldStockActual.getText().replace(",", "."));
            double stockMinimo = Double.parseDouble(textFieldStockMinimo.getText().replace(",", "."));

            productoSeleccionado.setNombre(textFieldNombre.getText().trim());
            productoSeleccionado.setCategoria(comboboxCategoria.getValue());
            productoSeleccionado.setCantidadActual(stockActual);
            productoSeleccionado.setStockMinimo(stockMinimo);
            productoSeleccionado.setUnidadMedida(comboboxUnidadMedida.getValue());

            inventarioDAO.actualizarProducto(productoSeleccionado);
            
            mostrarMensajeExito("Producto actualizado correctamente.");
            cargarProductos();
            limpiarCampos(null);

        } catch (NumberFormatException e) {
            mostrarError("El stock debe ser un número válido.");
        }
    }

    @FXML
    private void eliminarProductoInventario(MouseEvent event) {
        limpiarError();
        if (productoSeleccionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Producto");
        alert.setHeaderText("¿Eliminar " + productoSeleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            inventarioDAO.eliminarProducto(productoSeleccionado);
            
            cargarProductos();
            limpiarCampos(null);
        }
    }

    @FXML
    private void limpiarCampos(MouseEvent event) {
        limpiarError();
        textFieldNombre.clear();
        textFieldStockActual.clear();
        textFieldStockMinimo.clear();
        comboboxCategoria.setValue(null);
        comboboxUnidadMedida.setValue(null);

        productoSeleccionado = null;
        tablaGestionDeInventario.getSelectionModel().clearSelection();
        configurarBotones(true, false, false, true);
    }

    // --- UTILIDADES ---

    private boolean validarCampos() {
        if (textFieldNombre.getText().trim().isEmpty() || 
            textFieldStockActual.getText().trim().isEmpty() ||
            textFieldStockMinimo.getText().trim().isEmpty() ||
            comboboxCategoria.getValue() == null ||
            comboboxUnidadMedida.getValue() == null) {
            
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
        botonLimpiar1.setVisible(limpiar);
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