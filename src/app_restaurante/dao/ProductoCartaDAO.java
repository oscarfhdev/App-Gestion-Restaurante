package app_restaurante.dao;


import app_restaurante.modelo.ProductoCarta;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProductoCartaDAO {

    // Archivo específico para la carta (platos y bebidas)
    private static final String RUTA_ARCHIVO = "src/app_restaurante/bbdd_txt/carta.txt";

    // Constructor: Asegura que el archivo y carpeta existan
    public ProductoCartaDAO() {
        File archivo = new File(RUTA_ARCHIVO);
        try {
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

    // Método para leer todos
    public List<ProductoCarta> obtenerProductos() {
        List<ProductoCarta> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                
                // Intentamos convertir la línea a objeto
                try {
                    lista.add(ProductoCarta.fromString(linea));
                } catch (Exception e) {
                    System.err.println("Error leyendo línea de carta: " + linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Guardar nuevo plato/bebida
    public void guardarProducto(ProductoCarta producto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(producto.toString());
            bw.newLine(); // salto de línea
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Actualizar
    public void actualizarProducto(ProductoCarta productoActualizado) {
        List<ProductoCarta> lista = obtenerProductos();
        boolean encontrado = false;
        
        for (int i = 0; i < lista.size(); i++) {
            // Comparamos por ID
            if (lista.get(i).getIdProducto().equals(productoActualizado.getIdProducto())) {
                lista.set(i, productoActualizado); // Reemplazamos
                encontrado = true;
                break;
            }
        }
        
        if (encontrado) {
            sobrescribirArchivo(lista);
        }
    }

    // Eliminar
    public void eliminarProducto(ProductoCarta producto) {
        List<ProductoCarta> lista = obtenerProductos();
        // Borramos si el ID coincide
        boolean borrado = lista.removeIf(p -> p.getIdProducto().equals(producto.getIdProducto()));
        
        if (borrado) {
            sobrescribirArchivo(lista);
        }
    }

    // Sobreescribiré
    private void sobrescribirArchivo(List<ProductoCarta> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (ProductoCarta p : lista) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}