import java.util.Random;

public class CNN {
    static class Tensor {
        double[][][][] datos;
        int profundidad, altura, ancho;
        
        Tensor(int p, int h, int a) {
            profundidad = p;
            altura = h;
            ancho = a;
            datos = new double[p][h][a][1];
        }
    }

    private static final int TAM_IMAGEN = 32;
    private static final int CANALES = 3;
    private static final int NUM_CLASES = 10;
    private static final String[] NOMBRES_CLASES = {
        "vaca", "rana", "ave", "gato", "oveja",
        "perro", "iguana", "salamandra", "hormiga", "paloma"
    };
    
    static class CapaConvolucional {
        Tensor filtros;
        double[] sesgos;
        int numFiltros;
        int tamFiltro;
        int paso;
        
        CapaConvolucional(int numFiltros, int tamFiltro, int paso) {
            this.numFiltros = numFiltros;
            this.tamFiltro = tamFiltro;
            this.paso = paso;
            this.filtros = new Tensor(numFiltros, tamFiltro, tamFiltro);
            this.sesgos = new double[numFiltros];
            inicializarPesos();
        }
        
        void inicializarPesos() {
            Random rand = new Random(42);
            for (int f = 0; f < numFiltros; f++) {
                for (int i = 0; i < tamFiltro; i++) {
                    for (int j = 0; j < tamFiltro; j++) {
                        filtros.datos[f][i][j][0] = rand.nextGaussian() * 0.1;
                    }
                }
                sesgos[f] = 0.1;
            }
        }
        
        Tensor propagarAdelante(Tensor entrada) {
            int tamSalida = (entrada.altura - tamFiltro) / paso + 1;
            Tensor salida = new Tensor(numFiltros, tamSalida, tamSalida);
            
            for (int f = 0; f < numFiltros; f++) {
                for (int i = 0; i < tamSalida; i++) {
                    for (int j = 0; j < tamSalida; j++) {
                        double suma = sesgos[f];
                        for (int di = 0; di < tamFiltro; di++) {
                            for (int dj = 0; dj < tamFiltro; dj++) {
                                suma += entrada.datos[0][i*paso + di][j*paso + dj][0] * 
                                       filtros.datos[f][di][dj][0];
                            }
                        }
                        salida.datos[f][i][j][0] = Math.max(0, suma);
                    }
                }
            }
            return salida;
        }
    }

    static class CapaPoolingMax {
        int tamPool;
        int paso;
        
        CapaPoolingMax(int tamPool, int paso) {
            this.tamPool = tamPool;
            this.paso = paso;
        }
        
        Tensor propagarAdelante(Tensor entrada) {
            int tamSalida = (entrada.altura - tamPool) / paso + 1;
            Tensor salida = new Tensor(entrada.profundidad, tamSalida, tamSalida);
            
            for (int f = 0; f < entrada.profundidad; f++) {
                for (int i = 0; i < tamSalida; i++) {
                    for (int j = 0; j < tamSalida; j++) {
                        double maxVal = Double.NEGATIVE_INFINITY;
                        for (int di = 0; di < tamPool; di++) {
                            for (int dj = 0; dj < tamPool; dj++) {
                                double val = entrada.datos[f][i*paso + di][j*paso + dj][0];
                                if (val > maxVal) {
                                    maxVal = val;
                                }
                            }
                        }
                        
                        salida.datos[f][i][j][0] = maxVal;
                    }
                }
            }
            
            return salida;
        }
    }
    
    static class CapaDensa {
        double[][] pesos;
        double[] sesgos;
        int tamEntrada;
        int tamSalida;
        
        CapaDensa(int tamEntrada, int tamSalida) {
            this.tamEntrada = tamEntrada;
            this.tamSalida = tamSalida;
            this.pesos = new double[tamSalida][tamEntrada];
            this.sesgos = new double[tamSalida];
            inicializarPesos();
        }
        
        void inicializarPesos() {
            Random rand = new Random(42);
            for (int i = 0; i < tamSalida; i++) {
                for (int j = 0; j < tamEntrada; j++) {
                    pesos[i][j] = rand.nextGaussian() * 0.1;
                }
                sesgos[i] = 0.1;
            }
        }
        
        double[] propagarAdelante(double[] entrada) {
            double[] salida = new double[tamSalida];
            
            for (int i = 0; i < tamSalida; i++) {
                salida[i] = sesgos[i];
                for (int j = 0; j < tamEntrada; j++) {
                    salida[i] += pesos[i][j] * entrada[j];
                }
                salida[i] = Math.max(0, salida[i]);
            }
            
            return salida;
        }
    }
    
    static class CapaSalida {
        double[][] pesos;
        double[] sesgos;
        int tamEntrada;
        int tamSalida;
        
