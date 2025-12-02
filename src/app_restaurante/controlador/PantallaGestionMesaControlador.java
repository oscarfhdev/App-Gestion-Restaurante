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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceDialog;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;

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

        // 1. Preguntar Método de Pago
        List<String> opciones = List.of("Tarjeta", "Efectivo");
        ChoiceDialog<String> dialogoPago = new ChoiceDialog<>("Tarjeta", opciones);
        dialogoPago.setTitle("Cobrar Mesa");
        dialogoPago.setHeaderText("Total a pagar: " + String.format("%.2f €", total));
        dialogoPago.setContentText("Seleccione método de pago:");

        Optional<String> metodo = dialogoPago.showAndWait();
        
        if (metodo.isPresent()) {
            if (metodo.get().equals("Tarjeta")) {
                // Pago con Tarjeta directo
                finalizarCobro(total, total, 0.0, "TARJETA");
                
            } else {
                // Pago en Efectivo: Pedir cantidad entregada
                TextInputDialog dialogoEfectivo = new TextInputDialog();
                dialogoEfectivo.setTitle("Pago en Efectivo");
                dialogoEfectivo.setHeaderText("Total: " + String.format("%.2f €", total));
                dialogoEfectivo.setContentText("Importe entregado por el cliente (€):");

                Optional<String> dineroEntregadoStr = dialogoEfectivo.showAndWait();
                
                dineroEntregadoStr.ifPresent(dinero -> {
                    try {
                        double entregado = Double.parseDouble(dinero.replace(",", "."));
                        if (entregado < total) {
                            mostrarAlerta("Error: El importe entregado es menor que el total.");
                        } else {
                            double cambio = entregado - total;
                            // Mostrar Cambio al camarero
                            Alert infoCambio = new Alert(Alert.AlertType.INFORMATION);
                            infoCambio.setTitle("Cambio");
                            infoCambio.setHeaderText("Operación realizada con éxito");
                            infoCambio.setContentText("ENTREGADO: " + String.format("%.2f €", entregado) + 
                                                      "\nCAMBIO A DEVOLVER: " + String.format("%.2f €", cambio));
                            infoCambio.showAndWait();
                            
                            finalizarCobro(total, entregado, cambio, "EFECTIVO");
                        }
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Importe inválido.");
                    }
                });
            }
        }
    }

    private void finalizarCobro(double total, double entregado, double cambio, String metodoPago) {
        generarTicketArchivo(total, entregado, cambio, metodoPago);
        GestorMesas.limpiarMesa(numeroMesaActual);
        cerrarVentana();
    }
    
    // Guarda el ticket con (Ej: "Croquetas(2) Agua(1)")
    private void generarTicketArchivo(double total, double entregado, double cambio, String metodoPago) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 1. Construimos el String con los productos: "Nombre(Cant) "
        StringBuilder productosStr = new StringBuilder();
        List<PedidoSimpleMesa> pedidos = GestorMesas.getPedidosMesa(numeroMesaActual);
        
        for (PedidoSimpleMesa p : pedidos) {
            productosStr.append(p.getProducto().getNombre())
                        .append("(")
                        .append(p.getCantidad())
                        .append(") ");
        }

        // 2. Detalle del pago (Efectivo con cambio o Tarjeta)
        String detallePago;
        if (metodoPago.equals("EFECTIVO")) {
            detallePago = String.format(" | PAGO: EFECTIVO (Entregado: %.2f€ - Cambio: %.2f€)", entregado, cambio);
        } else {
            detallePago = " | PAGO: TARJETA";
        }

        // 3. Línea final para el TXT
        String linea = "MESA " + numeroMesaActual + " | " + fecha + " | " + productosStr.toString() + detallePago + " | TOTAL: " + String.format("%.2f€", total);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/app_restaurante/bbdd_txt/historial_cuentas.txt", true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

@FXML
    private void cobrarMesaPorSeparado(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaGestionPagoSeparado.fxml"));
            Parent root = loader.load();
            
            PantallaGestionPagoSeparadoControlador controller = loader.getController();
            controller.setDatosMesa(numeroMesaActual); // Pasamos el nº de mesa
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Pago Separado - Mesa " + numeroMesaActual);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Al cerrar, refrescamos la tabla de la ventana padre por si se ha pagado algo
            stage.showAndWait();
            refrescarTablaPedido(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
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