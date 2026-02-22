# TODO - PFM2OSM Converter

## ?? Prioridade Alta - Correções Necessárias

### 1. Encoding de Caracteres
- [ ] **Problema**: Warnings de caracteres UTF-8 não mapeáveis durante compilação
  - Linhas afetadas: 43, 51, 84, 85, 267, 294, 339, 374, 487, 574, 615 em `LerMP.java`
  - **Solução**: Converter comentários e strings para UTF-8 correto ou usar escape sequences
  - **Impacto**: Baixo - apenas warnings, não afeta funcionalidade

### 2. APIs Deprecadas
- [ ] **Float(double) e Float(float) constructors**
  - Arquivo: `Node.java` linha 201
  - Arquivo: `Util.java` linha 54
  - **Solução**: Usar `Float.valueOf()` ao invés de `new Float()`
  - **Impacto**: Médio - marcado para remoção em versões futuras do Java

### 3. Configuração Maven
- [ ] Adicionar `--release 11` ao invés de `-source 11 -target 11`
  - Atualizar `pom.xml` para usar release ao invés de source/target separados
  - **Benefício**: Melhor compatibilidade e warnings mais precisos

## ?? Prioridade Média - Melhorias

### 4. Testes Unitários
- [ ] Criar estrutura de testes
- [ ] Adicionar testes para parser PFM
- [ ] Adicionar testes para geração OSM
- [ ] Adicionar testes para conversão de coordenadas
- [ ] Testar diferentes encodings (CodePage)

### 5. Documentação
- [ ] Documentar formato PFM em detalhes
- [ ] Documentar mapeamento PFM ? OSM tags
- [ ] Criar exemplos de uso
- [ ] Documentar tipos de elementos suportados
- [ ] Adicionar JavaDoc aos métodos principais

### 6. Interface de Linha de Comando (CLI)
- [ ] Criar modo CLI além da GUI
- [ ] Adicionar opções de linha de comando
- [ ] Permitir conversão em batch
- [ ] Adicionar modo verbose/debug

### 7. Tratamento de Erros
- [ ] Melhorar mensagens de erro
- [ ] Adicionar validação de entrada
- [ ] Criar log estruturado
- [ ] Adicionar recuperação de erros

## ?? Prioridade Baixa - Funcionalidades Futuras

### 8. Modernização da Interface
- [ ] Considerar migração para JavaFX
- [ ] Melhorar UX da interface atual
- [ ] Adicionar preview do mapa
- [ ] Adicionar barra de progresso mais detalhada

### 9. Performance
- [ ] Otimizar leitura de arquivos grandes
- [ ] Implementar processamento paralelo
- [ ] Adicionar cache de resultados
- [ ] Profiling e otimização de memória

### 10. Integração com TSuite
- [ ] Estudar módulos do TSuite
- [ ] Identificar código reutilizável
- [ ] Integrar melhorias do ConversorOsm.java
- [ ] Sincronizar mapeamentos de tipos

### 11. Formatos Adicionais
- [ ] Suporte para outros formatos de entrada
- [ ] Suporte para outros formatos de saída
- [ ] Conversão bidirecional (OSM ? PFM)

### 12. Validação
- [ ] Validar OSM gerado
- [ ] Verificar integridade de geometrias
- [ ] Validar tags OSM
- [ ] Gerar relatório de qualidade

## ?? Backlog - Ideias

- [ ] Plugin para JOSM (Java OpenStreetMap Editor)
- [ ] API REST para conversão online
- [ ] Suporte para streaming de dados grandes
- [ ] Internacionalização (i18n)
- [ ] Configuração via arquivo externo
- [ ] Suporte para estilos de mapa customizados
- [ ] Integração com serviços de geocoding
- [ ] Exportação para outros formatos GIS

## ?? Bugs Conhecidos

Nenhum bug crítico identificado até o momento.

## ? Concluído

- [x] Setup inicial do projeto Maven
- [x] Remoção de import deprecado `sun.misc.CharacterEncoder`
- [x] Compilação bem-sucedida
- [x] Geração de JAR executável
- [x] Criação de README.md
- [x] Criação de .gitignore

## ?? Notas

- O projeto compila com warnings mas funciona
- Priorizar correção de APIs deprecadas antes de atualizar para Java 17+
- Considerar criar branch separada para refatorações grandes
- Manter compatibilidade com arquivos PFM existentes

---

**Última atualização**: 2026-02-11
