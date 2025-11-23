import java.time.LocalDate;

public class Inscripcion {
    private int id;
    private int estudianteId;
    private String materiaId;
    private String estado; // "Inscrito", "Baja", etc.
    private LocalDate fechaInscripcion;
    private LocalDate fechaBaja;

    public Inscripcion(int id, int estudianteId, String materiaId) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.materiaId = materiaId;
        this.estado = "Inscrito";
        this.fechaInscripcion = LocalDate.now();
    }

    // --- Getters y Setters ---
    public int getId() { return id; }
    public int getEstudianteId() { return estudianteId; }
    public String getMateriaId() { return materiaId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }

    @Override
    public String toString() {
        return "ID: " + id + ", Curso: " + materiaId + ", Estado: " + estado + 
                (fechaBaja != null ? " (Baja el: " + fechaBaja + ")" : "");
    }
}