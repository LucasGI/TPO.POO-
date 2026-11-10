#!/bin/bash
# Compila y ejecuta Rumbo Sur en Mac (doble clic en Finder)
cd "$(dirname "$0")"

# busca un JDK: el del sistema o alguno descargado por IntelliJ
JAVAC=""
if /usr/libexec/java_home >/dev/null 2>&1; then
    JAVAC="$(/usr/libexec/java_home)/bin/javac"
fi
if [ ! -x "$JAVAC" ]; then
    for d in "$HOME"/Library/Java/JavaVirtualMachines/*/Contents/Home /Library/Java/JavaVirtualMachines/*/Contents/Home /opt/homebrew/opt/openjdk*/libexec/openjdk.jdk/Contents/Home; do
        if [ -x "$d/bin/javac" ]; then
            JAVAC="$d/bin/javac"
        fi
    done
fi
if [ ! -x "$JAVAC" ]; then
    echo "No se encontro un JDK. Instale Java 17 o superior, o ejecute desde IntelliJ."
    read -p "Enter para cerrar"
    exit 1
fi
BIN="$(dirname "$JAVAC")"
echo "Usando $("$BIN/java" -version 2>&1 | head -1)"

rm -rf out
mkdir -p out
"$BIN/javac" -encoding UTF-8 -d out $(find src -name "*.java") || { read -p "Error al compilar. Enter para cerrar"; exit 1; }
echo "Compilado. Abriendo Rumbo Sur..."
"$BIN/java" -cp out app.Main
