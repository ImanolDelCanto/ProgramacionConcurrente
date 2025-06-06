// =========================================================================
// QUICKSORT SECUENCIAL Y CONCURRENTE
// Basado en: Cormen, T. H. et al. "Introduction to Algorithms" 4th Edition
// Y Oracle Java Documentation: Fork/Join Framework
// =========================================================================

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.Random;

// =========================================================================
// CLASE PRINCIPAL PARA DEMOSTRACION
// =========================================================================
public class QuickSort {
    
 public static void main(String[] args) {
        // =====================================================================
        // INFORMACIÓN DEL SISTEMA 
        // =====================================================================
        System.out.println("=== INFORMACIÓN DEL SISTEMA ===");
        System.out.println("Procesadores disponibles: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Memoria máxima JVM: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Sistema Operativo: " + System.getProperty("os.name"));
        System.out.println("Arquitectura: " + System.getProperty("os.arch"));
        System.out.println("");
        
        // =====================================================================
        // OPCIÓN 1: PRUEBAS BÁSICAS (menos detalladas)
        // =====================================================================
        System.out.println("=== PRUEBAS BÁSICAS ===");
        pruebasBasicas();
        
        System.out.println("\n" + "=".repeat(80));
        
        // =====================================================================
        // OPCIÓN 2: PRUEBAS COMPLETAS CON MÉTRICAS (más detalladas)
        // =====================================================================
        System.out.println("=== PRUEBAS COMPLETAS CON MÉTRICAS ===");
        MedicionRendimiento.medirRendimientoCompleto();
    }
    
    // Método que mantiene tus pruebas originales
    private static void pruebasBasicas() {
        // Creamos diferentes tamaños de arrays para probar
        int[] tamaños = {1000, 10000, 100000, 1000000};
        
        // Iteramos sobre cada tamaño de array
        for (int tamaño : tamaños) {
            System.out.println("\n=== PROBANDO CON ARRAY DE " + tamaño + " ELEMENTOS ===");
            
            // Generamos array aleatorio para la prueba
            int[] arrayOriginal = generarArrayAleatorio(tamaño);
            
            // Probamos versión secuencial
            probarVersionSecuencial(arrayOriginal.clone());
            
            // Probamos versión concurrente
            probarVersionConcurrente(arrayOriginal.clone());
        }
    }
    
    // Método para generar array con números aleatorios
    private static int[] generarArrayAleatorio(int tamaño) {
        // Creamos objeto Random para generar números aleatorios
        Random random = new Random();
        // Creamos array del tamaño especificado
        int[] array = new int[tamaño];
        
        // Llenamos el array con números aleatorios entre 0 y 999999
        for (int i = 0; i < tamaño; i++) {
            array[i] = random.nextInt(1000000);
        }
        
        // Retornamos el array generado
        return array;
    }
    
    // Método para probar la versión secuencial
    private static void probarVersionSecuencial(int[] array) {
        // Registramos el tiempo de inicio
        long tiempoInicio = System.currentTimeMillis();
        
        // Ejecutamos QuickSort secuencial
        QuickSortSecuencial.quickSort(array, 0, array.length - 1);
        
        // Calculamos el tiempo transcurrido
        long tiempoFin = System.currentTimeMillis();
        long tiempoTotal = tiempoFin - tiempoInicio;
        
        // Mostramos resultados
        System.out.println("QuickSort Secuencial: " + tiempoTotal + " ms");
    }
    
    // Método para probar la versión concurrente
    private static void probarVersionConcurrente(int[] array) {
        // Registramos el tiempo de inicio
        long tiempoInicio = System.currentTimeMillis();
        
        // Creamos el pool de hilos ForkJoin usando try-with-resources
        // Esto garantiza que el pool se cierre automáticamente
        try (ForkJoinPool pool = new ForkJoinPool()) {
            // Creamos la tarea concurrente
            QuickSortConcurrente tarea = new QuickSortConcurrente(array, 0, array.length - 1);
            
            // Ejecutamos la tarea en el pool
            pool.invoke(tarea);
            
        } // El pool se cierra automáticamente aquí
        
        // Calculamos el tiempo transcurrido
        long tiempoFin = System.currentTimeMillis();
        long tiempoTotal = tiempoFin - tiempoInicio;
        
        // Mostramos resultados
        System.out.println("QuickSort Concurrente: " + tiempoTotal + " ms");
    }
}

