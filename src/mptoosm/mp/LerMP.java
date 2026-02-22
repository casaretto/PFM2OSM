/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mptoosm.mp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mptoosm.elementosMapa.Cities;
import mptoosm.elementosMapa.POI;
import mptoosm.elementosMapa.Polyline;
import mptoosm.elementosMapa.Polyline_area;
import mptoosm.elementosMapa.Regions;
import mptoosm.elementosMapa.Restricao;
import mptoosm.utils.Util;

/**
 *
 * @author Pindaro
 */
public class LerMP implements Runnable {

//    public static MapaBoundBox boundBoxDoMapa;
    /**
     * quando se da um unico nome ao conjunto de mais de uma mapa indica mpId_OsmId numeracao do arquivo final*/
    public static short qtArquivosCriados = 0;
    /**
     * indica se é ou não para fazer conversão de tipos para pseudo3d
     */
    public static boolean transformaPseudo3D = false;
    /**
     * inidica se e para criar arquivos de alerta
     */
    public static boolean isCriarAlertas = false;
    /**
     * indica se é ara normalizar arquivos
     */
    public static boolean normalizatxt = false;
    /**
     * indica o nome inicial unico para o conjunto de maps
     */
    static StringBuilder arquivoOSM = new StringBuilder();
    public int linhasTotal = 0, linhaAtual = 0;
    static File arquivoMP;
    static boolean isImgId = false;
    static boolean isFimImgId = false;
    static boolean isCountriesInicio = false;
    static boolean isCountriesFim = false;
    static boolean isRegionsInicio = false;
    static boolean isRegionsFim = false;
    static boolean isCitiesInicio = false;
    static boolean isCitiesFim = false;
    static boolean isZipCodesInicio = false;
    static boolean isZipCodesFim = false;
    static boolean isRestrictInicio = false;
    static boolean isRestrictFim = false;
    static boolean isPOIInicio = false;
    static boolean isPOIFim = false;
    static boolean isPolylineInicio = false;
    static boolean isPolylineFim = false;
    static boolean isPolyline_areaInicio = false;
    static boolean isPolyline_areaFim = false;
    public static StringBuilder mensagem;
    public static StringBuilder porcentagem = new StringBuilder();
    //BufferedWriter[] bufferedWriteMapaDivididosArray;
    BufferedWriter bufferedWriteMapa;
    BufferedWriter outSpeedCam;
    /**
     * guarda mpId_OsmId referencia do nó no mapa mp e referencia do nó do mapa osm e 
     * mpId_OsmId ref no mp é 34 e no osm 56 entao fica 34 :56
     */
    public static HashMap<Integer, Integer> mpId_OsmId = new HashMap<Integer, Integer>(50);
    public static POI poi = new POI(); //transformei em public static 2013-07-19
    int poiqt = 1;
    public static Polyline polyline = new Polyline(); //transformei em public static 2013-07-19
    private List<Polyline> listaDePolylineParaEscrever = new ArrayList<Polyline>(50);
    public static Polyline_area polyline_area = new Polyline_area(); //transformei em public static 2013-07-19
    private Restricao restricao = new Restricao();
    public List<Restricao> restricoes = new ArrayList<Restricao>(15);
    public static Regions estados;
    public static Cities cidades;
    static int osmElementoId = 1;
    //public static String user = " user=\"diecavallax\"";
    public static String user = " ";
    //public static String uid = " uid=\"417837\"";
    public static String uid = " ";
    //public static String changeset = " changeset=\"7814683\"";
    public static String changeset = " ";
    public static String visible = " visible=\"true\"";
    public static String version = " "; //" version=\"1\""; //absence of version attribute is only acceptable for negative IDs
    public boolean isRunMaptool = false;
    public int maisDeUmMapaConvertido = -1;
    public static int alertaId = 0;
    public boolean ultimoMapaParaProcessa;

