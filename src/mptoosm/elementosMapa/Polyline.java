/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mptoosm.mp.LerMP;
import mptoosm.utils.FormataOsmTag;
import mptoosm.utils.Util;

/**
 *
 * @author diecavallax
 */
public class Polyline {
    
    private String polylineInicio = "[RGN40]-[POLYLINE]";
    private String polylineFim = "[END]-[END-RGN40]";
    private String polylineType = "";
    public String polylineLabel = "";
    public String polylineLabel2 = "";
    private short polylineLevel = -1;
    int polylineOsmId = 0;
    List<Node> listaNos = new ArrayList<Node>(10);
    Numeracoes listaNumeracoes = new Numeracoes();
    private short polylineCityIdx = 0;
    private boolean polylineOneWay = false;
    private String polylineRouteParam = "";
    private String polylineRoadID = "";
    static List<String> polylines_ways = new ArrayList<String>(10);
    private boolean polylineIsDraft = false;

    public static Pattern regexNumbers = Pattern.compile("^Numbers[0-9]+=(.+)$", Pattern.CASE_INSENSITIVE);
    public static Pattern regexInterstateName = Pattern.compile("^([a-z]{2,3}-[0-9]{3,4}).*$", Pattern.CASE_INSENSITIVE);

//    String polylineNos= " ";
    /**
     * se a polyline pertence ao level 0
     */
    boolean isValido = false;
    /**
     * se já está completo para ser escrito
     */
    boolean isCompleto = false;
    /**
     *02/03/12 informa se a polyline foi dividida para adequar a restricao
     */
    boolean polylineFoiDivididaPRestricao = false;
    
    public Polyline() {
        inicia_polyline_ways();
    }
    
    private Polyline(Polyline novaPolyline) {
        
        isCompleto = true;
        isValido = true;
        polylineCityIdx = novaPolyline.polylineCityIdx;
        polylineLabel = novaPolyline.polylineLabel;
        polylineLabel2 = novaPolyline.polylineLabel2;
        polylineLevel = novaPolyline.polylineLevel;
        polylineOneWay = novaPolyline.polylineOneWay;
        if (novaPolyline.polylineOsmId <= 0) {
            polylineOsmId = LerMP.getProximoElementoOsmId();
        } else {
            polylineOsmId = novaPolyline.polylineOsmId;
        };
        polylineRoadID = novaPolyline.polylineRoadID;
        polylineRouteParam = novaPolyline.polylineRouteParam;
        polylineType = novaPolyline.polylineType;
//        listaNos.addAll(novaPolyline.listaNos);
        for (Node no : novaPolyline.listaNos) {
            Node novoNO = new Node(no);
            listaNos.add(novoNO);
            
        }
        polylineIsDraft = novaPolyline.polylineIsDraft;
    }
    
    public void clear() {
        polylineType = "";
        polylineLabel = "";
        polylineLabel2 = "";
        polylineLevel = -1;
        polylineOsmId = 0;
        listaNos.clear();
        polylineCityIdx = 0;
        polylineOneWay = false;
        polylineRouteParam = "";
        isValido = false;
        isCompleto = false;
        polylineIsDraft = false;
//        polylineNos=" ";
        //static List<String> polylines_ways = new ArrayList<>(10);
    }

    //TODO maxspeed atraves de routeParam
    public void adicionaLinha(String item) {
        //if (!item.contains(poiInicio) & !item.contains(POIFim) & !item.isEmpty()) {
        if (!polylineInicio.contains(item) & !polylineFim.contains(item) & !item.isEmpty() & isValido) {
            if (item.length() >= 6 && item.substring(0, 6).contains("Type=")) {
                polylineType = new String(item.substring(item.indexOf("=") + 1, item.length()));
                
            } else if (item.length() >= 6 && item.substring(0, 6).contains("Label=")) {
                String text = item.substring(6, item.length());
                polylineLabel = LerMP.boolConvertLabelsToUpperCase ? text.toUpperCase() : text;
                if (polylineLabel.contains("~[")) {
                    polylineLabel = polylineLabel.substring(polylineLabel.indexOf("]") + 1, polylineLabel.length());
                }
                if( polylineLabel.toUpperCase().contains(" DRAFT") ){
                    polylineLabel = polylineLabel.replaceFirst(" DRAFT", "");
                    polylineIsDraft = true;
                } else {
                    polylineIsDraft = false;
                }
            } else if (item.length() >= 7 && item.substring(0, 7).contains("Label2=")) {
                String text = item.substring(7, item.length());
                polylineLabel2 = LerMP.boolConvertLabelsToUpperCase ? text.toUpperCase() : text;
            } else if (item.contains("CityIdx=")/**/) {
                polylineCityIdx = Short.parseShort(item.substring(item.indexOf("=") + 1, item.length()));
            } else if (item.contains("DirIndicator=1")/**/) {
                polylineOneWay = true;
            } else if (item.contains("RouteParam=")/**/) {
                polylineRouteParam = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("RoadID=")/**/) {
                polylineRoadID = item.substring(item.indexOf("=") + 1, item.length());
            } else if ((item.substring(0, 6).contains("Data") && item.substring(4, 7).contains("="))
                    || item.contains("Origin0")) {
                polylineLevel = Short.parseShort(item.substring(item.indexOf("=") - 1, item.indexOf("=")));
                if (polylineLevel == 0 && polylineLabel.length() > 1) {
                    polylineOsmId = LerMP.getProximoElementoOsmId();
                    criaNodeList(new StringBuilder(item));
                } else {
                    isValido = false;
                }
                
            } else if (item.substring(0, 4).contains("Nod")/**/) {
                adicionReferecias(item);
            } else if (item.length() >= 12 && item.substring(0, 12).contains("CountryName=")) {
                item = item.substring(12, item.length());
                if (item.contains("[")) {
                    //                  CountryName = item.replace(item.substring(item.indexOf("[")-1, item.length()),"");
                } else {
                    //                  CountryName = item;
                }
            } else if (item.length() >= 9 && item.substring(0, 9).contains("CityName=")) {
                //              CityName = item.substring(9, item.length());
            } else if (item.length() >= 11 && item.substring(0, 11).contains("RegionName=")) {
                item = item.substring(11, item.length());
                if (item.contains("~")) {
                    //                RegionName = item.replace(item.substring(item.indexOf("[")-1, item.length()),"");
                } else {
                    //               RegionName = item;
                }
            ///////////NUMERAÇÃO//////////////
            } else if ( Polyline.regexNumbers.matcher( item ).matches() ) { //linhas com numeracao
                Matcher matcher = Polyline.regexNumbers.matcher( item );
                this.listaNumeracoes.addNumeracao( new Numeracao( item ) );
            }
            
        }
    }

