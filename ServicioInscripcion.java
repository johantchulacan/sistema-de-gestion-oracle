import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ServicioInscripcion {
    // --- PARÁMETROS DEL SISTEMA ---
    private static final int MAX_CREDITOS = 22;
    private static final int MIN_CREDITOS = 10;
    
    // FECHAS LÍMITE (Hardcodeadas para simulación)
    private static final LocalDate FECHA_LIMITE_TRANSACCION = LocalDate.of(2025, 12, 01
    );
    private static final LocalDate FECHA_LIMITE_BAJA_RETENER = LocalDate.of(2025, 12, 15);

    // Simulación de Base de Datos (Mapas en memoria)
    private Map<Integer, Estudiante> estudiantes = new HashMap<>();
    private Map<String, Materia> materias = new HashMap<>();
    private Map<Integer, Inscripcion> inscripciones = new HashMap<>();
    private AtomicInteger inscripcionIdCounter = new AtomicInteger(1);

    public ServicioInscripcion() {
        // Inicializar Materias
        materias.put("MAT101", new Materia("MAT101", "Cálculo I", 4, "Activo"));
        materias.put("FIS202", new Materia("FIS202", "Física Básica", 5, "Activo"));
        materias.put("PRO303", new Materia("PRO303", "Prog. Avanzada", 6, "Activo"));
        materias.put("HIS404", new Materia("HIS404", "Historia Univ.", 3, "Vacacional"));
        
        // Inicializar Estudiantes
        estudiantes.put(1001, new Estudiante(1001, "Juan Perez"));
        estudiantes.put(1002, new Estudiante(1002, "Ana Gomez"));
    }
    
    // =======================================================
    // --- CONSULTAS Y UTILIDAD ---
    // =======================================================
    
    public List<Materia> getTodasLasMaterias() {
        return new ArrayList<>(materias.values());
    }
    
    public Estudiante getEstudiante(int id) { return estudiantes.get(id); }
    public Materia getMateria(String id) { return materias.get(id); }
    
    public void registrarNuevoAlumno(int id, String nombre) {
        if (estudiantes.containsKey(id)) {
            System.out.println("LOG: El ID de alumno ya existe.");
            return;
        }
        estudiantes.put(id, new Estudiante(id, nombre));
        System.out.println("LOG: Nuevo alumno registrado: " + nombre);
    }
    
    // =======================================================
    // --- FUNCIONES ADMINISTRATIVAS (AGREGAR/MODIFICAR) ---
    // =======================================================
    
    public void agregarNuevaMateria(String id, String nombre, int creditos, String estado) throws TransaccionException {
        if (materias.containsKey(id.toUpperCase())) {
            throw new TransaccionException("Ya existe una materia con el ID: " + id);
        }
        if (creditos <= 0) {
            throw new TransaccionException("Los créditos deben ser positivos.");
        }
        Materia nuevaMateria = new Materia(id.toUpperCase(), nombre, creditos, estado);
        materias.put(id.toUpperCase(), nuevaMateria);
        System.out.println("LOG: Materia " + id.toUpperCase() + " agregada con éxito.");
    }
    
    public void modificarMateriaExistente(String id, String nuevoNombre, Integer nuevosCreditos, String nuevoEstado) throws TransaccionException {
        Materia materia = materias.get(id.toUpperCase());
        if (materia == null) {
            throw new TransaccionException("Materia con ID " + id + " no encontrada.");
        }
        
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) { materia.setNombre(nuevoNombre); }
        if (nuevosCreditos != null && nuevosCreditos > 0) { materia.setCreditos(nuevosCreditos); }
        if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) { materia.setEstadoCurso(nuevoEstado); }
        
        System.out.println("LOG: Materia " + id.toUpperCase() + " modificada con éxito.");
    }

    public List<Estudiante> getEstudiantesInscritosEnMateria(String materiaId) {
        return inscripciones.values().stream()
            .filter(i -> i.getMateriaId().equals(materiaId) && i.getEstado().equals("Inscrito"))
            .map(i -> estudiantes.get(i.getEstudianteId()))
            .collect(Collectors.toList());
    }
    
    public List<String> getEstadoCursosEstudiante(int estudianteId) {
        return inscripciones.values().stream()
            .filter(i -> i.getEstudianteId() == estudianteId)
            .map(i -> {
                Materia m = materias.get(i.getMateriaId());
                String estado = i.getEstado();
                if (estado.equals("Inscrito") && m.getEstadoCurso().equals("Vacacional")) {
                    estado = "Inscrito (Vacacional)";
                }
                String nombreMateria = (m != null) ? m.getNombre() : "Materia Desconocida"; 
                return "[ID Insc: " + i.getId() + "] " + nombreMateria + " (" + m.getCreditos() + " Cr.): " + estado;
            })
            .collect(Collectors.toList());
    }
    
    // =======================================================
    // I. ADICIÓN (INSCRIPCIÓN) DE CURSOS (MODIFICADO)
    // =======================================================

    /**
     * Inscribe un solo curso y actualiza los créditos con el total calculado.
     * Ya no valida MAX/MIN. Esa lógica está en SistemaInscripcionesMain.
     */
    public Inscripcion inscribirCurso(int estudianteId, String materiaId, int creditosTotalesFinales) 
            throws TransaccionException {
        
        LocalDate hoy = LocalDate.now();
        Estudiante estudiante = estudiantes.get(estudianteId);
        Materia materia = materias.get(materiaId);

        if (estudiante == null || materia == null) {
              throw new TransaccionException("Estudiante o Materia no encontrada.");
        }
        
        // --- 1. VALIDACIÓN DE FECHA LÍMITE ---
        if (hoy.isAfter(FECHA_LIMITE_TRANSACCION)) {
            throw new TransaccionException("La fecha límite para añadir cursos ha expirado (" + FECHA_LIMITE_TRANSACCION + ").");
        }

        // Validación de curso duplicado
        boolean yaInscrito = estudiante.getInscripcionesIds().stream()
            .map(inscripciones::get)
            .anyMatch(i -> i != null && i.getMateriaId().equals(materiaId) && i.getEstado().equals("Inscrito"));
        
        if (yaInscrito) {
            throw new TransaccionException("El estudiante ya se encuentra inscrito en el curso " + materiaId + ".");
        }
        
        // --- EJECUCIÓN ---
        int nuevaInscripcionId = inscripcionIdCounter.getAndIncrement();
        Inscripcion inscripcion = new Inscripcion(nuevaInscripcionId, estudianteId, materiaId);
        
        inscripciones.put(nuevaInscripcionId, inscripcion);
        estudiante.setCreditosInscritos(creditosTotalesFinales); // Asigna el total calculado
        estudiante.addInscripcion(nuevaInscripcionId);
        
        return inscripcion;
    }
    
    // =======================================================
    // II. BAJA DE CURSOS
    // =======================================================

    public void bajarCurso(int inscripcionId) throws TransaccionException, CreditoException {
        LocalDate hoy = LocalDate.now();
        Inscripcion inscripcion = inscripciones.get(inscripcionId);

        if (inscripcion == null || !inscripcion.getEstado().equals("Inscrito")) {
            throw new TransaccionException("Inscripción no activa o no encontrada.");
        }
        
        Estudiante estudiante = estudiantes.get(inscripcion.getEstudianteId());
        Materia materia = materias.get(inscripcion.getMateriaId());

        if (hoy.isAfter(FECHA_LIMITE_BAJA_RETENER)) {
            throw new TransaccionException("Error: La fecha límite para cualquier tipo de baja ha expirado.");
        }
        
        // VALIDACIÓN DE CRÉDITOS MÍNIMOS 
        int creditosRestantes = estudiante.getCreditosInscritos() - materia.getCreditos();
        
        if (creditosRestantes > 0 && creditosRestantes < MIN_CREDITOS) {
             throw new CreditoException("La baja no es posible. El total de créditos no puede ser inferior a " + MIN_CREDITOS + ".");
        }

        // LÓGICA DE BAJA SEGÚN FECHA
        if (hoy.isBefore(FECHA_LIMITE_TRANSACCION) || hoy.isEqual(FECHA_LIMITE_TRANSACCION)) {
            inscripciones.remove(inscripcionId);
            estudiante.removeInscripcion(inscripcionId);
            System.out.println("LOG: Curso eliminado del sistema.");
        } else {
            inscripcion.setEstado("Baja");
            inscripcion.setFechaBaja(hoy);
            System.out.println("LOG: Curso retenido con estado 'Baja'.");
        }
        
        estudiante.setCreditosInscritos(creditosRestantes);
    }

    // =======================================================
    // III. SUSTITUCIÓN DE CURSOS
    // =======================================================

    public Inscripcion sustituirCurso(int estudianteId, String materiaIdAntigua, String materiaIdNueva) 
            throws TransaccionException, CreditoException {
        
        LocalDate hoy = LocalDate.now();
        Estudiante estudiante = estudiantes.get(estudianteId);
        Materia antigua = materias.get(materiaIdAntigua);
        Materia nueva = materias.get(materiaIdNueva);

        if (estudiante == null || antigua == null || nueva == null) {
            throw new TransaccionException("Datos de estudiante/materia inválidos.");
        }
        
        if (hoy.isAfter(FECHA_LIMITE_TRANSACCION)) {
            throw new TransaccionException("La fecha límite para sustituir cursos ha expirado.");
        }

        Inscripcion inscripcionAntigua = estudiante.getInscripcionesIds().stream()
            .map(inscripciones::get)
            .filter(i -> i != null && i.getMateriaId().equals(materiaIdAntigua) && i.getEstado().equals("Inscrito"))
            .findFirst()
            .orElseThrow(() -> new TransaccionException("El estudiante no tiene una inscripción activa para el curso antiguo."));
        
        // VALIDACIÓN DE CRÉDITOS
        int creditosResultantes = estudiante.getCreditosInscritos() - antigua.getCreditos() + nueva.getCreditos();
        
        if (creditosResultantes > MAX_CREDITOS) {
            throw new CreditoException("La sustitución excede el límite de " + MAX_CREDITOS + " créditos.");
        }
        
        if (creditosResultantes < MIN_CREDITOS) {
            throw new CreditoException("La sustitución no es posible. El total de créditos no puede ser inferior a " + MIN_CREDITOS + ".");
        }
        
        // EJECUCIÓN DE SUSTITUCIÓN
        // a) Baja del curso antiguo
        inscripciones.remove(inscripcionAntigua.getId());
        estudiante.removeInscripcion(inscripcionAntigua.getId());
        
        // b) Alta del curso nuevo
        int nuevaInscripcionId = inscripcionIdCounter.getAndIncrement();
        Inscripcion nuevaInscripcion = new Inscripcion(nuevaInscripcionId, estudianteId, materiaIdNueva);
        
        inscripciones.put(nuevaInscripcionId, nuevaInscripcion);
        estudiante.addInscripcion(nuevaInscripcionId);
        
        // c) Actualizar créditos
        estudiante.setCreditosInscritos(creditosResultantes);
        System.out.println("LOG: Sustitución ejecutada. Créditos finales: " + creditosResultantes);
        
        return nuevaInscripcion;
    }
}