    public static boolean boolConvertLabelsToUpperCase = false;
    public static boolean boolIncludeInfoForCompilers = false;
    public static boolean boolCreateStarTagsForUnrecognizesPFMAttributes = false;
    public static boolean boolGeneratePositiveIDs = false;
    public static String strIDsSignal = "-";
    public static boolean boolIncludeActionModify = false;
    public static String strIncludeActionModify = "";
    public static boolean boolIncludeUploadTrue = false;
    public static boolean boolLimitObjCount = false;
    public static int iLimitObjCount = 6000;

    public LerMP() {
        isImgId = false;
        isFimImgId = false;
        isCountriesInicio = false;
        isCountriesFim = false;
        isRegionsInicio = false;
        isRegionsFim = false;
        isCitiesFim = false;
        isCitiesInicio = false;
        isZipCodesFim = false;
        isZipCodesInicio = false;
        isRestrictInicio = false;
        isRestrictFim = false;
        isPOIFim = isPOIInicio = false;
        isPolylineFim = isPolylineInicio = false;
        isPolyline_areaFim = isPolyline_areaInicio = false;
        estados = new Regions();
        cidades = new Cities();
        arquivoOSM = new StringBuilder();
        arquivoMP = null;
        mensagem = new StringBuilder();
        osmElementoId = 1;
//        poi.clear();
//        polyline.clear();
//        polyline_area.clear();
//        isRunMaptool = false;
        mpId_OsmId.clear();
    }

    public static int getProximoElementoOsmId() {
        return osmElementoId++;
    }
    
    private static void setaInicioFimElementos() {


        if (isFimImgId) {
            isFimImgId = isImgId = false;
        }
        if (isCountriesFim) {
            isCountriesFim = isCountriesInicio = false;
        }
        if (isRegionsFim) {
            isRegionsFim = isRegionsInicio = false;
        }
        if (isCitiesFim) {
            isCitiesFim = isCitiesInicio = false;
        }
        if (isZipCodesFim) {
            isZipCodesFim = isZipCodesInicio = false;
        }
        if (isRestrictFim) {
            isRestrictFim = isRestrictInicio = false;
        }
        if (isPOIFim) {
            isPOIFim = isPOIInicio = false;
        }
        if (isPolylineFim) {
            isPolylineFim = isPolylineInicio = false;
        }
        if (isPolyline_areaFim) {
            isPolyline_areaFim = isPolyline_areaInicio = false;
        }
    }

    public void setArquivo(File arquivo) {
        arquivoMP = arquivo;
        arquivoOSM.append(arquivo.getPath().replace(".mp", ".osm"));
        System.out.println(arquivoOSM.toString());

    }

    void iniciaLeituraMPf() {

        try {
            mensagem.delete(0, mensagem.length()).append("Lendo propriedades do arquivo mpf");
//            linhasTotal = Util.countLines(arquivoMP.toString());
            //para nao parar o processamento durante a leitura das linhas do arquivo
            Thread thread_atualizacao = new Thread() {

                @Override
                public void run() {
                    try {
                        linhasTotal = Util.countLines(arquivoMP.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            };
            thread_atualizacao.start();

            porcentagem.delete(0, porcentagem.length()).append("0 %");
            //System.out.println(linhasTotal);

//            bufferedWriteMapaDivididosArray[0] = new BufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(
//                    arquivoOSM.toString()), "8859_1"));
            //output OSM will be encoded in UTF-8
            bufferedWriteMapa = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(
                    arquivoOSM.toString()), "UTF-8"));

            String linha = "";

            //positive IDs require the attribute version set
            if( LerMP.boolGeneratePositiveIDs )
                LerMP.version = " version=\"1\"";

            ////////////////READS PFM HEADER TO GET CHAR ENCODING/////////////
            BufferedReader leitor = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoMP), "8859_1"));
            Pattern regexCodePage = Pattern.compile("^\\s*CodePage\\s*=\\s*(.+?)\\s*$", Pattern.CASE_INSENSITIVE);
            String strCodePage = "8859_1"; //default code page is Latin-1, the most comprehensive of all code pages supported by PFM
            Matcher matCodePage = null;
            while ((linha = leitor.readLine()) != null) {
                matCodePage = regexCodePage.matcher( linha );
                if( matCodePage.matches() ){
                    CharsetEncoder encoder = null;
                    //gets the incomplete code from PFM
                    strCodePage = matCodePage.group(1);
                    //tries to find an encoder from the incomplete PFM code page id
                    try{
                        encoder = Charset.forName( "Cp" + strCodePage ).newEncoder(); //normally it is 1251 (Windows-1251), which is a.k.a ISO-8859-1 or Latin-1
                        strCodePage = encoder.charset().name();
                    } finally {
                        break; //terminates codepage even if it fails
                    }
                }
            }
            leitor.close();
            ////////////////////////////////////////////////////////////

