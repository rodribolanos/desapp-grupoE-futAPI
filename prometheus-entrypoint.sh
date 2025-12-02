#!/bin/sh

# Script de entrada para Prometheus que genera la configuración dinámicamente

echo "=== Iniciando configuración de Prometheus ==="

# Variables de entorno con valores por defecto
FUTAPP_TARGET=${FUTAPP_TARGET:-futapp:8080}
PROMETHEUS_TARGET=${PROMETHEUS_TARGET:-localhost:9090}
SCRAPE_INTERVAL=${SCRAPE_INTERVAL:-15s}
FUTAPP_SCRAPE_INTERVAL=${FUTAPP_SCRAPE_INTERVAL:-5s}
METRICS_PATH=${METRICS_PATH:-/actuator/prometheus}

# Esperar a que futapp esté realmente disponible
echo "=== Esperando a que ${FUTAPP_TARGET} esté disponible ==="
until wget -q --spider http://${FUTAPP_TARGET}/actuator/health 2>/dev/null; do
  echo "Esperando a ${FUTAPP_TARGET}..."
  sleep 2
done
echo "=== ${FUTAPP_TARGET} está disponible ==="

# Verificar el endpoint de prometheus
echo "=== Verificando endpoint de prometheus ==="
if wget -q --spider http://${FUTAPP_TARGET}${METRICS_PATH} 2>/dev/null; then
  echo "=== Endpoint ${METRICS_PATH} está disponible ==="
else
  echo "=== ADVERTENCIA: Endpoint ${METRICS_PATH} no está disponible ==="
fi

# Generar el archivo prometheus.yml dinámicamente
cat > /etc/prometheus/prometheus.yml <<EOF
global:
  scrape_interval: ${SCRAPE_INTERVAL}
  evaluation_interval: ${SCRAPE_INTERVAL}
  external_labels:
    monitor: 'futapp-monitor'

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['${PROMETHEUS_TARGET}']

  - job_name: 'futapp'
    metrics_path: '${METRICS_PATH}'
    static_configs:
      - targets: ['${FUTAPP_TARGET}']
    scrape_interval: ${FUTAPP_SCRAPE_INTERVAL}
EOF

echo "=== Archivo prometheus.yml generado ==="
cat /etc/prometheus/prometheus.yml

# Iniciar Prometheus con los argumentos originales
exec /bin/prometheus "$@"

