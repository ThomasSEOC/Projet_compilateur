# Propositions pour l'extension du projets GL

#### équipe 54 : Wang Caroline, Ho-Sun Jules, Falgayrac Loïc, Dijs Thomas, Noiry Sylvain

## Choix de l'extension :

​	Nous avons envisagé plusieurs extensions, notamment la compilation vers d'autres assembleurs comme ARM ou RISCV, mais nous avons finalement retenu l'extension optimisation.

​	Cette extension est assez libre, ce qui nous permet d'aller plus ou moins loin selon le temps que nous prendra la mise au point du compilateur de base. Cette liberté nous permet aussi d'envisager de nombreux cas d'optimisation, mais aussi de se concentrer sur un type d'optimisation particulier.  Le fait d'optimiser les performances d'un algorithme est également un thème plutôt apprécié dans l'équipe.

## Intégration de l'extension dans le projet

​	Nous souhaitons débuter l'implémentation de l'extension avant la fin du compilateur de base final, afin de l'intégrer au fur et à mesure de l'avancement et de faire des bon choix algorithmiques vis à vis des modifications nécessaires. La majorité de ces changements concerneront la partie C, l'étape de génération de code. 

​	Selon notre vision actuelle, qui peut-être sujette à changement, nous allons découper l'optimisation en sous étapes. la première consiste à simplifier l'arbre décoré, éventuellement avec plusieurs passes correspondant à plusieurs familles d'optimisation. La seconde sera au niveau de la génération de l'assembleur directement, ou d'autres types d'optimisations prendront place.

## Quelques pistes pour optimiser le code assembleur généré

 	La plupart des compilateurs modernes possèdes des options à indiquer selon de type et le degré d'optimisation que l'on souhaite. Sur gcc il y a par exemple -O2, -Os, -Ofast, etc... Nous pourrions alors proposer par exemple deux types d'optimisation, un concernant le temps d'exécution et l'autre concernant la mémoire. Cela se matérialisera par l'appel ou non des méthode concernant un point d'optimisation particulier.

​	Même si nous n'avons encore approfondi les recherches sur l'optimisation à la compilation, nous avons déjà quelques pistes :

- La reconnaissance d'expressions constantes directement à la compilation. Par exemple, le code suivant :

  ```c
  int a = 1;
  int b = 2*3;
  int c = a + b;
  ```

  peut-être simplifié en :

  ```c
  int c = 7;
  ```

- L'optimisation de l'utilisation des registres, par exemples en stockant en registres certaines variables locales plutôt qu'en mémoire si cela n'est pas nécessaire

- L'augmentation de la localité des instructions susceptibles d'être exécutés dans le même intervalle de temps, ou de la localité mémoire

- La réduction des dépendances artificielles entre instructions (sur les registres et les opérations mémoire par exemple). En effet les processeurs modernes sont plus efficaces s'il y a peu de dépendances entre les instructions

- La suppression de certaines structures de contrôle créant des branches par du code dit "branchless". On pourrait ici supprimer des branches parfois très coûteuses en nombre de cycles si mal prédites par des affectations masqués. Par exemple, le code suivant :

  ```c
  int a;
  ...
  if (a != 0) {
    b = 1;
  }
  else {
    b = 0;
  }
  ```

  peut se simplifier par :

  ```c
  int a;
  ...
  int b = 0 + 1*(a != 0);
  ```

Ce n'est qu'une première idée, mais on peut constater dès maintenant que les benchmarks seront cruciaux pour tester certaines d'entre elles. Néanmoins IMA semble, de part sa description, utilisé une micro-architecture fictive très ancienne, et pourrait ne pas être sensibles à certaines optimisation appliquées aux processeurs modernes.