package app_restaurante.modelo;


import app_restaurante.modelo.PedidoSimpleMesa;
import app_restaurante.modelo.ProductoCarta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorMesas {
    
    private static final Map<Integer, List<PedidoSimpleMesa>> mesas = new HashMap<>();

    public static List<PedidoSimpleMesa> getPedidosMesa(int numMesa) {
        mesas.putIfAbsent(numMesa, new ArrayList<>());
        return mesas.get(numMesa);
    }

    public static void agregarProducto(int numMesa, ProductoCarta prod, int cantidad) {
        List<PedidoSimpleMesa> lista = getPedidosMesa(numMesa);
        for (PedidoSimpleMesa l : lista) {
            if (l.getProducto().getIdProducto().equals(prod.getIdProducto())) {
                l.setCantidad(l.getCantidad() + cantidad);
                return;
            }
        }
        lista.add(new PedidoSimpleMesa(prod, cantidad));
    }
    
    public static void eliminarProducto(int numMesa, PedidoSimpleMesa linea) {
        if(mesas.containsKey(numMesa)) {
            mesas.get(numMesa).remove(linea);
        }
    }

    public static void limpiarMesa(int numMesa) {
        if (mesas.containsKey(numMesa)) mesas.get(numMesa).clear();
    }
    
    public static double calcularTotalMesa(int numMesa) {
        if (!mesas.containsKey(numMesa)) return 0.0;
        return mesas.get(numMesa).stream().mapToDouble(PedidoSimpleMesa::getTotalPedidosSimples).sum();
    }
    
    public static boolean isMesaOcupada(int numMesa) {
        return mesas.containsKey(numMesa) && !mesas.get(numMesa).isEmpty();
    }
}