/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.Date;
import mptoosm.mp.LerMP;
import org.apache.commons.lang3.time.DateFormatUtils;
//import uk.me.jstott.jcoord.LatLng;

/**
 *
 * @author Pindaro
 */
public class Node {

    /**
     * The X coordinate of the point.
     * In most long/lat systems, this is the longitude.
     */
    double latitude = 0;
    /**
     * The Y coordinate of the point.
     * In most long/lat systems, this is the latitude.
     */
    double longitude = 0;
    short nodeLevel = -1;
    private int noOsmId;

    int noMpfId;
    boolean isReferenciaAOutroNo = false;
    boolean possueRestricao =false;

    /** Constructs mpId_OsmId new Node
     * @param no 
     */
    public Node(Node no) {
        latitude = no.latitude;
        longitude = no.longitude;
        noOsmId = no.noOsmId;
        noMpfId = no.noMpfId;
        isReferenciaAOutroNo = no.isReferenciaAOutroNo;
        possueRestricao = no.possueRestricao;
        nodeLevel=no.nodeLevel;
    }

    /**
     * usado para dividir mapas
     */
    public Node() {
    }

    public void clear() {
        latitude = 0;

        longitude = 0;
        nodeLevel = -1;
        noOsmId = -1;
        noMpfId=-1;
        isReferenciaAOutroNo = false;
    }

    public int getNoOsmId() {
        if(noOsmId<=0){noOsmId= LerMP.getProximoElementoOsmId();}
        return noOsmId;
    }
    
    public void setNoOsmId(int id){
        noOsmId = id;
    }
    void recebeLatLong(String item) {

        if (item.contains("Data") || item.contains("Origin")) {
            setLatLong(item);
            //setNodeLevel(Short.parseShort(item.substring(item.indexOf("=") - 1, item.indexOf("="))));
        } else {
            setLatLong(item);
        }

    }

    private void setLatLong(String item) {
        if (nodeLevel == 0) {
            item = item.substring(item.indexOf("=") + 1, item.length());
            item = item.replace("(", "");
            item = item.replace(")", "");
            String[] pontos = item.split(",");
            latitude = Double.parseDouble(pontos[0]);
            longitude = Double.parseDouble(pontos[1]);
            //noOsmId = LerMP.getProximoElementoOsmId();
        }

    }

    void setNodeLevel(short i) {
        nodeLevel = i;
    }

    /**
     * Retorna os elementos do nó para ser escrito o arquivo osm <br>
     * Retorna:
     * < node id="25496583" lat="51.5173639" lon="-0.140043" version="1" uid="1238"
     * visible="true" timestamp="2007-01-28T11:40:26Z">
     * < tag k="highway" v="traffic_signals"/>
     * < /node>
     * @return 
     */
    public StringBuilder getItensParaArquivoOSM() {

        //<node id='602765270' timestamp='2010-01-01T22:06:02Z' uid='24551' user='teste18' visible='true' version='1' changeset='3514286' lat='-16.1453533' lon='-46.5843916' />
        StringBuilder retorno = new StringBuilder();
        if (nodeLevel == 0 & !isReferenciaAOutroNo) {
            //TODO ao criar polylines setar o nodid para o id da polilyne
            retorno.append(getNodes()).append(">");
            retorno.append("\n </node>");
            return retorno;
        }
        return retorno;
    }

    /**
     * Retorna todos os elementos do nó mas não envia o elemento xml de fechamento da 
     * tag de nó , retorna <br>
     * < node id="25496583" lat="51.5173639" lon="-0.140043" version="1" 
     * changeset="203496" user="80n" uid="1238" visible="true" 
     * timestamp="2007-01-28T11:40:26Z">
     * < tag k="highway" v="traffic_signals"/>
     *
     * @return 
     */
    public StringBuilder getNodes() {
        /*
         */
        // LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Nos - pegando dados...");
        StringBuilder retorno = new StringBuilder();
        if (!isReferenciaAOutroNo & nodeLevel == 0) {
            //TODO adicionado a linha abaixo
            getNoOsmId();
            retorno.append("\n <node id=\"").append(LerMP.strIDsSignal).append(noOsmId).append("\"")
                    .append(LerMP.strIncludeActionModify)
                    .append(getOsmLatLong())
                    .append(LerMP.user)
                    .append(LerMP.uid)
                    .append(LerMP.visible)
                    .append(LerMP.version)
                    .append(LerMP.changeset);
                    //.append(" timestamp=\"")
                    //.append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()))
                    //.append("Z\"");
            //.append(">");

            //retorno.append("\n  </node>");
        }
//        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Nos - dados ok...");
        return retorno;
    }

    /**
     * Formata mpId_OsmId lat e lon de acordo com o padrao osm
     *  lat="-16.3579210" lon="-46.9060182"
     * @return 
     */
    public String getOsmLatLong() {
//        lat="-16.3579210" lon="-46.9060182" poidata=-12121,-231 
        return " lat=\"" + latitude + "\" lon=\"" + longitude + "\"";
    }
    /**
     * Retorna a Longitude e Latitude no formato "-46.9060182,-16.3579210" 
     * @return 
     */
    String getLongLat() {
        return "" + longitude + ","+ latitude;
    }

    public final boolean equals(Node other) {
        boolean xequals = latitude == other.latitude;
        boolean yequals = longitude == other.longitude;

        boolean result = xequals && yequals;
        return result;
    }

    /**
     * Retorna mpId_OsmId distância em metros entre dois nós, se os nós não pertencem ao mesmo nivel retorna 0
     * @param other
     * @return 
     */
    public double distance(Node other) {


        double earthRadius = 3958.75;
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLng = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return new Float(dist * meterConversion).floatValue();


    }


    /**.
     * Retorna falso se o nó possui valores de latide e longitude nulos ou
     * com zero
     * @return 
     */
    public boolean isNull() {
        boolean retorno = true;
        if (latitude != 0 || longitude != 0) {
            retorno = false;
        }
        return retorno;
    }

    short getNodeLevel() {
        return nodeLevel;
    }

    /**
     * retorna true se o nó já foi marcado que possui restricao
     * @return 
     */
    public boolean isPossueRestricao() {
        return possueRestricao;
    }
/**
     * marca o nó indicando que possui restricao
     * @param possueRestricao 
     */
    public void setPossueRestricao(boolean possueRestricao) {
        this.possueRestricao = possueRestricao;
    }
    
  
}
