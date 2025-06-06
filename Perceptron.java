import java.util.*;

public class Perceptron {
    private double[] pesos;
    private double bias;
    private final double tasaAprendizaje;
    private final int maxIteraciones;
    private int[] erroresPorEpoca;

    public Perceptron(double tasaAprendizaje, int maxIteraciones) {
        this.tasaAprendizaje = tasaAprendizaje;
        this.maxIteraciones = maxIteraciones;
    }

    private int funcionActivacion(double z) {
        return z >= 0 ? 1 : 0;
    }

    public void entrenar(double[][] X, int[] y) {
        System.out.println("Iniciando entrenamiento del Perceptrón...");
        
        int nCaracteristicas = X[0].length;
        this.pesos = new double[nCaracteristicas];
        Random rand = new Random();
        for (int i = 0; i < nCaracteristicas; i++) {
            pesos[i] = rand.nextGaussian() * 0.01;
        }
        this.bias = 0;
        this.erroresPorEpoca = new int[maxIteraciones];
        
        System.out.println("   - Características: " + nCaracteristicas);
        System.out.println("   - Muestras de entrenamiento: " + X.length);
        System.out.println("   - Pesos iniciales: " + Arrays.toString(pesos));
        System.out.println("   - Bias inicial: " + bias);
        
        for (int epoca = 0; epoca < maxIteraciones; epoca++) {
            int errores = 0;
            
            for (int i = 0; i < X.length; i++) {
                double z = productoPunto(X[i], pesos) + bias;
                int prediccion = funcionActivacion(z);
                
                int error = y[i] - prediccion;
                
                if (error != 0) {
                    for (int j = 0; j < pesos.length; j++) {
                        pesos[j] += tasaAprendizaje * error * X[i][j];
                    }
                    bias += tasaAprendizaje * error;
                    errores++;
                }
            }
            
            erroresPorEpoca[epoca] = errores;

            if ((epoca + 1) % 10 == 0 || epoca == 0) {
                System.out.printf("   Época %3d: %d errores%n", epoca + 1, errores);
            }

            if (errores == 0) {
                System.out.printf("   ¡Convergencia alcanzada en época %d!%n", epoca + 1);
                this.erroresPorEpoca = Arrays.copyOf(erroresPorEpoca, epoca + 1);
                break;
            }
        }
        
        System.out.println("   - Pesos finales: " + Arrays.toString(pesos));
        System.out.println("   - Bias final: " + bias);
        System.out.println("Entrenamiento completado!\n");
    }
    
    private double productoPunto(double[] a, double[] b) {
        double resultado = 0;
        for (int i = 0; i < a.length; i++) {
            resultado += a[i] * b[i];
        }
        return resultado;
    }
    