            //opens the PFM again using the specified code page
            leitor = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoMP), strCodePage));

            bufferedWriteMapa.append(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "\n<osm version=\"0.6\" generator=\"mp2osm\"" + ( LerMP.boolIncludeUploadTrue ? " upload=\"true\"" : "" ) + ">");

            //////////////
            //      additional pass only to populate the turn restrictions list.
            //      the turn restriction processing further down is sensitive to the order with which
            //      they and the ways using them appear throughout the PFM file
            while ((linha = leitor.readLine()) != null) {
                if (linha.length() > 3 && !linha.startsWith(";")) {
                    //remove espaços duplos
                    linha = linha.replaceAll("  ", " ");
                    if (linha.toLowerCase().contains("[restrict]")) {
                        restricao.clear();
                        restricao.setValido(true);
                        isRestrictInicio = true;
                        isRestrictFim = false;
                    } else if (linha.toLowerCase().contains("[end-restrict]") || (linha.equalsIgnoreCase("[END]") & isRestrictInicio)) {
                        isRestrictFim = true;
                        restricao.setValido(false);
                        //restricao.setCompleto(true);
                    }
                    escreveLinha(linha);
                    setaInicioFimElementos();
                }
            }
            leitor.close();
            //opens the PFM yet again for the main parese loop
            leitor = new BufferedReader(new InputStreamReader(new FileInputStream(arquivoMP), strCodePage));
            //////////////

            while ((linha = leitor.readLine()) != null) {

                linhaAtual++;

                if (linha.length() > 3 && !linha.startsWith(";")) {
                    
                    //remove espaços duplos
                    linha = linha.replaceAll("  ", " ");
                    
                  
                    if (linha.contains("[IMG ID]")) {
                        isImgId = true;
                        isFimImgId = false;
                    } else if (linha.contains("END-IMG ID") || (linha.equalsIgnoreCase("[END]") & isImgId)) {
                        isFimImgId = true;
                        //linha += "\n";
                    } else if (linha.contains("[Countries]")) {
                        isCountriesInicio = true;
                        isCountriesFim = false;
                    } else if (linha.contains("[END-Countries]") || (linha.equalsIgnoreCase("[END]") & isCountriesInicio)) {
                        isCountriesFim = true;
                        linha += "\n";
                    } else if (linha.contains("[Regions]")) {
                        isRegionsInicio = true;
                        isRegionsFim = false;
                        estados.setAtivo(true);
                    } else if (linha.contains("[END-Regions]") || (linha.equalsIgnoreCase("[END]") & isRegionsInicio)) {
                        isRegionsFim = true;
                        linha += "\n";
                        estados.setAtivo(false);
                    } else if (linha.contains("[Cities]")) {
                        isCitiesInicio = true;
                        isCitiesFim = false;
                        cidades.setAtivo(true);
                    } else if (linha.contains("[END-Cities]") || (linha.equalsIgnoreCase("[END]") & isCitiesInicio)) {
                        isCitiesFim = true;
                        linha += "\n";
                        cidades.setAtivo(false);
                    } else if (linha.contains("[ZipCodes]")) {
                        isZipCodesInicio = true;
                        isZipCodesFim = false;
                    } else if (linha.contains("[END-ZipCodes]") || (linha.equalsIgnoreCase("[END]") & isZipCodesInicio)) {
                        isZipCodesFim = true;
                        linha += "\n";
                    }/* TURN RESTRICTION ACQUISITION MOVED TO ANOTHER PASS BEFORE THIS ONE
                      *
                        else if (linha.toLowerCase().contains("[restrict]")) {
                        restricao.clear();
                        restricao.setValido(true);
                        isRestrictInicio = true;
                        isRestrictFim = false;
                        mensagem.delete(0, mensagem.length()).append("Restricões - lendo...");
                        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
                    } else if (linha.toLowerCase().contains("[end-restrict]") || (linha.equalsIgnoreCase("[END]") & isRestrictInicio)) {
                        isRestrictFim = true;
                        restricao.setValido(false);
                        //restricao.setCompleto(true);
                    } */else if (linha.contains("[POI]") || linha.contains("[RGN10]")
                            || linha.contains("[RGN20]")) {
                        poi.clear();
                        poi.setValido(true);
                        isPOIInicio = true;
                        isPOIFim = false;
                        mensagem.delete(0, mensagem.length()).append("POI's - lendo...");
                        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
                    } else if ((linha.equalsIgnoreCase("[END]") || linha.contains("[END-RGN10]")
                            || linha.contains("[END-RGN20]")) & isPOIInicio) {
                        isPOIFim = true;
                        poi.setCompleto(true);
                    } else if (linha.contains("[POLYLINE]") || linha.contains("[RGN40]")) {
                        polyline.clear();
                        listaDePolylineParaEscrever.clear();
                        polyline.setValido(true);
                        isPolylineInicio = true;
                        isPolylineFim = false;
                        mensagem.delete(0, mensagem.length()).append("Vias - lendo...");
                        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
                    } else if ((linha.equalsIgnoreCase("[END]") || linha.contains("[END-RGN40]"))
                            & isPolylineInicio) {
                        isPolylineFim = true;
                        polyline.setCompleto(true);
                    } else if (linha.contains("[POLYGON]") || linha.contains("[RGN80]")) {
                        polyline_area.clear();
                        polyline_area.setValido(true);
                        isPolyline_areaInicio = true;
                        isPolyline_areaFim = false;
                        mensagem.delete(0, mensagem.length()).append("Áreas - lendo...");
                        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
                    } else if ((linha.equalsIgnoreCase("[END]") || linha.contains("[END-RGN80]"))
                            & isPolyline_areaInicio) {
                        isPolyline_areaFim = true;
                        polyline_area.setCompleto(true);
                    }


                    escreveLinha(linha);
                    setaInicioFimElementos();
                }
            }


            //escreve o mapa
