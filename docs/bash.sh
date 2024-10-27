#!/bin/bash

# Número de clientes
NUM_CLIENTES=10
# Número grande para calcular Fibonacci
NUM_FIBO=40
# Archivo para registrar resultados
RESULTS_FILE="resultados.txt"

# Limpiar el archivo de resultados
echo "Resultados de los clientes:" > $RESULTS_FILE

for ((i=1; i<=NUM_CLIENTES; i++)); do
    {
        # Simular el envío de una solicitud al servidor
        RESPONSE=$(curl -s -m 5 "hgrid15=$NUM_FIBO")  # Ajusta la URL según tu servidor
        echo "Cliente $i: $RESPONSE" >> $RESULTS_FILE
    } &
done

# Esperar a que todos los procesos terminen
wait

echo "Simulación completada. Resultados almacenados en $RESULTS_FILE."