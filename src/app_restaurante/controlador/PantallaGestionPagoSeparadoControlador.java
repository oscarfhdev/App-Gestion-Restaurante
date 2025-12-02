package app_restaurante.controlador;

import app_restaurante.modelo.GestorMesas;
import app_restaurante.modelo.PedidoSimpleMesa;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PantallaGestionPagoSeparadoControlador implements Initializable {

    @FXML private Pane panelPasarProducto;
    @FXML private Label labelTotalAPagar;
    @FXML private Button botonCobrarProductosAPagar;

    // TABLA IZQUIERDA (Original de la Mesa)
    @FXML private TableView<PedidoSimpleMesa> tablaProductosMesa;
    @FXML private TableColumn<PedidoSimpleMesa, String> columnaNombre;
    @FXML private TableColumn<PedidoSimpleMesa, Integer> columnaCantidad;
    @FXML private TableColumn<PedidoSimpleMesa, Double> columnaPrecio;

    // TABLA DERECHA (Lo que se paga ahora)
    @FXML private TableView<PedidoSimpleMesa> tablaProductosAPagar;
    @FXML private TableColumn<PedidoSimpleMesa, String> columnaNombreProductoAPagar;
    @FXML private TableColumn<PedidoSimpleMesa, Integer> columnaCantidadProductoAPagar;
    @FXML private TableColumn<PedidoSimpleMesa, Double> columnaPrecioTotalAPagar;

    private int numeroMesaActual;
    
    // Listas locales para simular el movimiento
    private ObservableList<PedidoSimpleMesa> listaMesaLocal;
    private ObservableList<PedidoSimpleMesa> listaPagoLocal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaPagoLocal = FXCollections.observableArrayList();

        // Configurar Columnas Izquierda
        columnaNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaPrecio.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalPedidosSimples()));

        // Configurar Columnas Derecha
        columnaNombreProductoAPagar.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        columnaCantidadProductoAPagar.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaPrecioTotalAPagar.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalPedidosSimples()));

        tablaProductosAPagar.setItems(listaPagoLocal);
    }    

    public void setDatosMesa(int numMesa) {
        this.numeroMesaActual = numMesa;
        cargarProductosMesa();
    }

    private void cargarProductosMesa() {
        List<PedidoSimpleMesa> original = GestorMesas.getPedidosMesa(numeroMesaActual);
        List<PedidoSimpleMesa> copia = new ArrayList<>();
        
        for (PedidoSimpleMesa p : original) {
            copia.add(new PedidoSimpleMesa(p.getProducto(), p.getCantidad()));
        }
        
        listaMesaLocal = FXCollections.observableArrayList(copia);
        tablaProductosMesa.setItems(listaMesaLocal);
    }

    @FXML
    private void moverProducto(MouseEvent event) {
        PedidoSimpleMesa seleccionado = tablaProductosMesa.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la izquierda para cobrar.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Dividir Cuenta");
        dialog.setHeaderText("Vas a cobrar: " + seleccionado.getProducto().getNombre());
        dialog.setContentText("Cantidad a pagar ahora (Máx: " + seleccionado.getCantidad() + "):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cantStr -> {
            try {
                int cantidadMover = Integer.parseInt(cantStr);
                
                if (cantidadMover > 0 && cantidadMover <= seleccionado.getCantidad()) {
                    procesarMovimiento(seleccionado, cantidadMover);
                } else {
                    mostrarAlerta("Cantidad inválida.");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Introduce un número válido.");
            }
        });
    }

    private void procesarMovimiento(PedidoSimpleMesa origen, int cantidad) {
        // 1. Restar de la izquierda
        origen.setCantidad(origen.getCantidad() - cantidad);
        if (origen.getCantidad() == 0) {
            listaMesaLocal.remove(origen);
        }
        tablaProductosMesa.refresh();

        // 2. Sumar a la derecha
        boolean encontrado = false;
        for (PedidoSimpleMesa p : listaPagoLocal) {
            if (p.getProducto().getIdProducto().equals(origen.getProducto().getIdProducto())) {
                p.setCantidad(p.getCantidad() + cantidad);
                encontrado = true;
                break;
            }
        }
        
        if (!encontrado) {
            listaPagoLocal.add(new PedidoSimpleMesa(origen.getProducto(), cantidad));
        }
        
        tablaProductosAPagar.refresh();
        actualizarTotalLabel();
    }

    private void actualizarTotalLabel() {
        double total = listaPagoLocal.stream().mapToDouble(PedidoSimpleMesa::getTotalPedidosSimples).sum();
        labelTotalAPagar.setText(String.format("Total a pagar: %.2f €", total));
    }

    @FXML
    private void cobrarProductosAPagar(MouseEvent event) {
        if (listaPagoLocal.isEmpty()) {
            mostrarAlerta("No hay productos en la lista de pago (derecha).");
            return;
        }

        double total = listaPagoLocal.stream().mapToDouble(PedidoSimpleMesa::getTotalPedidosSimples).sum();

        // Elegir método de pago
        List<String> opciones = List.of("Tarjeta", "Efectivo");
        ChoiceDialog<String> dialogo = new ChoiceDialog<>("Tarjeta", opciones);
        dialogo.setTitle("Cobro Parcial");
        dialogo.setHeaderText("Total: " + String.format("%.2f €", total));
        dialogo.setContentText("Método de pago:");

        Optional<String> metodo = dialogo.showAndWait();
        
        if (metodo.isPresent()) {
            if (metodo.get().equals("Tarjeta")) {
                // Pago directo
                finalizarCobro(total, total, 0.0, "TARJETA");
                
            } else {
                // Pago en Efectivo: Pedir dinero
                TextInputDialog dialogoEfectivo = new TextInputDialog();
                dialogoEfectivo.setTitle("Pago en Efectivo");
                dialogoEfectivo.setHeaderText("Total: " + String.format("%.2f €", total));
                dialogoEfectivo.setContentText("Importe entregado (€):");

                Optional<String> dineroStr = dialogoEfectivo.showAndWait();
                dineroStr.ifPresent(d -> {
                    try {
                        double entregado = Double.parseDouble(d.replace(",", "."));
                        if (entregado < total) {
                            mostrarAlerta("Dinero insuficiente.");
                        } else {
                            double cambio = entregado - total;
                            
                            // Mostrar la vuelta
                            Alert info = new Alert(Alert.AlertType.INFORMATION);
                            info.setTitle("Cambio");
                            info.setHeaderText("Pago correcto");
                            info.setContentText("ENTREGADO: " + String.format("%.2f€", entregado) + "\nCAMBIO: " + String.format("%.2f€", cambio));
                            info.showAndWait();
                            
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
        generarTicketParcial(total, entregado, cambio, metodoPago);
        aplicarCambiosAlGestor();
        cerrarVentana();
    }

    private void aplicarCambiosAlGestor() {
        GestorMesas.getPedidosMesa(numeroMesaActual).clear();
        for (PedidoSimpleMesa p : listaMesaLocal) {
            if (p.getCantidad() > 0) {
                GestorMesas.agregarProducto(numeroMesaActual, p.getProducto(), p.getCantidad());
            }
        }
    }

    private void generarTicketParcial(double total, double entregado, double cambio, String metodo) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Lista de productos pagados
        StringBuilder productosStr = new StringBuilder();
        for (PedidoSimpleMesa p : listaPagoLocal) {
            productosStr.append(p.getProducto().getNombre())
                        .append("(")
                        .append(p.getCantidad())
                        .append(") "); 
        }

        // Detalle pago
        String textoPago;
        if (metodo.equals("EFECTIVO")) {
            textoPago = String.format("PAGO: EFECTIVO (Entregado: %.2f€ - Cambio: %.2f€)", entregado, cambio);
        } else {
            textoPago = "PAGO: TARJETA";
        }

        String linea = "TICKET PARCIAL MESA " + numeroMesaActual + " | " + 
                       fecha + " | " + 
                       productosStr.toString().trim() + " | " + 
                       textoPago + " | " + 
                       "TOTAL: " + String.format("%.2f€", total);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/app_restaurante/bbdd_txt/historial_cuentas.txt", true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAGestiondeComandas(MouseEvent event) {
        cerrarVentana(); 
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) labelTotalAPagar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}