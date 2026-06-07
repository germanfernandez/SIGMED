# SIGMED – Sistema de Gestión de Consultorio Médico

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Java](https://img.shields.io/badge/Java-17-orange) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue) ![Status](https://img.shields.io/badge/status-prototipo-yellow)
 
Prototipo de sistema de gestión para un consultorio médico desarrollado en Java 17 con interfaz gráfica Swing y base de datos MySQL. Implementa el patrón Modelo Vista Controlador (MVC) y los pilares de la Programación Orientada a Objetos en el marco del Proceso Unificado de Desarrollo (PUD).
 
---

## Descripción

SIGMED permite gestionar las operaciones diarias de un consultorio médico con dos especialidades (Pediatría y Dermatología), cubriendo el ciclo completo de atención:

- Registro y búsqueda de pacientes
- Gestión de turnos con validación de disponibilidad horaria
- Recepción de pacientes y manejo de sala de espera
- Registro de notas clínicas e historial por paciente
- Configuración de disponibilidad del médico
- Autenticación con roles diferenciados (Secretaria / Médico)

---

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Interfaz gráfica | Java Swing |
| Base de datos | MySQL 8.0 |
| Servidor local | WampServer |
| Conectividad | JDBC — mysql-connector-java |
| Seguridad | BCrypt — jBCrypt 0.4 |
| IDE | IntelliJ IDEA |

---

## Arquitectura

El sistema aplica el patrón **MVC** organizado en tres paquetes principales:

```
src/
├── sigmed/               # Clase principal (Main)
├── modelo/
│   ├── entidades/        # Persona, Medico, Paciente, Turno, NotaClinica, Usuario
│   └── dao/              # ConexionBD, UsuarioDAO, MedicoDAO, PacienteDAO,
│                         # TurnoDAO, NotaClinicaDAO, DisponibilidadDAO
├── controlador/          # GestorSesion, GestorPaciente, GestorTurno,
│                         # GestorHistoria, GestorRecepcion, GestorSala,
│                         # GestorAgenda, GestorDisponibilidad
└── vista/                # MenuSIGMED, PantallaTurno, PantallaPaciente,
                          # PantallaRecepcion, PantallaSala,
                          # PantallaDisponibilidad, PantallaDetalleNota
```

---

## Pilares POO aplicados

| Pilar | Implementación |
|---|---|
| **Abstracción** | Clase abstracta `Persona` con método abstracto `mostrarInfo()` |
| **Herencia** | `Medico` y `Paciente` extienden `Persona` |
| **Polimorfismo** | `mostrarInfo()` implementado de forma distinta en cada subclase |
| **Encapsulamiento** | Atributos `private` con getters/setters validados en todas las entidades |

---

## Requisitos previos

- Java 17 o superior
- WampServer con MySQL 8.0 activo
- IntelliJ IDEA (Community o Ultimate)

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/SIGMED.git
```

### 2. Agregar las librerías externas

Descargar los siguientes archivos `.jar` y copiarlos en la carpeta `lib/`:

| Librería | Versión | Descarga |
|---|---|---|
| mysql-connector-java | 8.0.33 | [Maven Repository](https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.33) |
| jBCrypt | 0.4 | [Maven Repository](https://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.4) |

En IntelliJ: `File → Project Structure → Libraries → + → Java` y seleccionar cada `.jar`.

### 3. Crear la base de datos

Con WampServer activo, ejecutar los scripts SQL en orden desde HeidiSQL o cualquier cliente de bases de datos:

```
sql/
├── 01_crear_base_de_datos.sql
├── 02_crear_tablas.sql
├── 03_insertar_datos.sql
├── 04_consultar_datos.sql
└── 05_borrar_datos.sql (solo para pruebas de borrado)
```

### 4. Generar hashes BCrypt

Ejecutar `GenerarHash.java` una única vez para obtener los hashes de las contraseñas e insertarlos en el script `03_insertar_datos.sql`:

```bash
# En IntelliJ: click derecho sobre GenerarHash.java → Run 'GenerarHash.main()'
```

### 5. Configurar la conexión

Verificar los parámetros de conexión en `modelo/dao/ConexionBD.java`:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/sigmed_db" +
    "?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires";
private static final String USUARIO  = "root";
private static final String PASSWORD = "";
```

### 6. Ejecutar

Correr `sigmed/Main.java` desde IntelliJ.

---

## Usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `secretaria01` | `1234` | Secretaria |
| `drdimartino` | `1234` | Médico Pediatra |
| `drafernandez` | `1234` | Médica Dermatóloga |

> Las contraseñas se almacenan como hash BCrypt en la base de datos. Ejecutar `GenerarHash.java` para obtener los hashes antes de insertar los datos de prueba.

---

## Flujo de atención

```
Secretaria                        Médico
──────────────────                ──────────────────
Registrar turno                   
(estado: Pendiente)               

Recepcionar paciente              
(estado: Presente)      ────────► Ver en Sala de Espera

                                  Seleccionar paciente
                                  Guardar nota clínica
                                  (diagnóstico + tratamiento)

                                  Finalizar Atención
                                  (estado: Atendido)
```

---

## Licencia

Proyecto académico — uso educativo.
