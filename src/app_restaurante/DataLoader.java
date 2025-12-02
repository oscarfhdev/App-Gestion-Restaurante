package app_restaurante;

import app_restaurante.dao.InventarioDAO;
import app_restaurante.dao.UsuarioDAO;
import app_restaurante.modelo.CategoriaInventario;
import app_restaurante.modelo.ProductoInventario;
import app_restaurante.modelo.UnidadMedida;
import app_restaurante.modelo.Usuario;
import java.util.List;

public class DataLoader {

    private final UsuarioDAO usuarioDAO;
    private final InventarioDAO inventarioDAO;

    // 2. Constructor con los dao
    public DataLoader(UsuarioDAO usuarioDAO, InventarioDAO inventarioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.inventarioDAO = inventarioDAO;
    }

    // Método principal que llama a los demás
    public void cargarDatosIniciales() {
        System.out.println("--- Iniciando Carga de Datos ---");
        cargarUsuariosPorDefecto();
        cargarInventarioPorDefecto();
        System.out.println("--- Carga de Datos Finalizada ---");
    }

    // LÓGICA DE USUARIOS (La que ya tenías)
    private void cargarUsuariosPorDefecto() {
        List<Usuario> usuariosActuales = usuarioDAO.obtenerUsuarios();

        boolean existeAdmin = usuariosActuales.stream().anyMatch(u -> u.getUsuario().equals("admin"));
        boolean existeUsuario = usuariosActuales.stream().anyMatch(u -> u.getUsuario().equals("usuario"));

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

    // LÓGICA DE INVENTARIO (Nueva)
    private void cargarInventarioPorDefecto() {
        // Solo cargamos datos si la lista está vacía
        if (inventarioDAO.obtenerProductos().isEmpty()) {
            
            System.out.println("Inventario vacío. Creando productos de ejemplo...");

            // Producto 1: Coca-Cola (Bebidas)
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Coca-Cola Zero", 
                CategoriaInventario.BEBIDAS_NO_ALCOHOLICAS, 
                50.0, 
                UnidadMedida.LATAS, 
                10.0 // Alerta si baja de 10
            ));

            // Producto 2: Patatas (Verduras) - CON STOCK BAJO A PROPÓSITO PARA PROBAR LA ALERTA ROJA
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Patatas Variedad Monalisa", 
                CategoriaInventario.VERDURAS_HORTALIZAS, 
                4.0, // Tengo 4, mínimo 10 -> Saldrá ROJO
                UnidadMedida.KG, 
                10.0
            ));

            // Producto 3: Solomillo (Carnes)
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Solomillo de Ternera", 
                CategoriaInventario.CARNES, 
                15.5, 
                UnidadMedida.KG, 
                5.0
            ));

            // Producto 4: Servilletas (Otros/Limpieza)
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Servilletas de Papel", 
                CategoriaInventario.MENAJE_UTENSILIOS, 
                20.0, 
                UnidadMedida.PAQUETES, 
                5.0
            ));
            
            // Producto 5: Leche (Lácteos)
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Leche Entera", 
                CategoriaInventario.LACTEOS_HUEVOS, 
                12.0, 
                UnidadMedida.LITROS, 
                6.0
            ));
             
            // Producto 6: Pan (Cereales)
            inventarioDAO.guardarProducto(new ProductoInventario(
                "Pan de Hamburguesa", 
                CategoriaInventario.CEREALES_PAN_PASTA, 
                60.0, 
                UnidadMedida.UNIDADES, 
                24.0
            ));

            System.out.println("-> 6 Productos de ejemplo creados en el inventario.");
        }
    }
}