//            mapaVirtual.adicionaStreetDesc = adicionaStreetDesc;
            //          mapaVirtual.geraArquivoMPFNavitel(nomeDoMapaNavitel.toString(), boundBoxDoMapa, criaArquivoDeDetalhes);
            //         linhasAdicionadas = mapaVirtual.linhasAdicionadas;
            //        linhasModificadas = mapaVirtual.linhasModificadas;
            escreveRestricoes();
            leitor.close();
            //bufferedWriteMapaDivididosArray[0].append(boundbox.getBoundBoxParaOsm());

            //fecha o arquivo de poi's
            //bufferedWriteMapaDivididosArray[1].close();
            //escreve os poi's para o arquivo final
//            mensagem.delete(0, mensagem.length()).append("Escrevendo POI_old's");
            //transcreveDados(poi_file);
            //deleta o arquivo de poi's
            //poi_file.delete();
            //bufferedWriteMapaDivididosArray[2].close();
//            bufferedWriteMapaDivididosArray[0].append("\n</osm>");
            bufferedWriteMapa.append("\n</osm>");
//            bufferedWriteMapaDivididosArray[0].close();
            bufferedWriteMapa.close();
            if( outSpeedCam != null )
                outSpeedCam.close();
            if (isRunMaptool) {
                mensagem.delete(0, mensagem.length()).append("Convertendo para bin");
                Runtime r = Runtime.getRuntime();
                Process p;
                if(maisDeUmMapaConvertido>=0){
                p = r.exec("maptool -i " + arquivoOSM
                        + " " + arquivoMP.getParent() + "/map"+maisDeUmMapaConvertido+".bin " //                        + "&& cp " + arquivoMP.getParent() + "/map.bin"
                        //                        + " /home/diecavallax/.navit/map.bin" 
                        //                        + " && navit "
                        );
                }else{
                p = r.exec("maptool -i " + arquivoOSM
                        + " " + arquivoMP.getParent() + "/map.bin " //                        + "&& cp " + arquivoMP.getParent() + "/map.bin"
                        //                        + " /home/diecavallax/.navit/map.bin" 
                        //                        + " && navit "
                        );
                }
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                // read the output from the command
                String s = null;
//                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

//                System.out.println("Here is the standard output of the command:\n");
//                while ((s = stdInput.readLine()) != null) {
//                    System.out.println(s);
//                }

                // read any errors from the attempted command

                while ((s = stdError.readLine()) != null) {
//                    System.out.println(s);
                    mensagem.delete(0, mensagem.length()).append("maptool: ").append(s);
                }

                try {
                    p.waitFor();
                    mensagem.delete(0, mensagem.length()).append("Convertido... copiando");
                } catch (InterruptedException ex) {
                    System.out.println(ex.toString());
                }
                
                if(maisDeUmMapaConvertido>=0){
                p = r.exec(" cp " + arquivoMP.getParent() + 
                        "/map"+maisDeUmMapaConvertido+".bin"+" /home/dcx/navit-build/navit/map"+maisDeUmMapaConvertido+".bin");
                }else{
                p = r.exec(" cp " + arquivoMP.getParent() + "/map.bin /home/dcx/navit-build/navit/map.bin");}
                p = r.exec(" cp " + arquivoOSM.toString() + ".SpeedCam.txt /home/dcx/navit-build/navit/SpeedCam.txt");
                try {
                    p.waitFor();
                    mensagem.delete(0, mensagem.length()).append("Rodando navit");
                } catch (InterruptedException ex) {
                    System.out.println(ex.toString());
                }
                if(ultimoMapaParaProcessa){
                p = r.exec(".//home/dcx/navit-build/navit/navit ");}
                stdError.close();
//                stdInput.close();             

            }

        } catch (Exception ex) {
            Logger.getLogger(LerMP.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("LerMP-erro-\nlinha" + linhaAtual + "\n/");
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        if (arquivoMP.isFile()) {
            iniciaArquivoDeAlertas();
            iniciaLeituraMPf();
            mensagem.delete(0, mensagem.length()).append("Leitura finalizada");
        } else {
            System.out.println(arquivoMP + " não é um arquivo válido");
        }
    }

    /**
     * escreve mpId_OsmId linha para o mapa dividido
     * @param linha mpId_OsmId ser escriva
     * @param id do elemento do mapa 0- img, 1-countries, 2-regions, 3-cities, 4-zipcodes,
     * 6-restrict, 7-poi, 8- polyline, 9- polygons
     * 
     */
    void escreveLinha(String linha) {

        try {
            if (estados.isAtivo()) {
                estados.adicionaLinha(linha);
            } else if (cidades.isAtivo()) {
                cidades.adicionaLinha(linha);
            } else if (restricao.isValido()) {
                restricao.adicionaLinha(linha);
                if (restricao.isCompleto()) {
                    adicionaRestricao();
                    restricao.clear();
                    restricao.setValido(false);
                }
            } else if (poi.isValido()) {
                poi.adicionaLinha(linha);
                if (poi.isCompleto()) {
                    escrevePoi();
                    if (isCriarAlertas) {
                        escreveAlerta();
                    }
                    poi.clear();
                    poi.setValido(false);
                }
            } else if (polyline.isValido()) {
                polyline.adicionaLinha(linha);
                if (polyline.isCompleto()) {
                    verificaRestricao();
                    escrevePolyline();
                    polyline.setValido(false);
                }
            } else if (polyline_area.isValido()) {
                polyline_area.adicionaLinha(linha);
                if (polyline_area.isCompleto()) {
                    escrevePolyline_area();
                    polyline_area.setValido(false);
                }
            }

        } catch (IOException io) {
            mensagem.delete(0, mensagem.length()).append("Erro: ").append(io.toString());
            porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
            System.out.println(io.getMessage());
        }
    }

    private void escrevePoi() throws IOException {
        mensagem.delete(0, mensagem.length()).append("Escrevendo POI ").append(poiqt);
        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));

        bufferedWriteMapa.append(poi.getItensParaArquivoOSM());
        bufferedWriteMapa.flush();
        poiqt++;

    }

    private void escrevePolyline() throws IOException {
        mensagem.delete(0, mensagem.length()).append("Vias - escrevendo...").append(polyline.polylineLabel);
        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));

        if(listaDePolylineParaEscrever.isEmpty()){
            bufferedWriteMapa.append(polyline.getItensParaArquivoOSM());
            
        }else{
            for(Polyline p:listaDePolylineParaEscrever){
                bufferedWriteMapa.append(p.getItensParaArquivoOSM());    
//                polyline.getListaNos().removeAll(p.getListaNos());
            }}
            
        
        bufferedWriteMapa.flush();
        polyline.clear();
        listaDePolylineParaEscrever.clear();
    }

    private void escrevePolyline_area() throws IOException {
        mensagem.delete(0, mensagem.length()).append("Àreas - escrevendo...").append(polyline_area.polyline_areaLabel);
        porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));

        bufferedWriteMapa.append(polyline_area.getItensParaArquivoOSM());
        bufferedWriteMapa.flush();
        polyline_area.clear();
    }

    private void escreveRestricoes() throws IOException {
        mensagem.delete(0, mensagem.length()).append("Restricoes - escrevendo...");
        short i=0;
        for (Restricao rs : restricoes) {
            porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(++i, restricoes.size()));
            bufferedWriteMapa.append(rs.getItensParaArquivoOSM());
            bufferedWriteMapa.flush();
        }
    }

    private void escreveAlerta() {
        try {
            if (poi.isPodeGerarAlerta()) {

                //            outSpeedCam.append("ID,X,Y,TYPE,SPEED,DirType,Direction\n");
                outSpeedCam.append(poi.getAlerta());
                outSpeedCam.flush();
            }
            //outSpeedCam.close();
        } catch (IOException ex) {
            Logger.getLogger(LerMP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void iniciaArquivoDeAlertas() {
        try {
            if (isCriarAlertas) {
                if (new File(arquivoOSM.toString() + ".SpeedCam.txt").exists()) {
                    new File(arquivoOSM.toString() + ".SpeedCam.txt").delete();
                }
                outSpeedCam = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(
                        new File(arquivoOSM.toString() + ".SpeedCam.txt"), true), "Windows-1252"));
                //cria cabeçalho do speedcam
                //outSpeedCam.append("ID,X,Y,TYPE,SPEED,DirType,Direction\n");
            }
            //outSpeedCam.close();
        } catch (IOException ex) {
            Logger.getLogger(LerMP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void adicionaRestricao() {
        restricoes.add(new Restricao(restricao));
    }

    private void verificaRestricao() {
        for (Restricao res : restricoes) {
             //TODO adicionar verificacao de traffroadas das restrica com as road id da polylines
            // este metodo falha porque nao necessariamente as restricoes estarao declaradas no PFM
            // ANTES das vias que as usam.  Se todas as restricoes estiverem declaradas depois de todas
            // as vias roteaveis, este metodo nunca farah o que eh necessario, pois a lista de restricoes
            // estarah vazia e este bloco de codigo nunca serah chamado
            // seria necessario um passo adicional para carregar todas as restricoes antes de processar todo o PFM
            if(!res.isReferenciasCompletas() &&
                    res.isViaPertenceARestricao(polyline.getPolylineRoadID())){
                mensagem.delete(0, mensagem.length()).append("Verif. Restricao em ")
                        .append(polyline.polylineLabel)
                        .append("(")
                .append(polyline.getPolylineRoadID()).append(") ")
                        .append(restricoes.indexOf(res))
                        .append(" de ").append(restricoes.size());
                //res.polylineVerificaRestricao(polyline,listaDePolylineParaEscrever);
                res.polylineVerificaRestricao(polyline);
                mensagem.delete(0, mensagem.length());            
        }}
    }
}
