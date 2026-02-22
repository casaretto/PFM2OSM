/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.List;
import mptoosm.mp.LerMP;

/**
 * @author diecavallax
 */
public class Cities {
    /*[Cities]
    City410=VILAREJO
    RegionIdx410=4
    City411=VILAREJO FORTE
    RegionIdx411=3
    City412=VIRGEM DA LAPA
    RegionIdx412=4
    [END-Cities]*/

    String CitiesInicio = "[Cities]";
    List<String> citiesList = new ArrayList<String>();
    String CitiesFim = "[END-Cities]";
    boolean isAtivo=false;

    public Cities(Cities cities) {
        citiesList = cities.citiesList;
    }

    public Cities() {
        citiesList.clear();
        isCompleto=false;
    }
    public boolean isAtivo() {
        return isAtivo;
    }
    public void setAtivo(boolean b) {
        isAtivo=b;
    }boolean isCompleto=false;
    public boolean isCompleto(){return isCompleto;}
    public void setCompleto(boolean b) {
        isCompleto=b;
    }
/**
     * Retorna
     *  <tag k="is_in:region" v="Sudeste"/>
        <tag k="is_in:state" v="Minas Gerais"/>
        <tag k="is_in:state_code" v="MG"/>
         <tag k="addr:city" v="JANUÁRIA"/>
     * @param cityIdx
     * @return 
     */
    //TODO este metodo demora d+ pra processar
    public String getEstadoSiglaECidade(int cityIdx) {
        /* <tag k="is_in:region" v="Sudeste"/>
        <tag k="is_in:state" v="Minas Gerais"/>
        <tag k="is_in:state_code" v="MG"/>
         <tag k="addr:city" v="JANUÁRIA"/>
         */
        //para cada cidade
        String cidade = ""; //usado para lógica
        String original_cidade = ""; //usado para retonar tag
        //testa para verificar se existe index de cidade para não precisar 
        //correr todo o array
        if (cityIdx > 0) {
            for (String a : citiesList) {
                String original_a = a; //original_a = usado para retornar tag
                //coloca o nome em maisculo
                a = a.toUpperCase(); //a = usado para lógica
                if( LerMP.boolConvertLabelsToUpperCase )
                    original_a = a; //se o usuário optou por deixar tudo em maúsculas, o texto que vai para a tag fica igual ao usado na lógica
                //encontra o nome da cidade de acordo com o idx do poi
                if (a.substring(0, a.indexOf("=")).equalsIgnoreCase("CITY"+cityIdx)) {
                    cidade = a;
                    original_cidade = original_a;
                    //testa primeiro para verificar se é cidade
                } else if (cidade.length() > 1 && a.substring(0, 9).contains("REGIONIDX") && !a.contains("BAIRRO")) {
                    /*City410=VILAREJO
                    RegionIdx410=4*/
                    return LerMP.estados.getEstado(original_a.substring(original_a.indexOf("=") + 1, original_a.length()))
                    //<tag k="addr:city" v="JANUÁRIA"/>
                    + "\n  <tag k='addr:city' v='"+original_cidade.substring(original_cidade.indexOf("=")+1,original_cidade.length())+"' />";
                    
                } else {
                    //nao é cidade
                    cidade = "";
                    original_cidade = "";
                }
            }
        }
        return "";
    }

    public void adicionaLinha(String item) {
        if (!item.contains(CitiesInicio) & !item.contains(CitiesFim) & !item.isEmpty()) {
            citiesList.add(item);
        }
    }
    
    short getCityIdx(String Nome){        
        for(String s:citiesList){
            /*City410=VILAREJO
            RegionIdx410=4*/
            if(s.contains("City") && s.substring(s.indexOf("=")+1, s.length()).equalsIgnoreCase(Nome)){
            return Short.parseShort(s.substring(4,s.indexOf("=")));
            }            
        }        
        return -1;
    }
}
