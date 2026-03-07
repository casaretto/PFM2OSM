# Guia de Desenvolvimento - PFM2OSM

## ??? Arquitetura do Projeto

### Estrutura de Pacotes

```
mptoosm/
??? DesktopApplication1.java          # Aplicação principal
??? DesktopApplication1View.java      # View (GUI Swing)
??? DesktopApplication1AboutBox.java  # Diálogo About
??? elementosMapa/                    # Modelos de dados do mapa
?   ??? Cities.java                   # Cidades
?   ??? Node.java                     # Nós (coordenadas)
?   ??? Numeracao.java                # Numeração de endereços
?   ??? Numeracoes.java               # Coleção de numerações
?   ??? POI.java                      # Points of Interest
?   ??? Polyline.java                 # Linhas (ruas, estradas)
?   ??? Polyline_area.java            # Polígonos (áreas)
?   ??? Regions.java                  # Regiões/Estados
?   ??? Restricao.java                # Restrições de tráfego
??? mp/
?   ??? LerMP.java                    # Parser principal PFM
??? utils/
    ??? FormataOsmTag.java            # Formatação de tags OSM
    ??? Util.java                     # Utilitários gerais
```

### Fluxo de Conversão

```
???????????????
? Arquivo PFM ?
?   (.mp)     ?
???????????????
       ?
       ?
???????????????????????
?   LerMP.java        ?
?  (Parser Principal) ?
?                     ?
? 1. Detecta CodePage ?
? 2. Lê seções PFM   ?
? 3. Parse elementos  ?
???????????????????????
       ?
       ?
???????????????????????????????????
?  Elementos do Mapa              ?
?  ????????????  ????????????    ?
?  ?   POI    ?  ? Polyline ?    ?
?  ????????????  ????????????    ?
?  ????????????  ????????????    ?
?  ? Polygon  ?  ? Restrict ?    ?
?  ????????????  ????????????    ?
???????????????????????????????????
       ?
       ?
???????????????????????
?  Geração OSM XML    ?
?                     ?
? - Nodes             ?
? - Ways              ?
? - Relations         ?
???????????????????????
       ?
       ?
???????????????
? Arquivo OSM ?
?   (.osm)    ?
???????????????
```

## ?? Componentes Principais

### 1. LerMP.java - Parser PFM

**Responsabilidades**:
- Ler arquivo PFM com encoding correto
- Identificar seções do arquivo ([IMG ID], [POI], [POLYLINE], etc.)
- Parsear cada tipo de elemento
- Gerar saída OSM XML

**Métodos Importantes**:
- `iniciaLeituraMPf()`: Loop principal de leitura
- `escreveLinha(String linha)`: Processa cada linha
- `escrevePoi()`: Gera XML para POIs
- `escrevePolyline()`: Gera XML para polylines
- `escrevePolyline_area()`: Gera XML para polígonos
- `escreveRestricoes()`: Gera XML para restrições

**Seções PFM Suportadas**:
- `[IMG ID]` - Metadados do mapa
- `[Countries]` - Países
- `[Regions]` - Regiões/Estados
- `[Cities]` - Cidades
- `[ZipCodes]` - CEPs
- `[POI]` / `[RGN10]` / `[RGN20]` - Pontos de interesse
- `[POLYLINE]` / `[RGN40]` - Linhas (ruas)
- `[POLYGON]` / `[RGN80]` - Polígonos (áreas)
- `[RESTRICT]` - Restrições de tráfego

### 2. Elementos do Mapa

#### POI.java
Representa pontos de interesse (hospitais, postos, etc.)

**Atributos principais**:
- `Type`: Tipo Garmin (ex: 0x2f00)
- `Label`: Nome do POI
- `Data0`: Coordenadas

#### Polyline.java
Representa linhas (ruas, estradas, rios)

**Atributos principais**:
- `Type`: Tipo Garmin (ex: 0x01 para estrada)
- `Label`: Nome da via
- `Data`: Lista de coordenadas
- `RoadID`: ID para restrições de tráfego
- `RouteParam`: Parâmetros de roteamento

#### Polyline_area.java
Representa áreas/polígonos (parques, lagos, edifícios)

**Atributos principais**:
- `Type`: Tipo Garmin
- `Label`: Nome da área
- `Data`: Lista de coordenadas (fechada)

#### Restricao.java
Representa restrições de tráfego (proibido virar à esquerda, etc.)

**Atributos principais**:
- `Nod`: Nó onde ocorre a restrição
- `TraffRoads`: Vias envolvidas
- `TraffPoints`: Pontos de controle

### 3. Utilitários