// =========================================================================
// IMPLEMENTACION QUICKSORT SECUENCIAL
// Basado en algoritmo de Cormen et al. "Introduction to Algorithms"
// =========================================================================
class QuickSortSecuencial {
    
    // Método principal que inicia el algoritmo QuickSort
    public static void quickSort(int[] array, int inicio, int fin) {
        // Condición base: solo procesamos si hay más de un elemento
        if (inicio < fin) {
            // Particionamos el array y obtenemos la posición del pivote
            int indicePivote = partition(array, inicio, fin);
            
            // Recursivamente ordenamos la sublista izquierda (elementos menores al pivote)
            quickSort(array, inicio, indicePivote - 1);
            
            // Recursivamente ordenamos la sublista derecha (elementos mayores al pivote)
            quickSort(array, indicePivote + 1, fin);
        }
    }
    
    // Método de partición que coloca el pivote en su posición correcta
    private static int partition(int[] array, int inicio, int fin) {
        // Seleccionamos el último elemento como pivote
        int pivote = array[fin];
        
        // Índice del elemento más pequeño, indica la posición correcta del pivote
        int indiceMenor = inicio - 1;
        
        // Recorremos desde inicio hasta fin-1
        for (int j = inicio; j < fin; j++) {
            // Si el elemento actual es menor o igual al pivote
            if (array[j] <= pivote) {
                // Incrementamos el índice del elemento menor
                indiceMenor++;
                
                // Intercambiamos el elemento actual con el elemento en indiceMenor
                intercambiar(array, indiceMenor, j);
            }
        }
        
        // Colocamos el pivote en su posición correcta
        intercambiar(array, indiceMenor + 1, fin);
        
        // Retornamos la posición del pivote
        return indiceMenor + 1;
    }
    
    // Método auxiliar para intercambiar dos elementos en el array
    private static void intercambiar(int[] array, int i, int j) {
        // Guardamos temporalmente el valor en la posición i
        int temp = array[i];
        
        // Copiamos el valor de la posición j a la posición i
        array[i] = array[j];
        
        // Colocamos el valor temporal en la posición j
        array[j] = temp;
    }
}

// =========================================================================
// IMPLEMENTACION QUICKSORT CONCURRENTE
// Basado en Fork/Join Framework de Java
// Oracle Documentation: https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html
// =========================================================================
class QuickSortConcurrente extends RecursiveAction {
    
    
    
    // Array a ordenar
    private final int[] array;
    
    // Índices de inicio y fin del segmento a procesar
    private final int inicio;
    private final int fin;
    
    // Constructor que inicializa la tarea concurrente
    public QuickSortConcurrente(int[] array, int inicio, int fin) {
        // Asignamos la referencia al array
        this.array = array;
        
        // Establecemos los límites del segmento a procesar
        this.inicio = inicio;
        this.fin = fin;
    }
    
    // Método principal que define la lógica de la tarea concurrente
    @Override
    protected void compute() {
        // Verificamos si tenemos elementos para procesar
        if (inicio < fin) {
            {
                // Para segmentos grandes, aplicamos paralelización
                
                // Particionamos el array y obtenemos la posición del pivote
                int indicePivote = partitionConcurrente(array, inicio, fin);
                
                // Creamos dos subtareas para procesar en paralelo
                // Subtarea para la parte izquierda (elementos menores al pivote)
                QuickSortConcurrente tareaIzquierda = 
                    new QuickSortConcurrente(array, inicio, indicePivote - 1);
                
                // Subtarea para la parte derecha (elementos mayores al pivote)
                QuickSortConcurrente tareaDerecha = 
                    new QuickSortConcurrente(array, indicePivote + 1, fin);
                
                // Ejecutamos ambas subtareas en paralelo usando fork()
                // fork() programa la tarea para ejecución asíncrona
                tareaIzquierda.fork();
                tareaDerecha.fork();
                
                // Esperamos a que ambas subtareas terminen usando join()
                // join() espera la finalización de la tarea y retorna el resultado
                tareaIzquierda.join();
                tareaDerecha.join();
            }
        }
    }
    
