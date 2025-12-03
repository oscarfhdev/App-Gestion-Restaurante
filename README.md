Aquí lo tienes **todo junto**, sin separaciones adicionales y listo para **copiar y pegar directamente** en tu README de GitHub:

---

# 🍽️ App Gestión Restaurante - Mesón Fernández

<p align="center">
  <img src="src/app_restaurante/vista/images/comun/logoRedondeado.png" width="300">
</p>

> Aplicación de escritorio desarrollada en **JavaFX** para la gestión integral de un restaurante: desde la toma de comandas y gestión de mesas, hasta el control de inventario y facturación.

---

## 📖 Descripción

Este proyecto es una solución software diseñada para resolver la problemática real de la gestión diaria en hostelería.
La aplicación permite agilizar el flujo de trabajo mediante una interfaz visual intuitiva que gestiona el estado de las mesas, los pedidos, los cobros complejos y el stock del almacén.

El diseño sigue principios de **usabilidad** y estándares de programación, utilizando una paleta de colores coherente con la identidad del *Mesón Fernández*.

---

## ✨ Funcionalidades principales

### 🛋️ Gestión de mesas y comandas

* **Mapa visual de mesas:** Panel interactivo con las 12 mesas del restaurante.
* **Estado en tiempo real:** Código de colores para identificación rápida:

  * 🟢 **Verde:** Mesa libre.
  * 🟡 **Amarillo:** Mesa ocupada/pendiente de cobro.
* **Toma de comandas:** Añade platos y bebidas a una mesa específica calculando el total automáticamente.

### 💰 Sistema de cobro avanzado

* **Múltiples métodos de pago:** Soporte para **efectivo** (con cálculo de cambio) y **tarjeta**.
* **Pago separado (Split Bill):** Permite cobrar productos individuales de una mesa (cobro parcial), ideal para grupos que pagan por separado.
* **Historial de caja:** Registro persistente en ficheros de texto de todos los tickets generados.

### 📦 Gestión de inventario inteligente

* **Control de stock:** Alta, baja y modificación de productos en el almacén.
* **Alertas visuales de stock:** El sistema resalta automáticamente en **rojo** los productos por debajo del stock mínimo.
* **Categorización:** Organización por categorías (Carnes, Bebidas, etc.) y unidades de medida.

### 👥 Administración y seguridad

* **Roles de usuario:**

  * 🛡️ **Administrador:** Acceso total (gestión de usuarios, inventario, carta).
  * 👤 **Usuario (Camarero):** Acceso restringido a operaciones diarias.
* **Login seguro:** Pantalla de inicio de sesión para proteger el acceso.
* **Carta dinámica:** Los cambios realizados por los administradores se reflejan inmediatamente en la vista de la carta.

