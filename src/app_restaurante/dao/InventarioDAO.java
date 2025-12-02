package app_restaurante.dao;


import app_restaurante.modelo.ProductoInventario;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private static final String RUTA_ARCHIVO = "src/app_restaurante/bbdd_txt/inventario.txt";

    // Constructor: crea el archivo (y la carpeta) si no existe
    public InventarioDAO() {
        File archivo = new File(RUTA_ARCHIVO);
        try {
            // Aseguramos que la carpeta exista antes de crear el archivo
            if (!archivo.getParentFile().exists()) {
                archivo.getParentFile().mkdirs();
            }
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Devuelve todos los productos del archivo
    public List<ProductoInventario> obtenerProductos() {
        List<ProductoInventario> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                // Usamos el fromString del modelo ProductoInventario
                try {
                    lista.add(ProductoInventario.fromString(linea));
                } catch (Exception e) {
                    System.err.println("Error leyendo línea: " + linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Guarda un producto nuevo al final del archivo
    public void guardarProducto(ProductoInventario producto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(producto.toString());
            bw.newLine(); // salto de línea
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Actualiza un producto existente (por id)
    public void actualizarProducto(ProductoInventario productoActualizado) {
        List<ProductoInventario> lista = obtenerProductos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdProducto().equals(productoActualizado.getIdProducto())) {
                lista.set(i, productoActualizado); // reemplaza
                break;
            }
        }
        sobrescribirArchivo(lista);
    }

    // Elimina un producto del archivo (por id)
    public void eliminarProducto(ProductoInventario producto) {
        List<ProductoInventario> lista = obtenerProductos();
        lista.removeIf(p -> p.getIdProducto().equals(producto.getIdProducto()));
        sobrescribirArchivo(lista);
    }

    // Sobrescribe todo el archivo con la lista actual
    private void sobrescribirArchivo(List<ProductoInventario> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (ProductoInventario p : lista) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}