# Déclaration par annotations

L’utilisation des méthodes de fabrique avec l’annotation `@Bean` permet d’ajouter des objets dans un contexte d’application sans être intrusif dans le code des classes concernées.
Cela est surtout utile si nous voulons intégrer dans notre contexte d’application des beans à partir de classes dont nous ne pouvons pas ou ne voulons pas modifier le code source (par exemple des classes fournies par une bibliothèque tierce).

Pour les classes que nous développons dans notre appli (et qui sont spécifiques), on peut se permet d'utiliser des annotation supplémentaires, qui permettent d'être plus spécifiques sur leur rôle.

 - `@Autowired`

 - `@Primary`

 - `@Qualifier`

 - `@Value`

L'utilisation de ces annotations est plus intrusive.

## @Autowired

L’annotation `@Autowired` permet d’activer l’injection automatique de dépendance.
Elle peut être placée au dessus d'un constructeur, d'un setter ou d'une attribut (même privée).
Le framework va chercher le `Bean` dans le contexte d'application dont le type est similaire, et l'injecter

```java
public class WriterService implements Runnable {

  @Autowired
  private Supplier<String> supplier; // Spring est capable d’injecter un bean de type Supplier<String> (à condition qu’il n’en existe qu’un seul) dans l’attribut privé supplier
```

Et donc dans le programme :

#### Avant (avec @Bean)

```java
public class TaskApplication {

  @Bean
  public Runnable task(Supplier<String> dataSupplier) { // Nécessaire de passer un argument au constructeur et à la méthode
    return new WriterService(dataSupplier);
  }
```

#### Après (avec @Autowired)


```java
public class TaskApplication {

  @Bean
  public Runnable task() {
    return new WriterService(); // On a plus besoin de passer de paramètres au constructeur
  }
```

## Gérer les ambiguïtés grâce à @Primary et @Qualifier

Dans l'exemple précédent, Spring va chercher un bean de type ``Runnable``, c’est-à-dire un bean qui implémente l’interface ``Runnable`` dans le contexte d’application. Il est possible qu’il y ait plusieurs beans satisfaisant à ce critère. Dans ce cas, Spring va sélectionner le bean qui porte le même nom que l’attribut : tache dans notre exemple.

S’il existe plusieurs beans compatibles avec le type attendu et qu’aucun d’eux ne porte le bon nom, alors la création du contexte d’application échoue avec l’exception ``UnsatisfiedDependencyException``.

### L’annotation @Primary

L’annotation @Primary permet d’indiquer le bean qui devra être sélectionné en priorité en cas d’ambiguïté.

```java
@Bean
@Primary
public Runnable oneTask() {
  // ...
}

@Bean
public Runnable anotherTask() {
  // ...
}
```

### L’annotation @Qualifier

L’annotation ``@Qualifier`` permet de qualifier, c’est-à-dire de préciser le nom du bean à injecter. Dans la classe Java, on ajoute l’annotation sur un attribut ou sur un paramètre d’une méthode ou d’un constructeur.

```java
public class TaskManager {

  @Autowired
  @Qualifier("tache")
  private Runnable runnable; // si plusieurs beans compatibles avec l’interface Runnable sont trouvés dans le contexte d’application, alors c’est celui portant le nom de tache qui sera choisi
```

## L’annotation @Value

L’annotation @Value est utilisable sur un attribut ou un paramètre pour un type primitif ou une chaîne de caractères. Elle donne la valeur par défaut à injecter.

```java
public class ValueSupplier implements Supplier<String> {

  @Value("hello world")
  private String value;
```

## Les annotations JSR-250

### @Ressource

Assez similaire à `@Autowired` (voir les différences).

### @PostConstruct
Cette annotation s’utilise sur une méthode publique sans paramètre afin de signaler que cette méthode doit être appelée par le conteneur IoC après l’initialisation du bean. Il s’agit d’une alternative à la déclaration de la méthode d’initialisation.


### @PreConstruct
Cette annotation s’utilise sur une méthode publique sans paramètre afin de signaler que cette méthode doit être appelée juste avant la fermeture du contexte d’application. Il s’agit d’une alternative à la déclaration de la méthode de destruction.

Nous pouvons facilement écrire une classe qui se comporte comme un fournisseur de connexion à une base de données en utilisant l’API JDBC. Le conteneur IoC sera responsable d’ouvrir la connexion et de fermer la connexion en appelant les méthodes d’initialisation et de destruction.

```java
public class SimpleConnectionProvider implements Supplier<Connection> {

  //...

  @PostConstruct
  public void openConnection() throws SQLException {
    connection = DriverManager.getConnection(databaseUri, login, password);
  }

  @PreDestroy
  public void closeConnection() throws SQLException {
    if(connection != null) {
      connection.close();
    }
  }
```

## Détection automatique des composants (component scan)

Plutôt que de créer les beans avec des méthodes de fabrique, il est possible de demander au Spring Framework de rechercher dans des packages les classes qui doivent être instanciées pour ajouter des beans dans le contexte d’application. On appelle cette opération le **component scan**.

