package app_restaurante.controlador;

import app_restaurante.dao.UsuarioDAO;
import app_restaurante.modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class PantallaLoginControlador implements Initializable {

    @FXML
    private Button botonIniciarSesion;
    @FXML
    private Label labelErrorLogin;
    @FXML
    private TextField textFieldUsuario;
    @FXML
    private PasswordField textFieldContrasena;

 
    // Instanciamos el DAO para poder buscar en el archivo
    private UsuarioDAO usuarioDAO;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();  
    }    
    
    @FXML
    private void cerrarApp(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    private void iniciarSesion(MouseEvent event) throws IOException {
        String usuarioIngresado = textFieldUsuario.getText().trim();
        String passwordIngresado = textFieldContrasena.getText().trim();

        // Validaciones básicas de campos vacíos
        if (usuarioIngresado.isEmpty() || passwordIngresado.isEmpty()) {
            mostrarError("Por favor, rellena todos los campos.");
            return;
        }

        // Obtener lista de usuarios desde el archivo
        List<Usuario> listaUsuarios = usuarioDAO.obtenerUsuarios();
        boolean loginExitoso = false;
        Usuario usuarioLogueado = null;

        // 3. Buscar coincidencia
        for (Usuario u : listaUsuarios) {
            // Comprobamos usuario Y contraseña (Case sensitive para contraseña)
            if (u.getUsuario().equals(usuarioIngresado) && u.getContrasena().equals(passwordIngresado)) {
                
                // Verificamos si el usuario está habilitado
                if (u.isUsuarioHabilitado()) {
                    loginExitoso = true;
                    usuarioLogueado = u; 
                } else {
                    mostrarError("Este usuario ha sido deshabilitado.");
                    return; // Salimos aquí porque las credenciales eran correctas pero no tiene permiso
                }
                break; // Encontramos al usuario, dejamos de buscar
            }
        }

        // 5. Decidir acción
        if (loginExitoso) {
            System.out.println("Login correcto: " + usuarioLogueado.getNombre());
            irAPantallaPrincipal(usuarioLogueado); // Pasamos el usuario a la siguiente pantalla
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }
    
    // Método para mostrar errores
    private void mostrarError(String mensaje) {
        labelErrorLogin.setText(mensaje);
    }

    
    private void irAPantallaPrincipal(Usuario usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));
        
        Parent root = loader.load();
        
        // Pasamos los datos al siguiente controlador
        PantallaPrincipalControlador controlador = loader.getController();
        controlador.setUsuarioActual(usuario); 

        Scene escena = new Scene(root);
        
        // Obtener el Stage actual desde el botón
        Stage stage = (Stage) botonIniciarSesion.getScene().getWindow();

        // Reemplazar la escena actual
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");  
        stage.centerOnScreen(); // Opcional: centrar la ventana
    }
}
