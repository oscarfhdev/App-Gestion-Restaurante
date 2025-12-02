package app_restaurante.modelo;


public enum CategoriaInventario {
    // Asignamos un nombrea cada constante
    CARNES("Carnes"),
    PESCADOS_MARISCOS("Pescados y Mariscos"),
    VERDURAS_HORTALIZAS("Verduras y Hortalizas"),
    FRUTAS("Frutas"),
    LACTEOS_HUEVOS("Lácteos y Huevos"),
    CEREALES_PAN_PASTA("Cereales, Pan y Pasta"),
    LEGUMBRES("Legumbres"),
    SALSAS_ESPECIAS("Salsas y Especias"),
    BEBIDAS_ALCOHOLICAS("Bebidas Alcohólicas"),
    BEBIDAS_NO_ALCOHOLICAS("Bebidas No Alcohólicas"),
    CONGELADOS("Congelados"),
    POSTRES_REPOSTERIA("Postres y Repostería"),
    LIMPIEZA_HIGIENE("Limpieza e Higiene"),
    MENAJE_UTENSILIOS("Menaje y Utensilios"),
    OTROS("Otros");

    // Atributo para guardar el nombre bonito
    private final String nombreMostrar;

    // Constructor del Enum
    CategoriaInventario(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    // Al sobrescribir toString, el ComboBox muestra el nombre en vez de las may    @Override
    public String toString() {
        return nombreMostrar;
    }
    
    
    
}