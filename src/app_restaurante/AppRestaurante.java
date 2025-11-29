
package app_restaurante;

import app_restaurante.dao.UsuarioDAO;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class AppRestaurante extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializamos la capa de datos
            UsuarioDAO usuarioDAO = new UsuarioDAO();
        
            // Ejecutamos el DataLoader
            DataLoader dataLoader = new DataLoader(usuarioDAO);
            dataLoader.cargarDatosIniciales();
            
            
            Parent root = FXMLLoader.load(getClass().getResource("/app_restaurante/vista/pantallaLogin.fxml"));
            
            // Cargo el scene
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("Mesón Fernández");
            // Seteo la scene y la muestro
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
