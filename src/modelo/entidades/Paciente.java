package modelo.entidades;

public class Paciente extends Persona {

    // Atributos propios (encapsulados)
    private int    idPaciente;
    private String fechaNacimiento;
    private String email;
    private String obraSocial;

    // Constructor completo
    public Paciente(int idPaciente, String nombre, String apellido,
                    String dni, String telefono,
                    String fechaNacimiento, String email, String obraSocial) {
        super(nombre, apellido, dni, telefono); // llama al constructor de Persona
        this.idPaciente      = idPaciente;
        this.fechaNacimiento = fechaNacimiento;
        this.email           = email;
        this.obraSocial      = obraSocial;
    }

    // Constructor sin ID (para alta nueva antes de persistir)
    public Paciente(String nombre, String apellido,
                    String dni, String telefono,
                    String fechaNacimiento, String email, String obraSocial) {
        super(nombre, apellido, dni, telefono);
        this.fechaNacimiento = fechaNacimiento;
        this.email           = email;
        this.obraSocial      = obraSocial;
    }

    // Implementación de mostrarInfo() (polimorfismo)
    @Override
    public void mostrarInfo() {
        System.out.println("=== PACIENTE ===");
        System.out.println("Nombre:          " + getNombreCompleto());
        System.out.println("DNI:             " + getDni());
        System.out.println("Teléfono:        " + getTelefono());
        System.out.println("Email:           " + (email != null ? email : "—"));
        System.out.println("Fecha nacimiento:" + (fechaNacimiento != null ? fechaNacimiento : "—"));
        System.out.println("Obra social:     " + (obraSocial != null ? obraSocial : "—"));
    }

    // Getters y Setters
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getObraSocial() { return obraSocial; }
    public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }

    // toString
    @Override
    public String toString() {
        return getNombreCompleto() + " (DNI: " + getDni() + ")";
    }
}