/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mptoosm.mp.LerMP;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 *
 * @author diecavallax
 */
public class Restricao {

    private String restricaoFim = "[restrict]";
    private String restricaoInicio = "[end-restrict]";
    /*armazena os pontos TraffPoints=16968,25008,25009*/
    private String[] restricaoTraffPoints = new String[3];

    /*indica se a polyline a qual o no pertence já foi encontrada*/
    boolean[] restricaoTraffPointsEncontrado = new boolean[3];
    /*armazena TraffRoads=13185,13188*/
    private String[] restricaoTraffRoads = new String[]{"", "", ""};
    /*indica se a polyline a qual a via pertence já foi encontrada*/
    int restricaoWay_from = -1, restricaoWay_to = -1, restricaoNo_via = -1;
    private boolean isValido = false;
    private boolean isCompleto = false;
    List<Node> listaNosTemp = new ArrayList<Node>();

    /*
    [Restrict]
    Nod=1041889
    TraffPoints=1037168,1041889,1032330
    TraffRoads=13185,13188
    [END-Restrict]
    
    [RESTRICT]
    TraffPoints=16968,25008,25009
    TraffRoads=520763,532674
    [END-RESTRICT]
     * 
     * RestrParam=[Emergency],[delivery],[car],[bus],[taxi],[pedestrian],[bicycl
    e],[truck]
    RestrParam=0,1,1,0 (marca 1 para os veiculos que não se aplicam as restricoes
    
    
     * 
    <relation id='1555397' timestamp='2011-04-23T15:30:35Z' uid='24551' user='teste18' visible='true' version='1' changeset='7944468'>
    <member type='way' ref='107254757' role='from' />
    <member type='node' ref='1213918853' role='via' />
    <member type='way' ref='107254758' role='to' />
    <tag k='restriction' v='no_left_turn' />
    <tag k='type' v='restriction' />
    </relation>
     */
    public Restricao() {
    }

    public Restricao(Restricao restricao) {
        restricaoTraffPoints = restricao.restricaoTraffPoints;
        restricaoTraffPointsEncontrado = restricao.restricaoTraffPointsEncontrado;
        restricaoTraffRoads = restricao.restricaoTraffRoads;
        restricaoWay_from = restricao.restricaoWay_from;
        restricaoWay_to = restricao.restricaoWay_to;
        restricaoNo_via = restricao.restricaoNo_via;
        isValido = restricao.isValido;
        isCompleto = restricao.isCompleto;
        listaNosTemp = restricao.listaNosTemp;

    }

    public void clear() {

        restricaoTraffPoints = new String[]{"", "", ""};
        restricaoTraffPointsEncontrado = new boolean[]{false, false, false};
        /*armazena Nod=1041889*/
//        restricaoNo = "";
//        restricaoNoEcontrao = false;
        /*armazena TraffRoads=13185,13188*/
        restricaoTraffRoads = new String[]{"", "", ""};

//        restricaoTraffRoadsEncontrado[0] = false;
//        restricaoTraffRoadsEncontrado[1] = false;
//        restricaoTraffRoadsEncontrado[2] = false;
        restricaoWay_from = -1;
        restricaoWay_to = -1;
        restricaoNo_via = -1;
        isValido = false;
        isCompleto = false;
        listaNosTemp.clear();
    }

    public void adicionaLinha(String item) {
        //if (!item.contains(poiInicio) & !item.contains(POIFim) & !item.isEmpty()) {
        if (!restricaoInicio.contains(item.toLowerCase()) & !restricaoFim.contains(item.toLowerCase()) & !item.isEmpty()) {
            //Nod=1041889            
//            if (item.length() >= 4 && item.substring(0, 4).contains("Nod=")) {
//                restricaoNo = item.substring(item.indexOf("=") + 1, item.length());
//            } else
            //TraffPoints=16968,25008,25009
            if (item.length() >= 12 && item.substring(0, 12).contains("TraffPoints=")) {
                restricaoTraffPoints = item.substring(item.indexOf("=") + 1, item.length()).split(",");
            } else //TraffRoads=520763,532674
            if (item.length() >= 11 && item.substring(0, 11).contains("TraffRoads=")) {
                restricaoTraffRoads = item.substring(item.indexOf("=") + 1, item.length()).split(",");
                if (restricaoTraffRoads.length >= 2) {
                    isCompleto = true;
                } else {
                    isValido = false;
                }
            }
        }
    }
    /* 
    <relation id='1555397' timestamp='2011-04-23T15:30:35Z' uid='24551' user='teste18' visible='true' version='1' changeset='7944468'>
    <member type='way' ref='107254757' role='from' />
    <member type='node' ref='1213918853' role='via' />
    <member type='way' ref='107254758' role='to' />
    <tag k='restriction' v='no_left_turn' />
    <tag k='type' v='restriction' />
    </relation>
     */