    // Método de partición específico para la versión concurrente
    private static int partitionConcurrente(int[] array, int inicio, int fin) {
        // Seleccionamos el último elemento como pivote
        int pivote = array[fin];
        
        // Índice del elemento más pequeño
        int indiceMenor = inicio - 1;
        
        // Recorremos el segmento desde inicio hasta fin-1
        for (int j = inicio; j < fin; j++) {
            // Si el elemento actual es menor o igual al pivote
            if (array[j] <= pivote) {
                // Incrementamos el índice del elemento menor
                indiceMenor++;
                
                // Intercambiamos elementos
                intercambiarConcurrente(array, indiceMenor, j);
            }
        }
        
        // Colocamos el pivote en su posición final
        intercambiarConcurrente(array, indiceMenor + 1, fin);
        
        // Retornamos la posición del pivote
        return indiceMenor + 1;
    }
    
    // Método auxiliar para intercambio en versión concurrente
    private static void intercambiarConcurrente(int[] array, int i, int j) {
        // Guardamos temporalmente el valor en la posición i
        int temp = array[i];
        
        // Realizamos el intercambio
        array[i] = array[j];
        array[j] = temp;
    }
}

// =========================================================================
// CLASE PARA MEDICION DETALLADA DE RENDIMIENTO
// =========================================================================
class MedicionRendimiento {
    
    // Método para realizar mediciones completas
    public static void medirRendimientoCompleto() {
        // Tamaños de arrays para probar
        int[] tamaños = {1000, 5000, 10000, 50000, 100000, 500000, 1000000};
        
        // Encabezado de la tabla de resultados
        System.out.println("\n=== COMPARATIVA DE RENDIMIENTO QUICKSORT ===");
        System.out.printf("%-10s %-20s %-20s %-10s %-15s%n", 
                         "Tamaño", "Secuencial(ms)", "Concurrente(ms)", "Speedup", "Eficiencia(%)");
        System.out.println("------------------------------------------------------------------------");
        
        // Iteramos sobre cada tamaño
        for (int tamaño : tamaños) {
            // Generamos array de prueba
            int[] arrayOriginal = generarArrayAleatorio(tamaño);
            
            // Medimos versión secuencial
            long tiempoSecuencial = medirTiempoSecuencial(arrayOriginal.clone());
            
            // Medimos versión concurrente
            long tiempoConcurrente = medirTiempoConcurrente(arrayOriginal.clone());
            
            // Calculamos métricas de rendimiento
            double speedup = (double) tiempoSecuencial / tiempoConcurrente;
            double eficiencia = (speedup / Runtime.getRuntime().availableProcessors()) * 100;
            
            // Mostramos resultados formateados
            System.out.printf("%-10d %-20d %-20d %-10.2f %-15.1f%n", 
                             tamaño, tiempoSecuencial, tiempoConcurrente, speedup, eficiencia);
        }
        
        // Información del sistema
        System.out.println("\n=== INFORMACIÓN DEL SISTEMA ===");
        System.out.println("Procesadores disponibles: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Memoria máxima JVM: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
    }
    
    // Generar array aleatorio para pruebas
    private static int[] generarArrayAleatorio(int tamaño) {
        Random random = new Random(42); // Semilla fija para reproducibilidad
        int[] array = new int[tamaño];
        
        for (int i = 0; i < tamaño; i++) {
            array[i] = random.nextInt(1000000);
        }
        
        return array;
    }
    
    // Medir tiempo de ejecución secuencial
    private static long medirTiempoSecuencial(int[] array) {
        long inicio = System.currentTimeMillis();
        QuickSortSecuencial.quickSort(array, 0, array.length - 1);
        return System.currentTimeMillis() - inicio;
    }
    
    // Medir tiempo de ejecución concurrente
    private static long medirTiempoConcurrente(int[] array) {
        long inicio = System.currentTimeMillis();
        
        // Usamos try-with-resources para manejo automático del pool
        try (ForkJoinPool pool = new ForkJoinPool()) {
            QuickSortConcurrente tarea = new QuickSortConcurrente(array, 0, array.length - 1);
            pool.invoke(tarea);
        } // El pool se cierra automáticamente aquí
        
        return System.currentTimeMillis() - inicio;
    }
}