/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author diecavallax
 */
public class Countries {
    /*[Countries]
    Country1=VENEZUELA~[0x1d]VEN
    Country2=VENEZUELA~[/0x1d]VEN
    [END-Countries]*/

    String CountriesInicio = "[Countries]";
    List<StringBuilder> countriesList = new ArrayList<StringBuilder>();
    String CountriesFim = "[END-Countries]";
    boolean isAtivo = false;

    private String countryName = "";
    private String countryCode = "";

    public Countries() {
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean b) {
        isAtivo = b;
    }

    public void adicionaLinha(String item) {
        if (!item.contains(CountriesInicio) & !item.contains(CountriesFim) & !item.isEmpty()) {
            countriesList.add(new StringBuilder(item));
            // Extrai nome e código do país da primeira linha Country
            if (item.contains("Country1=") && countryName.isEmpty()) {
                String valor = item.substring(item.indexOf("=") + 1);
                if (valor.contains("~")) {
                    countryName = valor.substring(0, valor.indexOf("~"));
                } else {
                    countryName = valor;
                }
                // Extrai o código do país após o último ']'
                if (valor.contains("]")) {
                    countryCode = valor.substring(valor.lastIndexOf("]") + 1).trim();
                }
            }
        }
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
