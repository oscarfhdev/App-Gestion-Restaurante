package app_restaurante.dao;

import app_restaurante.modelo.Usuario;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String RUTA_ARCHIVO = "src/app_restaurante/basedatos_txt/usuarios.txt";

    // Constructor: crea el archivo si no existe
    public UsuarioDAO() {
        File archivo = new File(RUTA_ARCHIVO);
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Devuelve todos los usuarios del archivo
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                // Usamos el fromString del modelo, que retorna el objeto Usuario
                lista.add(Usuario.fromString(linea));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Guarda un usuario nuevo al final del archivo
    public void guardarUsuario(Usuario usuario) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(usuario.toString());
            bw.newLine(); // salto de línea
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Actualiza un usuario existente (por id)
    public void actualizarUsuario(Usuario usuarioActualizado) {
        List<Usuario> lista = obtenerUsuarios();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdUsuario().equals(usuarioActualizado.getIdUsuario())) {
                lista.set(i, usuarioActualizado); // reemplaza
                break;
            }
        }
        sobrescribirArchivo(lista);
    }

    // Elimina un usuario del archivo (por id)
    public void eliminarUsuario(Usuario usuario) {
        List<Usuario> lista = obtenerUsuarios();
        lista.removeIf(u -> u.getIdUsuario().equals(usuario.getIdUsuario()));
        sobrescribirArchivo(lista);
    }

    // Sobrescribe todo el archivo con la lista actual
    private void sobrescribirArchivo(List<Usuario> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (Usuario u : lista) {
                bw.write(u.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
