/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mptoosm.mp.LerMP;
import mptoosm.utils.FormataOsmTag;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 *
 * @author diecavallax
 */
public class Polyline_area {

    private List<String> polyline_area_types = new ArrayList<String>(10);
    List<Node> listaNos = new ArrayList<Node>(10);
    private String polyline_areaInicio = "[RGN80]-[POLYGON]";
    private String polyline_areaFim = "[END]-[END-RGN80]";
    private String polyline_areaType = "";
    public String polyline_areaLabel = "";
    private short polyline_areaLevel = -1;
    private int polyline_areaOsmId = 0;
    /**
     * se a polyline_area pertence ao level 0
     */
    boolean isValido = false;
    /**
     * se já está completo para ser escrito
     */
    boolean isCompleto = false;

    public Polyline_area() {
        inicia_polyline_area_ways();
    }

    public void clear() {
        polyline_areaType = "";
        polyline_areaLabel = "";
        polyline_areaLevel = -1;
        polyline_areaOsmId = 0;
        listaNos.clear();
        isValido = false;
        isCompleto = false;
        //static List<String> polyline_areas_ways = new ArrayList<>(10);
    }

    public void adicionaLinha(String item) {
        //if (!item.contains(poiInicio) & !item.contains(POIFim) & !item.isEmpty()) {
        if (!polyline_areaInicio.contains(item) & !polyline_areaFim.contains(item) & !item.isEmpty() & isValido) {
            if (item.length() >= 6 && item.substring(0, 6).contains("Type=")) {
                polyline_areaType = new String(item.substring(item.indexOf("=") + 1, item.length()));
            } else if (item.length() >= 6 && item.substring(0, 6).contains("Label=")) {
                String text = item.substring(6, item.length());
                polyline_areaLabel = LerMP.boolConvertLabelsToUpperCase ? text.toUpperCase() : text;
            } else if ((item.substring(0, 6).contains("Data") && item.substring(4, 7).contains("="))
                    || item.contains("Origin0")) {
                polyline_areaLevel = Short.parseShort(item.substring(item.indexOf("=") - 1, item.indexOf("=")));
                if (polyline_areaLevel == 0 && polyline_areaLabel.length() > 1) {
                    polyline_areaOsmId = LerMP.getProximoElementoOsmId();
                    criaNodeList(new StringBuilder(item));
                } else {
                    isValido = false;
                }

            }
        }
    }

    public StringBuilder getItensParaArquivoOSM() {

        StringBuilder retorno = new StringBuilder();
        if (isValido) {
            StringBuilder[] nos = getNodesOsm();
            //adicionar os nós que  que são referenciados pelo elemento
            retorno.append(nos[0]);
            //inicia o elemento
            retorno.append(getpolyline_areaOsmCabecalho());
            //adicionar referencias de nos
            retorno.append(nos[1]);

            retorno.append(getpolyline_areaToOsmType());

            if( LerMP.boolIncludeInfoForCompilers )
                retorno.append(FormataOsmTag.retornaTag("polygon_img_type="+polyline_areaType));
            
            retorno.append(FormataOsmTag.retornaTag("name=" + polyline_areaLabel)).append("\n </way>");
        }
        return retorno;
    }

    StringBuilder getpolyline_areaToOsmType() {
        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Poly - ").append(polyline_areaType).append(" ").append(polyline_areaLabel);

        StringBuilder teste = FormataOsmTag.getOsmType(polyline_area_types, polyline_areaType, "");
        if( teste.toString().contains("v=\"*\"")){
            System.out.print("\npolyline_area"+polyline_areaType);
        }
        
        return teste;
        //return FormataOsmTag.getOsmType(polyline_area_types, polyline_areaType);
    }

    /**
     * Retorna um arrey bidimensional onde os valores do indice 0 sao os nos para serem escritos
     * e os de indice 1 sao somente referencias pois já existem no arquivo osm
     * @return 
     */
    StringBuilder[] getNodesOsm() {
        StringBuilder[] retorno = new StringBuilder[2];
        retorno[0] = new StringBuilder();//nos para serem adicionados antes do polyline_area
        retorno[1] = new StringBuilder();// referencias dos nós 
        for (Node no : listaNos) {
            retorno[0].append(no.getNodes()).append("/>");

            retorno[1].append("\n  <nd ref=\"").append(LerMP.strIDsSignal).append(no.getNoOsmId()).append("\"/>");
        }
        //adiciona o primeiro nó na ultima posiçao para que forme o  o poligono
        retorno[1].append("\n  <nd ref=\"").append(LerMP.strIDsSignal).append(listaNos.get(0).getNoOsmId()).append("\"/>");
        return retorno;
    }

