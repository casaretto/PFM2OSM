/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author diecavallax
 */
public class Util {

    /**
     * Retorna um texto contendo a quantidade de bytes passada como par�metro
     * adequadamente formatada.  Por exemplo, 1267893984 � formatado como 1GB
     * @param quantidade
     * @return  
     */
    public static String getQuantidadeBytesFormatada(long quantidade) {
        double v = 0.0;
        String unidade = "";
        String valor;
        if (quantidade >= (long) (1024 * 1024 * 1024)) {
            v = quantidade / (double) (1024 * 1024 * 1024);
            unidade = " GB";
        } else if (quantidade >= (long) (1024 * 1024)) {
            v = quantidade / (double) (1024 * 1024);
            unidade = " MB";
        } else if (quantidade >= (long) (1024)) {
            v = quantidade / (double) (1024);
            unidade = " KB";
        } else {
            v = quantidade;
            unidade = " bytes";
        }
        valor = String.format("%.2f", v);
        return valor + unidade;
    }

    public static String porcentagem(float linhasAtual, float linhasTotal) {
        if (linhasTotal == 0) {
            return "<html><font color=FF0000><p>" + 0 + "%</p></font></html>";
        } else {
            float a = linhasAtual, t = linhasTotal;
            DecimalFormat df = new DecimalFormat();
            df.applyPattern("00.0;(00.0)");
            StringBuilder retorno = new StringBuilder(df.format(new Float(a * 100 / (t > 0 ? t : 1))));
            //vermelho red, laranja orange, amarelo yellow, verde green, blueviolet violeta azul
            if (retorno.toString().compareTo("90") >= 0) {
                return "<html><font color=000080><p>" + retorno.toString() + "%</p></font></html>";
            } else if (retorno.toString().compareTo("80") > 0) {
                return "<html><font color=0000FF><p>" + retorno.toString() + "%</p></font></html>";
            } else if (retorno.toString().compareTo("60") > 0) {
                return "<html><font color=008000><p>" + retorno.toString() + "%</p></font></html>";
            } else if (retorno.toString().compareTo("40") > 0) {
                return "<html><font color=008080><p>" + retorno.toString() + "%</p></font></html>";
            } else if (retorno.toString().compareTo("20") > 0) {
                return "<html><font color=800080><p>" + retorno.toString() + "%</p></font></html>";
            } else if (retorno.toString().compareTo("10") > 0) {
                return "<html><font color=800000><p>" + retorno.toString() + "%</p></font></html>";
            } else {
                return "<html><font color=FF0000><p>" + retorno.toString() + "%</p></font></html>";
            }
        }

    }

    public static String porcentagem(int linhasAtual, int linhasTotal) {
        return porcentagem((float) linhasAtual, (float) linhasTotal);
    }

    public static String porcentagem(String linhasAtual, String linhasTotal) {
        return porcentagem(Float.parseFloat(linhasAtual), Float.parseFloat(linhasTotal));
    }

    /**
     * Retorna a quantidade linhas de um arquivo
     * @param filename caminho do arquivo
     * @return
     * @throws IOException 
     */
    public static int countLines(String filename) throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        int cnt = 0;
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {
        }
        cnt = reader.getLineNumber();
        reader.close();
        return cnt;
    }


    /**
     * Determines de clockwise angle from the horizontal versor (1, 0) to the vector passed as parameter.
     *
     * @param xV1 The x coordinate of the origin of the vector.
     * @param yV1 The y coordinate of the origin of the vector.
     * @param xV2 The x coordinate of the distal of the vector.
     * @param yV2 The y coordinate of the distal of the vector.
     * @return Angle in radians.
     */
    public static double getClockwiseAngle(double xV1, double yV1, double xV2, double yV2){
        double theta = Math.acos( (xV2 - xV1) / Math.sqrt((xV2-xV1)*(xV2-xV1) + (yV2-yV1)*(yV2-yV1)) );
        if((yV2-yV1) > 0)
            return 2 * Math.PI - theta;
        else
            return theta;
    }

    /**
     * Determines de counter-clockwise angle from the horizontal versor (1, 0) to the vector passed as parameter.
     *
     * @param xV1 The x coordinate of the origin of the vector.
     * @param yV1 The y coordinate of the origin of the vector.
     * @param xV2 The x coordinate of the distal of the vector.
     * @param yV2 The y coordinate of the distal of the vector.
     * @return Angle in radians.
     */
    public static double getCounterClockwiseAngle(double xV1, double yV1, double xV2, double yV2){
        double theta = Math.acos( (xV2 - xV1) / Math.sqrt((xV2-xV1)*(xV2-xV1) + (yV2-yV1)*(yV2-yV1)) );
        if((yV2-yV1) > 0)
            return theta;
        else
            return 2 * Math.PI - theta;
    }

    /**
     * Determina o angulo orientado no sentido anti-horario do vetor 1 ao vetor 2, ambos com origem em (x0, y0).
     * O valor do angulo eh em radianos.
     *
     * Determines de counter-clockwise angle from vector 1 to vector 2, both with common origin at (x0, y0).
     *
     * @param x0 x coordinate of the common origin.
     * @param y0 y coordinate of the common origin.
     * @param xV1 x coordinate of distal of vector 1.
     * @param yV1 y coordinate of distal of vector 1.
     * @param xV2 x coordinate of distal of vector 2.
     * @param yV2 y coordinate of distal of vector 2.
     * @return
     */
    public static double getCounterClockwiseAngleBetweenVectors(double x0, double y0, double xV1, double yV1, double xV2, double yV2){
        double theta1 = Util.getClockwiseAngle(x0, y0, xV1, yV1); //get the clockwise angle from versor (1, 0) to V1
        double theta2 = Util.getCounterClockwiseAngle(x0, y0, xV2, yV2); //get the counter-clockwise angle from versor (1, 0) to V2
        double dt = theta2 + theta1; //calculate total counter-clockwise angle
        if(dt >= 2 * Math.PI) //subtract 360 if needed
            dt -= 2 * Math.PI;
        return dt;
    }

}