#### FormataOsmTag.java
Formata valores para tags OSM válidas

#### Util.java
Funções auxiliares:
- Contagem de linhas
- Cálculo de porcentagem
- Conversões de formato

## ?? Desenvolvimento

### Setup do Ambiente

```bash
# Clone o projeto
cd /Users/paulo/.gemini/antigravity/playground/tensor-pulsar

# Compile
mvn clean compile

# Execute testes (quando implementados)
mvn test

# Gere JAR
mvn clean package
```

### Executar em Modo Debug

```bash
# Com Maven
mvn exec:java -Dexec.mainClass="mptoosm.DesktopApplication1"

# Ou compile e execute com debug
mvn clean package
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -jar target/pfm2osm-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Adicionar Novos Tipos de Elementos

1. **Criar classe em `elementosMapa/`**
```java
public class NovoElemento {
    private String type;
    private String label;
    // ... getters, setters, métodos
    
    public String getItensParaArquivoOSM() {
        // Gera XML OSM
    }
}
```

2. **Adicionar seção em `LerMP.java`**
```java
// No método iniciaLeituraMPf()
else if (linha.contains("[NOVO_ELEMENTO]")) {
    novoElemento.clear();
    novoElemento.setValido(true);
    isNovoElementoInicio = true;
    // ...
}
```

3. **Implementar escrita**
```java
private void escreveNovoElemento() throws IOException {
    bufferedWriteMapa.append(novoElemento.getItensParaArquivoOSM());
    bufferedWriteMapa.flush();
}
```

### Mapeamento de Tipos Garmin ? OSM

O mapeamento está implementado em cada classe de elemento. Exemplo:

**PFM**:
```
[POI]
Type=0x2f00
Label=Hospital
```

**OSM**:
```xml
<node id="1" lat="-23.5" lon="-46.6">
  <tag k="amenity" v="hospital"/>
  <tag k="name" v="Hospital"/>
</node>
```

## ?? Testes

### Estrutura de Testes (a implementar)

```
src/test/java/
??? mptoosm/
?   ??? mp/
?   ?   ??? LerMPTest.java
?   ??? elementosMapa/
?   ?   ??? POITest.java
?   ?   ??? PolylineTest.java
?   ?   ??? PolygonTest.java
?   ??? utils/
?       ??? UtilTest.java
```

### Exemplo de Teste

```java
@Test
public void testParsePOI() {
    POI poi = new POI();
    poi.adicionaLinha("Type=0x2f00");
    poi.adicionaLinha("Label=Hospital");
    poi.adicionaLinha("Data0=(-23.5,-46.6)");
    
    String osm = poi.getItensParaArquivoOSM();
    assertTrue(osm.contains("amenity"));
    assertTrue(osm.contains("hospital"));
}
```

## ?? Debugging

### Logs Úteis

O projeto usa `mensagem` e `porcentagem` para feedback:

```java
mensagem.delete(0, mensagem.length()).append("Processando POI...");
porcentagem.delete(0, porcentagem.length()).append(Util.porcentagem(linhaAtual, linhasTotal));
```

### Arquivos de Debug

- **SpeedCam.txt**: Alertas de velocidade gerados
- **Arquivos .osm**: Saída OSM para inspeção

### Problemas Comuns

1. **Encoding incorreto**: Verificar CodePage no PFM
2. **Coordenadas inválidas**: Validar formato Data0
3. **Tags OSM inválidas**: Verificar caracteres especiais

## ?? Integração com TSuite

O projeto TSuite (`/Users/paulo/TSuite/TSuite/`) contém código relacionado:

- `ConversorOsm.java`: Conversor OSM mais completo
- `ConversorGarmin.java`: Conversão para formato Garmin
- Pode servir como referência para melhorias

## ?? Recursos

### Documentação de Formatos

- **PFM Format**: [cGPSmapper Manual](http://cgpsmapper.com/buy.htm)
- **OSM XML**: [OSM Wiki - XML Format](https://wiki.openstreetmap.org/wiki/OSM_XML)
- **Garmin Types**: [Garmin Type Codes](https://wiki.openstreetmap.org/wiki/OSM_Map_On_Garmin/Type_Code_List)

### Ferramentas Úteis

- **JOSM**: Editor OSM para validar saída
- **GPSMapEdit**: Editor de mapas PFM
- **cGPSmapper**: Compilador PFM ? IMG

## ?? Próximos Passos

1. Corrigir warnings de encoding
2. Implementar testes unitários
3. Adicionar modo CLI
4. Melhorar documentação de código
5. Otimizar performance

---

**Última atualização**: 2026-02-11
