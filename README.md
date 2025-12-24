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

## L’annotation @Value

L’annotation @Value est utilisable sur un attribut ou un paramètre pour un type primitif ou une chaîne de caractères. Elle donne la valeur par défaut à injecter.

```java
public class ValueSupplier implements Supplier<String> {

  @Value("hello world")
  private String value;
```

## Les annotations JSR-250

### @Ressource


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