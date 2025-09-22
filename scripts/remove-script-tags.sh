#!/usr/bin/env bash
set -euo pipefail

# Uso:
#   ./scripts/remove-script-tags.sh [ruta]
# Por defecto limpia src/main/resources/static
# Crea un respaldo .bak por cada archivo modificado.

ROOT_DIR=${1:-"src/main/resources/static"}

if [[ ! -d "$ROOT_DIR" ]]; then
  echo "Directorio no encontrado: $ROOT_DIR" >&2
  exit 1
fi

# Recorre archivos .html/.htm y elimina todas las etiquetas <script>...</script>
# - Perl lee el archivo completo (-0777) y aplica regex con flags i (case-insensitive) y s (dotall)
# - La copia de respaldo se guarda como .bak
find "$ROOT_DIR" -type f \( -name "*.html" -o -name "*.htm" \) -print0 | while IFS= read -r -d '' f; do
  cp "$f" "$f.bak"
  perl -0777 -pe 's{(?is)<script\b[^>]*>.*?</script>}{}g' "$f" > "$f.tmp" && mv "$f.tmp" "$f"
  echo "Limpio: $f"
done

echo "Hecho. Respaldos con extensión .bak en el mismo directorio."


