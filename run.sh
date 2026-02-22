#!/bin/bash

# Script para executar o conversor PFM2OSM
# Uso: ./run.sh [arquivo.mp]

JAR_FILE="target/pfm2osm-1.0-SNAPSHOT-jar-with-dependencies.jar"

# Verifica se o JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "? JAR não encontrado. Compilando projeto..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "? Erro na compilação!"
        exit 1
    fi
fi

# Executa a aplicação
echo "?? Iniciando PFM2OSM Converter..."
java -jar "$JAR_FILE" "$@"
