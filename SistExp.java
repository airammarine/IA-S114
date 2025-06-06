// Sistema Experto de Diagnóstico Asistido con Lógica Difusa
import java.util.*;

public class DiagnósticoAsistido {
    private Map<String, Double> respuestas;
    private List<Map<String, Object>> reglas;
    private Scanner scanner = new Scanner(System.in);

    public DiagnósticoAsistido() {
        respuestas = new HashMap<>();
        baseConocimiento();
    }

    private void baseConocimiento() {
        reglas = new ArrayList<>();

        Map<String, Object> regla1 = new HashMap<>();
        regla1.put("condiciones", Arrays.asList(
                new Object[]{"no_inicia", 0.8},
                new Object[]{"sin_indicadores", 0.9}
        ));
        regla1.put("conclusion", "Falla en alimentación eléctrica");
        regla1.put("sugerencia", "Revise el cable de corriente y fuente de poder");
        regla1.put("confianza_base", 90.0);
        reglas.add(regla1);

        Map<String, Object> regla2 = new HashMap<>();
        regla2.put("condiciones", Arrays.asList(new Object[]{"enciende", 0.7}, new Object[]{"sin_imagen", 0.9}, new Object[]{"ventiladores_operando", 0.8}));
        regla2.put("conclusion", "Fallo en pantalla o tarjeta gráfica");
        regla2.put("sugerencia", "Verificar cableado de video y tarjeta de gráficos");
        regla2.put("confianza_base", 85.0);
        reglas.add(regla2);

        Map<String, Object> regla3 = new HashMap<>();
        regla3.put("condiciones", Arrays.asList(new Object[]{"enciende", 0.6}, new Object[]{"pitidos_repetidos", 0.95}));
        regla3.put("conclusion", "Problema con la memoria RAM");
        regla3.put("sugerencia", "Retirar y reinstalar los módulos de RAM");
        regla3.put("confianza_base", 88.0);
        reglas.add(regla3);

        Map<String, Object> regla4 = new HashMap<>();
        regla4.put("condiciones", Arrays.asList(new Object[]{"desempeño_bajo", 0.85}, new Object[]{"ruido_ventiladores", 0.75}));
        regla4.put("conclusion", "Sobrecalentamiento del procesador");
        regla4.put("sugerencia", "Limpiar sistema térmico y cambiar pasta disipadora");
        regla4.put("confianza_base", 75.0);
        reglas.add(regla4);

        Map<String, Object> regla5 = new HashMap<>();
        regla5.put("condiciones", Arrays.asList(new Object[]{"error_pantalla", 0.8}, new Object[]{"reinicios_aleatorios", 0.7}));
        regla5.put("conclusion", "Error de software o drivers");
        regla5.put("sugerencia", "Actualizar drivers y ejecutar diagnóstico en modo seguro");
        regla5.put("confianza_base", 70.0);
        reglas.add(regla5);

        Map<String, Object> regla6 = new HashMap<>();
        regla6.put("condiciones", Arrays.asList(new Object[]{"sin_internet", 0.9}, new Object[]{"otros_conectados", 0.85}));
        regla6.put("conclusion", "Fallo de configuración de red");
        regla6.put("sugerencia", "Reiniciar adaptador de red y revisar parámetros");
        regla6.put("confianza_base", 80.0);
        reglas.add(regla6);
    }

    private void datosUsuario() {
        System.out.println("-".repeat(62));
        System.out.println("| Sistema difuso de análisis - Identificación de fallas      |");
        System.out.println("| --> Indique el grado de certeza para cada síntoma (0-100%) |");
        System.out.println("-".repeat(62));

        Map<String, String> preguntas = new HashMap<>();
        preguntas.put("no_inicia", "¿Qué tan seguro está de que el equipo no enciende en absoluto? (0-100)%: ");
        preguntas.put("sin_indicadores", "¿Qué tan seguro está de que no hay luces ni señales visibles? (0-100)%: ");
        preguntas.put("enciende", "¿Qué tan seguro está de que el equipo arranca pero no funciona correctamente? (0-100)%: ");
        preguntas.put("sin_imagen", "¿Qué tan seguro está de que la pantalla permanece en negro? (0-100)%: ");
        preguntas.put("ventiladores_operando", "¿Qué tan seguro está de que se escuchan los ventiladores funcionando? (0-100)%: ");
        preguntas.put("pitidos_repetidos", "¿Qué tan seguro está de que se oyen sonidos repetitivos al encender? (0-100)%: ");
        preguntas.put("desempeño_bajo", "¿Qué tan seguro está de que el sistema responde lentamente? (0-100)%: ");
        preguntas.put("ruido_ventiladores", "¿Qué tan seguro está de que los ventiladores hacen ruido fuerte? (0-100)%: ");
        preguntas.put("error_pantalla", "¿Qué tan seguro está de que ha aparecido una pantalla azul con errores? (0-100)%: ");
        preguntas.put("reinicios_aleatorios", "¿Qué tan seguro está de que el equipo se reinicia inesperadamente? (0-100)%: ");
        preguntas.put("sin_internet", "¿Qué tan seguro está de que no tiene acceso a internet? (0-100)%: ");
        preguntas.put("otros_conectados", "¿Qué tan seguro está de que otros dispositivos se conectan sin problema? (0-100)%: ");

        for (Map.Entry<String, String> entry : preguntas.entrySet()) {
            String clave = entry.getKey();
            String pregunta = entry.getValue();
            
            while (true) {
                try {
                    System.out.print(pregunta);
                    String input = scanner.nextLine().replace("%", "");
                    double valor = Double.parseDouble(input) / 100.0;
                    
                    if (valor >= 0 && valor <= 1) {
                        respuestas.put(clave, valor);
                        break;
                    } else
                        System.out.println("Por favor ingresar un valor entre 0 y 100.");
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Ingrese un número entre 0 y 100.");
                }
            }
        }
    }