    public StringBuilder getItensParaArquivoOSM() {
        StringBuilder retorno = new StringBuilder();
        if (restricaoNo_via > 0) {
            retorno.append(getRestricaoCabecalho())
                    .append("\n    <member type=\"way\" ref=\"")
                    .append(LerMP.strIDsSignal).append(restricaoWay_from)
                    .append("\" role=\"from\" />")
                    .append("\n    <member type=\"node\" ref=\"")
                    .append(LerMP.strIDsSignal)
                    .append(restricaoNo_via)
                    .append("\" role=\"via\" />")
                    .append("\n    <member type=\"way\" ref=\"")
                    .append(LerMP.strIDsSignal)
                    .append(restricaoWay_to)
                    .append("\" role=\"to\" />")
                    .append("\n    <tag k=\'restriction\' v=\'no_left_turn\' />")
                    .append("\n    <tag k=\"type\" v=\"restriction\" />\n </relation>");
        }

        return retorno;


    }

    StringBuilder getRestricaoCabecalho() {
        StringBuilder retorno = new StringBuilder();
        //retorno.append("\n <relation id=\"").append(LerMP.getProximoElementoOsmId()).append("\"").append(LerMP.user).append(LerMP.uid).append(LerMP.visible).append(LerMP.version).append(LerMP.changeset).append(" timestamp=\"").append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())).append("Z\">");
        retorno.append("\n <relation id=\"").append(LerMP.strIDsSignal).append(LerMP.getProximoElementoOsmId()).append("\"").append(LerMP.strIncludeActionModify).append(LerMP.user).append(LerMP.uid).append(LerMP.visible).append(LerMP.version).append(LerMP.changeset).append(">");
        //retorno.append("\n  </node>");

        return retorno;
    }

    public boolean isValido() {
        return isValido;
    }

    public void setValido(boolean valido) {
        isValido = valido;
    }

    public boolean isCompleto() {
        return isCompleto;
    }


    public void polylineVerificaRestricao( Polyline polyline ){
        //restricaoTraffRoads[0] == road from
        if( restricaoTraffRoads[0].equals( polyline.getPolylineRoadID() ) ){
            restricaoWay_from = polyline.polylineOsmId;
            restricaoTraffPointsEncontrado[0] = true;
        }
        //restricaoTraffRoads[1] == road to
        if( restricaoTraffRoads[1].equals( polyline.getPolylineRoadID() ) ){
            restricaoWay_to = polyline.polylineOsmId;
            restricaoTraffPointsEncontrado[2] = true;
        }
        //restricaoTraffPoints[1] == node where the restriction applies (the middle one)
        for( Node node : polyline.listaNos ){
            if( restricaoTraffPoints[1].equals( String.valueOf( node.noMpfId ) ) ){
                restricaoNo_via = node.getNoOsmId();
                restricaoTraffPointsEncontrado[1] = true;
                break;
            }
        }
    }

    /**
    Verifica se o nó da polyline faz parte da restricao
    
     * @param polyline
     * @return 
     */
    /*public boolean polylineVerificaRestricao(Polyline polyline, List<Polyline> listaDePolylineParaEscrever) {
        boolean retorno = false;
        //TODO se o roadid da polyline não pertencer aos traffroads nao execucar abaixo
        if (listaDePolylineParaEscrever.isEmpty()) {
            for (short i = 0; i <= polyline.listaNos.size() - 2; i++) {
                //se é a via from
                if (restricaoTraffPoints[0].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[1].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    //divide a polyline para que ela inicie e termine nas restricoes
                    restricaoWay_from = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever);
                    restricaoTraffPointsEncontrado[0] = true;
                    restricaoTraffPointsEncontrado[1] = true;
                    restricaoNo_via = polyline.listaNos.get(i + 1).getNoOsmId();
                    retorno = true;

                } else if (restricaoTraffPoints[1].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[0].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    //divide a polyline para que ela inicie e termine nas restricoes
                    restricaoWay_from = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever);
                    restricaoTraffPointsEncontrado[0] = true;
                    restricaoTraffPointsEncontrado[1] = true;
                    restricaoNo_via = polyline.listaNos.get(i).getNoOsmId();
                    retorno = true;

                } else //se é a via to
                if (restricaoTraffPoints[1].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[2].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    restricaoWay_to = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever);
//                    restricaoTraffPointsEncontrado[1]=true;
                    restricaoTraffPointsEncontrado[2] = true;
                    restricaoNo_via = polyline.listaNos.get(i).getNoOsmId();
                    retorno = true;
                } else if ((restricaoTraffPoints[2].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[1].equals(polyline.listaNos.get(i + 1).noMpfId + ""))) {
                    restricaoWay_to = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever);
//                    restricaoTraffPointsEncontrado[1]=true;
                    restricaoTraffPointsEncontrado[2] = true;
                    restricaoNo_via = polyline.listaNos.get(i + 1).getNoOsmId();
                    retorno = true;
                }

            }
        } else {
            return polylineVerificaRestricao(listaDePolylineParaEscrever);
        }
        return retorno;
    }*/

