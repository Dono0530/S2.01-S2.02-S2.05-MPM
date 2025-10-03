echo "Compilation du projet exFinal..."
javac "@Compile.list" -d ./Class

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Exécution du programme..."
    echo
    java -cp ./Class exFinal.Controleur
else
    echo "Erreur de compilation!"
    exit 1
fi