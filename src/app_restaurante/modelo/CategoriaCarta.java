package app_restaurante.modelo;


public enum CategoriaCarta {
    ENTRANTES("Entrantes"),
    CARNES("Carnes"),
    PESCADOS("Pescados"),
    BRASAS("A la Brasa"),
    POSTRES("Postres"),
    BEBIDAS("Bebidas");

    private final String nombreMostrar;

    CategoriaCarta(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    @Override
    public String toString() {
        return nombreMostrar;
    }
}