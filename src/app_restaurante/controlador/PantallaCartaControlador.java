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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

public class PantallaCartaControlador implements Initializable {

    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    
    // Contenedor donde inyectaremos las categorías y platos
    @FXML
    private VBox vboxContenidoCarta;

    // Variables de control
    private Usuario usuarioActual;
    private ProductoCartaDAO productoCartaDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
        
        productoCartaDAO = new ProductoCartaDAO();
        
        // Se crea la carta
        cargarCartaVisualmente();
    }    

    // --- LÓGICA para crear la carta
    private void cargarCartaVisualmente() {
        vboxContenidoCarta.getChildren().clear();
        
        List<ProductoCarta> listaProductos = productoCartaDAO.obtenerProductos();
        
        Map<CategoriaCarta, List<ProductoCarta>> mapaProductos = 
                listaProductos.stream().collect(Collectors.groupingBy(ProductoCarta::getCategoria));

        for (CategoriaCarta categoria : CategoriaCarta.values()) {
            
            if (mapaProductos.containsKey(categoria)) {
                List<ProductoCarta> productosDeEstaCategoria = mapaProductos.get(categoria);
                
                // A. Título de Categoría
                Label titulo = new Label(categoria.toString());
                titulo.getStyleClass().add("carta-titulo-categoria"); 
                titulo.setMaxWidth(Double.MAX_VALUE);
                
                // B. Contenedor de platos para esta categoría
                VBox cajaCategoria = new VBox(10); 
                cajaCategoria.getStyleClass().add("carta-caja-categoria"); 
                
                // C. Añadir cada plato como una fila
                for (ProductoCarta prod : productosDeEstaCategoria) {
                    HBox filaPlato = crearFilaPlato(prod);
                    cajaCategoria.getChildren().add(filaPlato);
                }
                
                // D. Añadir al VBox principal
                vboxContenidoCarta.getChildren().addAll(titulo, cajaCategoria);
            }
        }
    }

    // Crea una fila estilo menú con Clases CSS
    private HBox crearFilaPlato(ProductoCarta p) {
        HBox fila = new HBox();
        fila.getStyleClass().add("carta-fila-plato"); 
        
        // 1. Nombre del plato
        Label lblNombre = new Label(p.getNombre());
        lblNombre.getStyleClass().add("carta-nombre-plato"); 
        
        // 2. Línea de puntos separadora
        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);
        separador.getStyleClass().add("carta-separador-puntos"); 

        // 3. Precio
        Label lblPrecio = new Label(String.format("%.2f €", p.getPrecio()));
        lblPrecio.getStyleClass().add("carta-precio-plato"); 
        
        fila.getChildren().addAll(lblNombre, separador, lblPrecio);
        
        return fila;
    }
   
    
    // para guardar el usuario logueado
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));
        Parent root = loader.load();

        PantallaPrincipalControlador controlador = loader.getController();
        if (usuarioActual != null) {
            controlador.setUsuarioActual(usuarioActual);
        }

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