    public boolean isRoutable(){
        return !this.polylineRouteParam.isEmpty();
    }

    public StringBuilder getItensParaArquivoOSM() {
        
        StringBuilder retorno = new StringBuilder();
        //se polyline é válida, pertence ao nivel zero e possui mais de 1 nó
        if (isValido & polylineLevel == 0 & listaNos.size() > 1) {
            StringBuilder[] nos = getNodesOsm();
            //adicionar os nós que  que são referenciados pelo elemento
            retorno.append(nos[0]);
            //inicia o elemento 
            retorno.append(getPolylineOsmCabecalho());
            //adicionar referencias de nos
            retorno.append(nos[1]);
            
            retorno.append(getPolylineToOsmType());
            if (polylineOneWay) {
                retorno.append(FormataOsmTag.retornaTag("oneway=yes"));
            }
            if( LerMP.boolIncludeInfoForCompilers )
                retorno.append(FormataOsmTag.retornaTag("polyline_img_type=" + polylineType));

            //tag name is not generated for ways without name (only way class in label).
            if( this.isRoutable() ){
                String words[] = polylineLabel.split("[\\s+-]");
                if (words.length == 1)
                    //retorno.append(FormataOsmTag.retornaTag("name="));
                    ;
                else{
                    //tests whether this way has interstate designation in its name
                    Matcher m = regexInterstateName.matcher( polylineLabel );
                    if( m.matches() ){
                        //adds the ref= tag for ways with interstate designations, so a shield symbol is rendered
                        retorno.append(FormataOsmTag.retornaTag("ref=" + m.group(1)));
                        if( polylineLabel2.trim().length() > 0 )
                            //PFM from Tracksource maps has the name of the interstate as label2
                            retorno.append( FormataOsmTag.retornaTag( "name=" + polylineLabel2 ) );
                        else
                            retorno.append( FormataOsmTag.retornaTag( "name=" + polylineLabel ) );
                    } else
                        retorno.append(FormataOsmTag.retornaTag("name=" + polylineLabel));
                }
            } else
                retorno.append(FormataOsmTag.retornaTag("name=" + polylineLabel));

            //way may have an alternative label (Label2=... in PFM)
            if( ! polylineLabel2.isEmpty() )
                retorno.append( FormataOsmTag.retornaTag( "alt_name=" + polylineLabel2 ) );

            //way may be marked as having too few detail (keyword DRAFT in label)
            if( polylineIsDraft )
                retorno.append( FormataOsmTag.retornaTag("fixme=way needs detailing") );

            //if word "tunel" is present in label, mark way as being a tunnel
            if( polylineLabel.toUpperCase().startsWith("TÚNEL")
                || polylineLabel.toUpperCase().startsWith("TUNEL")
                || polylineLabel.toUpperCase().contains("TUNNEL"))
                retorno.append( FormataOsmTag.retornaTag("tunnel=yes") );

            //if word "viaduct" is present in label, mark way as being a viaduct
            if( polylineLabel.toUpperCase().startsWith("VIADUTO")
                || polylineLabel.toUpperCase().contains("VIADUCT"))
                retorno.append( FormataOsmTag.retornaTag("bridge=viaduct") );

            //if word "bridge" is present in label, mark way as being a bridge
            if( polylineLabel.toUpperCase().startsWith("PONTE")
                || polylineLabel.toUpperCase().contains("BRIDGE"))
                retorno.append( FormataOsmTag.retornaTag("bridge=yes") );


            retorno.append("\n </way>");

            //////////////////////////////////////////INICIO LOGICA DE SUPORTE A NUMERACAO////////////////////////////////////
            /////NUMERAÇÃO: um novo track (way) especial deve ser criado para cada lado de cada aresta.
            if( !this.listaNumeracoes.isVazia() ){
                retorno.append("\n<!-- ============ BEGIN of house numbers for ").append(this.polylineLabel).append(" ============ -->\n");

                StringBuilder nd_refs_para_numeracao_esq = new StringBuilder();
                StringBuilder nd_refs_para_numeracao_dir = new StringBuilder();
                String num_type_esq = null;
                String num_type_dir = null;
                for( Numeracao num : this.listaNumeracoes.lista() ){
                    //StringBuilder nd_refs_para_numeracao = new StringBuilder();
                    //LADO ESQUERDO
                    if( ! num.number_type_left_side.equalsIgnoreCase("N") ){
                        retorno.append("  <!-- === left side interval === -->\n");
                        //criar novos nodes para suporte
                        {  //TODO: Refactor this block: String Numeracao.makeNodes( Polyline );  String Numeracao.makeNdRefs( Polyline );
                            //colocar o último sequencial de distal node para completar a sequencia de nodes
                            if( num.distal_node_seq_in_poly == null )
                                num.setDistalNodeSeqInPoly( String.valueOf( this.listaNos.size()-1 ) );
                            ArrayList<Node> nos_paral_esq = this.getParalela( Integer.valueOf( num.base_node_seq_in_poly ), Integer.valueOf( num.distal_node_seq_in_poly ), false );
                            //gerar a sequencia de novos nodes
                            int ult_i = ( Integer.valueOf( num.distal_node_seq_in_poly ) - Integer.valueOf( num.base_node_seq_in_poly ) );
                            for( int i = 0; i <= ult_i; i++){
                                int osm_id = LerMP.getProximoElementoOsmId();
                                retorno.append(" <node id=\"").append(LerMP.strIDsSignal).append( osm_id ).append("\"");
                                retorno.append(LerMP.strIncludeActionModify);
                                retorno.append( nos_paral_esq.get(i).getOsmLatLong() );
                                retorno.append(LerMP.user);
                                retorno.append(LerMP.uid);
                                retorno.append(LerMP.visible);
                                retorno.append(LerMP.version);
                                retorno.append(LerMP.changeset);
                                //retorno.append(" timestamp=\"");
                                //retorno.append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
                                //retorno.append("Z\">");
                                retorno.append(">");
                                if( i == 0 )
                                    retorno.append("\n  <tag k=\"addr:housenumber\" v=\"").append( num.proximal_left_number).append("\" />\n");
                                else if (i == ult_i)
                                    retorno.append("\n  <tag k=\"addr:housenumber\" v=\"").append( num.distal_left_number).append("\" />\n");
                                retorno.append("\n  <tag k=\"addr:street\" v=\"").append( this.polylineLabel ).append("\" />\n");
                                retorno.append(" </node>\n");
                                //============ <relation... para ligar o número à via
                                //só existem números nos extremos
                                if( i == 0 || i == ult_i ){
                                    retorno.append(" <relation id=\"").append(LerMP.strIDsSignal).append(LerMP.getProximoElementoOsmId()).append("\"").append(LerMP.strIncludeActionModify).append(" visible=\"true\"").append(LerMP.version).append(">\n");
                                    retorno.append("   <member type=\"node\" ref=\"").append(LerMP.strIDsSignal).append(osm_id).append("\" role=\"house\" />\n");
                                    retorno.append("   <member type=\"way\" ref=\"").append(LerMP.strIDsSignal).append(this.polylineOsmId).append("\" role=\"street\" />\n");
                                    retorno.append("   <tag k=\"type\" v=\"associatedStreet\" />\n");
                                    retorno.append(" </relation>\n");
                                }
                                //============ <nd_ref...
                                nd_refs_para_numeracao_esq.append("  <nd ref=\"").append(LerMP.strIDsSignal).append( osm_id ).append("\"/>\n");
                            }
                        }
                        if( num.number_type_left_side.equalsIgnoreCase("E") )
                            num_type_esq = "even";
                        else if(num.number_type_left_side.equalsIgnoreCase("O"))
                            num_type_esq = "odd";
                        else if(num.number_type_left_side.equalsIgnoreCase("B"))
                            num_type_esq = "all";
                    }

                    //LADO DIREITO
                    if( ! num.number_type_right_side.equalsIgnoreCase("N") ){
                        retorno.append("  <!-- === right side interval === -->\n");
                        //criar novos nodes para suporte
                        {  //TODO: Refactor this block: String Numeracao.makeNodes( Polyline );  String Numeracao.makeNdRefs( Polyline );
                            //colocar o último sequencial de distal node para completar a sequencia de nodes
                            if( num.distal_node_seq_in_poly == null )
                                num.setDistalNodeSeqInPoly( String.valueOf( this.listaNos.size()-1 ) );
                            ArrayList<Node> nos_paral_dir = null;
                            nos_paral_dir = this.getParalela( Integer.valueOf( num.base_node_seq_in_poly ), Integer.valueOf( num.distal_node_seq_in_poly ), true );
                            //gerar a sequencia de novos nodes
                            int ult_i = ( Integer.valueOf( num.distal_node_seq_in_poly ) - Integer.valueOf( num.base_node_seq_in_poly ) );
                            for( int i = 0; i <= ult_i; i++){
                                int osm_id = LerMP.getProximoElementoOsmId();
                                retorno.append(" <node id=\"").append(LerMP.strIDsSignal).append( osm_id ).append("\"");
                                retorno.append(LerMP.strIncludeActionModify);
                                retorno.append( nos_paral_dir.get(i).getOsmLatLong() );
                                retorno.append(LerMP.user);
                                retorno.append(LerMP.uid);
                                retorno.append(LerMP.visible);
                                retorno.append(LerMP.version);
                                retorno.append(LerMP.changeset);
                                //retorno.append(" timestamp=\"");
                                //retorno.append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
                                //retorno.append("Z\">");
                                retorno.append(">");
                                if( i == 0 )
                                    retorno.append("\n  <tag k=\"addr:housenumber\" v=\"").append( num.proximal_right_number).append("\" />\n");
                                else if (i == ult_i)
                                    retorno.append("\n  <tag k=\"addr:housenumber\" v=\"").append( num.distal_right_number).append("\" />\n");
                                retorno.append("\n  <tag k=\"addr:street\" v=\"").append( this.polylineLabel ).append("\" />\n");
                                retorno.append(" </node>\n");
                                //============ <relation... para ligar o número à via
                                //só existem números nos extremos
                                if( i == 0 || i == ult_i ){
                                    retorno.append(" <relation id=\"").append(LerMP.strIDsSignal).append(LerMP.getProximoElementoOsmId()).append("\"").append(LerMP.strIncludeActionModify).append(" visible=\"true\"").append(LerMP.version).append(">\n");
                                    retorno.append("   <member type=\"node\" ref=\"").append(LerMP.strIDsSignal).append(osm_id).append("\" role=\"house\" />\n");
                                    retorno.append("   <member type=\"way\" ref=\"").append(LerMP.strIDsSignal).append(this.polylineOsmId).append("\" role=\"street\" />\n");
                                    retorno.append("   <tag k=\"type\" v=\"associatedStreet\" />\n");
                                    retorno.append(" </relation>\n");
                                }
                                //============<nd refs...
                                nd_refs_para_numeracao_dir.append("  <nd ref=\"").append(LerMP.strIDsSignal).append( osm_id ).append("\"/>\n");
                            }
                        }
                        if( num.number_type_right_side.equalsIgnoreCase("E") )
                            num_type_dir = "even";
                        else if(num.number_type_right_side.equalsIgnoreCase("O"))
                            num_type_dir = "odd";
                        else if(num.number_type_right_side.equalsIgnoreCase("B"))
                            num_type_dir = "all";
                    }
                }

                //header do way para todo o lado esquerdo da via
                retorno.append(" <way id=\"").append(LerMP.strIDsSignal);
                retorno.append( LerMP.getProximoElementoOsmId() ).append("\"");
                retorno.append(LerMP.strIncludeActionModify);
                retorno.append(LerMP.user);
                retorno.append(LerMP.uid);
                retorno.append(LerMP.visible);
                retorno.append(LerMP.version);
                retorno.append(LerMP.changeset);
                //retorno.append(" timestamp=\"").append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())).append("Z\">\n");
                retorno.append(">\n");
                retorno.append(nd_refs_para_numeracao_esq);
                retorno.append("  <tag k=\"addr:interpolation\" v=\"").append(num_type_esq).append("\" />\n");
                retorno.append("  <tag k=\"addr:inclusion\" v=\"potential\"/>\n");
                retorno.append(" </way>\n");

                //header do way para todo lado direito da via
                retorno.append(" <way id=\"").append(LerMP.strIDsSignal);
                retorno.append( LerMP.getProximoElementoOsmId() ).append("\"");
                retorno.append(LerMP.strIncludeActionModify);
                retorno.append(LerMP.user);
                retorno.append(LerMP.uid);
                retorno.append(LerMP.visible);
                retorno.append(LerMP.version);
                retorno.append(LerMP.changeset);
                //retorno.append(" timestamp=\"").append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())).append("Z\">\n");
                retorno.append(">\n");
                retorno.append(nd_refs_para_numeracao_dir);
                retorno.append("  <tag k=\"addr:interpolation\" v=\"").append(num_type_dir).append("\" />\n");
                retorno.append("  <tag k=\"addr:inclusion\" v=\"potential\"/>\n");
                retorno.append(" </way>\n");


                retorno.append("<!-- ================ END of house numbers for ").append(this.polylineLabel).append(" ============== -->");

                this.listaNumeracoes.limpar();
            }
            //////////////////////////////////////////FIM LOGICA DE SUPORTE A NUMERACAO////////////////////////////////////
        }
        return retorno;
    }
    
