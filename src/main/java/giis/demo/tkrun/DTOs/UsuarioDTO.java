package giis.demo.tkrun.DTOs;

/** Representa a un usuario/ciudadano del sistema (DTO) */
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String dni;
    private Integer rol;

    public UsuarioDTO(Integer id, String nombre, String email, String dni, Integer rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.dni = dni;
        this.rol = rol;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getDni() { return dni; }
    public Integer getRol() { return rol; }

    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setDni(String dni) { this.dni = dni; }
    public void setRol(Integer rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "UsuarioDTO{id=" + id + ", nombre='" + nombre + "', email='" + email + "', dni='" + dni + "', rol=" + rol + "}";
    }
}