    private List<Map<String, Object>> EvaluacionDiagnostico() {
        List<Map<String, Object>> resultados = new ArrayList<>();
        
        for (Map<String, Object> regla : reglas) {
            List<Object[]> condiciones = (List<Object[]>) regla.get("condiciones");
            double gradoActivacion = 1.0;
            
            for (Object[] condicion : condiciones) {
                String sintoma = (String) condicion[0];
                double peso = (Double) condicion[1];
                double valorUsuario = respuestas.getOrDefault(sintoma, 0.0);
                double gradoCondicion = Math.min(valorUsuario, peso);
                gradoActivacion = Math.min(gradoActivacion, gradoCondicion);
            }
            
            if (gradoActivacion > 0) {
                double confianzaBase = (Double) regla.get("confianza_base");
                double confianzaAjustada = confianzaBase * gradoActivacion;
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("conclusion", regla.get("conclusion"));
                resultado.put("sugerencia", regla.get("sugerencia"));
                resultado.put("confianza", confianzaAjustada);
                resultado.put("grado_activacion", gradoActivacion);
                
                resultados.add(resultado);
            }
        }
        
        resultados.sort((a, b) -> Double.compare((Double)b.get("confianza"), (Double)a.get("confianza")));
        return resultados;
    }

    private void Diagnostico(List<Map<String, Object>> resultados) {
        System.out.println("\n" + "-".repeat(55));
        System.out.println("| Resultado del diagnóstico difuso" + " ".repeat(20) + "|");

        if (!resultados.isEmpty()) {
            System.out.println("| --> Posibles diagnósticos ordenados por confianza.  |");
            System.out.println("-".repeat(55));

            int top = Math.min(resultados.size(), 6);
            for (int i = 0; i < top; i++) {
                Map<String, Object> resultado = resultados.get(i);
                
                System.out.printf("\nOpción #%d:\n", i+1);
                System.out.printf("Diagnóstico: %s\n", resultado.get("conclusion"));
                System.out.printf("Grado de activación: %.1f%%\n", (Double)resultado.get("grado_activacion") * 100);
                System.out.printf("Confianza ajustada: %.1f%%\n", (Double)resultado.get("confianza"));
                System.out.printf("Recomendación: %s\n", resultado.get("sugerencia"));
                
                double confianza = (Double)resultado.get("confianza");
                if (confianza >= 80) {
                    System.out.println("Diagnóstico con alta certeza");
                } else if (confianza >= 60) {
                    System.out.println("Diagnóstico con certeza moderada");
                } else {
                    System.out.println("Diagnóstico con baja certeza");
                }
            }
        } else {
            System.out.println("No se encontraron diagnósticos relevantes con la información proporcionada.");
            System.out.println("-".repeat(50));
        }
    }

    public void ejecutar() {
        System.out.println("  Inicializando Diagnóstico Asistido con Lógica Difusa...  ");
        datosUsuario();
        List<Map<String, Object>> resultados = EvaluacionDiagnostico();
        Diagnostico(resultados);

        System.out.println("\n¿Desea realizar un nuevo análisis? (si/no)");
        String respuesta = scanner.nextLine().toLowerCase();
        
        if (respuesta.equals("si") || respuesta.equals("sí") || respuesta.equals("s") || 
            respuesta.equals("yes") || respuesta.equals("y")) {
            respuestas.clear();
            ejecutar();
        }
    }
    public static void main(String[] args) {
        DiagnósticoAsistido sistema = new DiagnósticoAsistido();
        sistema.ejecutar();
    }
}