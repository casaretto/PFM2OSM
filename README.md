# PFM to OSM Converter (pfm2osm)

## ?? Descrição

Conversor de formato **PFM (Polish Format Map)** para **OSM (OpenStreetMap)**. Este é um projeto legado que está sendo revitalizado para dar continuidade ao desenvolvimento.

O formato PFM é um formato de texto utilizado para descrever mapas vetoriais, comumente usado em dispositivos GPS Garmin. Este conversor permite transformar esses arquivos em formato OSM, compatível com ferramentas modernas de mapeamento.

## ?? Funcionalidades

- ? Conversão de arquivos `.mp` (Polish Format) para `.osm` (OpenStreetMap XML)
- ? Suporte para múltiplos tipos de elementos:
  - POIs (Points of Interest)
  - Polylines (Vias/Estradas)
  - Polygons (Áreas)
  - Restrições de tráfego
  - Cidades e Regiões
- ? Detecção automática de codificação de caracteres (CodePage)
- ? Geração de IDs positivos ou negativos para OSM
- ? Suporte para conversão de pseudo-3D
- ? Geração de arquivos de alertas (SpeedCam)
- ? Interface gráfica Swing

## ??? Estrutura do Projeto

```
tensor-pulsar/
??? src/
?   ??? mptoosm/
?       ??? DesktopApplication1.java          # Classe principal
?       ??? DesktopApplication1View.java      # Interface gráfica
?       ??? DesktopApplication1AboutBox.java  # Diálogo About
?       ??? elementosMapa/                    # Modelos de dados
?       ?   ??? Cities.java
?       ?   ??? Node.java
?       ?   ??? POI.java
?       ?   ??? Polyline.java
?       ?   ??? Polyline_area.java
?       ?   ??? Regions.java
?       ?   ??? Restricao.java
?       ?   ??? Numeracao.java
?       ??? mp/
?       ?   ??? LerMP.java                    # Parser principal PFM
?       ??? utils/
?           ??? FormataOsmTag.java
?           ??? Util.java
??? lib/                                       # Bibliotecas JAR
??? pom.xml                                    # Configuração Maven
??? README.md
```

## ?? Como Compilar

### Pré-requisitos

- **Java JDK 11** ou superior
- **Maven 3.6+**

### Compilação

```bash
# Compilar o projeto
mvn clean compile

# Criar JAR executável
mvn clean package

# O JAR será gerado em: target/pfm2osm-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## ?? Como Executar

### Executar via Maven

```bash
mvn exec:java -Dexec.mainClass="mptoosm.DesktopApplication1"
```

### Executar JAR compilado

```bash
java -jar target/pfm2osm-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## ?? Uso

1. **Abra a aplicação** - A interface gráfica será exibida
2. **Selecione o arquivo PFM** (.mp) que deseja converter
3. **Configure as opções de conversão**:
   - Converter labels para maiúsculas
   - Gerar IDs positivos
   - Incluir informações para compiladores
   - Criar tags especiais para atributos não reconhecidos
4. **Clique em Converter**
5. **O arquivo OSM** será gerado no mesmo diretório do arquivo de entrada

## ?? Formato PFM

O formato PFM (Polish Format) é um formato de texto que descreve elementos de mapa. Exemplo:

```
[IMG ID]
ID=63240001
Name=Meu Mapa
[END-IMG ID]

[POI]
Type=0x2f00
Label=Hospital Central
Data0=(12.345678,-45.678901)
[END]

[POLYLINE]
Type=0x01
Label=Rua Principal
Data0=(12.1,45.2),(12.3,45.4)
[END]
```

## ?? Relação com TSuite

Este projeto utiliza conceitos e código base do projeto **TSuite**, que contém módulos Java para leitura e escrita de formatos PFM e OSM. O TSuite está localizado em `/Users/paulo/TSuite/TSuite/src/br/org/tracksource/tsuite/conversor/`.

## ?? Problemas Conhecidos e Melhorias Futuras

- [ ] Modernizar interface gráfica (considerar JavaFX)
- [ ] Adicionar testes unitários
- [ ] Melhorar tratamento de erros
- [ ] Adicionar suporte para mais tipos de elementos PFM
- [ ] Otimizar performance para arquivos grandes
- [ ] Adicionar modo CLI (linha de comando)
- [ ] Documentar formato de tags OSM geradas

## ?? Histórico

- **Versão Original**: Projeto abandonado pelos autores originais
- **2026-02**: Revitalização do projeto com Maven e Java 11+

## ?? Autores

- **Autor Original**: Pindaro (conforme comentários no código)
- **Manutenção Atual**: Paulo

## ?? Licença

[Verificar licença original do projeto]

## ?? Contribuindo

Contribuições são bem-vindas! Este é um projeto de revitalização de código legado.

## ?? Suporte

Para questões e suporte, abra uma issue no repositório do projeto.

---

**Nota**: Este é um projeto em desenvolvimento ativo. Algumas funcionalidades podem estar incompletas ou necessitar de ajustes.
