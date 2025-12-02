package app_restaurante.modelo;


public class PedidoSimpleMesa {
    private ProductoCarta producto;
    private int cantidad;

    public PedidoSimpleMesa(ProductoCarta producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public ProductoCarta getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getTotalPedidosSimples() { return producto.getPrecio() * cantidad; }

    @Override
    public String toString() {
        return String.format("%dx %s (%.2f€)", cantidad, producto.getNombre(), getTotalPedidosSimples());
    }
}