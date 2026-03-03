package Izan_33804;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IncidenciaEntity {
    private int id;
    private int tipo;
    private String descripcion;
    private String localizacion;
    private int usuario;
    private int tecnico;
    private String Coste;            
    private String descr_reparación; 
    private String fecha;
    private int estado;
    private boolean validación;      
}