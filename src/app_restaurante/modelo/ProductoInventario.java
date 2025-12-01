package app_restaurante.modelo;


import java.util.Objects;
import java.util.UUID;

public class ProductoInventario {

    private String idProducto;
    private String nombre;
    private CategoriaInventario categoria; // Ahora es del tipo Enum
    private double cantidadActual;
    private String unidadMedida;
    private double stockMinimo;

    // Constructor 1: Para crear un producto nuevo (genera ID automático)
    public ProductoInventario(String nombre, CategoriaInventario categoria, double cantidadActual, String unidadMedida, double stockMinimo) {
        this.idProducto = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadActual = cantidadActual;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    // Constructor 2: Para cargar desde el archivo (ID ya existente)
    public ProductoInventario(String idProducto, String nombre, CategoriaInventario categoria, double cantidadActual, String unidadMedida, double stockMinimo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadActual = cantidadActual;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    // --- GETTERS Y SETTERS ---

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaInventario getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaInventario categoria) {
        this.categoria = categoria;
    }

    public double getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(double cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    
    // Método de utilidad para el color rojo en la tabla
    public boolean estaBajoStock() {
        return this.cantidadActual <= this.stockMinimo;
    }

    // --- MÉTODOS EQUALS & HASHCODE ---
    

    // --- PERSISTENCIA (TXT) ---

    // Convertir objeto a línea de texto
    // Usamos categoria.name() para guardar "CARNES" como texto
    @Override
    public String toString() {
        return this.idProducto + "|" + this.nombre + "|" + this.categoria.name() + "|" + 
               this.cantidadActual + "|" + this.unidadMedida + "|" + this.stockMinimo;
    }

    // Convertir línea de texto a objeto
    public static ProductoInventario fromString(String line) {
        // Separa la línea por "|"
        String[] lineaSeparada = line.split("\\|", -1);

        if (lineaSeparada.length != 6) {
            throw new IllegalArgumentException("Formato de inventario incorrecto: " + line);
        }

        // Convertimos el texto "CARNES" de vuelta al Enum CategoriaInventario.CARNES
        CategoriaInventario catEnum;
        try {
            catEnum = CategoriaInventario.valueOf(lineaSeparada[2]);
        } catch (IllegalArgumentException e) {
            // Si por algún error manual en el txt la categoría no existe, ponemos OTROS por defecto para que no falle la app
            catEnum = CategoriaInventario.OTROS;
        }

        return new ProductoInventario(
            lineaSeparada[0],                     // id
            lineaSeparada[1],                     // nombre
            catEnum,                              // categoria (Enum)
            Double.parseDouble(lineaSeparada[3]), // cantidad
            lineaSeparada[4],                     // unidad
            Double.parseDouble(lineaSeparada[5])  // stockMinimo
        );
    }
}