    /**
     * Retorna todos os elementos do nó mas não envia o elemento xml de fechamento da 
     * tag de nó , retorna <br>
     *   < way id=\"106114370\" timestamp=\"2011-04-04T16:08:59Z\" uid=\"417837\" 
     * user=\"diecavallax\" visible=\"true\" version=\"4\" changeset=\"7766427\">
     *
     * @return 
     */
    StringBuilder getpolyline_areaOsmCabecalho() {
        StringBuilder retorno = new StringBuilder();
        retorno.append("\n <way id=\"").append(LerMP.strIDsSignal)
                .append(polyline_areaOsmId).append("\"")
                .append(LerMP.strIncludeActionModify)
                .append(LerMP.user)
                .append(LerMP.uid)
                .append(LerMP.visible)
                .append(LerMP.version)
                .append(LerMP.changeset)
                //.append(" timestamp=\"")
                //.append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())).append("Z\">");
                .append(">");
        //retorno.append("\n  </node>");

        return retorno;
    }

    /**
     * Cria lista de nos para mpId_OsmId polyline_area de acordo com mpId_OsmId estring passada, DataX=(x,y)
     * e seta o tamanho da polyline_area em metros
     * @param item string com coordenadas
     * @param boundBox boundbox do mapa
     * @return Lista de nós
     */
    private void criaNodeList(StringBuilder item) {
        //Data0=(-16.36298,-46.88731),(-16.36218,-46.88413)
        item = new StringBuilder(item.toString().replace(item.substring(0, 6), ""));
        //(-16.36298,-46.88731),(-16.36218,-46.88413)
        item = new StringBuilder(item.toString().replace("(", ""));
        //-16.36298,-46.88731),-16.36218,-46.88413)        
        String[] retornoStr = item.toString().split("\\),");
        //[-16.36298,-46.88731] [-16.36218,-46.88413)]        
        Node no = new Node();

        for (short i = 0; i < retornoStr.length; i++) {
            retornoStr[i] = retornoStr[i].replace(")", "");
            no.setNodeLevel(polyline_areaLevel);
            //[-16.36298,-46.88731]
            no.recebeLatLong(retornoStr[i]);
            listaNos.add(no);
            no = new Node();
        }

    }

    public boolean isValido() {
        return isValido;
    }

    public void setValido(boolean b) {
        isValido = b;
    }

    public boolean isCompleto() {
        return isCompleto;
    }

    public void setCompleto(boolean b) {
        isCompleto = b;
    }

    public void reinicia_polyline_area_ways() {
        this.polyline_area_types.clear();
        this.inicia_polyline_area_ways();
    }

    private void inicia_polyline_area_ways() {
        polyline_area_types.add("0x4::0x04::landuse=military");//osm.c
        polyline_area_types.add("0x5::0x05::amenity=parking");//osm.c
        polyline_area_types.add("0x7::0x07::aeroway=aerodrome");//osm.c
        polyline_area_types.add("0x9::0x09::leisure=marina");//osm.c
        polyline_area_types.add("0xb::0x0b::0x13::amenity=hospital");//osm.c
        polyline_area_types.add("0x6f::0xc::0x0c::landuse=industrial");//osm.c
        polyline_area_types.add("0x8::0x08::landuse=retail");//osm.c
        polyline_area_types.add("0xd::boundary=protected_area,protect_class=24,ethnic_group=indigenous");//osm.c
        polyline_area_types.add("0x6d::landuse=comercial");//osm.c
        polyline_area_types.add("0xa::0x0a::amenity=university");//osm.c
        polyline_area_types.add("0x14::leisure=nature_reserve");//osm.c
        polyline_area_types.add("0x17::leisure=park");//osm.c
        polyline_area_types.add("0x18::leisure=golf_course");//osm.c
        polyline_area_types.add("0x19::0x6e::leisure=sports_centre");//osm.c
        polyline_area_types.add("0x1a::landuse=cemetery");//osm.c
        polyline_area_types.add("0x28::natural=coastline");//osm.c
        polyline_area_types.add("0x41::natural=water");//osm.c
        polyline_area_types.add("0x4b::boundary=administrative" + ( LerMP.boolCreateStarTagsForUnrecognizesPFMAttributes ? ",*=fundo_de_mapa" : "") );//osm.c
        polyline_area_types.add("0x4c::natural=marsh,natural=wetland");//osm.c natural=marsh não é mais usado no osm soemnte no garmin
        polyline_area_types.add("0x4e::landuse=farm");//osm.c
        polyline_area_types.add("0x4f::natural=scrub");//osm.c
        polyline_area_types.add("0x50::landuse=forest");//osm.c
        polyline_area_types.add("0x51::landuse=residential");//osm.c
        polyline_area_types.add("0x53::natural=land");//osm.c
      




    }
}
