/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.utils;

import java.util.ArrayList;
import java.util.List;
import mptoosm.mp.LerMP;

/**
 *
 * @author diecavallax
 */
public class FormataOsmTag {  
     /**
     * Retorna a a tag osm para a via passada
     * retorna 
     * < tag k=\"highway\" v=\"residential\"/ > 
     * < tag k=\"surface\" v=\"asphalt\"/ >
     * @return 
     */
    public static StringBuilder getPolygonToOsmType(String imgType) {
        List<String> garminToOsmArray = new ArrayList<String>(10);
        
        garminToOsmArray.add("0x5::amenity=parking,area=yes");
        garminToOsmArray.add("0xd::landuse=reservation,area=yes");
        garminToOsmArray.add("0x3c::0x40::0x41::natural=water,area=yes");       
        garminToOsmArray.add("0x48::0x49::waterway=riverbank,area=yes");
        garminToOsmArray.add("0x4c::waterway=intermitent,area=yes");
        garminToOsmArray.add("0x51::natural=marsh,area=yes");
        
        return getOsmType(garminToOsmArray, imgType, "");

    }
  
    public static StringBuilder getOsmType(List<String> tipos, String imgType, String label) {
        for (String a : tipos) {
            
            if (a.contains(imgType.toLowerCase())) {
                String tag[] = a.split("::");

                String garmin_type = tag[0].split(",")[0];

                ////////SPECIAL CASE FOR THE TWO UNPAVED ROAD TYPES//////////
                //type 0xa is used for both
                //main and secondary unpave roads
                //distinction is made by the first flag of RouteParam PFM attribute
                if( garmin_type.equals("0xa") && tag[1].toLowerCase().contains("highway") ){
                    //get the first route parameter flag for the given type
                    String route_param_0 = tag[0].split(",")[1];
                    //if the first route parameter of the type does not match
                    //the one found in PFM
                    if( !LerMP.polyline.getRouteParam().startsWith( route_param_0 )  )
                        continue; //abort loop for the next type
                }
                //////////////////////////////////////////////////////////////

                ////////SPECIAL CASE FOR THE THREE ROAD TYPES USING GARMIN TYPE 0x16//////////
                //distinction is made by the first and seventh flags of RouteParam PFM attribute
                if( garmin_type.equals("0x16") && tag[1].toLowerCase().contains("highway") ){
                    //get the first route parameter flag for the given type
                    String route_param_0 = tag[0].split(",")[1];
                    //get the seventh route parameter flag for the given type
                    String route_param_6 = tag[0].split(",")[2];
                    //get the label flag for the given type
                    String label_flag = tag[0].split(",")[3];
                    //get the first route parameter flag as read from PFM
                    String route_param_read_0 = LerMP.polyline.getRouteParam().split(",")[0];
                    //get the seventh route parameter flag as read from PFM
                    String route_param_read_6 = LerMP.polyline.getRouteParam().split(",")[6];
                    if( ! ( route_param_0.equals(route_param_read_0) && route_param_6.equals(route_param_read_6) ) )
                        continue; //abort loop for the next type
                    else if( label_flag.equals("p") ){ //if route params match, but the label flag doesn't
                        if( ! ( label.toUpperCase().startsWith("TRILHA") ||
                            label.toUpperCase().contains("TREKKING") ) )
                                continue; //abort loop for the next type
                    } else if (label_flag.equals("e")) { //if route params match, but the label flag doesn't
                        if( ! ( label.toUpperCase().startsWith("ESCADA") ||
                            label.toUpperCase().startsWith("ESCADARIA") ||
                            label.toUpperCase().contains("STEPS") ||
                            label.toUpperCase().contains("STAIRCASE") ||
                            label.toUpperCase().contains("LADDER") ) )
                                continue; //abort loop for the next type
                    }
                }
                //////////////////////////////////////////////////////////////

                ////////SPECIAL CASE FOR THE TWO ROAD TYPES USING GARMIN TYPE 0x1//////////
                //distinction is made by the second flag of RouteParam PFM attribute (road class)
                if( garmin_type.equals("0x1") && tag[1].toLowerCase().contains("highway") ){
                    //get the road class route parameter flag for the given type
                    String route_param_0 = tag[0].split(",")[1];
                    //get the second route parameter flag as read from PFM
                    String route_param_read_1 = LerMP.polyline.getRouteParam().split(",")[1];
                    if( ! route_param_0.equals(route_param_read_1) )
                        continue; //abort loop for the next type
                }
                //////////////////////////////////////////////////////////////

                ////////SPECIAL CASE FOR WAY LINKS, RAMPS AND ROUNDABOUTS. GARMIN TYPES 0x8, 0x9 and 0xC//////////
                //distinction is made by the second flag of RouteParam PFM attribute (road class)
                if( ( garmin_type.equals("0x8") || garmin_type.equals("0x9") || garmin_type.equals("0xc") )
                        && tag[1].toLowerCase().contains("highway") ){
                    //get the road class route parameter flag for the given type
                    String route_param_0 = tag[0].split(",")[1];
                    //get the second route parameter flag as read from PFM
                    String route_param_read_1 = LerMP.polyline.getRouteParam().split(",")[1];
                    if( ! route_param_0.equals(route_param_read_1) )
                        continue; //abort loop for the next type
                }
                ///////////////////////////////////////////////////////////////////////////


                    //se o existe mais de um tipo de tag para a via
                    //sempre pega tag.length-1(ultimo elemento do arry) que é a definicao osm
                    if (tag[tag.length-1].contains(",")) {
                        String tags[] = tag[tag.length-1].split(",");
                        return retornaTag(tags);
                    } else {
                        return retornaTag(tag[tag.length-1]);
                    }
                
            }
        }

        if( LerMP.boolCreateStarTagsForUnrecognizesPFMAttributes )
            return new StringBuilder(retornaTag("*=*")); // retorna ponto desconhecido

        return new StringBuilder();

    }

    /**
     * Retorna todas as tags osm para os pares de string contidas no array
     * @param tags
     * @return 
     */ 
    public static StringBuilder retornaTag(String[] tags) {
        StringBuilder retorno = new StringBuilder();
        for (String a : tags) {
            retorno.append(retornaTag(a));
        }
        return retorno;
    }
  /**
     * Retorna uma tag osm para o par de string passada, recebe higway=residential
     * retorna \ tag k= highway v=residential 
     * @param tags
     * @return 
     */
    public static StringBuilder retornaTag(String tags) {
        StringBuilder retorno = new StringBuilder();
        if( ! tags.contains("=") )
            return retorno;
        String[] tag_v = tags.split("=");
        if(tag_v.length>1){
            retorno.append("\n  <tag k=\"").append(tag_v[0]).append("\" v=\"").append(tag_v[1]).append("\"/>");
        } else { //tag value missing: give zero-length string as default
            retorno.append("\n  <tag k=\"").append(tag_v[0]).append("\" v=\"").append(" ").append("\"/>");
        }
        return retorno;
    }
}
