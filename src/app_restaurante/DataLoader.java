package app_restaurante;
 // O en un paquete 'config'

import app_restaurante.dao.UsuarioDAO;
import app_restaurante.modelo.Usuario;
import java.util.List;

public class DataLoader {

    private final UsuarioDAO usuarioDAO;

    //  Le pasamos el DAO que va a usar
    public DataLoader(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void cargarDatosIniciales() {
        System.out.println("Verificando datos iniciales...");
        List<Usuario> usuariosActuales = usuarioDAO.obtenerUsuarios();

        boolean existeAdmin = usuariosActuales.stream()
                .anyMatch(u -> u.getUsuario().equals("admin"));
        
        boolean existeUsuario = usuariosActuales.stream()
                .anyMatch(u -> u.getUsuario().equals("usuario"));

        if (!existeAdmin) {
            Usuario admin = new Usuario("Administrador", "Sistema", "admin", "admin", true, true);
            usuarioDAO.guardarUsuario(admin);
            System.out.println("-> Usuario 'admin' creado.");
        }

        if (!existeUsuario) {
            Usuario user = new Usuario("Usuario", "Invitado", "usuario", "usuario", false, true);
            usuarioDAO.guardarUsuario(user);
            System.out.println("-> Usuario 'usuario' creado.");
        }
    }
}