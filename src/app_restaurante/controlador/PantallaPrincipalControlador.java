package app_restaurante.controlador;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PantallaPrincipalControlador implements Initializable {

    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private Label labelBienvenida;
    @FXML
    private ImageView imagenLogOut;
    @FXML
    private ImageView iconoUsuarios;
    
    // ATRIBUTO para guardar quién ha iniciado sesión
    private Usuario usuarioActual;
    @FXML
    private Pane paneGestionUsuarios;
    @FXML
    private ImageView iconoInventario;
    @FXML
    private ImageView iconoCarta;
    @FXML
    private ImageView iconoBar;
    @FXML
    private ImageView iconoProductos;
    @FXML
    private Pane paneGestionInventario;
    @FXML
    private Pane paneGestionProductos;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
    }    
    
    
    //Este es el que llamamos desde el Login
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Actualizamos el mensaje de bienvenida
        if (usuarioActual != null) {
            labelBienvenida.setText("¡Bienvenido, " + usuarioActual.getNombre() + "!");
            // si es administrador puede acceder a usuarios
            if (!usuarioActual.isAdministrador()) {
                paneGestionUsuarios.setDisable(true);
                paneGestionInventario.setDisable(true);
                paneGestionProductos.setDisable(true);
            }

        }
    }
    
    
    public void inicializarFechayHora(){
        // Formato de fecha y hora
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Evento que se ejecuta cada segundo
        Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime dateTimeActual = LocalDateTime.now();
            labelHora.setText(formatoHora.format(dateTimeActual));
            labelFecha.setText(formatoFecha.format(dateTimeActual));
        }));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }

    @FXML
    private void irALogin(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaLogin.fxml"));

        Parent root = loader.load();
        Scene escena = new Scene(root);
        
        // Obtener el Stage actual desde el botón
        Stage stage = (Stage) imagenLogOut.getScene().getWindow();

        // Reemplazar la escena actual
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");  
    }

    @FXML
    private void irAGestionUsuarios(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaGestionUsuarios.fxml"));

        Parent root = loader.load();
        
        // Pasamos los datos al siguiente controlador
        PantallaGestionUsuariosControlador controlador = loader.getController();
        controlador.setUsuarioLogueado(usuarioActual); 
        
        Scene escena = new Scene(root);
        
        // Obtener el Stage actual desde el botón
        Stage stage = (Stage) iconoUsuarios.getScene().getWindow();

        // Reemplazar la escena actual
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Gestión de Usuarios");  
    }

    @FXML
    private void irAGestionInventario(MouseEvent event) {
    }

    @FXML
    private void irACarta(MouseEvent event) {
    }

    @FXML
    private void irAGestionComandas(MouseEvent event) {
    }

    @FXML
    private void irAGestionProductos(MouseEvent event) {
    }
}
