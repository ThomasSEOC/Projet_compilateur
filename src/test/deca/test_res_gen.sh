#! /bin/sh

# Auteur : gl54
# Version initiale : 01/01/2022

# script permettant de générer automatiquement les fichiers .lis ou .ass des tests
# le fichier deca de test doit être passé en argument (chemin relatif ou absolu)
# le choix du launcher est automatique selon le chemin du fichier .dec
# une validation manuelle du contenu est demandé avant de créer le fichier

FILE=$(cd "$(dirname "$1")" || exit; pwd)/$(basename "$1")

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

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
    *"src/test/deca/syntax/valid/lexer"*)
      RES=$(test_lex "./src/test/deca/syntax/valid/lexer/$FILENAME" 2>&1)
      echo "le résultat du test est :"
      echo "$RES"
      echo -n "est-ce que cela vous convient ? y/n : "
      read keep
      if [ $keep = "y" ]
      then
        LIS="${FILENAME%.*}.lis"
        echo "$RES" > "./src/test/deca/syntax/valid/lexer/$LIS"
#        test_lex "./src/test/deca/syntax/valid/lexer/$FILENAME" 1> "./src/test/deca/syntax/valid/lexer/$LIS" 2> "./src/test/deca/syntax/valid/lexer/$LIS"
        echo "-> Le fichier lis correctement généré"
      else
        echo "-> Abandon"
      fi
      ;;
    *"src/test/deca/syntax/invalid/lexer"*)
        RES=$(test_lex "./src/test/deca/syntax/invalid/lexer/$FILENAME" 2>&1)
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          echo "$RES" > "./src/test/deca/syntax/invalid/lexer/$LIS"
#          test_lex "./src/test/deca/syntax/invalid/lexer/$FILENAME" 1> "./src/test/deca/syntax/invalid/lexer/$LIS" 2> "./src/test/deca/syntax/invalid/lexer/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"src/test/deca/syntax/valid/parser"*)
        RES=$(test_synt "./src/test/deca/syntax/valid/parser/$FILENAME" 2>&1)
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          echo "$RES" > "./src/test/deca/syntax/valid/parser/$LIS"
#          test_synt "./src/test/deca/syntax/valid/parser/$FILENAME" 1> "./src/test/deca/syntax/valid/parser/$LIS" 2> "./src/test/deca/syntax/valid/parser/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"src/test/deca/syntax/invalid/parser"*)
        RES=$(test_synt "./src/test/deca/syntax/invalid/parser/$FILENAME" 2>&1)
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          echo "$RES" > "./src/test/deca/syntax/invalid/parser/$LIS"
#          test_synt "./src/test/deca/syntax/invalid/parser/$FILENAME" 1> "./src/test/deca/syntax/invalid/parser/$LIS" 2> "./src/test/deca/syntax/invalid/parser/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"src/test/deca/context/valid"*)
        RES=$(test_context "./src/test/deca/context/valid/$FILENAME" 2>&1)
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          echo "$RES" > "./src/test/deca/context/valid/$LIS"
#          test_context "./src/test/deca/context/valid/$FILENAME" 1> "./src/test/deca/context/valid/$LIS" 2> "./src/test/deca/context/valid/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"src/test/deca/context/invalid"*)
        RES=$(test_context "./src/test/deca/context/invalid/$FILENAME" 2>&1)
        echo "le résultat du test est :"
        echo "$RES"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          LIS="${FILENAME%.*}.lis"
          echo "$RES" > "./src/test/deca/context/invalid/$LIS"
#          test_context "./src/test/deca/context/invalid/$FILENAME" 1> "./src/test/deca/context/invalid/$LIS" 2> "./src/test/deca/context/invalid/$LIS"
          echo "-> Le fichier lis correctement généré"
        else
          echo "-> Abandon"
        fi
      ;;
    *"src/test/deca/codegen/valid"*)
       ASS="${FILENAME%.*}.ass"
       echo -n "est-ce ce que vous voulez aussi générer un fichier ass ? y/n : "
       read generate_ASS
       rm "./src/test/deca/codegen/valid/$ASS" 2> /dev/null
       RES=$(decac "./src/test/deca/codegen/valid/$FILENAME" 2>&1)
       if [ "$generate_ASS" = "y" ]
       then
         echo "le fichier ASS du test est : "
         echo "$RES"
         echo "le fichier assembleur généré est : "
         cat "./src/test/deca/codegen/valid/$ASS"
         echo -n "est-ce que cela vous convient ? y/n : "
         read keep
         if [ "$keep" = "y" ]
         then
           #decac "./src/test/deca/codegen/valid/$FILENAME" 1> "./src/test/deca/codegen/valid/$ASS" 2> "./src/test/deca/codegen/valid/$ASS"
           cp "./src/test/deca/codegen/valid/$ASS" "./src/test/deca/codegen/valid/result/$ASS"
           echo "-> Le fichier ass correctement généré"
         else
           echo "-> Abandon"
           rm "./src/test/deca/codegen/valid/$ASS" 2> /dev/null
           exit 0
         fi
       fi
       RES_IMA=$(ima "./src/test/deca/codegen/valid/$ASS" 2>&1)
       echo "le resultat ima pour ce test est : "
       echo "$RES_IMA"
       echo -n "est-ce que cela vous convient ? y/n : "
       read keep
       if [ "$keep" = "y" ]
       then
        IMA="${FILENAME%.*}.ima"
        echo "$RES_IMA" > "./src/test/deca/codegen/valid/result/$IMA"
        echo "-> le fichier IMA a été correctement généré"
       else
        echo "-> abandon"
       fi
       rm "./src/test/deca/codegen/valid/$ASS" 2> /dev/null
      ;;
    *"src/test/deca/codegen/invalid"*)
        ASS="${FILENAME%.*}.ass"
        rm "./src/test/deca/codegen/invalid/$ASS" 2> /dev/null
        RES=$(decac "./src/test/deca/codegen/invalid/$FILENAME" 2>&1)
        echo "le debug du test est : "
        echo "$RES"
        echo "le fichier assembleur généré est : "
        cat "./src/test/deca/codegen/invalid/$ASS"
        echo -n "est-ce que cela vous convient ? y/n : "
        read keep
        if [ $keep = "y" ]
        then
          #decac "./src/test/deca/codegen/invalid/$FILENAME" 1> "./src/test/deca/codegen/invalid/$ASS" 2> "./src/test/deca/codegen/invalid/$ASS"
          mv "./src/test/deca/codegen/invalid/$ASS" "./src/test/deca/codegen/invalid/result/$ASS"
          echo "-> Le fichier ass correctement généré"
        else
          echo "-> Abandon"
        fi
        rm "./src/test/deca/codegen/invalid/$ASS" 2> /dev/null
      ;;
    **)
      echo "impossible"
  esac
  ;;
  *)
    echo "merci de choisir un fichier .deca"
  ;;
esac
