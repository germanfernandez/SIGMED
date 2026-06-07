package controlador;

import modelo.dao.TurnoDAO;
import modelo.dao.PacienteDAO;
import modelo.dao.MedicoDAO;
import controlador.GestorDisponibilidad;
import modelo.entidades.Turno;
import modelo.entidades.Paciente;
import modelo.entidades.Medico;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.List;

public class GestorTurno {

    private TurnoDAO           turnoDAO;
    private PacienteDAO        pacienteDAO;
    private MedicoDAO          medicoDAO;
    private GestorDisponibilidad gestorDisponibilidad;

    // Constructor
    public GestorTurno() throws SQLException {
        this.turnoDAO          = new TurnoDAO();
        this.pacienteDAO       = new PacienteDAO();
        this.medicoDAO         = new MedicoDAO();
        this.gestorDisponibilidad = new GestorDisponibilidad();
    }

    /**
     * Registra un nuevo turno.
     * Valida la disponibilidad del médico antes de confirmar.
     *
     * @return ID del turno creado
     */
    public int registrarTurno(int idPaciente, int idMedico, String fecha,
                              String hora, String motivo) throws SQLException {

        // verificar disponibilidad horaria
        if (!verificarDisponibilidad(idMedico, fecha, hora))
            throw new IllegalStateException(
                    "El médico no tiene disponibilidad en el horario solicitado.");

        Paciente paciente = pacienteDAO.buscarPorId(idPaciente);
        Medico   medico   = medicoDAO.buscarPorId(idMedico);

        if (paciente == null) throw new IllegalArgumentException("Paciente no encontrado.");
        if (medico   == null) throw new IllegalArgumentException("Médico no encontrado.");

        Turno nuevo = new Turno(paciente, medico, fecha, hora, motivo);
        return turnoDAO.insertar(nuevo);
    }


    //Modifica un turno existente.
    //Revalida la disponibilidad si cambiaron fecha u hora.
    public boolean modificarTurno(Turno turno, String nuevaFecha,
                                  String nuevaHora) throws SQLException {
        // Solo revalidar si cambió fecha u hora
        if (!turno.getFecha().equals(nuevaFecha) || !turno.getHora().equals(nuevaHora)) {
            if (!verificarDisponibilidad(turno.getMedico().getIdMedico(), nuevaFecha, nuevaHora))
                throw new IllegalStateException(
                        "El médico no tiene disponibilidad en el nuevo horario.");
        }
        turno.setFecha(nuevaFecha);
        turno.setHora(nuevaHora);
        return turnoDAO.modificar(turno);
    }


    //Cancela un turno cambiando su estado.
    //No elimina el registro — lo marca como Ausente.
    public boolean cancelarTurno(int idTurno) throws SQLException {
        return turnoDAO.actualizarEstado(idTurno, Turno.ESTADO_AUSENTE);
    }

    //Elimina un turno de la base de datos.
    public boolean eliminarTurno(int idTurno) throws SQLException {
        return turnoDAO.eliminar(idTurno);
    }

    /**
     * Verifica disponibilidad del médico.
     * Comprueba: 1) que el horario esté dentro de su configuración
     *            2) que no exista otro turno en ese horario
     */
    public boolean verificarDisponibilidad(int idMedico,
                                           String fecha, String hora) throws SQLException {
        // 1. Verificar horario dentro del rango configurado
        String diaSemana = obtenerDiaSemana(fecha);
        if (!gestorDisponibilidad.verificarDisponibilidad(idMedico, diaSemana, hora))
            return false;

        // 2. Verificar que no exista turno superpuesto
        return turnoDAO.verificarDisponibilidad(idMedico, fecha, hora);
    }


    //Retorna los turnos del día para todos los médicos.
    public List<Turno> obtenerTurnosDelDia(String fecha) throws SQLException {
        return turnoDAO.listarPorFecha(fecha);
    }


    //Retorna los turnos de un médico en una fecha.
    public List<Turno> obtenerAgendaMedico(int idMedico, String fecha) throws SQLException {
        return turnoDAO.listarPorMedicoYFecha(idMedico, fecha);
    }

    //Retorna los turnos de un médico en un período.
    public List<Turno> obtenerAgendaPorPeriodo(int idMedico,
                                               String fechaInicio, String fechaFin) throws SQLException {
        return turnoDAO.listarPorMedicoYPeriodo(idMedico, fechaInicio, fechaFin);
    }


    //Actualiza el estado de un turno.
    public boolean actualizarEstado(int idTurno, String estado) throws SQLException {
        return turnoDAO.actualizarEstado(idTurno, estado);
    }


    //Busca un turno por ID.
    public Turno buscarTurno(int idTurno) throws SQLException {
        return turnoDAO.buscarPorId(idTurno);
    }


    //Obtiene el día de la semana en español a partir de una fecha yyyy-MM-dd.
    private String obtenerDiaSemana(String fecha) {
        String[] dias = {"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};
        java.time.LocalDate ld = java.time.LocalDate.parse(fecha);
        return dias[ld.getDayOfWeek().getValue() - 1];
    }

    /**
     * Genera la lista de horarios disponibles para un médico en una fecha.
     * Considera:
     *  - El día de la semana debe estar configurado en disponibilidad_medico
     *  - La fecha no puede ser anterior a hoy
     *  - Se excluyen los horarios ya ocupados por turnos Pendiente o Presente
     * Los slots se generan cada 30 minutos dentro del rango configurado.
     */
    public List<String> obtenerHorasDisponibles(int idMedico,
                                                String fecha) throws SQLException {
        List<String> slots = new ArrayList<>();

        // Validar que la fecha no sea pasada
        java.time.LocalDate fechaLocal = java.time.LocalDate.parse(fecha);
        if (fechaLocal.isBefore(java.time.LocalDate.now())) return slots;

        // Obtener día de la semana
        String diaSemana = obtenerDiaSemana(fecha);

        // Obtener configuración de disponibilidad del médico
        Map<String, String[]> disponibilidad =
                new controlador.GestorDisponibilidad().obtenerDisponibilidad(idMedico);
        String[] horario = disponibilidad.get(diaSemana);
        if (horario == null) return slots; // no atiende ese día

        // Generar slots cada 30 minutos
        LocalTime actual = LocalTime.parse(horario[0]);
        LocalTime fin    = LocalTime.parse(horario[1]);
        while (actual.isBefore(fin)) {
            slots.add(actual.toString());
            actual = actual.plusMinutes(30);
        }

        // Normalizar horas ocupadas a formato HH:mm para comparar con los slots
        List<String> ocupadas = turnoDAO.obtenerHorasOcupadas(idMedico, fecha);
        List<String> ocupadasNorm = new ArrayList<>();
        for (String h : ocupadas) {
            try {
                // Convierte "08:00:00" → "08:00"
                ocupadasNorm.add(LocalTime.parse(h).toString());
            } catch (Exception ex) {
                ocupadasNorm.add(h);
            }
        }
        slots.removeAll(ocupadasNorm);

        return slots;
    }

}