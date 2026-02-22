/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.elementosMapa;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mptoosm.DesktopApplication1View;
import mptoosm.mp.LerMP;
import mptoosm.utils.FormataOsmTag;

/**
 * @author diecavallax
 */
public class POI implements Comparable<POI> {

    String poiInicio = "[POI]-[RGN10]-[RGN20]";
    String poiFim = "[END]-[END-RGN10] -[END-RGN20]";
    String poiType;
    public short poiCityIdx = -1;
    private String poiLabel = "";
    String CountryName = "";
    String poiCityName = "";
    String poiRegionName = "";
    Node no = new Node();

    public static Pattern regexTracksourceMilestone = Pattern.compile("^\\s*([a-z]{2,}\\s*-\\s*\\d{3,})?\\s*(?:km)\\s*(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);

    public static String gas_station_operators[] = {"Shell", "BR", "Repsol", "Ipiranga", "Forza", "Texaco", "Ale"};

    /**
     * se o poi pertence ao level 0
     */
    boolean isValido = false;
    /**
     * se já está completo para ser escrito no mapa osm
     */
    boolean isCompleto = false;
    String continenteTag = "\n  <tag k=\"is_in:continent\" v=\"South America\" />";
    String brasilTag = "\n  <tag k=\"is_in:country\" v=\"Brazil\" />";
    String brasilTagCode = "\n  <tag k=\"is_in:country_code\" v=\"BR\" />";
    static List<String> a_poi_points = new ArrayList<String>(10);
    private String poiHouseNumber = "";
    private String fone = "";
    StringBuilder retornoGeral = new StringBuilder();
    private String streetDesc = "";
    private String cep = "";
    private String fax = "";
    private String email = "";
    //para alertas
    boolean isPodeGerarAlerta = false;

    public POI() {
        inicia_poi_points();
    }

    public POI(POI poi) {
        inicia_poi_points();
        poiType = poi.poiType;
        poiCityIdx = poi.poiCityIdx;
        no = poi.no;
        poiLabel = poi.poiLabel;
    }

    //TODO mudar definicao de 0x3006 0x5700 para nao criar alertas no osm
    public boolean isPodeGerarAlerta() {

        return isPodeGerarAlerta;
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

    public void clear() {
        poiType = "";
        poiCityIdx = -1;
        no.clear();
        poiLabel = "";
        isCompleto = false;
        isValido = false;
        poiHouseNumber = "";
        fone = "";
        streetDesc = "";
        cep = "";
        fax = "";
        email = "";
        retornoGeral.delete(0, retornoGeral.length());
        boolean isPodeGerarAlerta = false;
    }

    public StringBuilder getItensParaArquivoOSM() {
        retornoGeral.delete(0, retornoGeral.length());
        if (isValido()) {
            retornoGeral.append(no.getNodes()).append(">");
            retornoGeral.append(getFone());
            retornoGeral.append(getFax());
            retornoGeral.append(getEmail());
            retornoGeral.append(getStreetDesc());
            retornoGeral.append(getCep());
            retornoGeral.append(getNumero());
            retornoGeral.append(getOsmType(poiType));
            retornoGeral.append(get24h());
            //TODO tratar o streetdesc - endereco do poi
            if( LerMP.boolIncludeInfoForCompilers )
                retornoGeral.append(FormataOsmTag.retornaTag("img_type=" + poiType));
            retornoGeral.append(nome()).append("\n  </node>");
        }
        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Escrevendo - POI - Ok");
        return retornoGeral;
    }

    public void adicionaLinha(String item) {
        //if (!item.contains(poiInicio) & !item.contains(poiFim) & !item.isEmpty()) {
        if (!poiInicio.contains(item) & !poiFim.contains(item) & !item.isEmpty() & isValido) {

            if (item.contains("Data0") || item.contains("Origin0")) { ////CHANGE: 2013-07-09: "Data" to "Data0" because it was catching labels containing the "Data" sequence
                no.setNodeLevel(Short.parseShort(item.substring(item.indexOf("=") - 1, item.indexOf("="))));
                no.recebeLatLong(item);
                if (no.getNodeLevel() != 0) {
                    isValido = false;
                }
            } else if (item.length() >= 6 && item.substring(0, 6).contains("Type=")) {
                poiType = new String(item.substring(item.indexOf("=") + 1, item.length()));
                isPodeGerarAlerta = poiType.equalsIgnoreCase("0x2500")
                        || poiType.equalsIgnoreCase("0x6401")
                        || poiType.equalsIgnoreCase("0x5700")
                        || poiType.equalsIgnoreCase("0x3001")
                        || poiType.equalsIgnoreCase("0x3002")
                        || poiType.equalsIgnoreCase("0x3006")
                        || poiType.equalsIgnoreCase("0x6406");
            } else if (item.contains("CityIdx=")/* index da cidade para mapedit*/) {
                poiCityIdx = Short.parseShort(item.substring(item.indexOf("=") + 1, item.length()));
            } else if (item.length() >= 6 && item.substring(0, 6).contains("Label=")) {
                String text = item.substring(6, item.length());
                poiLabel =  LerMP.boolConvertLabelsToUpperCase ? text.toUpperCase() : text;
            } else if (item.length() >= 13 && item.substring(0, 12).contains("CountryName=")) {
                item = item.substring(12, item.length());
                if (item.contains("[")) {
                    CountryName = item.replace(item.substring(item.indexOf("[") - 1, item.length()), "");
                } else {
                    CountryName = item;
                }
            } else if (item.contains("HouseNumber=")) {
                poiHouseNumber = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("Zip=")) {
                cep = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("Phone=")) {
                fone = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("Fax=")) {
                fax = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("Email=")) {
                email = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.contains("StreetDesc=")) {
                streetDesc = item.substring(item.indexOf("=") + 1, item.length());
            } else if (item.length() >= 9 && item.substring(0, 9).contains("CityName=")) {
                poiCityName = item.substring(9, item.length());
            } else if (item.length() >= 11 && item.substring(0, 11).contains("RegionName=")) {
                item = item.substring(11, item.length());
                if (item.contains("~")) {
                    poiRegionName = item.replace(item.substring(item.indexOf("[") - 1, item.length()), "");
                } else {
                    poiRegionName = item;
                }
            }
        }
    }

    /**
     * Informa se o poi em questão indica uma cidade
     * @return true se é uma cidade
     **/
    public boolean isCidade() {
        boolean retorno = false;
        //TODO verifica em outros leves as grandes cidades
        if ((poiType.equalsIgnoreCase("0x100")||poiType.equalsIgnoreCase("0x1")
                || poiType.equalsIgnoreCase("0x200")|| poiType.equalsIgnoreCase("0x2")
                || poiType.equalsIgnoreCase("0x300")|| poiType.equalsIgnoreCase("0x3")
                || poiType.equalsIgnoreCase("0x400")|| poiType.equalsIgnoreCase("0x4")
                || poiType.equalsIgnoreCase("0x500")|| poiType.equalsIgnoreCase("0x5")
                || poiType.equalsIgnoreCase("0x600") || poiType.equalsIgnoreCase("0x6")
                || poiType.equalsIgnoreCase("0x700") || poiType.equalsIgnoreCase("0x7")
                || poiType.equalsIgnoreCase("0x800")|| poiType.equalsIgnoreCase("0x8")
                || poiType.equalsIgnoreCase("0x900") || poiType.equalsIgnoreCase("0x9")
                // || poiType.equalsIgnoreCase("0xa00") || poiType.equalsIgnoreCase("0xb00")
                //|| poiType.equalsIgnoreCase("0xc00")
                )
                & no.getNodeLevel() == 0 & !poiLabel.toUpperCase().contains("BAIRRO ")
                & !poiLabel.toUpperCase().contains("VILA ") & !poiLabel.toUpperCase().contains("ASSENTAMENTO ")
                & !poiLabel.toUpperCase().contains("POVOADO ") & !poiLabel.toUpperCase().contains("CONJUNTO ")
                & !poiLabel.toUpperCase().contains("DISTRITO ") & !poiLabel.toUpperCase().contains("COLÔNIA ")
                & !poiLabel.toUpperCase().contains("LINHA ") & !poiLabel.toUpperCase().contains("PRAÇA ")) {
//            if (poiCityName.length() > 1 && !poiLabel.equalsIgnoreCase(poiCityName)) {
//                return retorno;
//            }
            poiCityIdx = LerMP.cidades.getCityIdx(poiLabel);
            retorno = true;
        }
        return retorno;
    }

    @Override
    public int compareTo(POI o) {
        return poiLabel.compareTo(o.poiLabel);
    }

    /**
     * Formata mpId_OsmId lat e lon de acordo com o padrao osm
     *  lat="-16.3579210" lon="-46.9060182"
     * @return 
     */
    /**
     * Retorna 
     *  <tag k="name" v="HOTEL SESC" />
     * @return 
     */
    String nome() {
        //return FormataOsmTag.retornaTag("name=" + poiLabel + " " + poiType).toString();
        //retirando o tipo img (Garmin) do label do POI para o arquivo OSM

        String labelUpper = poiLabel.trim().toUpperCase();
        if( poiType.equalsIgnoreCase("0x5700") ) //lombadas ficam sem nome no OSM
            return "";

        return FormataOsmTag.retornaTag("name=" + poiLabel).toString();
    }

    /**
     * Retorna
     *  <tag k="is_in:continent" v="South America" />
    <tag k="is_in:country" v="Brazil" />
    <tag k="is_in:country_code" v="BR" />
     *  <tag k="is_in:region" v="Sudeste" />
    <tag k="is_in:state" v="Minas Gerais" />
    <tag k="is_in:state_code" v="MG" />
     * <tag k="addr:city" v="JANUÁRIA" />
     * @return 
     */
    String continentePaisRegiaoEstadoCidadeTags() {
        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("Escrevendo - POI - buscando cidade");
        return continenteTag + brasilTag
                + brasilTagCode + getRegiaoEstadoESigla();
    }

    /**
     * Retorna
     *  <tag k="is_in:region" v="Sudeste" />
    <tag k="is_in:state" v="Minas Gerais" />
    <tag k="is_in:state_code" v="MG" />
    <tag k="addr:city" v="JANUÁRIA" />
     * @param cityIdx
     * @return 
     */
    String getRegiaoEstadoESigla() {

        if (CountryName.length() > 0 && poiCityName.length() > 0 && poiRegionName.length() > 0) {
            return "" + FormataOsmTag.retornaTag("is_in:state_code=" + LerMP.estados.getRegiao(poiRegionName))
                    + FormataOsmTag.retornaTag("is_in:state=" + poiRegionName)
                    + FormataOsmTag.retornaTag("addr:city=" + poiCityName);
        } else {
            return LerMP.cidades.getEstadoSiglaECidade(poiCityIdx);
        }
    }

    /**
     * Retorna tag opening_hours=24/7 em POIs contendo
     * 24 horas, 24horas, 24hrs, 24 hrs, 24 hr, 24hr, 24Hs, 24 Hs, 24H ou 24 H no label.
     * Nada retorna se esses textos não forem encontrados.
     */
    private StringBuilder get24h() {
        String lblUpper = poiLabel.toUpperCase();
        if (lblUpper.contains("24 HORAS")
                || lblUpper.contains("24HORAS")
                || lblUpper.contains("24HRS")
                || lblUpper.contains("24 HRS")
                || lblUpper.contains("24HR")
                || lblUpper.contains("24 HR")
                || lblUpper.contains("24HS")
                || lblUpper.contains("24 HS")
                || lblUpper.contains("24H")
                || lblUpper.contains("24 H")) {
            return FormataOsmTag.retornaTag("opening_hours=24/7");
        }
        return new StringBuilder();
    }


    /**
     * Retorna o tipo do objeto osm de acordo com o tipo do objeto do mpf
     * @param poiType
     * @return 
     */
    private StringBuilder getOsmType(String poiType) {
        LerMP.mensagem.delete(0, LerMP.mensagem.length()).append("POI - ").append(poiType).append(" ").append(poiLabel);
        if (isCidade()) {
            StringBuilder retorno = new StringBuilder();
            if (poiType.compareTo("0x800") < 0 || poiType.compareTo("0x8") < 0) {
//                retornoGeral.append(FormataOsmTag.retornaTag("place=town"));
                retorno.append(FormataOsmTag.retornaTag("place=city"));
            } else if (poiType.equalsIgnoreCase("0x900") || poiType.equalsIgnoreCase("0x9")) {
                if (poiLabel.toUpperCase().contains("BAIRRO")) {
                    retorno.append(FormataOsmTag.retornaTag("place=suburb"));
                } else if (poiLabel.toUpperCase().contains("ASSENTAMENTO")
                        || poiLabel.toUpperCase().contains("POVOADO")
                        || poiLabel.toUpperCase().contains("DISTRITO")) {
                    retorno.append(FormataOsmTag.retornaTag("place=hamlet"));
                } else if (poiLabel.toUpperCase().contains("VILA")) {
                    retorno.append(FormataOsmTag.retornaTag("place=village"));
                } else
                    retorno.append(FormataOsmTag.retornaTag("place=town")); //2013-12-25: era place=locality
            } else {
//                retornoGeral.append(FormataOsmTag.retornaTag("place=city"));
                retorno.append(FormataOsmTag.retornaTag("place=town"));
            }
            retorno.append(continentePaisRegiaoEstadoCidadeTags());
            return retorno;

        // ATTENTION: whenever a POI has "sauna" in its label, it is typed sauna independently of its Garmin type
        } else if (poiLabel.toUpperCase().startsWith("SAUNA")) {
            return FormataOsmTag.retornaTag("amenity=sauna");
        } else if (poiLabel.toUpperCase().contains("CERVEJARIA")) {
            return FormataOsmTag.retornaTag("craft=brewery");
        } else if (poiLabel.toUpperCase().contains("BARBEARIA") ||
                   poiLabel.toUpperCase().contains("CABELEIREIR") ||
                   poiLabel.toUpperCase().startsWith("SALÃO") || poiLabel.toUpperCase().startsWith("SALAO") ||
                   poiLabel.toUpperCase().contains("COIFFEUR") ) {
            return FormataOsmTag.retornaTag("shop=hairdresser");
        } else if (poiLabel.toUpperCase().contains("LAVANDERIA")) {
            return FormataOsmTag.retornaTag("shop=laundry");
        } else if (poiLabel.toUpperCase().contains("PET SHOP") ||
                   poiLabel.toUpperCase().contains("PETSHOP")) {
            return FormataOsmTag.retornaTag("shop=pet");
        } else if (poiLabel.toUpperCase().contains("CHAVEIRO") ) {
            return FormataOsmTag.retornaTag("shop=locksmith");
        } else if (poiLabel.toUpperCase().contains("SEX SHOP") ) {
            return FormataOsmTag.retornaTag("shop=erotic");
        } else if (poiLabel.toUpperCase().contains("ÓTICA") ||
                   poiLabel.toUpperCase().contains("OTICA") ||
                   poiLabel.toUpperCase().contains("ÓPTICA") ||
                   poiLabel.toUpperCase().contains("OPTICA") ||
                   poiLabel.toUpperCase().contains("ÓCULOS") ||
                   poiLabel.toUpperCase().contains("OCULOS") ) {
            return FormataOsmTag.retornaTag("shop=erotic");
        } else if (poiType.equalsIgnoreCase("0x2C05")) {
            //se for universidade
            if (poiLabel.toUpperCase().contains("UNIVERSIDADE")
                    || poiLabel.toUpperCase().contains("FACULDADE")
                    || poiLabel.toUpperCase().contains("SUPERIOR")) {
                return FormataOsmTag.retornaTag("amenity=university");
            } else if (poiLabel.toUpperCase().contains("JARDIM")
                    || poiLabel.toUpperCase().contains("INFANTIL")
                    || poiLabel.toUpperCase().contains("CRECHE")) {
                return FormataOsmTag.retornaTag("amenity=kindergarten");
            } else if (poiLabel.toUpperCase().contains("CFC")
                    || poiLabel.toUpperCase().contains("AUTO ESCOLA")
                    || poiLabel.toUpperCase().contains("AUTOESCOLA")
                    || poiLabel.toUpperCase().contains("AUTO-ESCOLA")) {
                return FormataOsmTag.retornaTag("amenity=driving_school");
            } else if (poiLabel.toUpperCase().contains("ESCOLA TÉCNICA")
                    || poiLabel.toUpperCase().contains("ESCOLA TECNICA")) {
                return FormataOsmTag.retornaTag("amenity=college");
            } else {
                return FormataOsmTag.retornaTag("amenity=school");
            }
        } else if (poiType.equalsIgnoreCase("0x2d02")) {
            //se for boate 
            if (poiLabel.toUpperCase().contains("BOATE") || poiLabel.toUpperCase().contains("BOITE")) {
                return FormataOsmTag.retornaTag("amenity=nightclub");
            } else {
                return FormataOsmTag.retornaTag("amenity=bar");
            }
        } else if (poiType.equalsIgnoreCase("0x2f08")) {
            //se é uma estação
            if (poiLabel.toUpperCase().contains("ESTAÇÃO")) {
                if (poiLabel.toUpperCase().contains("METRO") || poiLabel.toUpperCase().contains("METRÔ")
                        || poiLabel.toUpperCase().contains("FERRO") || poiLabel.toUpperCase().contains("FÉRREA")) {
                    return FormataOsmTag.retornaTag("railway=station");
                } else if (poiLabel.toUpperCase().contains("BONDE")) {
                    return FormataOsmTag.retornaTag("railway=tram_stop");
                }
                return FormataOsmTag.retornaTag("amenity=bus_station");
            }
            //se é uma rodovária ou terminal rodoviário
            else if(poiLabel.toUpperCase().startsWith("RODOVIÁRIA") ||
                    poiLabel.toUpperCase().startsWith("TERMINAL")) {
                return FormataOsmTag.retornaTag("amenity=bus_station");
            }
            //se for uma parada
            else {
                //se for de taxi
                if (poiLabel.toUpperCase().contains("TÁXI") || poiLabel.toUpperCase().contains("TAXI")) {

                    return FormataOsmTag.retornaTag("amenity=taxi");
                } else {

                    return FormataOsmTag.retornaTag("highway=bus_stop");
                    /*
                     * operator=
                     */
                }
            }

        } else if (poiType.equalsIgnoreCase("0x3000") || poiType.equalsIgnoreCase("0x3003")) {
            //se é prefeitura
            if (poiLabel.toUpperCase().contains("PREFEITURA")) {
                return FormataOsmTag.retornaTag("amenity=townhall"); //osm.c
            }// se é predio público 
            else if(poiLabel.toUpperCase().contains("EMBAIXADA")) {
                return FormataOsmTag.retornaTag("amenity=embassy"); //osm.c
            }// se é predio público
            else {
                return FormataOsmTag.retornaTag("building=civic").append(FormataOsmTag.retornaTag("office=government"));//osm.c
            }
        } else if (poiType.equalsIgnoreCase("0x3001")) {

            //se é um presidio cadeia penitenciária
            if (poiLabel.toUpperCase().contains("PRESÍDIO") || poiLabel.toUpperCase().contains("PENITENCIÁRIA")
                    || poiLabel.toUpperCase().contains("PRESIDIO") || poiLabel.toUpperCase().contains("PENITENCIARIA")
                    || poiLabel.toUpperCase().contains("CADEIA") || poiLabel.toUpperCase().contains("CDP ")
                    || poiLabel.toUpperCase().equals("CDP")) {
                return FormataOsmTag.retornaTag("amenity=prison");
            }//polícia
            else {

                return FormataOsmTag.retornaTag("amenity=police");
            }
        } else if (poiType.equalsIgnoreCase("0x3006")) { //changed from "radar0x3006" in 2013-08-07
            //TODO usando regex
            String padrao = "^.+? (\\d+)$";//encontra o padrao 20 km

            Pattern pattern = Pattern.compile(padrao);
            Matcher matcher = pattern.matcher(poiLabel);
            if (matcher.matches()) {
                return FormataOsmTag.retornaTag("highway=speed_camera").append(FormataOsmTag.retornaTag("amenity=tec_common")).append(FormataOsmTag.retornaTag("maxspeed="
                        + matcher.group(1)));//osm.c
            } else {

                return FormataOsmTag.retornaTag("highway=speed_camera").append(FormataOsmTag.retornaTag("amenity=tec_common"));//osm.c 
            }

        } else if (poiType.equalsIgnoreCase("0x6411")) {

            //se for de energia elétrica            
            if (poiLabel.toUpperCase().contains("ESTAÇÃO") || poiLabel.toUpperCase().contains("ENERGIA")) {
                return FormataOsmTag.retornaTag("power=tower");
            } else {
                return FormataOsmTag.retornaTag("man_made=tower").append(FormataOsmTag.retornaTag("tower:type=comunication"));
            }
        } else if (poiType.equalsIgnoreCase("0x6406")) {
            if (poiLabel.toUpperCase().contains("ROTATÓRIA") || poiLabel.toUpperCase().contains("ROTATORIA")) {
                return FormataOsmTag.retornaTag("highway=mini_roundabout");
            } else if (poiLabel.toUpperCase().contains("FÉRREA") || poiLabel.toUpperCase().contains("TREM")) {
                return FormataOsmTag.retornaTag("railway=level_crossing");
            }
            return FormataOsmTag.retornaTag("highway=motorway_junction");
        } else if (poiType.equalsIgnoreCase("0x5000")) {
            //se não for potável
            if (poiLabel.toUpperCase().contains("NÃO POTÁVEL")) {
                return FormataOsmTag.retornaTag("amenity=fountain").append(FormataOsmTag.retornaTag("drinkable=no"));
            } else {
                //se for mineral
                if (poiLabel.toUpperCase().contains("MINERAL")) {
                    return FormataOsmTag.retornaTag("amenity=drinking_water").append(FormataOsmTag.retornaTag("drinkable=mineral"));

                } else {
                    return FormataOsmTag.retornaTag("amenity=drinking_water").append(FormataOsmTag.retornaTag("drinkable=yes"));
                }
            }
        } else if (poiType.equalsIgnoreCase("0x6616")) {
                //TODO usando regex
                String padrao = "(\\d+)\\s*m?\\s*\\)?\\s*$";//encontra o padrao NOME (ELEVACAOm)

                Pattern pattern = Pattern.compile(padrao);
                Matcher matcher = pattern.matcher(poiLabel);
                if (matcher.find()) {
                    return FormataOsmTag.retornaTag("natural=peak") //.append(FormataOsmTag.retornaTag("ele=nao_tem_equivalente_no_navit"));
                            .append(FormataOsmTag.retornaTag("ele="
                            + matcher.group( 1 ) ) );
                } else {
                    return FormataOsmTag.retornaTag("natural=peak");
                }

        } else if (poiType.equalsIgnoreCase("0x2c04")) {
            if (poiLabel.toUpperCase().contains("MEMORIAL")) {
                return FormataOsmTag.retornaTag("historic=memorial"); //osm.c
            } else if (poiLabel.toUpperCase().contains("MONUMENTO")) {

                return FormataOsmTag.retornaTag("historic=monument");//osm.c
            } else if (poiLabel.toUpperCase().contains("RUINA")) {

                return FormataOsmTag.retornaTag("historic=ruins");//osm.c
            } else if (poiLabel.toUpperCase().contains("PEDRA")) {

                return FormataOsmTag.retornaTag("tourism=viewpoint");//osm.c
            }
            return FormataOsmTag.retornaTag("tourism=viewpoint");
        } else if (poiType.equalsIgnoreCase("0x2e0a") || poiType.equalsIgnoreCase("0x2e00")) {
            if (poiLabel.toUpperCase().contains("BEBIDA")) {
                return FormataOsmTag.retornaTag("shop=beverages");//osm.c
            } else if (poiLabel.toUpperCase().contains("ELETRIC") || poiLabel.toUpperCase().contains("CONSTRUÇÃO")
                    || poiLabel.toUpperCase().contains("DEPÓSITO") || poiLabel.toUpperCase().contains("TINTA")
                    || poiLabel.toUpperCase().contains("TELHA")) {
                return FormataOsmTag.retornaTag("shop=hardware");//osm.c
            } else if (poiLabel.toUpperCase().contains("GRÁFICA") || poiLabel.toUpperCase().contains("PAPEL")) {
                return FormataOsmTag.retornaTag("amenity=library");//osm.c
            } else if (poiLabel.toUpperCase().contains("BIKE") || poiLabel.toUpperCase().contains("BICICLETA")
                    || poiLabel.toUpperCase().contains("CICLISTA")) {
                return FormataOsmTag.retornaTag("shop=bicycle");//osm.c
            } else if (poiLabel.toUpperCase().contains("AÇOUGUE") || poiLabel.toUpperCase().contains("CARNES")) {
                return FormataOsmTag.retornaTag("shop=butcher");//osm.c
            } else if (poiLabel.toUpperCase().contains("FLORIC")) {
                return FormataOsmTag.retornaTag("shop=florist");//osm.c
            } else if (poiLabel.toUpperCase().contains("AUTO") || poiLabel.toUpperCase().contains("PNEUS") || poiLabel.toUpperCase().contains("PEÇAS")) {
                return FormataOsmTag.retornaTag("shop=car_repair");//osm.c

            } else if (poiLabel.toUpperCase().contains("CABEL")) {
                return FormataOsmTag.retornaTag("shop=hairdresser");//osm.c

            } else if (poiLabel.toUpperCase().contains("ÓTICA") || poiLabel.toUpperCase().contains("ÓCULOS")) {
                return FormataOsmTag.retornaTag("shop=optician");//osm.c

            } else if (poiLabel.toUpperCase().contains("FOTO") || poiLabel.toUpperCase().contains("REVELAÇÃO")) {
                return FormataOsmTag.retornaTag("shop=photo");
            } else if (poiLabel.toUpperCase().contains("CALÇADOS") || poiLabel.toUpperCase().contains("SAPATOS")) {
                return FormataOsmTag.retornaTag("shop=shoes"); //osm.c
            } else if (poiLabel.toUpperCase().contains("SUPERMERCADO") || poiLabel.toUpperCase().contains("MERCADO")) {
                return FormataOsmTag.retornaTag("shop=supermarket"); //osm.c

            } else if (poiLabel.toUpperCase().contains("PERFUM")) {
                return FormataOsmTag.retornaTag("shop=parfum"); //osm.c
            } else if (poiLabel.toUpperCase().contains("PEIXARIA")) {
                return FormataOsmTag.retornaTag("shop=fish"); //osm.c
            } else if (poiLabel.toUpperCase().contains("JOALHERIA") || poiLabel.toUpperCase().contains("JÓIAS")
                      || poiLabel.toUpperCase().contains("JOIAS")) {
                return FormataOsmTag.retornaTag("shop=jewelry"); //osm.c
            }
            return FormataOsmTag.retornaTag("shop=yes"); //.append(FormataOsmTag.retornaTag("*=nao_tem_equivalente_no_navit"));//osm.c
        } else if (poiType.equalsIgnoreCase("0x6412")) {
            if (poiLabel.toUpperCase().contains("ESCALA")) {
                return FormataOsmTag.retornaTag("sport=climbing"); //osm.c 
            }
            StringBuilder result = new StringBuilder(); //FormataOsmTag.retornaTag("tourism=information"); //.append(FormataOsmTag.retornaTag("*=nao_tem_equivalente_no_navit"));//osm.c
            result.append( FormataOsmTag.retornaTag( "name=Trail: " + poiLabel ) );
            result.append( FormataOsmTag.retornaTag( "tourism=yes" ) );
            result.append( FormataOsmTag.retornaTag( "trailhead=yes" ) );
            result.append( FormataOsmTag.retornaTag( "fixme=Detail trail" ) );
            return result;
        } else if (poiType.equalsIgnoreCase("0x2c08")) {
            if (poiLabel.toUpperCase().contains("BASQUETE")) {
                return FormataOsmTag.retornaTag("sport=basketball");
            } else if (poiLabel.toUpperCase().contains("BASEB")) {
                return FormataOsmTag.retornaTag("sport=baseball");
            } else if (poiLabel.toUpperCase().contains("ESTÁDIO") || poiLabel.toUpperCase().contains("CAMPO")
                    || poiLabel.toUpperCase().contains("FUTEBOL")) {
                return FormataOsmTag.retornaTag("sport=soccer");
            } else if (poiLabel.toUpperCase().contains("KART")) {
                return FormataOsmTag.retornaTag("sport=moto_sports");

            } else if (poiLabel.toUpperCase().contains("TENIS")) {
                return FormataOsmTag.retornaTag("sport=tennis");
            }
            return FormataOsmTag.retornaTag("leisure=stadium");//osm.c
        } else if (poiType.equalsIgnoreCase("0x2b01") || poiType.equalsIgnoreCase("0x2b00")) {
            if (poiLabel.toUpperCase().contains("ALBERGUE") || poiLabel.toUpperCase().contains("DORMITÓRIO")
                    || poiLabel.toUpperCase().contains("PENSÃO")  || poiLabel.toUpperCase().contains("HOSTEL")) {
                return FormataOsmTag.retornaTag("tourism=hostel");//osm.c
            }
            if (poiLabel.toUpperCase().contains("HOTEL")) {
                return FormataOsmTag.retornaTag("tourism=hotel");//osm.c
            }
            if (poiLabel.toUpperCase().contains("MOTEL")) {
                return FormataOsmTag.retornaTag("amenity=love_hotel");//osm.c
            }
            if (poiLabel.toUpperCase().contains("RESORT")) {
                return FormataOsmTag.retornaTag("tourism=theme_park");//osm.c
            }
            return FormataOsmTag.retornaTag("tourism=hotel");//osm.c
        } else if (poiType.toUpperCase().contains("0x5300")) {
            if (poiLabel.toUpperCase().contains("SKI") || poiLabel.toUpperCase().contains("ESQUI")) {
                return FormataOsmTag.retornaTag("sport=water_ski");//osm.c
            } else if (poiLabel.toUpperCase().contains("CANOA") || poiLabel.toUpperCase().contains("RAFTIN")) {
                return FormataOsmTag.retornaTag("sport=canoe").append(FormataOsmTag.retornaTag("leisure=sports_centre"));//osm.c
            } else if (poiLabel.toUpperCase().contains("SURF")) {
                return FormataOsmTag.retornaTag("sport=surfing").append(FormataOsmTag.retornaTag("leisure=sports_centre"));//osm.c
            }
            return FormataOsmTag.retornaTag("sport=water_sport");//osm.c
        } else if (poiType.equalsIgnoreCase("0x6614")) {
            if (poiLabel.toUpperCase().contains("GRUTA")) {
                return FormataOsmTag.retornaTag("natural=cave_entrance");//.append(FormataOsmTag.retornaTag("tourism=information")); //.append(FormataOsmTag.retornaTag("*=nao_tem_equivalente_no_navit")); // como nao possui no osm.c adicionado tag locality para aparecer como label
            }
            return FormataOsmTag.retornaTag("natural=stone");
                    //append(FormataOsmTag.retornaTag("tourism=information")); //.append(FormataOsmTag.retornaTag("*=nao_tem_equivalente_no_navit")); // como nao possui no osm.c adicionado tag locality para aparecer como label

        } else if (poiType.equalsIgnoreCase("0x650C")) {

            if(  DesktopApplication1View.lerArquivoMp.isRunMaptool )
                return FormataOsmTag.retornaTag("place=locality"); //Maptool does not recognize place=island
            else
                return FormataOsmTag.retornaTag("place=island");//for general purpose OSM

        } else if (poiType.equalsIgnoreCase("0x2F06")) {

            if (poiLabel.toUpperCase().contains(" ATM"))
                return FormataOsmTag.retornaTag("amenity=atm");
            else
                return FormataOsmTag.retornaTag("amenity=bank");

        } else if (poiType.equalsIgnoreCase("0x5A00")) {
            Matcher mat = regexTracksourceMilestone.matcher( poiLabel );
            if( mat.matches() ){
                StringBuilder res = FormataOsmTag.retornaTag("highway=milestone");
                res.append( FormataOsmTag.retornaTag("pk=" + mat.group(2)) );
                res.append( FormataOsmTag.retornaTag("ref=" + mat.group(1)) );
                return res;
            } else
                return FormataOsmTag.retornaTag("highway=milestone");
        } else if (poiType.equalsIgnoreCase("0x2A07")) {
            if (poiLabel.toUpperCase().contains("SORVETERIA"))
                return FormataOsmTag.retornaTag("amenity=ice_cream");
            else
                return FormataOsmTag.retornaTag("amenity=fast_food");
        } else if (poiType.equalsIgnoreCase("0x6403")) {
            if (poiLabel.toUpperCase().contains("FUNERARIA") || poiLabel.toUpperCase().contains("FUNERÁRIA"))
                return FormataOsmTag.retornaTag("shop=funeral_directors");
        } else if (poiType.equalsIgnoreCase("0x2E05")) {
            if (poiLabel.toUpperCase().contains("DROGARIA"))
                return FormataOsmTag.retornaTag("amenity=pharmacy").append(FormataOsmTag.retornaTag("dispensing=no"));//osm.c
            else if(poiLabel.toUpperCase().contains("FARMÁCIA") || poiLabel.toUpperCase().contains("FARMACIA"))
                return FormataOsmTag.retornaTag("amenity=pharmacy").append(FormataOsmTag.retornaTag("dispensing=yes"));//osm.c
        } else if (poiType.equalsIgnoreCase("0x2e06") ||
                   poiType.equalsIgnoreCase("0x2f01") ||
                   poiType.equalsIgnoreCase("0x4400")) {
            StringBuilder res = FormataOsmTag.retornaTag("amenity=fuel");
            if( poiType.equalsIgnoreCase("0x2e06") )
                res.append( FormataOsmTag.retornaTag("shop=convenience") );
            //try to found out operator from the gas station name
            for( int i = 0; i < gas_station_operators.length; i++){
                //this must be case sensitive because BR, BP, etc. may match letter pairs found in many words
                String oper = gas_station_operators[i];
                if( poiLabel.contains( oper ) )
                    res.append( FormataOsmTag.retornaTag("operator=" + oper ) );
            }
            return res;
        } 

        StringBuilder teste = FormataOsmTag.getOsmType(a_poi_points, poiType, "");
//            if (teste.toString().contains("v=\"*\"")) {
//                System.out.print(" === poi " + poiType);
//            }

        return teste;
        //return FormataOsmTag.getOsmType(a_poi_points, poiType);
    }

    public boolean isTipoAssociadoRodovia(){
        if( this.poiType.equalsIgnoreCase("0x2100") )
            return true;
        if( this.poiType.equalsIgnoreCase("0x2200") )
            return true;
        if( this.poiType.equalsIgnoreCase("0x2600") )
            return true;
        if( this.poiType.equalsIgnoreCase("0x2500") )
            return true;
        if( this.poiType.equalsIgnoreCase("0x2000") )
            return true;
        return false;
    }

    /**
     * Retorna o número formatado
     * @return 
     */
    StringBuilder getNumero() {

        if (poiHouseNumber.length() > 0) {
            String prefixo = "";
            if( this.isTipoAssociadoRodovia() )
                prefixo = "Km ";
            return new StringBuilder(FormataOsmTag.retornaTag("addr:housenumber=" + prefixo + poiHouseNumber));
        }
        return new StringBuilder();
    }

    /**
     * Retorna o telefone formatado
     * @return 
     */
    StringBuilder getFone() {
        if (fone.length() > 0) {
            return new StringBuilder(FormataOsmTag.retornaTag("phone=" + fone));
        }
        return new StringBuilder();
    }

    StringBuilder getFax() {
        if (fax.length() > 0) {
            return new StringBuilder(FormataOsmTag.retornaTag("fax=" + fax));
        }
        return new StringBuilder();
    }

    StringBuilder getEmail() {
        if (email.length() > 0) {
            return new StringBuilder(FormataOsmTag.retornaTag("addr:email=" + email));
        }
        return new StringBuilder();
    }

    /**
     * Retorna o nome da via formatada
     * @return 
     */
    StringBuilder getStreetDesc() {
        if (streetDesc.length() > 0) {
            return new StringBuilder(FormataOsmTag.retornaTag("addr:street=" + streetDesc));
        }
        return new StringBuilder();
    }

    StringBuilder getCep() {
        if (cep.length() > 0) {
            StringBuilder res = new StringBuilder();
            if( LerMP.boolIncludeInfoForCompilers )
                res.append(FormataOsmTag.retornaTag("postal_code=" + cep));
            return res.append(FormataOsmTag.retornaTag("addr:place=" + cep));
        }
        return new StringBuilder();
    }

    public void reinicia_poi_points(){
        POI.a_poi_points.clear();
        this.inicia_poi_points();
    }

    private void inicia_poi_points() {
        /* para alertas 
         * navitel 102 lombadas subistituir ,102,0, por ,102,20,
         * navitel 106 outros avisos, sub ,106,0 por ,106,40,   
         */

        a_poi_points.add("0x1b02::man_made=tower");//osm.c
        a_poi_points.add("0x1A02::0x1902::0x1802::0x1702::0x1602::man_made=beacon");//osm.c
        a_poi_points.add("0x1b01::0x1b0a::0x1b07::0x1b0f::0x1b10::0x1b11::0x1b12::0x1b13::0x1b14::0x1b15::0x1b16::");// não possui no osm então adicionado como informaçao
        a_poi_points.add("0x1c05::0x1c06::0x1c07::0x1c09::0x1c0a::military=danger_area"); //osm.c
        a_poi_points.add("0x5700::traffic_calming=hump");

        a_poi_points.add("0x2000::highway=motorway_junction"); //osm.c

        a_poi_points.add("0x2400::man_made=weighbridge"); //osm.c

        a_poi_points.add("0x2500::highway=toll_booth"); //osm.c

        a_poi_points.add("0x2600::"); //osm.c

        a_poi_points.add("0x2800::natural=peak,tourism=attraction"); // a implementacao natural=peak do osm não vale para o navit por isso adaptado para tourism=attraction

        a_poi_points.add("0x2a00::amenity=restaurant"); //osm.c
        a_poi_points.add("0x2a01::amenity=restaurant,cuisine=american");//osm.c
        a_poi_points.add("0x2a02::amenity=restaurant,cuisine=asian");//osm.c
        a_poi_points.add("0x2a03::amenity=restaurant,cuisine=barbecue");//osm.c
        a_poi_points.add("0x2a04::amenity=restaurant,cuisine=chinese");//osm.c
        a_poi_points.add("0x2a05::shop=bakery");//osm.c
        a_poi_points.add("0x2a06::amenity=restaurant,cuisine=international");//osm.c
        a_poi_points.add("0x2a07::amenity=fast_food"); // osm.c 		
        a_poi_points.add("0x2a08::amenity=restaurant,cuisine=italian");//osm.c
        a_poi_points.add("0x2a09::amenity=restaurant,cuisine=mexican");//osm.c
        a_poi_points.add("0x2a0a::amenity=restaurant,cuisine=pizza");//osm.c
        a_poi_points.add("0x2a0b::amenity=restaurant,cuisine=seafood");//osm.c
        a_poi_points.add("0x2a0c::amenity=restaurant,cuisine=steak_grill");//osm.c
        a_poi_points.add("0x2a0d::amenity=restaurant,cuisine=bagel_donut");//osm.c
        a_poi_points.add("0x2a0e::amenity=cafe");//osm.c
        a_poi_points.add("0x2a0f::amenity=restaurant,cuisine=french");//osm.c
        a_poi_points.add("0x2a10::amenity=restaurant,cuisine=german");//osm.c
        a_poi_points.add("0x2a11::amenity=restaurant,cuisine=british");//osm.c

        a_poi_points.add("0x2b03::tourism=camp_site"); //osm.c
        a_poi_points.add("0x2c01::leisure=playground"); //osm.c
        a_poi_points.add("0x2c06::leisure=park"); //osm.c
        a_poi_points.add("0x2c02::tourism=museum"); //osm.c
        a_poi_points.add("0x2c03::amenity=library"); //osm.c
        //a_poi_points.add("0x2c04::historic=boundary_stone"); // tratado antes pois existe mais tipos

        a_poi_points.add("0x2c07::tourism=zoo"); //osm.c

        a_poi_points.add("0x2c09::amenity=arts_centre");// nao implementado no navit para o padrao osm entao adaptado para information

        a_poi_points.add("0x2c0b::amenity=place_of_worship"); //osm.c

        a_poi_points.add("0x2c0d::amenity=place_of_worship,religion=muslim"); //osm.c
        a_poi_points.add("0x2c0e::amenity=place_of_worship,religion=christian"); //osm.c
        a_poi_points.add("0x2c0f::amenity=place_of_worship"); //osm.c

        a_poi_points.add("0x2d01::amenity=theatre"); //osm.c 
        //a_poi_points.add("0x2d02:: "); // tratado antes pois possui variações
        a_poi_points.add("0x2d03::amenity=cinema");// osm.c

        a_poi_points.add("0x2d05::sport=golf"); //osm.c

        a_poi_points.add("0x2d07::sport=10pin"); //osm.c

        a_poi_points.add("0x2d09::leisure=sports_centre");//osm.c

        a_poi_points.add("0x2d0a::leisure=sports_centre"); //osm.c

        a_poi_points.add("0x2e02::shop=convenience");//osm.c
        a_poi_points.add("0x2e01::shop=department_store");//osm.c
        a_poi_points.add("0x2e03::shop=supermarket"); //osm.c
        a_poi_points.add("0x2e04::shop=mall"); //osm.c
        a_poi_points.add("0x2e05::amenity=pharmacy"); // osm.c 
        a_poi_points.add("0x2e06::amenity=fuel,shop=convenience"); //osm.c
        a_poi_points.add("0x2e07::shop=clothes");//osm.c
        a_poi_points.add("0x2e08::shop=garden_centre"); //osm.c
        a_poi_points.add("0x2e09::shop=furniture");//osm.c

        a_poi_points.add("0x2e0b::shop=computer");//osm.c

        a_poi_points.add("0x2f01::0x4400::amenity=fuel"); //osm.c
        a_poi_points.add("0x2f02::amenity=car_rental"); //osm.c
        a_poi_points.add("0x2f03::shop=car_repair"); //osm.c
        a_poi_points.add("0x2100::highway=services"); //osm.c
        a_poi_points.add("0x2f04::aeroway=aerodrome"); //osm.c
        a_poi_points.add("0x2f05::0x640f::amenity=post_office"); //osm.c
        a_poi_points.add("0x2f06::amenity=bank"); //osm.c
        a_poi_points.add("0x2f07::shop=car");//osm.c

        a_poi_points.add("0x2f09::leisure=marina"); //osm.c

        a_poi_points.add("0x2f0b::0x4d00::amenity=parking"); // osm.c 

        a_poi_points.add("0x2f0e::amenity=car_wash"); //osm.c
        a_poi_points.add("0x2f12::internet_access=wlan"); //osm.c


        a_poi_points.add("0x3002::0x6408::amenity=hospital"); //osm.c
        a_poi_points.add("0x3004::amenity=courthouse"); // osm.c
        a_poi_points.add("0x3005::amenity=community_centre");// nao implementado na conversao do osm no navit entao setado para information

        a_poi_points.add("0x3008::amenity=fire_station");//osm.c  

        a_poi_points.add("0x4a00::tourism=picnic_site");// osm.c 
        a_poi_points.add("0x4100::leisure=fishing");// osm.c 

        a_poi_points.add("0x4200::historic=wreck");// nao implementado na conversao do osm no navit entao setado para information

        a_poi_points.add("0x4700::leisure=slipway,amenity=ferry_terminal");// na realidade deveria ser um amenity mas como ainda não é implementado no navit fica como leisure

        a_poi_points.add("0x4800::tourism=camp_site"); //osm.c

        a_poi_points.add("0x4a00::tourism=picnic_site"); //osm.c

        a_poi_points.add("0x4c00::tourism=information");//osm.c  ESSE é tourism=information mesmo

        a_poi_points.add("0x2200::0x4e00::amenity=toilets"); //osm.c

        a_poi_points.add("0x5100::amenity=telephone"); //osm.c

        a_poi_points.add("0x5200::tourism=viewpoint"); //osm.c

        a_poi_points.add("0x5400::sport=swimming");//osm.c

        a_poi_points.add("0x5904::aeroway=helipad");//osm.c

        a_poi_points.add("0x5a00::historic=boundary_stone");//osm.c

        a_poi_points.add("0x5c00::sport=scuba_diving"); // osm.c nao implemntado modificado para swmming

        //a_poi_points.add("0x6401::bridge=yes,tourism=information,*=nao_tem_equivalente_no_navit,"
        a_poi_points.add("0x6401::bridge=yes"
                + (LerMP.boolCreateStarTagsForUnrecognizesPFMAttributes ? ",*=usado_em_vias_nao_pois" : "") ); // como nao possui no osm.c adicionado tag information para aparecer como label
        //a_poi_points.add("0x6402::amenity=building,building=yes,tourism=information,*=nao_tem_equivalente_no_navit,"
        //several types of building: . http://wiki.openstreetmap.org/wiki/Key:building
        a_poi_points.add("0x6402::amenity=building,building=yes"
                + (LerMP.boolCreateStarTagsForUnrecognizesPFMAttributes ? ",*=usado_em_vias_nao_pois" : "") ); // como nao possui no osm.c adicionado tag information para aparecer como label
        a_poi_points.add("0x6403::landuse=cemetery"); // osm.c

//        a_poi_points.add("0x6406::highway=crossing"); // tratado anterior
        //a_poi_points.add("0x6407::waterway=dam,tourism=information,*=nao_tem_equivalente_no_navit"); // como nao possui no osm.c adicionado tag locality para aparecer como label
        a_poi_points.add("0x6407::waterway=dam"); // como nao possui no osm.c adicionado tag locality para aparecer como label

        a_poi_points.add("0x6415::place=locality");//nao implemtnado na conversao do osm para navit entao adaptado paa locality

        a_poi_points.add("0x640b::military=range");//osm.c
        a_poi_points.add("0x640c::man_made=adit");// não implementado no osm adaptado para information

        a_poi_points.add("0x640d::man_made=pumping_rig");// não implementado no osm adaptado para information

        a_poi_points.add("0x6413::fixme=create tunnel way if not already done");// não é usado em pois e sim nas vias

        a_poi_points.add("0x6508::waterway=waterfall,tourism=attraction"); // não possui equivalente no navit usado como tourism=attraction

        a_poi_points.add("0x650b::leisure=marina,harbor=yes"); //osm.c

        a_poi_points.add("0x650c::place=island,place=locality"); // não possui equivalente na conversao do maptool de osm para o navit, usado como place=locality

        a_poi_points.add("0x650d::natural=water,tourism=attraction"); // não possui equivalente no navit usado como tourism=attraction

        a_poi_points.add("0x6602::barrier=lift_gate");//osm.c

        a_poi_points.add("0x6604::natural=beach" + ( LerMP.boolCreateStarTagsForUnrecognizesPFMAttributes ? ",*=usado_em_via_nao_em_pois" : "" ) ); // como nao possui no osm.c adicionado tag locality para aparecer como label

        a_poi_points.add("0x660a::natural=wood"); // osm.c
        //a_poi_points.add("0x6605::natural=bench"); // nao osm.c

    }

    public String getAlerta() {
        String tipodeAlerta = getTipoDeAlerta();
        if (!tipodeAlerta.equalsIgnoreCase("0")) {
            return LerMP.alertaId++ + getLongLat() + tipodeAlerta + getVelocidadeAlerta()
                    + "," + "0,0 " + poiLabel + "\n";
        }

        return "";

    }

    String getVelocidadeAlerta() {
        if(poiLabel.toUpperCase().contains("LOMBADA")){
            return ",20";
        }
        String padrao = "\\d.*";//encontra o padrao 20 km
        Pattern pattern = Pattern.compile(padrao);
        Matcher matcher = pattern.matcher(poiLabel);
        if (matcher.find()) {
            return ","+matcher.group().replaceAll("\\D.*", "");//osm.c 
        }
        return ",0";
    }

    String getLongLat() {
        return ","+no.getLongLat();
    }

    String getTipoDeAlerta() {

        //se contem atenção, pedágio, perigo e contem pedágio,ponte e lombada
        if ((poiLabel.toUpperCase().contains("ATENÇ") || poiLabel.toUpperCase().contains("FÉRREA")
                || poiLabel.toUpperCase().contains("TREM")
                || poiLabel.toUpperCase().contains("PERIGO") || poiLabel.toUpperCase().contains("PEDAGIO")
                || poiLabel.toUpperCase().contains("PEDÁGIO"))
                && (poiType.contains("0x2500") || poiType.contains("0x6401")
                || poiType.contains("0x5700") || poiType.contains("0x6406"))) {
            return ",106";

        } else //alerta para lombadas
        if (poiType.contains("0x5700")) {
            return ",102";
            // se é polícia
        }
        if (poiType.contains("0x3001") || poiType.contains("0x3002")) {
            return ",106";

        } else //para radares
        if (poiType.contains("0x3006")) {
            //testa o tipo do radar
            if (poiLabel.toUpperCase().contains("SEMÁFORO") || poiLabel.toUpperCase().contains("SEMAFORO")) {
                return ",2";
            }
            if (poiLabel.toUpperCase().contains("RADAR")) {
                return ",1";
//                  
            }
            if (poiLabel.toUpperCase().contains("MOVEL") || poiLabel.toUpperCase().contains("MÓVEL")) {
                return ",16";
            }
        }


        return ",0";

    }
}
