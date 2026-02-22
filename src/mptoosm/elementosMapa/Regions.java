/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.List;
import mptoosm.mp.LerMP;

/**
 *
 * @author diecavallax
 */
public class Regions {
    /*[Regions]
    Region1=GOIAS~[0x1d]GO
    CountryIdx1=1
    Region2=MINAS GERAIS~[0x1d]MG
    CountryIdx2=1
    [END-Regions]*/

    String RegionsInicio = "[Regions]";
    List<StringBuilder> regionsList = new ArrayList<StringBuilder>();
    String RegionsFim = "[END-Regions]";
    boolean isAtivo = false;

    public Regions() {
    }

    Regions(Regions regions) {
        regionsList = regions.regionsList;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean b) {
        isAtivo = b;
    }boolean isCompleto=false;
    public boolean isCompleto(){return isCompleto;}
    public void setCompleto(boolean b) {
        isCompleto=b;
    }

    public void adicionaLinha(String item) {
        if (!item.contains(RegionsInicio) & !item.contains(RegionsFim) & !item.isEmpty()) {
            regionsList.add(new StringBuilder(item));
        }
    }

    /**
     * retorna
    <tag k="is_in:region" v="Sudeste"/>
    <tag k="is_in:state" v="Minas Gerais"/>
    <tag k="is_in:state_code" v="MG"/>
     * @return 
     */
    public String getEstado(String indexEstado) {
        StringBuilder retorno = new StringBuilder();

        for (StringBuilder a : regionsList) {
            if (a.toString().contains("Region" + indexEstado)) {
                String estado = a.substring(a.indexOf("=") + 1, a.toString().contains("~") ? a.indexOf("~") : a.length());
                retorno.append("\n  <tag k=\"is_in:region\" v=\"").append(getRegiao(estado)).append("\"/>");
                retorno.append("\n  <tag k=\"is_in:state\" v=\"").append(estado).append("\"/>");
                if (a.toString().contains("]")) {
                    retorno.append("\n  <tag k=\"is_in:state_code\" v=\"").append(a.substring(a.length() - 2)).append("\"/>");
                }
            }
        }
        return retorno.toString();
    }

    String getRegiao(String estado) {
        String s = estado.toUpperCase();

            if ( s.equals("ACRE") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "ALAGOAS") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "AMAPÁ") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "AMAZONAS") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "BAHIA") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "CEARÁ") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "DISTRITO FEDERAL") )
                return LerMP.boolConvertLabelsToUpperCase ? "CENTRO OESTE" : "Centro Oeste";
            if ( s.equals( "ESPIRITO SANTO") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUDESTE" : "Sudeste";
            if ( s.equals( "GOIÁS") )
                return LerMP.boolConvertLabelsToUpperCase ? "CENTRO OESTE" : "Centro Oeste";
            if ( s.equals( "MARANHÃO") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "MATO GROSSO") )
                return LerMP.boolConvertLabelsToUpperCase ? "CENTRO OESTE" : "Centro Oeste";
            if ( s.equals( "MATO GROSSO DO SUL") )
                return LerMP.boolConvertLabelsToUpperCase ? "CENTRO OESTE" : "Centro Oeste";
            if ( s.equals( "MINAS GERAIS") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUDESTE" : "Sudeste";
            if ( s.equals( "PARÁ") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "PARAÍBA") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "PARANÁ") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUL" : "Sul";
            if ( s.equals( "PERNAMBUCO") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "PIAUÍ") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "RIO DE JANEIRO") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUDESTE" : "Sudeste";
            if ( s.equals( "RIO GRANDE DO NORTE") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "RIO GRANDE DO SUL") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUL" : "Sul";
            if ( s.equals( "RONDÔNIA") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "RORAIMA") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            if ( s.equals( "SANTA CATARINA") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUL" : "Sul";
            if ( s.equals( "SÃO PAULO") )
                return LerMP.boolConvertLabelsToUpperCase ? "SUDESTE" : "Sudeste";
            if ( s.equals( "SERGIPE") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORDESTE" : "Nordeste";
            if ( s.equals( "TOCANTINS") )
                return LerMP.boolConvertLabelsToUpperCase ? "NORTE" : "Norte";
            return " ";
    }
}
