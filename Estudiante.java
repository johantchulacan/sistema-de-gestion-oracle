import java.util.ArrayList;
import java.util.List;

public class Estudiante {
    private int id;
    private String nombre;
    private int creditosInscritos;
    private List<Integer> inscripcionesIds; // IDs de las inscripciones activas/retenidas

    public Estudiante(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.creditosInscritos = 0;
        this.inscripcionesIds = new ArrayList<>();
    }

    // --- Getters y Setters ---
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCreditosInscritos() { return creditosInscritos; }
    public void setCreditosInscritos(int creditosInscritos) { this.creditosInscritos = creditosInscritos; }
    public List<Integer> getInscripcionesIds() { return inscripcionesIds; }
    public void addInscripcion(int id) { this.inscripcionesIds.add(id); }
    
    // CORRECCIÓN: Usamos Integer.valueOf(id) para remover el objeto por valor, no por índice.
    public void removeInscripcion(int id) { 
        this.inscripcionesIds.remove(Integer.valueOf(id)); 
    }
    
    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Créditos: " + creditosInscritos;
    }
}