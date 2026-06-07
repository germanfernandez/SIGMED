package modelo.entidades;

public class Medico extends Persona {

    // Atributos propios (encapsulados)
    private int    idMedico;
    private String especialidad;
    private String matricula;

    // Constructor completo
    public Medico(int idMedico, String nombre, String apellido,
                  String dni, String telefono,
                  String especialidad, String matricula) {
        super(nombre, apellido, dni, telefono); // llama al constructor de Persona
        this.idMedico    = idMedico;
        this.especialidad = especialidad;
        this.matricula   = matricula;
    }

    // Constructor sin ID (para alta nueva antes de persistir)
    public Medico(String nombre, String apellido,
                  String dni, String telefono,
                  String especialidad, String matricula) {
        super(nombre, apellido, dni, telefono);
        this.especialidad = especialidad;
        this.matricula    = matricula;
    }

    // Implementación de mostrarInfo() (polimorfismo)
    @Override
    public void mostrarInfo() {
        System.out.println("=== MÉDICO ===");
        System.out.println("Nombre:      " + getNombreCompleto());
        System.out.println("DNI:         " + getDni());
        System.out.println("Teléfono:    " + getTelefono());
        System.out.println("Especialidad:" + especialidad);
        System.out.println("Matrícula:   " + matricula);
    }

    // Getters y Setters
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty())
            throw new IllegalArgumentException("La especialidad no puede estar vacía.");
        this.especialidad = especialidad;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty())
            throw new IllegalArgumentException("La matrícula no puede estar vacía.");
        this.matricula = matricula;
    }

    // toString
    @Override
    public String toString() {
        return "Dr/a. " + getNombreCompleto() + " – " + especialidad;
    }
}