package app_restaurante;

import app_restaurante.dao.InventarioDAO;
import app_restaurante.dao.ProductoCartaDAO;
import app_restaurante.dao.UsuarioDAO;
import app_restaurante.modelo.CategoriaCarta;
import app_restaurante.modelo.CategoriaInventario;
import app_restaurante.modelo.ProductoCarta;
import app_restaurante.modelo.ProductoInventario;
import app_restaurante.modelo.UnidadMedida;
import app_restaurante.modelo.Usuario;
import java.util.List;

public class DataLoader {

    private final UsuarioDAO usuarioDAO;
    private final InventarioDAO inventarioDAO;
    private final ProductoCartaDAO productoCartaDAO; 

    // Constructor actualizado con los 3 DAOs
    public DataLoader(UsuarioDAO usuarioDAO, InventarioDAO inventarioDAO, ProductoCartaDAO productoCartaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.inventarioDAO = inventarioDAO;
        this.productoCartaDAO = productoCartaDAO;
    }

    // Método principal que llama a los demás
    public void cargarDatosIniciales() {
        System.out.println("--- Iniciando Carga de Datos ---");
        cargarUsuariosPorDefecto();
        cargarInventarioPorDefecto();
        cargarCartaPorDefecto();
        System.out.println("--- Carga de Datos Finalizada ---");
    }

    // LÓGICA DE USUARIOS
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

    // LÓGICA DE INVENTARIO 
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
    
    // --- 3. CARTA (NUEVO) ---
    private void cargarCartaPorDefecto() {
        if (productoCartaDAO.obtenerProductos().isEmpty()) {
            System.out.println("Carta vacía. Creando platos deliciosos...");

            // ENTRANTES
            productoCartaDAO.guardarProducto(new ProductoCarta("Croquetas de Jamón", 8.50, CategoriaCarta.ENTRANTES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Ensalada César", 9.00, CategoriaCarta.ENTRANTES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Tabla de Quesos", 12.50, CategoriaCarta.ENTRANTES));

            // CARNES
            productoCartaDAO.guardarProducto(new ProductoCarta("Entrecot de Ternera", 18.00, CategoriaCarta.CARNES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Carrillada Ibérica", 14.50, CategoriaCarta.CARNES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Hamburguesa Angus", 11.00, CategoriaCarta.CARNES));

            // PESCADOS
            productoCartaDAO.guardarProducto(new ProductoCarta("Merluza a la Vasca", 16.00, CategoriaCarta.PESCADOS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Salmón a la Plancha", 15.50, CategoriaCarta.PESCADOS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Sepia con Alioli", 13.00, CategoriaCarta.PESCADOS));

            // A LA BRASA
            productoCartaDAO.guardarProducto(new ProductoCarta("Chuletón de Ávila (500g)", 22.00, CategoriaCarta.BRASAS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Costillar BBQ", 15.00, CategoriaCarta.BRASAS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Churrasco de Pollo", 10.50, CategoriaCarta.BRASAS));

            // POSTRES
            productoCartaDAO.guardarProducto(new ProductoCarta("Tarta de Queso", 5.50, CategoriaCarta.POSTRES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Coulant de Chocolate", 6.00, CategoriaCarta.POSTRES));
            productoCartaDAO.guardarProducto(new ProductoCarta("Flan Casero", 4.50, CategoriaCarta.POSTRES));

            // BEBIDAS
            productoCartaDAO.guardarProducto(new ProductoCarta("Cerveza Mahou", 2.50, CategoriaCarta.BEBIDAS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Vino Rioja (Copa)", 3.00, CategoriaCarta.BEBIDAS));
            productoCartaDAO.guardarProducto(new ProductoCarta("Agua Mineral", 1.80, CategoriaCarta.BEBIDAS));

            System.out.println("-> Carta inicial creada con éxito.");
        }
    }
}