public class Materia {
    private String id;
    private String nombre;
    private int creditos;
    private String estadoCurso; // Ej: "Activo", "Vacacional", "Cerrado"

    public Materia(String id, String nombre, int creditos, String estadoCurso) {
        this.id = id;
        this.nombre = nombre;
        this.creditos = creditos;
        this.estadoCurso = estadoCurso;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCreditos() { return creditos; }
    public String getEstadoCurso() { return estadoCurso; }

    // --- Setters Añadidos para la modificación administrativa ---
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCreditos(int creditos) { this.creditos = creditos; }
    public void setEstadoCurso(String estadoCurso) { this.estadoCurso = estadoCurso; }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + creditos + " créditos) [" + estadoCurso + "]";
    }
}