        CapaSalida(int tamEntrada, int tamSalida) {
            this.tamEntrada = tamEntrada;
            this.tamSalida = tamSalida;
            this.pesos = new double[tamSalida][tamEntrada];
            this.sesgos = new double[tamSalida];
            inicializarPesos();
        }
        
        void inicializarPesos() {
            Random rand = new Random(42);
            for (int i = 0; i < tamSalida; i++) {
                for (int j = 0; j < tamEntrada; j++) {
                    pesos[i][j] = rand.nextGaussian() * 0.1;
                }
                sesgos[i] = 0.1;
            }
        }
        
        double[] propagarAdelante(double[] entrada) {
            double[] salida = new double[tamSalida];
            double max = Double.NEGATIVE_INFINITY;
            double suma = 0.0;
            
            for (int i = 0; i < tamSalida; i++) {
                salida[i] = sesgos[i];
                for (int j = 0; j < tamEntrada; j++) {
                    salida[i] += pesos[i][j] * entrada[j];
                }
                if (salida[i] > max) {
                    max = salida[i];
                }
            }
            
            for (int i = 0; i < tamSalida; i++) {
                salida[i] = Math.exp(salida[i] - max);
                suma += salida[i];
            }
            
            for (int i = 0; i < tamSalida; i++) {
                salida[i] /= suma;
            }
            
            return salida;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Red Neuronal Convolucional para CIFAR-10");
        
        CapaConvolucional conv1 = new CapaConvolucional(32, 3, 1);  
        CapaPoolingMax pool1 = new CapaPoolingMax(2, 2);          
        
        CapaConvolucional conv2 = new CapaConvolucional(64, 3, 1);  
        CapaPoolingMax pool2 = new CapaPoolingMax(2, 2);           
        
        CapaConvolucional conv3 = new CapaConvolucional(128, 3, 1); 
        CapaPoolingMax pool3 = new CapaPoolingMax(2, 2);          
        
        CapaDensa densa1 = new CapaDensa(128 * 2 * 2, 128);
        CapaDensa densa2 = new CapaDensa(128, 64);
        CapaSalida salida = new CapaSalida(64, NUM_CLASES);
        
        System.out.println("\nSimulando propagación hacia adelante...");
        
        Tensor entrada = new Tensor(1, TAM_IMAGEN, TAM_IMAGEN);
        Random rand = new Random();
        for (int i = 0; i < TAM_IMAGEN; i++) {
            for (int j = 0; j < TAM_IMAGEN; j++) {
                entrada.datos[0][i][j][0] = rand.nextDouble();
            }
        }
        
        Tensor x = conv1.propagarAdelante(entrada);
        System.out.println("Tras conv1: " + x.profundidad + "x" + x.altura + "x" + x.ancho);
        
        x = pool1.propagarAdelante(x);
        System.out.println("Tras pool1: " + x.profundidad + "x" + x.altura + "x" + x.ancho);
        
        x = conv2.propagarAdelante(x);
        System.out.println("Tras conv2: " + x.profundidad + "x" + x.altura + "x" + x.ancho);
        
        x = pool2.propagarAdelante(x);
        System.out.println("Tras pool2: " + x.profundidad + "x" + x.altura + "x" + x.ancho);
        
        x = conv3.propagarAdelante(x);
        System.out.println("Tras conv3: " + x.profundidad + "x" + x.altura + "x" + x.ancho);
        
        x = pool3.propagarAdelante(x);
        System.out.println("Tras pool3: " + x.profundidad + "x" + x.altura + "x" + x.ancho);

        double[] aplanado = aplanarTensor(x);
        System.out.println("Tamaño aplanado: " + aplanado.length);

        double[] d1 = densa1.propagarAdelante(aplanado);
        double[] d2 = densa2.propagarAdelante(d1);
        double[] predicciones = salida.propagarAdelante(d2);

        System.out.println("\nProbabilidades de clasificación:");
        for (int i = 0; i < NUM_CLASES; i++) {
            System.out.printf("%-12s: %.4f\n", NOMBRES_CLASES[i], predicciones[i]);
        }
        
        int clasePredicha = indiceMaximo(predicciones);
        System.out.printf("\nPredicción: %s (%.2f%% de confianza)\n", 
            NOMBRES_CLASES[clasePredicha], predicciones[clasePredicha]*100);
    }

    private static double[] aplanarTensor(Tensor t) {
        double[] vector = new double[t.profundidad * t.altura * t.ancho];
        int idx = 0;
        for (int p = 0; p < t.profundidad; p++) {
            for (int h = 0; h < t.altura; h++) {
                for (int a = 0; a < t.ancho; a++) {
                    vector[idx++] = t.datos[p][h][a][0];
                }
            }
        }
        return vector;
    }

    private static int indiceMaximo(double[] array) {
        int indiceMax = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[indiceMax]) {
                indiceMax = i;
            }
        }
        return indiceMax;
    }
}