    public int[] predecir(double[][] X) {
        int[] predicciones = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            double z = productoPunto(X[i], pesos) + bias;
            predicciones[i] = funcionActivacion(z);
        }
        return predicciones;
    }
    
    public double calcularPrecision(double[][] X, int[] y) {
        int[] predicciones = predecir(X);
        int correctos = 0;
        for (int i = 0; i < y.length; i++) {
            if (predicciones[i] == y[i]) {
                correctos++;
            }
        }
        return (double) correctos / y.length;
    }
    
    public void mostrarEcuacion() {
        System.out.println("Ecuación del Perceptrón:");
        StringBuilder ecuacion = new StringBuilder("   y = step(");
        
        for (int i = 0; i < pesos.length; i++) {
            if (i > 0) {
                ecuacion.append(pesos[i] >= 0 ? " + " : " - ")
                        .append(String.format("%.3f", Math.abs(pesos[i])))
                        .append("*x").append(i+1);
            } else {
                ecuacion.append(String.format("%.3f", pesos[i])).append("*x").append(i+1);
            }
        }
        
        if (bias >= 0) {
            ecuacion.append(" + ").append(String.format("%.3f", bias)).append(")");
        } else {
            ecuacion.append(" - ").append(String.format("%.3f", Math.abs(bias))).append(")");
        }
        
        System.out.println(ecuacion.toString());
        System.out.println("   donde step(z) = 1 si z >= 0, sino 0\n");
    }
    
    public int[] getErroresPorEpoca() {
        return erroresPorEpoca;
    }

    public static Datos generarDatosAND() {
        System.out.println("Generando datos para compuerta AND...");
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {0, 0, 0, 1}; // Solo 1,1 -> 1
        
        System.out.println("   Tabla de verdad AND:");
        System.out.println("   x1 | x2 | salida");
        System.out.println("   ---|----|---------");
        for (int i = 0; i < X.length; i++) {
            System.out.printf("   %d  | %d  |   %d%n", (int)X[i][0], (int)X[i][1], y[i]);
        }
        System.out.println();
        
        return new Datos(X, y, "Compuerta AND");
    }
    
    public static Datos generarDatosOR() {
        System.out.println("Generando datos para compuerta OR...");
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {0, 1, 1, 1}; // Solo 0,0 -> 0
        
        System.out.println("   Tabla de verdad OR:");
        System.out.println("   x1 | x2 | salida");
        System.out.println("   ---|----|---------");
        for (int i = 0; i < X.length; i++) {
            System.out.printf("   %d  | %d  |   %d%n", (int)X[i][0], (int)X[i][1], y[i]);
        }
        System.out.println();
        
        return new Datos(X, y, "Compuerta OR");
    }
    
    public static Datos generarDatosLineales() {
        System.out.println("Generando datos linealmente separables...");
        Random rand = new Random(42);
        
        double[][] clase0 = new double[20][2];
        for (int i = 0; i < clase0.length; i++) {
            clase0[i][0] = rand.nextGaussian() * 0.5 + 1;
            clase0[i][1] = rand.nextGaussian() * 0.5 + 1;
        }
        
        double[][] clase1 = new double[20][2];
        for (int i = 0; i < clase1.length; i++) {
            clase1[i][0] = rand.nextGaussian() * 0.5 + 3;
            clase1[i][1] = rand.nextGaussian() * 0.5 + 3;
        }
        
        double[][] X = new double[40][2];
        int[] y = new int[40];
        
        System.arraycopy(clase0, 0, X, 0, 20);
        System.arraycopy(clase1, 0, X, 20, 20);
        Arrays.fill(y, 20, 40, 1);
        
        System.out.printf("   - %d puntos de clase 0%n", clase0.length);
        System.out.printf("   - %d puntos de clase 1%n", clase1.length);
        System.out.println();
        
        return new Datos(X, y, "Clasificación Lineal");
    }
    
    public static void probarPredicciones(Perceptron perceptron, double[][] X, int[] y, String nombreProblema) {
        System.out.println("Probando predicciones - " + nombreProblema);
        System.out.println("-".repeat(50));
        
        int[] predicciones = perceptron.predecir(X);
        double precision = perceptron.calcularPrecision(X, y);
        
        System.out.println("   Entrada  | Real | Predicción | ¿Correcto?");
        System.out.println("   ---------|------|------------|------------");
        
        for (int i = 0; i < X.length; i++) {
            String entrada = Arrays.toString(X[i]).replace("[", "").replace("]", "");
            int real = y[i];
            int pred = predicciones[i];
            String correcto = real == pred ? " Verdadero " : " Falso ";
            
            System.out.printf("   [%s] |  %d   |     %d      |     %s%n", 
                             entrada.replace(",", ", "), real, pred, correcto);
        }
        
        System.out.printf("%n     Precisión: %.3f (%.1f%%)%n%n", precision, precision * 100);
    }
    public static int menuPrincipal() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Perceptrón simple - Demostración");
        System.out.println("-".repeat(40));
        System.out.println("Elige qué problema quieres resolver:");
        System.out.println("1. Compuerta lógica AND");
        System.out.println("2. Compuerta lógica OR");
        System.out.println("3. Datos linealmente separables");
        
        while (true) {
            try {
                System.out.print("\nIngresa tu opción (1, 2 o 3): ");
                int opcion = scanner.nextInt();
                if (opcion >= 1 && opcion <= 3) {
                    return opcion;
                } else {
                    System.out.println("Por favor ingresa 1, 2 o 3");
                }
            } catch (Exception e) {
                System.out.println("Por favor ingresa un número válido");
                scanner.next();
            }
        }
    }
    
    public static void main(String[] args) {
        int opcion = menuPrincipal();
        
        Datos datos;
        switch (opcion) {
            case 1:
                datos = generarDatosAND();
                break;
            case 2:
                datos = generarDatosOR();
                break;
            case 3:
                datos = generarDatosLineales();
                break;
            default:
                datos = generarDatosAND(); 
        }
        
        Perceptron perceptron = new Perceptron(0.1, 100);
        perceptron.entrenar(datos.X, datos.y);

        perceptron.mostrarEcuacion();

        probarPredicciones(perceptron, datos.X, datos.y, datos.titulo);
        
        System.out.println(" ¡Demostración del Perceptrón completada!");
    }

    static class Datos {
        double[][] X;
        int[] y;
        String titulo;
        
        public Datos(double[][] X, int[] y, String titulo) {
            this.X = X;
            this.y = y;
            this.titulo = titulo;
        }
    }
}