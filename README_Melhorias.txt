O projeto PFM2OSM atual foi modificado de fontes obtidas na internet, de software aberto desenvolvido por Paulo Carvalho e outros.

Foram feitas as seguintes melhorias.

1-Atualizado código para compilar com JDK 21. Era JDK 11
2-Preparado ambiente e código para compilar com Maven
3-Corrigido problema de caracteres especiais no XML
- Added escapeXmlSpecialCharacters() method to properly escape XML special characters
- Escapes: & to &amp;, < to &lt;, > to &gt;, " to &quot;, ' to &apos;
- Applies escaping to all tag values in retornaTag() method
- Fixes issue where '&' character in POI names (e.g. 'DECO ABRUSCI TOLDOS & PERSINAS')
  caused Mkgmap compilation errors
- Compliance with XML specification for attribute values
4-Garantido IDs OSM positivos, inclusão do JAR no GIT 
5-Conversão dos códigos-fontes e do pom.xml para usar o encoding padrão UTF-8. O projeto era construído com ISO-8859-1 e continha uma variação mista de encodes de arquivos.
6-Adicionada chamada do processamento em lote via linha de comando (CLI) na classe principal (DesktopApplication1.java). O usuário pode agora passar o caminho de um diretório contendo arquivos .mp, e a ferramenta os converterá sucessivamente sem precisar abrir a Interface Gráfica.