    StringBuilder getPolylineToOsmType() {
        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Poly - ").append(polylineType).append(" ").append(polylineLabel);
        
        StringBuilder teste = FormataOsmTag.getOsmType(polylines_ways, polylineType, this.polylineLabel);
                
        return teste;
        //return FormataOsmTag.getOsmType(polylines_ways, polylineType);
    }

    /**
     * Retorna um arrey bidimensional onde os valores do indice 0 sao os nos para serem escritos
     * e os de indice 1 sao somente referencias pois já existem no arquivo osm
     * @return 
     */
    StringBuilder[] getNodesOsm() {
        StringBuilder[] retorno = new StringBuilder[2];
        retorno[0] = new StringBuilder();//nos para serem adicionados antes do polygon
        retorno[1] = new StringBuilder();// referencias dos nós 
        for (Node no : listaNos) {
            //se o nó não for somente referencia ou  ainda nao existe no arquivo osm
            if (!no.isReferenciaAOutroNo) {
                retorno[0].append(no.getNodes()).append("/>");
            }
            //retorno[1].append("\n  <nd ref=\"").append(no.noOsmId).append("\"/>");
            retorno[1].append("\n  <nd ref=\"").append(LerMP.strIDsSignal).append(no.getNoOsmId()).append("\"/>");
        }
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
    StringBuilder getPolylineOsmCabecalho() {
        StringBuilder retorno = new StringBuilder();
        //retorno.append("\n <way id=\"").append(polylineOsmId).append("\"").append(LerMP.user).append(LerMP.uid).append(LerMP.visible).append(LerMP.version).append(LerMP.changeset).append(" timestamp=\"").append(DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATETIME_FORMAT.getPattern())).append("Z\">");
        retorno.append("\n <way id=\"").append(LerMP.strIDsSignal).append(polylineOsmId).append("\"").append(LerMP.strIncludeActionModify).append(LerMP.user).append(LerMP.uid).append(LerMP.visible).append(LerMP.version).append(LerMP.changeset).append(">");
        //retorno.append("\n  </node>");

        return retorno;
    }

    /**
     * Cria lista de nos para mpId_OsmId polyline de acordo com mpId_OsmId estring passada, DataX=(x,y)
     * e seta o tamanho da polyline em metros
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
            no.setNodeLevel(polylineLevel);
            //[-16.36298,-46.88731]
            no.recebeLatLong(retornoStr[i]);
            listaNos.add(no);
            no = new Node();
        }
        
    }

    /**
     * Cria a referencia do id do no no mpf ao id do nó no osm.xml
     * * armazena mpId_OsmId relacao do nó com o número de identificacao no mp
     * 0 = numero d nó  968604= id no mp - 0:968604
     */
    private void adicionReferecias(String item) {
        /*Nod1=0,968604,0
        Nod2=1,968822,0*/
        if (item.toString().contains("Nodes")) {
        } else {
            /**
             * armazena mpId_OsmId relacao do nó com o número de identificacao no mp
             * 0 = numero d nó  968604= id no mp - 0:968604
             */
            String[] relacao = item.substring(item.indexOf("=") + 1, item.length()).split(",");

            //verifica se já foi adicionado nó com o mesmo mpId
            if (LerMP.mpId_OsmId.containsKey(Integer.parseInt(relacao[1]))) {
                //seta o nó para não ser escrito no arquivo osm somente a sua referencia na way
                listaNos.get(Integer.parseInt(relacao[0])).isReferenciaAOutroNo = true;
                //seta o id osm do nó
//                listaNos.get(Integer.parseInt(relacao[0])).noOsmId =
                listaNos.get(Integer.parseInt(relacao[0])).setNoOsmId(
                        LerMP.mpId_OsmId.get(Integer.parseInt(relacao[1])));
                listaNos.get(Integer.parseInt(relacao[0])).noMpfId = Integer.parseInt(relacao[1]);
            } else {
                //armazena mpId_OsmId referencia do mpId do nó com o seu osmId
                LerMP.mpId_OsmId.put(Integer.parseInt(relacao[1]), listaNos.get(Integer.parseInt(relacao[0])).getNoOsmId());
                listaNos.get(Integer.parseInt(relacao[0])).noMpfId = Integer.parseInt(relacao[1]);
            }
            
        }
        
    }
    
    public void reinicia_polyline_ways(){
        Polyline.polylines_ways.clear();
        this.inicia_polyline_ways();
    }

    private void inicia_polyline_ways() {

        //PFM has two types of road that share the same Garmin code 0x1
        //Distinction is made by the road class in RouteParams=
        //4 = Roads
        //3 = Highways
        polylines_ways.add("0x1,4::highway=motorway");//osm.c
        polylines_ways.add("0x1,3::highway=primary");//osm.c

        polylines_ways.add("0x2::highway=secondary");// highway=trunk osm.c //changed from primary in 2013-07-08
        polylines_ways.add("0x3::highway=secondary"); //changed from primary in 2013-07-08
        polylines_ways.add("0x4::highway=tertiary");//osm.c //changed from secondary in 2013-07-08
        polylines_ways.add("0x5::highway=tertiary");
        polylines_ways.add("0x6::highway=residential");//osm.c 
        polylines_ways.add("0x7::aeroway=aerodrome");//osm.c

        //Way links:
        //Depending on its RouteParam (road class flag) PFM attribute, tag set is different
        polylines_ways.add("0x8,4::highway=motorway_link");//osm.c
        polylines_ways.add("0x8,3::highway=primary_link");//osm.c
        polylines_ways.add("0x8,2::highway=secondary_link");//osm.c
        polylines_ways.add("0x8,1::highway=tertiary_link");//osm.c
        polylines_ways.add("0x8,0::highway=residential_link");//osm.c

        //Ramps:
        //Depending on its RouteParam (road class flag) PFM attribute, tag set is different
        polylines_ways.add("0x9,4::highway=motorway_link,junction=ramp");// osm.c
        polylines_ways.add("0x9,3::highway=primary_link,junction=ramp");// osm.c
        polylines_ways.add("0x9,2::highway=secondary_link,junction=ramp");// osm.c
        polylines_ways.add("0x9,1::highway=tertiary_link,junction=ramp");// osm.c
        polylines_ways.add("0x9,0::highway=residential_link,junction=ramp");// osm.c

        //PFM has two types of unpaved roads: main and secondary
        //but the same Garmin TYP code is used for both in the default TYP
        //so distinction is made by the first routing parameter (speed).
        //RouteParam=2,0,0,0,0,0,0,0,0,0,0,0 -> typical for main unpaved roads
        //RouteParam=1,0,0,0,0,0,0,0,0,0,0,0 -> typical for secondary unpaved roads
        polylines_ways.add("0xa,2::highway=tertiary,surface=unpaved"); //osm.c
        polylines_ways.add("0xa,1::highway=unclassified,surface=unpaved"); //osm.c

        polylines_ways.add("0xb::highway=motorway_link");

        //Roundabouts:
        //Depending on its RouteParam (road class flag) PFM attribute, tag set is different
        polylines_ways.add("0xc,4::highway=motorway,junction=roundabout");//osm.c
        polylines_ways.add("0xc,3::highway=primary,junction=roundabout");//osm.c
        polylines_ways.add("0xc,2::highway=secondary,junction=roundabout");//osm.c
        polylines_ways.add("0xc,1::highway=tertiary,junction=roundabout");//osm.c
        polylines_ways.add("0xc,0::highway=unclassified,junction=roundabout");//osm.c

        polylines_ways.add("0x14::railway=rail");//osm.c

        //PFM type 0x16 can be used for three types of ways, depending on routing params
        //1,0,0,0,1,1,1,1,1,0,0,1, --> moutainbike or motocross track
        //1,0,0,0,1,1,0,1,1,0,0,1, --> 4wd track
        //0,0,0,0,1,1,1,1,1,0,0,1, --> cycle/pedestrian way
        polylines_ways.add("0x16,1,1,*::highway=path,surface=unpaved,bicycle=yes,foot=designated,motorcycle=yes"); //highway=path osm.c
        polylines_ways.add("0x16,1,0,*::highway=track,surface=unpaved,4wd_only=yes"); //highway=path osm.c
        polylines_ways.add("0x16,0,1,e::highway=steps"); //this must come BEFORE the rule with * because it's more generic
        polylines_ways.add("0x16,0,1,p::highway=path,surface=unpaved"); //this must come BEFORE the rule with * because it's more generic
        polylines_ways.add("0x16,0,1,*::highway=pedestrian,foot=yes,bicycle=yes"); //highway=path osm.c

        polylines_ways.add("0x18::waterway=canal");//osm.c
        polylines_ways.add("0x1a::route=ferry");//osm.c
        polylines_ways.add("0x1c::boundary=administrative,admin_level=4::highway=bridleway");//osm.c highway=bridleway para aparecer na tela
        polylines_ways.add("0x1d::boundary=administrative,admin_level=8::highway=bridleway");//osm.c highway=bridleway para aparecer na tela
        polylines_ways.add("0x1e::boundary=administrative,admin_level=2");//osm.c highway=bridleway para aparecer na tela
        polylines_ways.add("0x1f::waterway=river");//osm.c
        //polylines_ways.add("0x23::0x24::0x25::barrier=ditch");//osm.c
        polylines_ways.add("0x20::0x23::contour_ext=elevation_minor");//osm.c
        polylines_ways.add("0x21::0x24::contour_ext=elevation_medium");//osm.c
        polylines_ways.add("0x22::0x25::contour_ext=elevation_major");//osm.c
        polylines_ways.add("0x26::waterway=stream");//osm.c
        polylines_ways.add("0x27::aeroway=runway");//osm.c
        polylines_ways.add("0x28::man_made=pipeline,power=line");//como ainda nao possui equivalente na conversão para bin do navit setado como line
        polylines_ways.add("0x29::power=line");


        /*para as elevacoes
        contour_ext=elevation_major	height_line_1\n"
        "w	contour_ext=elevation_medium	height_line_2\n"
        "w	contour_ext=elevation_minor	height_line_3\n"
         */
        
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


    /*/**
     * Retorna o osmid da polyline que faz parte a restricao, tambem divide a polyline
     * para que se adeque as regras das restricoes
     * divide a polyline a partir do nó passado, criando outras polylines que inicie e termine
     * nos nós das restrições, retorna o id_osm da polyline que pertence  restricao
     * @param listaDePolylineParaEscrever
     * @return 
     */
    public int splitPolilyne(Node node, List<Polyline> novalistaDePolylineParaEscrever) {
        int nodeidx = listaNos.indexOf(node);
        int from_via_to_id = -1;
        Polyline novaPolyline;
        //se a via possui dois nós e estes pertence a restricao retorna o osmid da via
        if (nodeidx == 0 && nodeidx + 1 == listaNos.size() - 1) {
            //retorna como from_via_to_id
            return polylineOsmId;
        } else //Se o primeiro e segundo nó da via pertencem a restriao
        if (nodeidx == 0 && nodeidx + 1 < listaNos.size() - 1) {
            
            novaPolyline = new Polyline(this);
            //cria uma polyline que inicia e termina nos pontos da restricao
            novaPolyline.listaNos.clear();
            novaPolyline.polylineOsmId = -1;
            novaPolyline.listaNos.addAll(listaNos.subList(0, nodeidx + 2));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
            from_via_to_id = novalistaDePolylineParaEscrever.get(novalistaDePolylineParaEscrever.size() - 1).polylineOsmId;

            //cria uma polylina que inicia no ponto final a restricao ate o final da antiga polyline
            novaPolyline.listaNos.clear();
            novaPolyline.listaNos.addAll(listaNos.subList(nodeidx + 1, listaNos.size() - 1));
            novaPolyline.listaNos.add(listaNos.get(listaNos.size() - 1));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
            polylineFoiDivididaPRestricao = true;
            
        } else if (nodeidx > 0 && nodeidx + 1 == listaNos.size() - 1) {
            novaPolyline = new Polyline(this);

            //cria uma polylina que inicia no ponto final a restricao ate o final da antiga polyline
            novaPolyline.listaNos.clear();
            novaPolyline.listaNos.addAll(listaNos.subList(0, nodeidx + 1));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));

            //cria um polyline que inicia e termina nos pontos da restricao
            novaPolyline.listaNos.clear();
            novaPolyline.polylineOsmId = -1;
            novaPolyline.listaNos.addAll(listaNos.subList(nodeidx, nodeidx + 2));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
            from_via_to_id = novalistaDePolylineParaEscrever.get(novalistaDePolylineParaEscrever.size() - 1).polylineOsmId;
            polylineFoiDivididaPRestricao = true;
            
        } else if (nodeidx > 0 && nodeidx + 1 < listaNos.size() - 1) {
            novaPolyline = new Polyline(this);
            //cria uma polylina que inicia no ponto final a restricao ate o final da antiga polyline
            novaPolyline.listaNos.clear();
            novaPolyline.listaNos.addAll(listaNos.subList(0, nodeidx + 1));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));

