package controlador;

import modelo.dao.PacienteDAO;
import modelo.entidades.Paciente;
import java.sql.SQLException;
import java.util.List;

public class GestorPaciente {

    private PacienteDAO pacienteDAO;

    // Constructor
    public GestorPaciente() throws SQLException {
        this.pacienteDAO = new PacienteDAO();
    }

    /**
     * Registra un nuevo paciente.
     * Verifica que el DNI no esté duplicado antes de insertar.
     *
     * @return ID generado o -1 si el DNI ya existe
     */
    public int registrarPaciente(String nombre, String apellido, String dni,
                                 String telefono, String fechaNacimiento,
                                 String email, String obraSocial) throws SQLException {
        // Verificar DNI duplicado
        if (pacienteDAO.buscarPorDni(dni) != null)
            throw new IllegalArgumentException("Ya existe un paciente con DNI: " + dni);

        Paciente nuevo = new Paciente(nombre, apellido, dni, telefono,
                fechaNacimiento, email, obraSocial);
        return pacienteDAO.insertar(nuevo);
    }


    //Busca pacientes por nombre, apellido o DNI.
    public List<Paciente> buscarPaciente(String criterio) throws SQLException {
        if (criterio == null || criterio.trim().isEmpty())
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío.");
        return pacienteDAO.buscarPorNombre(criterio);
    }


    //Busca un paciente por DNI exacto.
    public Paciente buscarPorDni(String dni) throws SQLException {
        return pacienteDAO.buscarPorDni(dni);
    }


    //Retorna el listado completo de pacientes.
    public List<Paciente> listarTodos() throws SQLException {
        return pacienteDAO.listarTodos();
    }


    //Modifica los datos de un paciente existente.
    public boolean modificarPaciente(Paciente paciente) throws SQLException {
        if (paciente.getIdPaciente() <= 0)
            throw new IllegalArgumentException("El paciente no tiene un ID válido.");
        return pacienteDAO.modificar(paciente);
    }


    //Elimina un paciente por ID.
    public boolean eliminarPaciente(int idPaciente) throws SQLException {
        return pacienteDAO.eliminar(idPaciente);
    }
}