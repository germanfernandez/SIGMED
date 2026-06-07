package modelo.entidades;

public class Turno {

    // Constantes de estado
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_PRESENTE  = "Presente";
    public static final String ESTADO_AUSENTE   = "Ausente";
    public static final String ESTADO_ATENDIDO  = "Atendido";

    // Atributos (encapsulados)
    private int      idTurno;
    private Paciente paciente;
    private Medico   medico;
    private String   fecha;
    private String   hora;
    private String   motivoConsulta;
    private String   estado;

    // Constructor completo
    public Turno(int idTurno, Paciente paciente, Medico medico,
                 String fecha, String hora, String motivoConsulta) {
        this.idTurno        = idTurno;
        this.paciente       = paciente;
        this.medico         = medico;
        this.fecha          = fecha;
        this.hora           = hora;
        this.motivoConsulta = motivoConsulta;
        this.estado         = ESTADO_PENDIENTE; // estado inicial siempre Pendiente
    }

    // Constructor sin ID (para alta nueva)
    public Turno(Paciente paciente, Medico medico,
                 String fecha, String hora, String motivoConsulta) {
        this(0, paciente, medico, fecha, hora, motivoConsulta);
    }

    // Metodo para actualizar estado con validación
    public void actualizarEstado(String nuevoEstado) {
        if (!nuevoEstado.equals(ESTADO_PENDIENTE) &&
                !nuevoEstado.equals(ESTADO_PRESENTE)  &&
                !nuevoEstado.equals(ESTADO_AUSENTE)   &&
                !nuevoEstado.equals(ESTADO_ATENDIDO)) {
            throw new IllegalArgumentException(
                    "Estado inválido: " + nuevoEstado +
                            ". Los valores permitidos son: Pendiente, Presente, Ausente, Atendido.");
        }
        this.estado = nuevoEstado;
    }

    // Mostrar resumen del turno
    public void mostrarResumen() {
        System.out.println("=== TURNO ===");
        System.out.println("ID:      " + idTurno);
        System.out.println("Paciente:" + paciente.getNombreCompleto());
        System.out.println("Médico:  " + medico.getNombreCompleto());
        System.out.println("Fecha:   " + fecha + " " + hora);
        System.out.println("Motivo:  " + (motivoConsulta != null ? motivoConsulta : "—"));
        System.out.println("Estado:  " + estado);
    }

    // Getters y Setters
    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getEstado() { return estado; }

    // toString
    @Override
    public String toString() {
        return "[" + fecha + " " + hora + "] " +
                paciente.getNombreCompleto() + " → " +
                medico.getNombreCompleto() + " (" + estado + ")";
    }
}