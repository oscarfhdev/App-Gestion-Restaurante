package app_restaurante.controlador;

import app_restaurante.dao.UsuarioDAO;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaGestionUsuariosControlador implements Initializable {

    // --- ELEMENTOS FXML ---
    @FXML
    private Label labelHora;
    @FXML
    private Label labelFecha;
    @FXML
    private ImageView iconoHome;
    @FXML
    private Label labelError; // Label para mensajes en rojo

    // Tabla y Columnas (Tipadas con <Usuario>)
    @FXML
    private TableView<Usuario> tablaGestionDeUsuarios;
    @FXML
    private TableColumn<Usuario, String> columnaNombre;
    @FXML
    private TableColumn<Usuario, String> columnaApellido;
    @FXML
    private TableColumn<Usuario, String> columnaUsuario;

    // Campos
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldUsuario;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldApellido;
    @FXML
    private CheckBox checkBoxHabilitado;
    @FXML
    private CheckBox checkBoxAdministrador;

    // Botones
    @FXML
    private Button botonGuardar;
    @FXML
    private Button botonEliminar;
    @FXML
    private Button botonActualizar;
    @FXML
    private Button botonLimpiar1;

    // VARIABLES DE CONTROL 
    private UsuarioDAO usuarioDAO;
    private Usuario administradorLogueado; // El usuario que está usando la app
    private Usuario usuarioSeleccionado;   // El usuario seleccionado en la tabla
    private ObservableList<Usuario> listaUsuariosObservable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarFechayHora();
        
        // 1. Instanciamos el DAO
        usuarioDAO = new UsuarioDAO();
        
        // 2. Configuramos la tabla
        configurarTabla();
        
        // 3. Cargamos los datos iniciales
        cargarUsuarios();
        
        // 4. Dejamos la pantalla limpia
        limpiarCampos(null); 
    }    

    // Pasar el usuario entre pantallas
    public void setUsuarioLogueado(Usuario usuario) {
        this.administradorLogueado = usuario;
        System.out.println("Gestionando usuarios como: " + (usuario != null ? usuario.getUsuario() : "Desconocido"));
    }

    @FXML
    private void irAPantallaPrincipal(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app_restaurante/vista/pantallaPrincipal.fxml"));
        Parent root = loader.load();
        
        // Devolvemos el usuario a la pantalla principal
        PantallaPrincipalControlador controlador = loader.getController();
        if (administradorLogueado != null) {
            controlador.setUsuarioActual(administradorLogueado);
        }
        
        Scene escena = new Scene(root);
        Stage stage = (Stage) iconoHome.getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle("Mesón Fernández - Inicio");  
    }

    // LÓGICA DE TABLA Y CARGA DE DATOS
    private void configurarTabla() {
        // Vinculamos columnas con atributos del modelo Usuario
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        columnaUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        // Listener: Cuando detecta click en la tabla, rellenamos el formulario
        tablaGestionDeUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosEnFormulario(newSelection);
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> lista = usuarioDAO.obtenerUsuarios();
        listaUsuariosObservable = FXCollections.observableArrayList(lista);
        tablaGestionDeUsuarios.setItems(listaUsuariosObservable);
        tablaGestionDeUsuarios.refresh(); // Forzamos refresco visual
    }

    private void cargarDatosEnFormulario(Usuario usuario) {
        limpiarError(); // Quitamos errores viejos
        usuarioSeleccionado = usuario;
        
        // Rellenar campos
        textFieldNombre.setText(usuario.getNombre());
        textFieldApellido.setText(usuario.getApellido1());
        textFieldUsuario.setText(usuario.getUsuario());
        textFieldPassword.setText(usuario.getContrasena());
        checkBoxAdministrador.setSelected(usuario.isAdministrador());
        checkBoxHabilitado.setSelected(usuario.isUsuarioHabilitado());

        // Gestionar visibilidad de botones
        configurarBotones(false, true, true, true); // Ocultar Guardar, Mostrar Actualizar/Eliminar

        // PROTECCIÓN DEL ADMIN
        if (usuario.getUsuario().equalsIgnoreCase("admin")) {
            // Bloqueamos edición de datos críticos del super admin
            textFieldUsuario.setDisable(true);
            checkBoxAdministrador.setDisable(true); 
            checkBoxHabilitado.setDisable(true); 
            botonEliminar.setDisable(true); // No se puede borrar al jefe
        } else {
            habilitarCamposBloqueados();
        }
    }

    // OPERACIONES CRUD 
    @FXML
    private void guardarCampos(MouseEvent event) {
        limpiarError();

        // 1. Validaciones
        if (!validarCampos()) return; 

        if (existeUsuario(textFieldUsuario.getText().trim())) {
            mostrarError("Error: El usuario '" + textFieldUsuario.getText() + "' ya existe.");
            return;
        }

        // 2. Crear Objeto
        Usuario nuevoUsuario = new Usuario(
            textFieldNombre.getText().trim(),
            textFieldApellido.getText().trim(),
            textFieldUsuario.getText().trim(),
            textFieldPassword.getText().trim(),
            checkBoxAdministrador.isSelected(),
            checkBoxHabilitado.isSelected()
        );

        // 3. Guardar en Archivo
        usuarioDAO.guardarUsuario(nuevoUsuario);
        
        // 4. REFRESCAR Y LIMPIAR
        mostrarMensajeExito("Usuario registrado correctamente.");
        cargarUsuarios(); 
        limpiarCampos(null);
    }

    @FXML
    private void actualizarUsuario(MouseEvent event) {
        limpiarError();

        if (usuarioSeleccionado == null) {
            mostrarError("Selecciona un usuario de la tabla primero.");
            return;
        }
        if (!validarCampos()) return;

        // Comprobamos si cambió el username a uno que ya existe
        String nuevoUser = textFieldUsuario.getText().trim();
        if (!nuevoUser.equals(usuarioSeleccionado.getUsuario()) && existeUsuario(nuevoUser)) {
             mostrarError("Error: El nombre de usuario ya está ocupado.");
             return;
        }

        // Actualizamos los datos del objeto seleccionado
        usuarioSeleccionado.setNombre(textFieldNombre.getText().trim());
        usuarioSeleccionado.setApellido1(textFieldApellido.getText().trim());
        usuarioSeleccionado.setUsuario(nuevoUser);
        usuarioSeleccionado.setContrasena(textFieldPassword.getText().trim());
        usuarioSeleccionado.setAdministrador(checkBoxAdministrador.isSelected());
        usuarioSeleccionado.setUsuarioHabilitado(checkBoxHabilitado.isSelected());

        // Guardamos cambios
        usuarioDAO.actualizarUsuario(usuarioSeleccionado);
        
        // REFRESCAR Y LIMPIAR
        mostrarMensajeExito("Usuario actualizado correctamente.");
        cargarUsuarios();
        limpiarCampos(null);
    }

    @FXML
    private void eliminarUsuario(MouseEvent event) {
        limpiarError();

        if (usuarioSeleccionado == null) return;
        
        // VALIDACIÓN: No borrar al admin principal
        if (usuarioSeleccionado.getUsuario().equalsIgnoreCase("admin")) {
            mostrarError("No puedes eliminar al administrador principal.");
            return;
        }
        
        // VALIDACIÓN: No borrarse a sí mismo
        if (administradorLogueado != null && 
            usuarioSeleccionado.getIdUsuario().equals(administradorLogueado.getIdUsuario())) {
            mostrarError("No puedes eliminar tu propio usuario mientras estás conectado.");
            return;
        }

        // Confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Usuario");
        alert.setHeaderText("¿Estás seguro de eliminar a " + usuarioSeleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            usuarioDAO.eliminarUsuario(usuarioSeleccionado);
            
            // REFRESCAR Y LIMPIAR
            System.out.println("Usuario eliminado.");
            cargarUsuarios();
            limpiarCampos(null);
        }
    }

    @FXML
    private void limpiarCampos(MouseEvent event) {
        limpiarError(); // Ocultar mensaje rojo
        
        // Vaciar texts
        textFieldNombre.clear();
        textFieldApellido.clear();
        textFieldUsuario.clear();
        textFieldPassword.clear();
        checkBoxAdministrador.setSelected(false);
        checkBoxHabilitado.setSelected(false);
        
        // Resetear variables internas
        usuarioSeleccionado = null;
        tablaGestionDeUsuarios.getSelectionModel().clearSelection();
        
        // Resetear estado visual de campos y botones
        habilitarCamposBloqueados(); 
        configurarBotones(true, false, false, true); // Mostrar solo Guardar y Limpiar
    }

    // UTILIDADES Y VALIDACIONES
    private boolean validarCampos() {
        if (textFieldNombre.getText().trim().isEmpty() || 
            textFieldApellido.getText().trim().isEmpty() ||
            textFieldUsuario.getText().trim().isEmpty() ||
            textFieldPassword.getText().trim().isEmpty()) {
            
            mostrarError("Por favor, rellena todos los campos.");
            return false;
        }
        return true;
    }

    // Método auxiliar para simular la query de "buscarPorUsuario"
    private boolean existeUsuario(String username) {
        if (listaUsuariosObservable == null) return false;
        for (Usuario u : listaUsuariosObservable) {
            if (u.getUsuario().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private void mostrarError(String mensaje) {
        labelError.setText(mensaje);
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
    
    private void habilitarCamposBloqueados() {
        textFieldUsuario.setDisable(false);
        checkBoxAdministrador.setDisable(false);
        checkBoxHabilitado.setDisable(false);
        botonEliminar.setDisable(false);
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