package app_restaurante.modelo;

// Clase que representa una línea del pedido (Ej: "2x Hamburguesas")
public class PedidoSimpleMesa {
    
    private ProductoCarta producto; // El objeto con nombre, precio, etc.
    private int cantidad;           // Cuántos han pedido

    public PedidoSimpleMesa(ProductoCarta producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public ProductoCarta getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    // Calcula el precio total de esta línea (Precio del plato * Unidades pedidas)
    public double getTotalPedidosSimples() { return producto.getPrecio() * cantidad; }

    @Override
    public String toString() {
        // Formatea el texto con este foormato 2x Hamburguesa (15.50€)
        return String.format("%dx %s (%.2f€)", cantidad, producto.getNombre(), getTotalPedidosSimples());
    }
}