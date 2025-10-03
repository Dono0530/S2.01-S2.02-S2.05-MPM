@echo off

setlocal EnableDelayedExpansion
echo Compilation du projet exFinal...
javac "@Compile.list" -d ./Class

REM Vérifier si la compilation a réussi
if !ERRORLEVEL! EQU 0 (
    echo Exécution du programme...
    echo.
    java -cp ./Class exFinal.Controleur
) else (
    echo Erreur de compilation!
)
pause