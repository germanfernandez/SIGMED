package modelo.entidades;

/**
 * Clase abstracta Persona
 */
public abstract class Persona {

    // Atributos
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;

    // Constructor
    public Persona(String nombre, String apellido, String dni, String telefono) {
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.dni       = dni;
        this.telefono  = telefono;
    }

    // Metodo concreto compartido por todas las subclases
    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }

    // Metodo abstracto: cada subclase define su propia implementación
    // Polimorfismo: Medico y Paciente lo implementan de forma distinta
    public abstract void mostrarInfo();

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty())
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty())
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // toString
    @Override
    public String toString() {
        return getNombreCompleto() + " (DNI: " + dni + ")";
    }
}