    /**
     * Retorna o osmid da polyline que faz parte a restricao, tambem divide a polyline
     * para que se adeque as regras das restricoes
     * @param listaDePolylineParaEscrever
     * @return 
     */
    /*public boolean polylineVerificaRestricao(List<Polyline> listaDePolylineParaEscrever) {
        boolean retorno = false;
        List<Polyline> listaDePolylineParaEscrever_temp = new ArrayList<Polyline>();
        for (Polyline polyline : listaDePolylineParaEscrever) {
            for (short i = 0; i <= polyline.listaNos.size() - 2; i++) {
                //se é a via from
                if (restricaoTraffPoints[0].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[1].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    //divide a polyline para que ela inicie e termine nas restricoes
                    restricaoWay_from = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever_temp);
                    restricaoTraffPointsEncontrado[0] = true;
                    restricaoTraffPointsEncontrado[1] = true;
                    restricaoNo_via = polyline.listaNos.get(i + 1).getNoOsmId();
                    retorno = true;

                } else if (restricaoTraffPoints[1].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[0].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    //divide a polyline para que ela inicie e termine nas restricoes
                    restricaoWay_from = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever_temp);
                    restricaoTraffPointsEncontrado[0] = true;
                    restricaoTraffPointsEncontrado[1] = true;
                    restricaoNo_via = polyline.listaNos.get(i).getNoOsmId();
                    retorno = true;

                } else //se é a via to
                if (restricaoTraffPoints[1].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[2].equals(polyline.listaNos.get(i + 1).noMpfId + "")) {
                    restricaoWay_to = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever_temp);
//                    restricaoTraffPointsEncontrado[1]=true;
                    restricaoTraffPointsEncontrado[2] = true;
                    restricaoNo_via = polyline.listaNos.get(i).getNoOsmId();
                    retorno = true;
                } else if ((restricaoTraffPoints[2].equals(polyline.listaNos.get(i).noMpfId + "")
                        && restricaoTraffPoints[1].equals(polyline.listaNos.get(i + 1).noMpfId + ""))) {
                    restricaoWay_to = polyline.splitPolilyne(polyline.listaNos.get(i), listaDePolylineParaEscrever_temp);
//                    restricaoTraffPointsEncontrado[1]=true;
                    restricaoTraffPointsEncontrado[2] = true;
                    restricaoNo_via = polyline.listaNos.get(i + 1).getNoOsmId();
                    retorno = true;
                }

            }
            if(!polyline.polylineFoiDivididaPRestricao){
            listaDePolylineParaEscrever_temp.add(polyline);}
        }
        //se a polyline for dividida em mais vetores
        if (retorno && listaDePolylineParaEscrever.size() < listaDePolylineParaEscrever_temp.size()) {
            //System.out.println(listaDePolylineParaEscrever.size()+" -> "+listaDePolylineParaEscrever_temp.size());
            listaDePolylineParaEscrever.clear();           
            
            listaDePolylineParaEscrever.addAll(listaDePolylineParaEscrever_temp);
           
        }
        listaDePolylineParaEscrever_temp = null;
        
        return retorno;
    }*/

    public boolean isReferenciasCompletas() {
        return restricaoTraffPointsEncontrado[0]
                & restricaoTraffPointsEncontrado[1]
                & restricaoTraffPointsEncontrado[2];
    }
    
    
/*
     * Informa se a via pertence a restrição com base na sua id
     */
    public boolean isViaPertenceARestricao(String mpfRoadId){
        for(String a:restricaoTraffRoads){
            if(a.equalsIgnoreCase(mpfRoadId)){
                return true;
            }        
        }       
        return false;
    
    }
}
