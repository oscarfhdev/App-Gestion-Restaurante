package app_restaurante.modelo;

import java.util.Objects;
import java.util.UUID;

public class ProductoInventario {

    private String idProducto;
    private String nombre;
    private CategoriaInventario categoria; // Ponemos los enum
    private double cantidadActual;
    private UnidadMedida unidadMedida; // Aquí el de unidad de medida
    private double stockMinimo;

    // Constructor 1: Nuevo producto
    public ProductoInventario(String nombre, CategoriaInventario categoria, double cantidadActual, UnidadMedida unidadMedida, double stockMinimo) {
        this.idProducto = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadActual = cantidadActual;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    // Constructor 2: Cargar desde archivo
    public ProductoInventario(String idProducto, String nombre, CategoriaInventario categoria, double cantidadActual, UnidadMedida unidadMedida, double stockMinimo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadActual = cantidadActual;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
    }

    // --- GETTERS Y SETTERS ---

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public CategoriaInventario getCategoria() { return categoria; }
    public void setCategoria(CategoriaInventario categoria) { this.categoria = categoria; }

    public double getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(double cantidadActual) { this.cantidadActual = cantidadActual; }

    public UnidadMedida getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(UnidadMedida unidadMedida) { this.unidadMedida = unidadMedida; }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public boolean estaBajoStock() {
        return this.cantidadActual <= this.stockMinimo;
    }

    // --- MÉTODOS EQUALS & HASHCODE ---
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.idProducto);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ProductoInventario other = (ProductoInventario) obj;
        return Objects.equals(this.idProducto, other.idProducto);
    }
    
    // --- PERSISTENCIA (TXT) ---
    @Override
    public String toString() {
        // Guardamos categoria.name() y unidadMedida.name() (ej: "KG", "LITROS")
        return this.idProducto + "|" + this.nombre + "|" + this.categoria.name() + "|" + 
               this.cantidadActual + "|" + this.unidadMedida.name() + "|" + this.stockMinimo;
    }

    public static ProductoInventario fromString(String line) {
        String[] lineaSeparada = line.split("\\|", -1);

        if (lineaSeparada.length != 6) {
            throw new IllegalArgumentException("Formato de inventario incorrecto: " + line);
        }

        // Recuperar CATEGORÍA
        CategoriaInventario catEnum;
        try {
            catEnum = CategoriaInventario.valueOf(lineaSeparada[2]);
        } catch (IllegalArgumentException e) {
            catEnum = CategoriaInventario.OTROS;
        }

        // Recuperar UNIDAD MEDIDA 
        UnidadMedida uniEnum;
        try {
            uniEnum = UnidadMedida.valueOf(lineaSeparada[4]);
        } catch (IllegalArgumentException e) {
            // Si falla o no existe, ponemos UNIDADES por defecto
            uniEnum = UnidadMedida.UNIDADES;
        }

        return new ProductoInventario(
            lineaSeparada[0],                     // id
            lineaSeparada[1],                     // nombre
            catEnum,                              // categoria
            Double.parseDouble(lineaSeparada[3]), // cantidad
            uniEnum,                              // unidad (Enum)
            Double.parseDouble(lineaSeparada[5])  // stockMinimo
        );
    }
}