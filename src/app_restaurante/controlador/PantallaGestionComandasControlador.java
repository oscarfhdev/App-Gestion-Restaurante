package app_restaurante.controlador;

import app_restaurante.modelo.GestorMesas;
import app_restaurante.modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaGestionComandasControlador implements Initializable {

    @FXML private ImageView iconoHome;
    @FXML private Label labelHora;
    @FXML private Label labelFecha;
    
    @FXML private Pane paneContenedorMesas; // El padre de todas las mesas
    
    @FXML private Button botonCobrarComandasIndividuales;
    @FXML private Button botonHistorialCuentasPagadas;
    @FXML private Label labelNumeroMesasLibres;
    @FXML private Label labelNumeroMesasOcupadas;

    private Usuario usuarioLogueado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
        
        // 1. Configurar eventos y colores de las mesas AUTOMÁTICAMENTE
        configurarMesasVisualmente();
        
        // 2. Actualizar contadores
        actualizarContadores();
    }    

    // --- MAGIA: Escanear mesas y asignar clics ---
    private void configurarMesasVisualmente() {
        // Recorremos todos los hijos del panel contenedor (las 13 mesas)
        for (Node nodo : paneContenedorMesas.getChildren()) {
            if (nodo instanceof Pane) {
                Pane paneMesa = (Pane) nodo;
                
                // Intentamos averiguar el número de mesa leyendo su Label interno
                int numMesa = obtenerNumeroMesa(paneMesa);
                
                if (numMesa != -1) {
                    // A. Asignar Evento Clic: Abrir detalle
                    paneMesa.setOnMouseClicked(e -> abrirDetalleMesa(numMesa));
                    
                    // B. Pintar según estado (Verde/Amarillo)
                    actualizarColorMesa(paneMesa, numMesa);
                }
            }
        }
    }
    
    // Busca el Label dentro del Pane y extrae "Mesa X" -> X
    private int obtenerNumeroMesa(Pane paneMesa) {
        for (Node hijo : paneMesa.getChildren()) {
            if (hijo instanceof Label) {
                String texto = ((Label) hijo).getText(); // "Mesa 1"
                try {
                    // Quitamos "Mesa " y parseamos el número
                    return Integer.parseInt(texto.replace("Mesa ", "").trim());
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    private void actualizarColorMesa(Pane paneMesa, int numMesa) {
        // Limpiamos estilos anteriores
        paneMesa.getStyleClass().removeAll("paneMesaLibre", "paneMesaOcupada");
        
        if (GestorMesas.isMesaOcupada(numMesa)) {
            paneMesa.getStyleClass().add("paneMesaOcupada"); // Amarillo
        } else {
            paneMesa.getStyleClass().add("paneMesaLibre");   // Verde
        }
    }

    private void abrirDetalleMesa(int numMesa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaGestionMesa.fxml"));
            Parent root = loader.load();
            
            // Pasamos datos al controlador de la ventanita
            PantallaGestionMesaControlador controller = loader.getController();
            controller.setDatosMesa(numMesa, usuarioLogueado);
            
            // Crear ventana MODAL (bloquea la de atrás hasta que cierras)
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión Mesa " + numMesa);
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            
            // ESPERAR A QUE SE CIERRE para actualizar colores
            stage.showAndWait();
            
            // Al volver, refrescamos colores y contadores
            configurarMesasVisualmente();
            actualizarContadores();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void actualizarContadores() {
        int ocupadas = 0;
        int libres = 0;
        
        // Recorremos de nuevo para contar (un poco bruto pero seguro y rápido)
        for (Node nodo : paneContenedorMesas.getChildren()) {
            if (nodo instanceof Pane) {
                int n = obtenerNumeroMesa((Pane) nodo);
                if (n != -1) {
                    if (GestorMesas.isMesaOcupada(n)) ocupadas++;
                    else libres++;
                }
            }
        }
        
        labelNumeroMesasOcupadas.setText("Mesas Ocupadas: " + ocupadas);
        labelNumeroMesasLibres.setText("Mesas Libres: " + libres);
    }

    // --- NAVEGACIÓN ---
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));
        Parent root = loader.load();
        
        PantallaPrincipalControlador controlador = loader.getController();
        if (usuarioLogueado != null) {
            controlador.setUsuarioActual(usuarioLogueado);
        }
        
        Scene escena = new Scene(root);
        Stage stage = (Stage) iconoHome.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");
        stage.centerOnScreen();
    }

    // --- OTROS BOTONES (Pendientes) ---
    @FXML
    private void cobrarComandaIndependiente(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad no implementada en esta demo.");
        alert.showAndWait();
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