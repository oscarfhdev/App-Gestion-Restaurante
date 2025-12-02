package app_restaurante.controlador;

import app_restaurante.modelo.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaHistorialPagosControlador implements Initializable {

    @FXML private ImageView iconoHome;
    @FXML private Label labelHora;
    @FXML private Label labelFecha;
    
    @FXML private VBox vboxHistorial; 

    private Usuario usuarioLogueado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
        cargarHistorial();
    }    

    private void cargarHistorial() {
        vboxHistorial.getChildren().clear();
        File archivo = new File("src/app_restaurante/bbdd_txt/historial_cuentas.txt");
        
        if (!archivo.exists()) {
            Label aviso = new Label("No hay registros de ventas todavía.");
            aviso.getStyleClass().add("historial-label-vacio"); // Clase CSS
            vboxHistorial.getChildren().add(aviso);
            return;
        }

        List<String> lineas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.reverse(lineas);

        for (String linea : lineas) {
            VBox tarjeta = crearTarjetaVisual(linea);
            vboxHistorial.getChildren().add(tarjeta);
        }
    }

    private VBox crearTarjetaVisual(String linea) {
        String[] partes = linea.split("\\|");
        
        VBox tarjeta = new VBox(5);
        tarjeta.setPadding(new Insets(15));
        
        // Asignamos la clase CSS de la tarjeta
        tarjeta.getStyleClass().add("historial-tarjeta-ticket");

        if (partes.length >= 5) {
            // Cabecera: Mesa y Fecha
            Label header = new Label(partes[0].trim() + "   —   " + partes[1].trim());
            header.getStyleClass().add("historial-header-ticket");

            // Productos
            Label productos = new Label(partes[2].trim());
            productos.setWrapText(true); 
            productos.getStyleClass().add("historial-productos-ticket");

            // Pago y Total
            Label total = new Label(partes[3].trim() + "   >>   " + partes[4].trim());
            total.getStyleClass().add("historial-total-ticket");

            tarjeta.getChildren().addAll(header, productos, total);
        } else {
            tarjeta.getChildren().add(new Label(linea));
        }

        return tarjeta;
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
        if (usuarioLogueado != null) controlador.setUsuarioActual(usuarioLogueado);
        
        Scene escena = new Scene(root);
        Stage stage = (Stage) iconoHome.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");
        stage.centerOnScreen();
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