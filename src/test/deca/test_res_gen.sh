#! /bin/sh

# Auteur : gl54
# Version initiale : 01/01/2022

# script permettant de générer automatiquement les fichiers .lis ou .ass des tests

FILE=$(cd "$(dirname "$1")" || exit; pwd)/$(basename "$1")

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

if [ -f "$FILE" ]; then
    echo "$FILE exists."
else
    echo "$FILE n'existe pas"
    exit 0
fi

FILENAME="$(basename -- $FILE)"
case "$FILENAME" in
  *".deca")
  case "$FILE" in
    *"gl54/src/test/deca/syntax/valid/lexer"*)
      RES=$(test_lex "./src/test/deca/syntax/valid/lexer/$FILENAME")
      echo "le résultat du test est :"
      echo "$RES"
      echo -n "est-ce que cela vous convient ? y/n : "
      read keep
      if [ $keep = "y" ]
      then
        LIS="${FILENAME%.*}.lis"
        test_lex "./src/test/deca/syntax/valid/lexer/$FILENAME" 1> "./src/test/deca/syntax/valid/lexer/$LIS" 2> "./src/test/deca/syntax/valid/lexer/$LIS"
        echo "-> Le fichier lis correctement généré"
      else
        echo "-> Abandon"
      fi
      ;;
    *"gl54/src/test/deca/syntax/invalid/lexer"*)
       RES=$(test_lex "./src/test/deca/syntax/invalid/lexer/$FILENAME")
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          test_lex "./src/test/deca/syntax/invalid/lexer/$FILENAME" 1> "./src/test/deca/syntax/invalid/lexer/$LIS" 2> "./src/test/deca/syntax/invalid/lexer/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"gl54/src/test/deca/syntax/valid/parser"*)
       RES=$(test_synt "./src/test/deca/syntax/valid/parser/$FILENAME")
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          test_synt "./src/test/deca/syntax/valid/parser/$FILENAME" 1> "./src/test/deca/syntax/valid/parser/$LIS" 2> "./src/test/deca/syntax/valid/parser/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"gl54/src/test/deca/syntax/invalid/parser"*)
       RES=$(test_synt "./src/test/deca/syntax/invalid/parser/$FILENAME")
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          test_synt "./src/test/deca/syntax/invalid/parser/$FILENAME" 1> "./src/test/deca/syntax/invalid/parser/$LIS" 2> "./src/test/deca/syntax/invalid/parser/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"gl54/src/test/deca/context/valid"*)
       RES=$(test_context "./src/test/deca/context/valid/$FILENAME")
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          test_context "./src/test/deca/context/valid/$FILENAME" 1> "./src/test/deca/context/valid/$LIS" 2> "./src/test/deca/context/valid/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"gl54/src/test/deca/context/invalid"*)
       RES=$(test_context "./src/test/deca/context/invalid/$FILENAME")
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          test_context "./src/test/deca/context/invalid/$FILENAME" 1> "./src/test/deca/context/invalid/$LIS" 2> "./src/test/deca/context/invalid/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    **)
      echo "impossible"
  esac
  ;;
  *)
    echo "merci de choisir un fichier .deca"
  ;;
esac