Pour activer cette fonctionnalité, il suffit de rajouter l’annotation `@ComponentScan` sur la classe qui est passée en paramètre de création du contexte d’application. Par défaut, le Spring Framework va chercher les classes dans le package de cette classe et tous les sous packages. Vous pouvez modifier ce comportement à l’aide de l’attribut `basePackages` qui vous permet de donner la liste des packages (incluant automatiquement leurs sous-packages) à scruter. Vous pouvez également utiliser l’attribut `basePackageClasses` pour fournir une liste de classes. Dans ce cas, ce sont les packages auxquelles appartiennent ces classes (en incluant automatiquement les sous-packages) qui seront scrutés.

Le framework va scruter toutes les classes et créer un bean dans le conteneur IoC pour celles qui sont identifiées comme des composants. Une classe désigne un composant si elle possède l’annotation ``@Component`` ou une annotation de stéréotype de composant (Cf. ci-dessous).

Nous pouvons reprendre notre exemple du chapitre précédent en réduisant considérablement le code en remplaçant les méthodes de fabrique par une détection automatique des composants :

## Notion de composant

C'est une classe qui porte l'annotation `@Component` dont on peut spécifier le nom du *bean* :
```java
@Component("servicePrincipal")
public class WriterService implements Runnable
// Si on ne fournit pas de nom pour le bean, le Spring Framework le déduit du nom de la classe en mettant la première lettre en minuscule
```

Comme les méthodes de fabrique, une classe de composant peut également être annotée avec ``@Primary``. De plus, elle peut elle-même déclarer des méthodes de fabrique, c’est-à-dire des méthodes portant l’annotation ``@Bean``. Dans ce cas, ces méthodes de fabrique seront appelées après la création du bean.

*Si la classe du composant déclare un constructeur avec des paramètres, alors Spring essaiera de résoudre les dépendances pour chaque paramètre. Les paramètres du constructeur peuvent recevoir les annotations ``@Value`` ou ``@Qualifier`` pour guider le framework dans le processus d’injection. Si la classe du composant déclare plusieurs constructeurs alors vous devez ajouter l’annotation ``@Autowired`` sur le constructeur qui doit être utilisé par le Spring Framework.*

## Stéréotype de composant

Il est possible de donner un stéréotype au composant, c’est-à-dire d'$etre plus spécifique. Certains stéréotypes sont purement descriptifs et ne servent qu’à fournir une information aux développeurs. D’autres bénéficient d’un traitement particulier dans certains contextes d’exécution.

### @Component 

C'est le type générique (qui signifie que cette classe doit être utiliser pour instancier un 
*bean*). Les autres dérivés sont :

- ### @Service 
  Renvoie aux classes qui ont la charge de réaliser les fonctionnalités principales

- ### @Respository 
  Fait référence à une classe dont le rôle est d'intéragir avec la BDD (couche DAO).

- ### @Configuration
  Un composant de configuration sert à configurer le contexte d’application. Généralement, il déclare des méthodes de fabrique annotées avec ``@Bean`` (Cf. ci-dessous).

- ### @Controller & @RestController
  Un composant qui joue le rôle d’un contrôleur dans une architecture MVC pour une application Web ou une API Web

*Les stéréotypes `@Service` et `@Repository` sont purement descriptifs. Ils permettent d’offrir une information supplémentaire aux développeurs*


## @Configuration

L’annotation @Configuration permet de déclarer pour Spring un composant qui ne sert qu’à configurer le contexte de l’application. Normalement ce composant n’est pas destiné à être injecté comme dépendance mais à déclarer des méthodes de fabrique annotées avec ``@Bean``.

Un bean de configuration doit être scanné par le Spring Framework pour pouvoir être instancié et participer à la création du contexte d’application. Si vous développez des bibliothèques destinées à être réutilisées dans plusieurs applications ou si vous voulez introduire de la modularité dans l’architecture de votre application, vous pouvez fournir des beans de configuration pour chaque module et importer ces beans grâce à l’annotation ``@Import``.

Imaginons que nous ayons isolé dans notre code un package et tous ses sous packages pour les classes gérant la sécurité dans notre application et un autre package et tous ses sous packages pour les classes gérant l’accès aux services nécessaires à notre application.

Dans le premier package, nous pouvons ajouter la classe ``SecurityConfiguration`` qui peut contenir des méthodes de fabrique et indiquer à Spring qu’il doit réaliser une détection automatique à partir de ce package :

[class SecurityConfiguration]("./src/main/java/com/spring/annotation/security/SecurityConfiguration.java")

Puis le seconde package :

[class SecurityAccessConfiguration]("./src/main/java/com/spring/annotation/access/SecurityAccessConfiguration.java")

Pour enfin, dans le fichier principal utiliser `@Import` pour importer ces deux classes de configuration.

```java

```

```java

```

```java

```