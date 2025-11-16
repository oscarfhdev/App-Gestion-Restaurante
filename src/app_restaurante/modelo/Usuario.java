package app_restaurante.modelo;

import java.util.Objects;
import java.util.UUID;

public class Usuario {
    private String idUsuario;
    private String nombre;
    private String apellido1;
    private String usuario;
    private String contrasena;
    private boolean administrador;
    private boolean usuarioHabilitado;

    // Constructor normal para crear el usuario
    public Usuario(String nombre, String apellido1, String usuario, String contrasena, boolean administrador, boolean usuarioHabilitado) {
        this.idUsuario = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.administrador = administrador;
        this.usuarioHabilitado = usuarioHabilitado;
    }

    // Constructor adicional para crear los usuarios al obtener de archivo
    public Usuario(String idUsuario, String nombre, String apellido1, String usuario, String contrasena, boolean administrador, boolean usuarioHabilitado) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.administrador = administrador;
        this.usuarioHabilitado = usuarioHabilitado;
    }
    
    // Getters y setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public boolean isUsuarioHabilitado() {
        return usuarioHabilitado;
    }

    public void setUsuarioHabilitado(boolean usuarioHabilitado) {
        this.usuarioHabilitado = usuarioHabilitado;
    }

    
    
   // Métodos equals & hashcode

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.idUsuario);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        return Objects.equals(this.idUsuario, other.idUsuario);
    }
    
    
    
    
    // Para guardar fácilmente en el archivo sobreescribimos toString
    @Override
    public String toString() {
        return this.idUsuario + "|" + this.nombre + "|" + this.apellido1 + "|" + this.usuario + "|" + this.contrasena +
                "|" + this.administrador + "|" + this.usuarioHabilitado;
    }
    
    
    
    // Esto es para que me pasen una línea y yo devuelva lo contrario, el objeto
    public static Usuario fromString(String line) {
        // Separa la línea por "|"
        String[] lineaSeparada = line.split("\\|", -1); // -1 para conservar campos vacíos

        if (lineaSeparada.length != 7) {
            throw new IllegalArgumentException("Formato incorrecto: " + line);
        }

        // Crea el usuario usando el constructor completo
        return new Usuario(
            lineaSeparada[0],                     // idUsuario
            lineaSeparada[1],                     // nombre
            lineaSeparada[2],                     // apellido1
            lineaSeparada[3],                     // usuario
            lineaSeparada[4],                     // contrasena
            Boolean.parseBoolean(lineaSeparada[5]), // administrador
            Boolean.parseBoolean(lineaSeparada[6])  // usuarioHabilitado
        );
    }

    
}
