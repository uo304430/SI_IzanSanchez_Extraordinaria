package giis.demo.tkrun.Entities;

public class UsuarioEntity {
    private Integer id;
    private String nombre;
    private String email;
    private String dni;
    private Integer rol;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }
}
