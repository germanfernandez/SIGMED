import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase utilitaria — GenerarHash
 *
 * Ejecutar UNA VEZ para obtener los hashes BCrypt
 * de las contraseñas que se van a insertar en la BD.
 *
 * Los hashes generados se copian manualmente en
 * el script 03_insertar_datos.sql
 *
 * No forma parte del sistema en producción.
 */
public class GenerarHash {

    public static void main(String[] args) {

        String[] usuarios = {
            "secretaria01", "1234",
            "drdimartino",  "1234",
            "drafernandez", "1234"
        };

        System.out.println("=== Hashes BCrypt para insertar en la BD ===\n");

        for (int i = 0; i < usuarios.length; i += 2) {
            String username = usuarios[i];
            String password = usuarios[i + 1];
            String hash     = BCrypt.hashpw(password, BCrypt.gensalt(10));

            System.out.println("Usuario:  " + username);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
        }
    }
}
