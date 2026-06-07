package modelo.entidades;

public class NotaClinica {

    // Atributos (encapsulados)
    private int    idNota;
    private Turno  turno;
    private String fecha;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;

    // Constructor completo
    public NotaClinica(int idNota, Turno turno, String fecha,
                       String diagnostico, String tratamiento,
                       String observaciones) {
        if (diagnostico == null || diagnostico.trim().isEmpty())
            throw new IllegalArgumentException("El diagnóstico es obligatorio.");
        this.idNota        = idNota;
        this.turno         = turno;
        this.fecha         = fecha;
        this.diagnostico   = diagnostico;
        this.tratamiento   = tratamiento;
        this.observaciones = observaciones;
    }

    // Constructor sin ID (para alta nueva)
    public NotaClinica(Turno turno, String fecha,
                       String diagnostico, String tratamiento,
                       String observaciones) {
        this(0, turno, fecha, diagnostico, tratamiento, observaciones);
    }

    // Mostrar nota
    public void mostrarNota() {
        System.out.println("=== NOTA CLÍNICA ===");
        System.out.println("Fecha:         " + fecha);
        System.out.println("Paciente:      " + turno.getPaciente().getNombreCompleto());
        System.out.println("Médico:        " + turno.getMedico().getNombreCompleto());
        System.out.println("Diagnóstico:   " + diagnostico);
        System.out.println("Tratamiento:   " + (tratamiento   != null ? tratamiento   : "—"));
        System.out.println("Observaciones: " + (observaciones != null ? observaciones : "—"));
    }

    // Getters y Setters
    public int getIdNota() { return idNota; }
    public void setIdNota(int idNota) { this.idNota = idNota; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) {
        if (diagnostico == null || diagnostico.trim().isEmpty())
            throw new IllegalArgumentException("El diagnóstico no puede estar vacío.");
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // toString
    @Override
    public String toString() {
        return "[" + fecha + "] " + turno.getPaciente().getNombreCompleto() +
                " – " + diagnostico;
    }
}