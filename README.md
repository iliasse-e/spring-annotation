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

## Stéréotype de composant

### @Component 

### @Service 

### @Respository 

### @Configuration

### @Controller & @RestController




```java

```

```java

```

```java

```