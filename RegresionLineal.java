import javax.swing.*;
import java.awt.*;

public class RegresionLineal extends JPanel {
    // x -> edad, y -> frecuencia
    double[] x = {15, 16, 17, 18, 19, 20, 21, 22};
    double[] y = {10, 20, 24, 19, 15, 29, 31, 28};

    double pendiente, intercepto, mediaY;

    public RegresionLineal() {
        calcularRegresion();
        mostrarResultados();
    }

    private void calcularRegresion() {
        int n = x.length;
        double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0;

        for (int i = 0; i < n; i++) {
            sumaX += x[i];
            sumaY += y[i];
            sumaXY += x[i] * y[i];
            sumaX2 += x[i] * x[i];
        }

        double mediaX = sumaX / n;
        mediaY = sumaY / n;

        double denom = sumaX2 - n * mediaX * mediaX;
        if (denom == 0) {
            System.err.println("Error: División por cero en cálculo de pendiente.");
            pendiente = 0;
            intercepto = mediaY;
            return;
        }

        pendiente = (sumaXY - n * mediaX * mediaY) / denom;
        intercepto = mediaY - pendiente * mediaX;
    }

    private void mostrarResultados() {
        System.out.printf("Intercepto (b₀): %.2f\n", intercepto);
        System.out.printf("Pendiente (b₁): %.2f\n", pendiente);

        double mse = 0, ssTot = 0;
        for (int i = 0; i < x.length; i++) {
            double pred = pendiente * x[i] + intercepto;
            mse += Math.pow(y[i] - pred, 2);
            ssTot += Math.pow(y[i] - mediaY, 2);
        }

        mse /= x.length;
        double r2 = 1 - mse * x.length / ssTot;

        System.out.printf("Error cuadrático medio: %.2f\n", mse);
        System.out.printf("Coeficiente de determinación (R²): %.2f\n", r2);

        double nuevaX = 19;
        double prediccion = pendiente * nuevaX + intercepto;
        System.out.printf("Predicción para edad %.0f: %.1f frecuencia\n", nuevaX, prediccion);
    }

    private double min(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v < m) m = v;
        return m;
    }

    private double max(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Activar antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        int margin = 60;
        int gx = w - 2 * margin;
        int gy = h - 2 * margin;

        double minX = min(x), maxX = max(x);
        double minY = min(y), maxY = max(y);

        g2.setColor(Color.BLACK);
        g2.drawRect(margin, margin, gx, gy);

        // Dibujar ejes con marcas
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        int numTicks = 5;
        for (int i = 0; i <= numTicks; i++) {
            // Eje Y
            int yPos = margin + gy - i * gy / numTicks;
            double yVal = minY + i * (maxY - minY) / numTicks;
            g2.drawLine(margin - 5, yPos, margin, yPos);
            g2.drawString(String.format("%.0f", yVal), margin - 40, yPos + 5);

            // Eje X
            int xPos = margin + i * gx / numTicks;
            double xVal = minX + i * (maxX - minX) / numTicks;
            g2.drawLine(xPos, margin + gy, xPos, margin + gy + 5);
            g2.drawString(String.format("%.0f", xVal), xPos - 10, margin + gy + 20);
        }

        // Etiquetas de los ejes
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Edad", w / 2 - 10, h - 20);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Frecuencia", -h / 2 - 20, 20);
        g2.rotate(Math.PI / 2);

        // Dibujar puntos
        g2.setColor(Color.BLUE);
        for (int i = 0; i < x.length; i++) {
            int px = margin + (int) ((x[i] - minX) * gx / (maxX - minX));
            int py = margin + gy - (int) ((y[i] - minY) * gy / (maxY - minY));
            g2.fillOval(px - 4, py - 4, 8, 8);
        }

        // Dibujar línea de regresión
        g2.setColor(Color.RED);
        int x1 = margin;
        int y1 = margin + gy - (int) ((pendiente * minX + intercepto - minY) * gy / (maxY - minY));
        int x2 = margin + gx;
        int y2 = margin + gy - (int) ((pendiente * maxX + intercepto - minY) * gy / (maxY - minY));
        g2.drawLine(x1, y1, x2, y2);

        // Mostrar ecuación en pantalla
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(String.format("y = %.2fx + %.2f", pendiente, intercepto), margin + 20, margin + 20);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Regresión Lineal sin librerías");
        RegresionLineal panel = new RegresionLineal();
        frame.setContentPane(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}