            //cria um polyline que inicia e termina nos pontos da restricao
            novaPolyline.listaNos.clear();
            novaPolyline.polylineOsmId = -1;
            novaPolyline.listaNos.addAll(listaNos.subList(nodeidx, nodeidx + 2));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
            from_via_to_id = novalistaDePolylineParaEscrever.get(novalistaDePolylineParaEscrever.size() - 1).polylineOsmId;

            //cria uma polylina que inicia no ponto final a restricao ate o final da antiga polyline
            novaPolyline.listaNos.clear();
            novaPolyline.listaNos.addAll(listaNos.subList(nodeidx + 1, listaNos.size() - 1));
            novaPolyline.listaNos.add(listaNos.get(listaNos.size() - 1));
            novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
            polylineFoiDivididaPRestricao = true;
        }



        //polylineFoiDivididaPRestricao=false;
        //se o nó existe na polylin e não é o primeiro elemento
//        if (nodeidx > 0 & nodeidx < listaNos.size() - 1) {
//            novaPolyline = new Polyline(this);
//            novaPolyline.listaNos.clear();
//
//            //primeira parte da via, para os nos que ficam antes da interceção
//            novaPolyline.listaNos.addAll(listaNos.subList(0, nodeidx + 1));
//            if (novaPolyline.listaNos.size() > 1) {
//                
//                novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
//                polylineFoiDivididaPRestricao = true;
//            }
//            //segunda parte da via
//            if (nodeidx + 1 < listaNos.size() - 1) {
//                novaPolyline.polylineOsmId = -1;
//                novaPolyline.listaNos.clear();
//                novaPolyline.listaNos.addAll(listaNos.subList(nodeidx, nodeidx + 2));
//
//                //informa que o nó já existe em outra polyline e então não precisa ser impresso somente referenciado
//                novaPolyline.listaNos.get(0).isReferenciaAOutroNo = true;
//                novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
//                from_via_to_id = novalistaDePolylineParaEscrever.get(novalistaDePolylineParaEscrever.size() - 1).polylineOsmId;
//            }
//            //terceira parte da via
//            //se existir mais nos  na polyline
//            if (nodeidx + 2 <= listaNos.size() - 1) {
//                //if (nodeidx <= listaNos.size() - 1) {
//                novaPolyline.polylineOsmId = -1;
//                novaPolyline.listaNos.clear();
//                novaPolyline.listaNos.addAll(listaNos.subList(nodeidx + 1, listaNos.size() - 1));
//                //    novaPolyline.listaNos.addAll(listaNos.subList(nodeidx, listaNos.size() - 1));
//                novaPolyline.listaNos.add(listaNos.get(listaNos.size() - 1));
//                
//                if (novaPolyline.listaNos.size() > 1) {
//                    //informa que o nó já existe em outra polyline e então não precisa ser impresso somente referenciado
//                    novaPolyline.listaNos.get(0).isReferenciaAOutroNo = true;
//                    novalistaDePolylineParaEscrever.add(new Polyline(novaPolyline));
//                    polylineFoiDivididaPRestricao = true;
//                }
//            }
//            //listaNos.remove(node);
//        } else if (nodeidx == 0) {
//        }
//        if (from_via_to_id == -1) {
//            return polylineOsmId;
//        }
        return from_via_to_id;
    }
    
    public List<Node> getListaNos() {
        return listaNos;
    }
    
    public String getPolylineRoadID() {
        return polylineRoadID;
    }

    /**
     * Computa uma nova lista de nós de forma que o resultado seja
     * uma poly paralela e à esquerda a esta
     * dando-se um intervalo de nós.
     * @param seq_node_inicial Índice do primeiro nó.
     * @param seq_node_final Índice do último nó.
     * @param direita  Se true, gera a poly à direita (ref. ao sentido de desenho) desta.
     */
    public ArrayList<Node> getParalela(int seq_node_inicial, int seq_node_final, boolean direita){
        ArrayList<Node> resultado = new ArrayList<Node>();

        {
            //iterar pelos pontos do tracklog
            //de forma a ter as extremidades de cada segmento
            Node no1 = null, no2 = null;
            double dois_pontos[] = new double[4];
            AffineTransform transformacao = new AffineTransform();
            double angulo = 0.0d;
            for( int i = seq_node_inicial; i <= seq_node_final; i++  ) {

                int ino1 = i; //nó atual
                int ino2 = ino1 + 1; //outro nó (quase sempre o seguinte na poly)

                //se o nó atual for o último, o outro nó será o penúltimo
                if( i == seq_node_final )
                    ino2 = ino1 - 1;

                no1 = this.listaNos.get(ino1);
                no2 = this.listaNos.get(ino2);

                //ler as coordenadas das extremidades do segmento
                double xi = no1.longitude;
                double yi = no1.latitude;
                double xf = no2.longitude;
                double yf = no2.latitude;

                //determinar o ângulo do segmento em relação à horizontal
                angulo = Util.getClockwiseAngle(xi, yi, xf, yf);

                //ângulo a ser usado para rotacionar o ponto
                double angulo_final = 0.0d;

                //medida de deslocamento lateral;
                double delta = 0.0d;

                //se o nó é o primeiro
                if( ino1 == seq_node_inicial ){
                    if( direita )
                        angulo_final = angulo + 0.5;
                    else
                        angulo_final = angulo - 0.5;
                    delta = 0.00005;
                }
                //se o nó é o último
                else if( ino1 == seq_node_final ){
                    if( direita )
                        angulo_final = angulo - 0.5;
                    else
                        angulo_final = angulo + 0.5;
                    delta = 0.00005;
                }
                //se o no não é o primeiro nem o último
                else if(ino1 > seq_node_inicial && ino1 < seq_node_final){
                    delta = 0.000025; //o delocamento lateral é feito à metada para manter a linha visualmente paralela
                    //calcular o ângulo orientado entre as duas arestas
                    double x0 = xi;
                    double y0 = yi;
                    double xV1 = xf;
                    double yV1 = yf;
                    double xV2 = this.listaNos.get( ino1 - 1 ).longitude;
                    double yV2 = this.listaNos.get( ino1 - 1 ).latitude;
                    double angulo_no_ponto = Util.getCounterClockwiseAngleBetweenVectors(x0, y0, xV1, yV1, xV2, yV2);
                    //fazer o ângulo de rotação do ponto de forma a posicioná-lo
                    //no ângulo médio entre as duas arestas
                    angulo_final = -(angulo_no_ponto/2.0d) + angulo;
                    //colocar do lado oposto ao cotovelo se a paralela
                    //for ao lado direito
                    if( direita )
                        angulo_final += Math.PI; //pi radianos == 180 graus
                }


                //iniciando a geometria do novo ponto
                //um pouco para a direita do ponto original
                dois_pontos[0] = xi + delta;
                dois_pontos[1] = yi;

                //preparar a transformação linear para fazer a rotação da geometria
                //de forma a alinhar o novo ponto com o sentido do segmento
                transformacao.setToIdentity();
                transformacao.translate(xi, yi);
                transformacao.rotate(-angulo_final);
                transformacao.translate(-xi, -yi);

                //executando a transformação oposta na geometria do novo ponto.
                transformacao.transform(dois_pontos, 0, dois_pontos, 2, 1);

                //derivando novo nó a partir do original
                //e o adicionando ao resultado
                Node novo_no = new Node( no1 );
                novo_no.longitude = dois_pontos[2];
                novo_no.latitude = dois_pontos[3];
                resultado.add(novo_no);

            }
            //Node novo_no = new Node( no2 );
            //resultado.add(novo_no);

        }



        return resultado;
    }

    /**
     * Returns the value of the RouteParam= attribute read from the PFM file.
     * It is formatted as a comma-separated list of digits specifying routing
     * parameters for the way such as max speed, road class, etc.
     * Refer to the cGPSmapper manual for the PFM specification regarding the meaning
     * of each routing parameter.
     */
    public String getRouteParam(){
        return polylineRouteParam;
    }
}
