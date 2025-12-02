package app_restaurante.modelo;


public enum UnidadMedida {
    KG("Kg"),
    LITROS("Litros"),
    UNIDADES("Unidades"),
    BOTELLAS("Botellas"),
    LATAS("Latas"),
    CAJAS("Cajas"),
    PAQUETES("Paquetes"),
    GRAMOS("Gramos"),
    MILILITROS("Mililitros");

    private final String nombreMostrar;

    UnidadMedida(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}