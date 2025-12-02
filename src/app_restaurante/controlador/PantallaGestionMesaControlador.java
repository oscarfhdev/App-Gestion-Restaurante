package app_restaurante.controlador;

import app_restaurante.dao.ProductoCartaDAO;
import app_restaurante.modelo.CategoriaCarta;
import app_restaurante.modelo.PedidoSimpleMesa;
import app_restaurante.modelo.ProductoCarta;
import app_restaurante.modelo.Usuario;
import app_restaurante.modelo.GestorMesas;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PantallaGestionMesaControlador implements Initializable {

    @FXML private Label labelTituloMesa;
    @FXML private Label labelTotalAPagar;

    // Tabla de la carta (derecha)
    @FXML private TableView<ProductoCarta> tablaProductosDisponibles;
    @FXML private TableColumn<ProductoCarta, String> columnaNombreProductoDisponible;
    
    // Asegúrate de que este ID está igual en el FXML
    @FXML private ComboBox<CategoriaCarta> comboboxCategoriaProductosCartaDisponibles;

    // Tabla del pedido (izquierda)
    @FXML private TableView<PedidoSimpleMesa> tablaProductosMesa;
    @FXML private TableColumn<PedidoSimpleMesa, String> columnaNombre;
    @FXML private TableColumn<PedidoSimpleMesa, Double> columnaPrecio;
    @FXML private TableColumn<PedidoSimpleMesa, Integer> columnaCantidad;

    @FXML private Button botonCobrarMesaCompleta;
    @FXML private Button botonCobrarMesaPorSeparado;
    @FXML private Button botonAnadirProducto;
    @FXML private Button botonGuardar;
    @FXML private Button botonEliminarProducto;

    private int numeroMesaActual;
    private Usuario usuarioLogueado;
    private ProductoCartaDAO productoCartaDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoCartaDAO = new ProductoCartaDAO();

        // Configuración columna nombre carta
        columnaNombreProductoDisponible.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Configuración columnas pedido (usando lambdas para acceder a propiedades anidadas)
        columnaNombre.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        
        columnaPrecio.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getTotalPedidosSimples()));
            
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Cargar categorías en el combo
        if (comboboxCategoriaProductosCartaDisponibles != null) {
            comboboxCategoriaProductosCartaDisponibles.setItems(FXCollections.observableArrayList(CategoriaCarta.values()));
            comboboxCategoriaProductosCartaDisponibles.setOnAction(e -> filtrarCarta());
        }

        cargarCartaCompleta();
    }    

    // Recibe el número de mesa y usuario desde la ventana anterior
    public void setDatosMesa(int numMesa, Usuario usuario) {
        this.numeroMesaActual = numMesa;
        this.usuarioLogueado = usuario;
        
        labelTituloMesa.setText("Gestión Mesa " + numMesa);
        refrescarTablaPedido();
    }

    // Carga todos los productos en la tabla derecha
    private void cargarCartaCompleta() {
        if (tablaProductosDisponibles != null) {
            tablaProductosDisponibles.setItems(FXCollections.observableArrayList(productoCartaDAO.obtenerProductos()));
        }
    }

    // Filtra la carta según la categoría seleccionada
    private void filtrarCarta() {
        CategoriaCarta seleccion = comboboxCategoriaProductosCartaDisponibles.getValue();
        if (seleccion == null) {
            cargarCartaCompleta();
            return;
        }
        List<ProductoCarta> filtrados = productoCartaDAO.obtenerProductos().stream()
                .filter(p -> p.getCategoria() == seleccion)
                .collect(Collectors.toList());
        
        tablaProductosDisponibles.setItems(FXCollections.observableArrayList(filtrados));
    }

    // Actualiza la tabla izquierda con los datos de GestorMesas
    private void refrescarTablaPedido() {
        List<PedidoSimpleMesa> pedidos = GestorMesas.getPedidosMesa(numeroMesaActual);
        
        if (tablaProductosMesa != null) {
            tablaProductosMesa.setItems(FXCollections.observableArrayList(pedidos));
            tablaProductosMesa.refresh();
        }
        
        // Calcular total y mostrar en el label
        double total = GestorMesas.calcularTotalMesa(numeroMesaActual);
        if (labelTotalAPagar != null) {
            labelTotalAPagar.setText(String.format("Total: %.2f €", total));
        }
    }

    @FXML
    private void anadirProductoAMesa(MouseEvent event) {
        ProductoCarta seleccionado = tablaProductosDisponibles.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la carta (derecha).");
            return;
        }

        // Pedir cantidad al usuario
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Añadir " + seleccionado.getNombre());
        dialog.setContentText("Unidades:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cantidadStr -> {
            try {
                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad > 0) {
                    GestorMesas.agregarProducto(numeroMesaActual, seleccionado, cantidad);
                    refrescarTablaPedido();
                } else {
                    mostrarAlerta("La cantidad debe ser mayor que 0.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Número inválido.");
            }
        });
    }

    @FXML
    private void eliminarProductoAMesa(MouseEvent event) {
        PedidoSimpleMesa seleccionado = tablaProductosMesa.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            GestorMesas.eliminarProducto(numeroMesaActual, seleccionado);
            refrescarTablaPedido();
        } else {
            mostrarAlerta("Selecciona una línea del pedido para borrar.");
        }
    }

    @FXML
    private void cobrarMesaCompleta(MouseEvent event) {
        double total = GestorMesas.calcularTotalMesa(numeroMesaActual);
        if (total == 0) {
            mostrarAlerta("Mesa vacía. No se puede cobrar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cobrar Mesa");
        alert.setHeaderText("Total a cobrar: " + String.format("%.2f €", total));
        alert.setContentText("¿Confirmar pago y cerrar mesa?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            generarTicketArchivo(total);
            GestorMesas.limpiarMesa(numeroMesaActual);
            cerrarVentana();
        }
    }
    
    // Guarda el ticket en historial_cuentas.txt
    private void generarTicketArchivo(double total) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String linea = "TICKET MESA " + numeroMesaActual + " | " + fecha + " | TOTAL: " + String.format("%.2f", total) + "€";
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/app_restaurante/bbdd_txt/historial_cuentas.txt", true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cobrarMesaPorSeparado(MouseEvent event) {
        mostrarAlerta("Funcionalidad en desarrollo.");
    }

    @FXML
    private void irAGestiondeComandas(MouseEvent event) {
        cerrarVentana();
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) labelTituloMesa.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}