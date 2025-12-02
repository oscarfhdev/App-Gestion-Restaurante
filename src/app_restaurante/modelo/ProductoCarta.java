package app_restaurante.modelo;


import java.util.Objects;
import java.util.UUID;

public class ProductoCarta {

    private String idProducto;
    private String nombre;
    private double precio;
    private CategoriaCarta categoria; // Enum para las categorías de la carta

    // Constructor 1: Para crear nuevo (genera ID)
    public ProductoCarta(String nombre, double precio, CategoriaCarta categoria) {
        this.idProducto = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    // Constructor 2: Para cargar desde archivo (recibe ID)
    public ProductoCarta(String idProducto, String nombre, double precio, CategoriaCarta categoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    // GETTERS Y SETTERS 
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public CategoriaCarta getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCarta categoria) {
        this.categoria = categoria;
    }

    
    // EQUALS & HASHCODE por ID

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idProducto);
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
        final ProductoCarta other = (ProductoCarta) obj;
        return Objects.equals(this.idProducto, other.idProducto);
    }
    

    

    // PERSISTENCIA (TXT) 
    @Override
    public String toString() {
        // ID | Nombre | Precio | Categoria
        return this.idProducto + "|" + this.nombre + "|" + this.precio + "|" + this.categoria.name();
    }

    public static ProductoCarta fromString(String line) {
        String[] partes = line.split("\\|", -1);

        if (partes.length != 4) {
            throw new IllegalArgumentException("Formato de carta incorrecto: " + line);
        }

        // Recuperar Categoría con seguridad
        CategoriaCarta catEnum;
        try {
            catEnum = CategoriaCarta.valueOf(partes[3]);
        } catch (IllegalArgumentException e) {
            // Si falla, por defecto ponemos ENTRANTES
            catEnum = CategoriaCarta.ENTRANTES;
        }

        return new ProductoCarta(
            partes[0],                      // id
            partes[1],                      // nombre
            Double.parseDouble(partes[2]),  // precio
            catEnum                         // categoria
        );
    }
}