/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mptoosm.elementosMapa;

import java.util.regex.Matcher;

/**
 * Esta classe modela uma entrada NumbersNN= do PFM, ou seja, ela representa
 * um par de números apoiados em dois nós de roteamento (nós que ligam duas ou mais vias ou são terminais)
 * consecutivos.  Esta classe também modela o tipo de numeração em cada lado.
 *
 * @author Paulo Carvalho paulo.r.m.carvalho@gmail.com
 */
public class Numeracao {

    //read from PFM directly
    public String base_node_seq_in_poly = null;
    public String number_type_left_side = null;
    public String proximal_left_number = null;
    public String distal_left_number = null;
    public String number_type_right_side = null;
    public String proximal_right_number = null;
    public String distal_right_number = null;

    //derived during PFM parse
    public String distal_node_seq_in_poly = null;

    public Numeracao( String linha ){
        Matcher matcher = Polyline.regexNumbers.matcher(linha);
        matcher.find();
        String valor = matcher.group(1); //pega string depois do sinal de igual
        String tokens[] = valor.split(","); //quebra o valor pelas virgulas
        //o primeiro token é o sequencial do primeiro nó da aresta da poly onde a numeração está
        //é normalmente zero
        base_node_seq_in_poly = tokens[0];
        //o segundo token é uma letra com o tipo de numeração do lado esquerdo
        number_type_left_side = tokens[1];
        //o terceiro token é o número do lado esquerdo apoiado no nó indentificado no primeiro token (base)
        proximal_left_number = tokens[2];
        //o quarto token é o número do lado esquerdo apoiado no nó de roteamento (nó que liga 2+ vias) seguinte da poly
        distal_left_number = tokens[3];
        //o quinto token é o tipo da numeração do lado direito
        number_type_right_side = tokens[4];
        //o sexto token é o número do lado direito apoiado no nó indentificado no primeiro token (base)
        proximal_right_number = tokens[5];
        //o sétimo token é o número do lado dirieto apoiado no nó de roteamento (nó que liga 2+ vias) seguint da poly
        distal_right_number = tokens[6];
    }

    public void setDistalNodeSeqInPoly( String value ){
        distal_node_seq_in_poly = value;
    }

}
