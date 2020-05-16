# WitchCraft ![Build status](https://github.com/Ivan1pl/witchcraft/workflows/Build/badge.svg)
WitchCraft Framework is a java framework for creating Bukkit plugins. It provides annotation-based command configuration and `plugin.yml` file generation.

# Setup
If you want to use WitchCraft Framework with building tools like `maven` or `gradle`, you will need to add Ivan1pl's repository.

## Maven

```xml
<repositories>
    <repository>
        <id>ivan1pl</id>
        <name>ivan1pl</name>
        <url>http://nexus.ivan1pl.com/repository/maven-releases/</url>
    </repository>
</repositories>
```

## Gradle

```gradle
repositories {
    maven {
        url 'http://nexus.ivan1pl.com/repository/maven-releases/'
    }
}
```

# Dependency injection

WitchCraft Framework will create instances of your classes for you, and it will also link them together. Your classes can depend on other classes or configuration values. The framework will create a single instance of each managed class. Dependencies are injected by constructor parameters.

## Setup

You will need to add `witchcraft-plugin` library to your project.

### Maven

Add dependency on `witchcraft-plugin`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-plugin</artifactId>
        <version>0.6.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-plugin`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-plugin:0.6.0"
}
```

### Setup

Then simply extend `WitchCraftPlugin` instead of `JavaPlugin` in your main plugin class. That's it.

All classes annotated with `@Managed` or `@Command` and placed in the same package tree as your main plugin class will be managed by WitchCraft's dependency injection feature. If you want to put your managed classes in a different package tree (i.e. neither in the same package nor in any of its subpackages) from the main plugin class, use `@Plugin` annotation on your main plugin class to tell the framework where to search for them.

## Limitations

There are several limitations to how dependency injection works. Each managed class should have exactly one constructor. It should be public and all its parameters should be instances of other managed classes or configuration values (default constructors count and are valid). Consider the following example:
```java
@Managed
public class A {
    public A(B dep1, C dep2, @ConfigurationValue("some.configuration.key") int someConfigurationKey) {
        //constructor body...
    }
}

@Managed
public class B {
}

public class C {
}
```

In that example dependency injection will fail. Class `A` depends on classes `B` and `C` and on configuration value `some.configuration.key`. However, class `C` is not managed by dependency injection feature (there's no `@Managed` annotation), which means that the framework will not be able to create an instance of that class and pass it as parameter to the constructor of class `A`.

Another limitation is that there can be no dependency cycles. Consider the following example:
```java
@Managed
public class A {
    public A(B dep) {}
}

@Managed
public class B {
    public B(C dep) {}
}

@Managed
public class C {
    public C(A dep) {}
}
```

In this example, in order to create an instance of class `A`, the framework needs to create an instance of class `B` first. To create an instance of class `B` it needs to create an instance of class `C` first. That is impossible because it would then need to create an instance of class `A` which is already under construction.

## Example

Take a look at a working example of dependency injection configuration containing both `@Managed` and `@Command` annotated classes (commands will be explained in the next section).

File `config.yml`:
```yaml
witchcraft:
  example:
    counter:
      initVal: 42
```

File `Counter.java`:
```java
@Managed
public class Counter {
    private final JavaPlugin javaPlugin;
    private int value;

    public Counter(JavaPlugin javaPlugin, @ConfigurationValue("witchcraft.example.counter.initVal") int initVal) {
        this.javaPlugin = javaPlugin;
        this.value = initVal;
    }

    public void increase() {
        value++;
        javaPlugin.getLogger().info("Increase counter");
    }

    public void decrease() {
        value--;
        javaPlugin.getLogger().info("Decrease counter");
    }

    public int getValue() {
        return value;
    }
}
```

File `WcAdd.java`:
```java
@Command(name = "cadd", description = "Increase counter")
public class WcAdd {
    private final Counter counter;

    public WcAdd(Counter counter) {
        this.counter = counter;
    }

    @SubCommand
    @Description(shortDescription = "Increase counter",
            detailedDescription = "Add 1 to current value of the global counter")
    public void increaseCounter(@Sender CommandSender commandSender) {
        counter.increase();
        commandSender.sendMessage("Increased counter, current value: " + counter.getValue());
    }
}
```

File `WcSubtract.java`:
```java
@Command(name = "csubtract", description = "Decrease counter")
public class WcSubtract {
    private final Counter counter;

    public WcSubtract(Counter counter) {
        this.counter = counter;
    }

    @SubCommand
    @Description(shortDescription = "Decrease counter",
            detailedDescription = "Subtract 1 from current value of the global counter")
    public void decreaseCounter(@Sender CommandSender commandSender) {
        counter.decrease();
        commandSender.sendMessage("Decreased counter, current value: " + counter.getValue());
    }
}
```

File `WitchCraftExamplePlugin.java`:
```java
public class WitchCraftExamplePlugin extends WitchCraftPlugin {
    @Override
    protected void preInit() {
        saveDefaultConfig();
    }
}
```

In this example a single instance of `Counter` class is created and shared between instances of `WcAdd` and `WcSubtract` classes. This can be seen if you execute the following command chain:
* `/cadd`
* `/cadd`
* `/cadd`
* `/csubtract`
* `/csubtract`
* `/cadd`

You will get the following messages after executing commands in that order:
* `Increased counter, current value: 43`
* `Increased counter, current value: 44`
* `Increased counter, current value: 45`
* `Decreased counter, current value: 44`
* `Decreased counter, current value: 43`
* `Increased counter, current value: 44`

## Predefined managed classes

The framework comes with its own set of classes annotated with `@Managed` annotation. All classes within packages `com.ivan1pl.witchcraft.commands.adapters` and `com.ivan1pl.witchcraft.commands.completers` will be automatically instantiated and you can use them as dependencies of your managed classes.

There are two classes that are not annotated with `@Managed` or `@Command`, but you can still use them as dependencies in your managed classes. One of them is your main plugin class. The other is `WitchCraftContext`, which is a class that is used to manage the entire dependency injection feature and contains instances of all managed classes (which you can acquire at runtime by invoking method `get`, e.g. `witchCraftContext.get(JavaPlugin.class)` will give you your plugin instance).

# Modules

WitchCraft allows its users to create their own modules and hook them into the framework. This section explains how to do that. Predefined modules will be explained in separate sections.

To be able to create modules, you need the exact same dependencies as for the dependency injection feature.

## Creating a module

To create a module, you will have to create an annotation with runtime retention policy. Then annotate your annotation with `@Module` annotation and annotate your main plugin class with it. That's it, your module is now enabled. WitchCraft will scan the entire package tree of that annotation for managed classes and add them to its context. You can then depend on them in your plugin.

It may not seem very useful, and in fact in regular plugin development it is not. This is a feature designed for library/API developers who want to hook their work into the framework. Also, some of the predefined features are provided as modules, so that you can enable them if you need them, but you do not have to depend on them if you do not.

## Aspects

Modules come with an interesting feature that allows you to use AOP advices in your plugin or library. If you're not familiar with aspect-oriented programming, basically it allows you to add some behaviour to already existing methods, which is used to separate distinct functionalities. For example, if you want to enable logging of method invocations, you could create a method containing your business logic and add logging as an advice so that it does not clutter your code.

Of course there are many more useful things that can be done with aspects, for example they could be used for database connection management, refreshing configuration, validating some data before method execution.

Creating an aspect is very easy. Simply create a class, mark it as `@Managed`, implement `Aspect` interface and add it to your module's aspect list (`aspects` attribute in `@Module` annotation).

The interface is very simple and self-explanatory. It contains the following methods:
* `void beforeMethod(Object self, Method method, Method originalMethod, Object[] args)` - execute advice that should happen before method execution; `self`, `method` and `args` give you informations about the object on which the method is being executed, the method itself and its arguments, `originalMethod` is the original method that would have been invoked if there were no advices, you should not use it directly but you can for example process its annotations.
* `void afterMethod(Object self, Method method, Method originalMethod, Object[] args)` - execute advice that should happen after method execution.
* `InvocationCallback aroundMethod(InvocationCallback proceed)` - execute advice that should happen around method execution.
* `int getPriority()` - execution priority of this aspect. The lower the number, the earlier it will be executed. Default is 0 (you do not have to implement this method).

The only method that may require some additional explaining is `aroundMethod`. Basically it allows you to add logic before and after the invocation at the same time. The actual invocation is carried in `proceed` parameter, wrapped by `InvocationCallback` functional interface. The interface itself contains only one method `Object apply(Object self, Method method, Method originalMethod, Object[] args)` which is used to invoke the method. Wherever you wish to invoke it in your code, simply use `proceed.apply(self, method, originalMethod, args);`.

Aspects will be applied to all methods in all managed classes, expect classes implementing `Aspect` or `Listener` interface.

# Commands

WitchCraft Framework provides a very simple way of defining commands using annotated classes and functions. The framework will take care of everything: annotated classes will be automatically registered and added to command
executor.

## Setup

You will need to add `witchcraft-plugin` library to your project.

### Maven

Add dependency on `witchcraft-plugin`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-plugin</artifactId>
        <version>0.6.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-plugin`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-plugin:0.6.0"
}
```

### Setup

Then simply extend `WitchCraftPlugin` instead of `JavaPlugin` in your main plugin class. That's it.

If you want to put your command in a different package tree (i.e. neither in the same package nor in any of its subpackages) from the main plugin class, use `@Plugin` annotation on your main plugin class to tell the framework where to search for them.

In most cases, this is all you need:
```java
public class WitchCraftExamplePlugin extends WitchCraftPlugin {
}
```

### Additional logic during initialization

You may want to add some logic during plugin initialization. The method `onEnable` has been marked as `final` which means you can no longer override it. Two other methods have been provided instead: `preInit` and `postInit`. The first one is invoked before WitchCraft initializes (before the plugin is scanned for commands and before dependency injection). The second one is invoked after WitchCraft initializes.

A natural use for `preInit` method is saving default config:
```java
@Override
protected void preInit() {
    saveDefaultConfig();
}
```

## Commands and subcommands

Note: _the framework assumes that each command may have many subcommands. If your command does not use any, simply define a single subcommand with empty name._

To define a new command, create a class within some package (the framework will not scan the default package) and annotate it with `@Command` annotation:
```java
@Command(name = "witchcraft-test")
public class TestCommand {
    //...
}
```
Of course, you can provide more data to the annotation: command description, required permission node and aliases:
```java
@Command(name = "witchcraft-test", aliases = {"wtest", "wctest"},
        permission = "witchcraft-example-plugin.witchcraft-test",
        description = "Test command for witchcraft-examples")
public class TestCommand {
    //...
}
```
Each command needs to have at least one subcommand (perhaps with empty name). To create a subcommand, create a `public` method returning `void`:
```java
@SubCommand("subcommand")
public void subcommand(/* parameters */) {
    //command body
}
```
You can give the method any name you want, the actual name will be taken from the `SubCommand` annotation. You can also provide a description and permission node required to execute the subcommand:
```java
@SubCommand(value = "sub", permission = "witchcraft-example-plugin.witchcraft-test.sub")
@Description(shortDescription = "Short description", detailedDescription = "Detailed description")
public void sub(/* parameters */) {
    //command body
}
```

### Command parameters

This is where the real magic starts. The framework will automatically resolve your subcommand method's parameters, and it will also generate tab completions for them! Let's use an example here:
```java
@Command(name = "witchcraft-test", aliases = {"wtest", "wctest"},
        permission = "witchcraft-example-plugin.witchcraft-test",
        description = "Test command for witchcraft-examples")
public class TestCommand {

    @SubCommand("")
    @Description(shortDescription = "Short description for witchcraft-test", detailedDescription = "Detailed description for witchcraft-test")
    public void witchcraftTest(@Sender CommandSender commandSender, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing default command; params: number=%d, otherNumber=%d", number, otherNumber));
    }

    @SubCommand(value = "any-sender", permission = "witchcraft-example-plugin.witchcraft-test.any")
    @Description(shortDescription = "Short description for any-sender", detailedDescription = "Detailed description for any-sender")
    public void anySenderCommand(@Sender CommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "any-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "player-sender", permission = "witchcraft-example-plugin.witchcraft-test.player")
    @Description(shortDescription = "Short description for player-sender", detailedDescription = "Detailed description for player-sender")
    public void playerSenderCommand(@Sender Player commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "player-sender", player.getName(), number, otherNumber));
    }

    @SubCommand(value = "console-sender", permission = "witchcraft-example-plugin.witchcraft-test.console")
    @Description(shortDescription = "Short description for console-sender", detailedDescription = "Detailed description for console-sender")
    public void consoleSenderCommand(@Sender ConsoleCommandSender commandSender, Player player, int number, @Optional("42") int otherNumber) {
        commandSender.sendMessage(
                String.format("Executing command %s; params: player=%s, number=%d, otherNumber=%d",
                        "console-sender", player.getName(), number, otherNumber));
    }
}
```
Let's explain the new annotations that appeared in this example:
* `@Sender`: the framework will try to assign the command executor to all parameters annotated with this annotation. Use this only if you need to know who executed the command.
* `@Optional`: if the parameter was not passed to the command, the value provided with this annotation will be used instead.

There is one more very useful annotation that can be used here: `@ConfigurationValue`. This way you can tell the framework to read the value from the plugin configuration file and it will automatically do this for you!

The framework will try to match the first parameter to all declared subcommands. If a matching one is found, it will go through parameters (except those annotated with `@Sender` or `@ConfigurationValue`) in order they were declared and try to assign command parameters to them (automatically performing any needed type conversions).

Now let's assume a player types in a command: `/wtest any-sender somePlayer 55`. The method `anySenderCommand` will be called, with parameters:
* `commandSender`: an object of type `Player` (representing the player who executed the command) will be assigned here
* `player`: if a player `somePlayer` exists, the framework will assign that player here; otherwise the command will fail
* `number`: `55`
* `otherNumber`: the value was not provided, so the default one will be used: `42`

The command body is very simple here, the player who executed the command will simply receive a message describing command parameters: `Executing command any-sender; params: player=somePlayer, number=55, otherNumber=42`.

If the same player tries to execute the subcommand `console-sender`, the command will fail, even if all required parameters are provided - because the player cannot be assigned to `ConsoleCommandSender` type; that subcommand can only be executed from console.

### Parameter types

Of course it is impossible to handle all possible cases, so here is a list of all supported types:

For parameters annotated with `@Sender`, all types are allowed (but the command will fail if the command sender cannot be assigned to it, so this is your limitation here).

For parameters annotated with `@ConfigurationValue`, all types supported by Bukkit's `Configuration` class will be accepted.

For other parameters, this is a (still growing) list of all currently supported types:
* `String`
* `boolean`
* `Boolean`
* `int`
* `Integer`
* `long`
* `Long`
* `float`
* `Float`
* `double`
* `Double`
* `Biome`
* `BlockData`
* `EntityEffect`
* `EntityType`
* `EquipmentSlot`
* `GameMode`
* `Instrument`
* `Material`
* `Player`
* `PotionType`
* `WeatherType`
* `World`
* `WorldType`

But even here, if a type is not yet supported, the framework provides a way to use it. You can use the annotation `@Adapter` and create a class implementing the `TypeAdapter` interface and annotated with `@Managed` annotation. The interface contains only one method `Object convert(String arg)` which provides a way to convert a `String` value to your desired type. Your class should be located in the same package tree as your plugin class (or the one indicated by `@Plugin` annotation if you use it) and should contain a single public constructor with parameters supported by dependency injection feature.

### Tab completion

The framework will automatically complete subcommand names and parameters of the following types (more types will be added in the future):
* `Biome`
* `BlockData`
* `EntityEffect`
* `EntityType`
* `EquipmentSlot`
* `GameMode`
* `Instrument`
* `Material`
* `Player`
* `PotionType`
* `WeatherType`
* `World`
* `WorldType`

However, it is very easy to provide possible tab completions to any other parameter: simply create a class implementing `TabCompleter` interface (it contains only one method `Set<String> getSuggestions(String partial)` returning all suggestions for given partial value), annotate that class with `@Managed` annotation and annotate the parameter with `@TabComplete` annotation. Your class should be located in the same package tree as your plugin class (or the one indicated by `@Plugin` annotation if you use it) and should contain a single public constructor with parameters supported by dependency injection feature.

### Help

The framework will automatically generate help pages for your commands. They are registered as subcommands named "help". If there are many subcommants and the help message would be too long, it will be automatically divided into pages. Help message is interactive - you can navigate to different pages simply by clicking on the message text, and if you click on any of the subcommands, it will display help for that subcommand.

If you want to prepare help message yourself, simply create your own subcommand named "help" - default help messages will not be generated if you do.

#### Preserving parameter names

Generated help messages will use method parameter names as command parameter names. By default java compiler discards such information during compilation. You can tell java compiler to preserve parameter names by passing `-parameters` argument to it.

##### Maven

Java compiler command line parameters can be configured in `maven-compiler-plugin` configuration.

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

##### Gradle

Simply add this in your `build.gradle` file.

```gradle
tasks.withType(JavaCompile) {
    options.compilerArgs << '-parameters'
}
```

### Varargs

If the last parameter of your subcommand method is an array, all the remaining parameters will be converted into the array component type and passed to that parameter. If you use `@Adapter` or `@TabComplete` annotations for this parameter, they will be applied to each element of the array.

```java
@SubCommand("vararg1")
@Description(shortDescription = "Varargs test", detailedDescription = "Varargs test")
public void vararg1(@Sender CommandSender commandSender, int num, double[] doubles) {
    commandSender.sendMessage("num=" + num + ";doubles=" + Arrays.toString(doubles));
}

@SubCommand("vararg2")
@Description(shortDescription = "Vararg test 2", detailedDescription = "Vararg test 2")
public void vararg2(@Sender CommandSender commandSender, String someText, Material[] materials) {
    commandSender.sendMessage("someText=" + someText + ";materials=" + Arrays.toString(materials));
}
```

### Options

You can enable both short (`-a`) and long (`--option`) options in your command. To do that, you will need to annotate a parameter with `@Option` annotation (you can still use `@Adapter` and `@TabComplete` for option parameters). If you annotate a `boolean` or `Boolean` parameter, only the option is passed. If you annotate parameter of a different type, a value has to be passed to it (`-a value`, `--option value`).

Options by definitions are not required and if you do not provide them when executing a command, `null` values will be passed (`false` for booleans). That means you should not use primitive types for option parameter (i.e. use `Integer` instead of `int`, `Long` instead of `long` etc). The only exception is `boolean`.

#### Example

```java
@SubCommand("opt")
@Description(shortDescription = "Options test", detailedDescription = "Options test")
public void opt(@Sender CommandSender commandSender, Material material1, Material material2,
                @Option(shortName = 'a', longName = "all", description = "All option") boolean optA,
                @Option(longName = "list", description = "List option") boolean optL,
                @Option(shortName = 'x', longName = "extra", max = 3, description = "Additional materials") Material[] parX,
                @Option(shortName = 'w', description = "World") World parW) {
    commandSender.sendMessage("material1=" + material1 + ";material2=" + material2 + ";all=" + optA + ";list=" +
            optL + ";extra=" + Arrays.toString(parX) + ";world=" + (parW == null ? "null" : parW.getName()));
}
```

If you use this subcommand:

`/wctest opt --list -x minecraft:oak_planks -x minecraft:birch_planks minecraft:slime_block minecraft:stone`,

you will see the message:

`material1=SLIME_BLOCK;material2=STONE;all=false;list=true;extra=[OAK_PLANKS, BIRCH_PLANKS];world=null`.

The generated help message looks like this:

```
witchcraft-test opt [options] <material1> <material2> - Options test
Options test
Available options:
  -a         --all           All option
             --list          List option
  -x <parX>  --extra <parX>  Additional materials
  -w <parW>                  World
```

#### Multiple values

As you may have already seen in the example, you can pass multiple option values. To do that, use an array as parameter type and set `max` attribute of your `@Option` annotation. Default value is `1`, if you set it to `0` or less, the uses of this option will not be limited. If no values are passed, you will get an empty array.

# JDBC module

WitchCraft comes with its own JDBC module that can be enabled if you need it, but does not need to be included in your classpath. This module makes database connection management very easy. It will create connections for you, it supports named query parameters and greatly simplifies transaction management. And it will work with any JDBC you want! You can use PostgreSQL, MySQL, Oracle, pretty much any database for which a JDBC driver exists.

## Setup

To use any module, you will need to setup dependency injection first. This was explained earlier in this document. When that's done, you will need to add `witchcraft-jdbc` library to your project.

### Maven

Add dependency on `witchcraft-jdbc`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-jdbc</artifactId>
        <version>0.6.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-jdbc`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-jdbc:0.6.0"
}
```

### Setup

Then simply annotate your main plugin class with `@EnableJdbc` annotation. This will add a managed class implementing `javax.sql.DataSource`, which you can use as a dependency in your managed classes. Connection with the database will be created automatically from the provided configuration values. You will need to provide the following configuration values:
* `witchcraft.jdbc.url` - database url
* `witchcraft.jdbc.username` - database user
* `witchcraft.jdbc.password` - database password
* `witchcraft.jdbc.driverClassName` - your database driver class name

Don't forget to include your database driver in the classpath.

For more advanced users, here is a full list of configuration values you can set (all keys use the same prefix `witchcraft.jdbc.`): `defaultAutoCommit`, `defaultReadOnly`, `defaultTransactionIsolation`, `defaultCatalog`, `defaultSchema`, `cacheState`, `driverClassName`, `lifo`, `maxTotal`, `maxIdle`, `minIdle`, `initialSize`, `maxWaitMillis`, `testOnCreate`, `testOnBorrow`, `testOnReturn`, `timeBetweenEvictionRunsMillis`, `numTestsPerEvictionRun`, `minEvictableIdleTimeMillis`, `softMinEvictableIdleTimeMillis`, `evictionPolicyClassName`, `testWhileIdle`, `password`, `url`, `username`, `validationQuery`, `validationQueryTimeout`, `jmxName`, `connectionFactoryClassName`, `connectionInitSqls`, `accessToUnderlyingConnectionAllowed`, `removeAbandonedOnBorrow`, `removeAbandonedOnMaintenance`, `removeAbandonedTimeout`, `logAbandoned`, `abandonedUsageTracking`, `poolPreparedStatements`, `maxOpenPreparedStatements`, `connectionProperties`, `maxConnLifetimeMillis`, `logExpiredConnections`, `rollbackOnReturn`, `enableAutoCommitOnReturn`, `defaultQueryTimeout`, `fastFailValidation`. Their purpose is described [here](https://commons.apache.org/proper/commons-dbcp/configuration.html).

Example configuration file used to connect to PostgreSQL database:
```yml
witchcraft:
  jdbc:
    url: jdbc:postgresql://localhost:5432/witchcraft
    username: witchcraft
    password: witchcraft
    driverClassName: myplugin.shaded.org.postgresql.Driver
```

## Statement template

JDBC module encourages the use of built-in `StatementTemplate` class. If you use it, you will no longer need to deal with establishing and closing connections, preparing and closing statements and other database-related operations cluttering your code.

The class comes with the following methods:
* `<T> List<T> query(Query query, RowMapper<T> rowMapper)` - execute select query and map the result to a list of objects using given row mapper to map from database columns to java objects
* `<T> List<T> query(Query query, Class<T> requestedType)` - execute a single column select query and map the result to a list of objects of the requested type
* `ResultSet query(Query query)` - execute query and get the result set
* `<T> T queryForObject(Query query, RowMapper<T> rowMapper)` - query for a single object if you're sure your query will return exactly one row
* `<T> T queryForObject(Query query, Class<T> requestedType)` - query for a single object if you're sure your query will return exactly one row
* `int update(Query query)` - execute update or insert query, return updated rows count
* `<T> List<T> update(Query query, RowMapper<T> rowMapper, String... autoGeneratedColumnNames)` - execute insert query and retrieve automatically generated data (e.g. auto-increment columns); some drivers require `autoGeneratedColumnNames` to be passed to this method to provide names of the columns that contain automatically generated data
* `<T> List<T> update(Query query, Class<T> requestedType, String... autoGeneratedColumnNames)` - execute insert query and retrieve automatically generated data (e.g. auto-increment columns); some drivers require `autoGeneratedColumnNames` to be passed to this method to provide names of the columns that contain automatically generated data
* `<T> T executeStatement(StatementCallback<T> statementCallback)` - execute statement, the callback is a functional interface in which you can deal with already created statement
* `<T> T execute(String sql, CallableStatementCallback<T> callableStatementCallback)` - execute callable statement, the callback is a functional interface in which you can deal with already prepared call
* `<T> T execute(String sql, PreparedStatementCallback<T> preparedStatementCallback)` - execute prepared statement, the callback is a functional interface in which you can deal with already prepared statement

## Query

This class supports queries with parameters just like prepared statements do, but it also supports named parameters, so that you can write queries like this: `INSERT INTO USER_DATA (username, data) VALUES (:user, :data)`. Then simply set parameter values and execute query:
```java
Query query = new Query("INSERT INTO USER_DATA (username, data) VALUES (:user, :data)");
query.setParameter("user", user);
query.setParameter("data", data);
List<Integer> autoGeneratedIds = statementTemplate.update(query, Integer.class);
```

## Transactions

This module comes with automated transaction management. If you want to do something inside a database transaction, simply annotate your method with `@Transactional`. A transaction will be automatically started before that method is invoked and it will automatically be commited after the invocation (or rolled back if an exception occurs). You can provide specific transaction isolation leven in this annotation, as well as control how the transactions are created (whether a new transaction should be created every time it's invoked, or existing transactions should be used, or any existing transactions should be suspended and the method should not use transactions). You can also provide exception types for which the transaction should be rolled back (by default it rolls back for all exceptions).

**Warning:** this will not work if your class implements `Aspect` or `Listener` interfaces (which it should not, because database operations should be invoked asynchronously anyway).

## Shading

As explained later in this document, you should shade your jar when using external libraries. If you use this module, you will also need to relocate, apart from packages mentioned there, the following packages:
* `org.apache.commons.dbcp2`
* `org.apache.commons.pool2`
* `org.apache.commons.logging`
* your database driver

# Plugin.yml generation

If you want to save yourself some time and avoid some of the boring work while developing your plugins, this is a feature you need. WitchCraft will generate your `plugin.yml` for you! Of course you still need to provide all the required data for generation, but it is now much easier and quicker.

## Setup

You will need to add `witchcraft-plugin-generator` annotation processor to your project.

### Maven

Add dependency on `witchcraft-plugin-generator`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-plugin-generator</artifactId>
        <version>0.6.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-plugin-generator`:
```gradle
dependencies {
    annotationProcessor "com.ivan1pl.witchcraft:witchcraft-plugin-generator:0.6.0"
}
```

## Standard data

Nothing very fancy here, this data just needs to be passed in some way, it will not appear out of nowhere. The real good thing about this is that this is all you need:

```java
@PluginData(
        name = "witchcraft-example",
        version = "0.3.0",
        description = "Example plugin built with WitchCraft Framework",
        apiVersion = "1.13",
        authors = "Ivan1pl",
        permissions = {
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.*",
                        description = "Default permission",
                        defaultValue = PermissionDefault.TRUE,
                        children = {
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.any",
                                        inherit = false),
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.player",
                                        inherit = false),
                                @ChildPermission(
                                        node = "witchcraft-example-plugin.witchcraft-test.console",
                                        inherit = false)
                        }),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.any",
                        description = "Permission to execute /witchcraft-test any-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.player",
                        description = "Permission to execute /witchcraft-test player-sender",
                        defaultValue = PermissionDefault.TRUE),
                @Permission(
                        node = "witchcraft-example-plugin.witchcraft-test.console",
                        description = "Permission to execute /witchcraft-test console-sender",
                        defaultValue = PermissionDefault.TRUE)
        })
public class WitchCraftExamplePlugin extends WitchCraftPlugin {
}
```

This looks like a lot, but you would need to write all that in your `plugin.yml` anyway. But if you look closely, you'll notice that there are no commands here. And there is no need for them to be! The framework will find all the classes you annotate with `@Command` annotation and generate `commands` section of the file from them, filled with descriptions, auto-generated usage and permissions.

Let's go through all the other data you can pass to the generator with `@PluginData` annotation:
* `name` - this is your plugin's name
* `version` - this is your plugin's version
* `description` - this is your plugin's description
* `apiVersion` - this is the version of Bukkit API you use
* `loadStage` - this is the server load stage when the plugin should be loaded; possible values are `LoadStage.STARTUP` and `LoadStage.POSTWORLD`, default is `LoadStage.POSTWORLD`
* `authors` - this is an array of plugin's authors
* `website` - this is plugin's or author's website
* `depend` - this is an array of plugins needed by this plugin to load
* `prefix` - this is a prefix that will be used in all console log messages instead your plugin's name if you provide it
* `softDepend` - this is an array of plugins needed by this plugin to have full functionality
* `loadBefore` - this is an array of plugins that should be loaded after this plugin
* `permissions` - permissions supported by this plugin

## Permissions

Permissions are structured in the same way as in the `plugin.yml` file: you need to provide root nodes (using `@Permission` annotation) and then you can provide a list of child permissions for all root permissions.

`@Permission` annotation accepts the following data:
* `node` - permission node name
* `description` - a short description of the permission
* `defaultValue` - default permission value; possible values are `PermissionDefault.DEFAULT`, `PermissionDefault.TRUE`, `PermissionDefault.FALSE`, `PermissionDefault.OP` and `PermissionDefault.NOT_OP`
* `children` - array of child nodes of this permission

Child permission data consists of only two informations: permission node name (`node`) and an information whether to inherit parent permission (`inherit`).

# Other features

WitchCraft Framework comes with several other features, mostly added to be used within other features of the project. However, you can use them separately, you don't even need to include libraries containing those other features, thus keeping your `jar` file small.

## Setup

You will need to add `witchcraft-core` library to your project. If you use other libraries of this framework with tools like `maven` or `gradle`, this library will be automatically included and you don't have to do anything.

### Maven

Add dependency on `witchcraft-core`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-core</artifactId>
        <version>0.6.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-core`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-core:0.6.0"
}
```

## Features

This is a (still growing) list of core features of this framework.

### MessageBuilder and RawMessageBuilder

MessageBuilder provides a convenient way to build simple messages.

Example:
```java
player.sendMessage(new MessageBuilder()
    .color(ChatColor.AQUA).append("Use ")
    .color(ChatColor.DARK_RED).append(commandName).append(" help <")
    .color(ChatColor.RED).append("subcommand name").color(ChatColor.DARK_RED).append(">")
    .color(ChatColor.AQUA).append(" to display detailed help for specific subcommand.")
    .build());
```

RawMessageBuilder provides a convenient way to build complex JSON messages.

Example:
```java
MessageUtils.sendJsonMessage(player, new RawMessageBuilder()
    .color(ChatColor.AQUA).append("Click <")
    .textHover("Click to execute command")
        .action(ClickEvent.Action.RUN_COMMAND, "/some-command")
            .color(ChatColor.RED).append("here")
        .end()
    .end()
    .color(ChatColor.AQUA).append("> to execute some-command.")
    .toString());
```

# Building your plugin
When you use external libraries in your plugin development, remember one important principle: **ALWAYS** shade your jar. This way you avoid any issues that may arise if more than one plugin uses the same library.

## Maven

To properly shade this library using maven tool, simply include this section in your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.3</version>
            <executions>
                <!-- Run shade goal on package phase -->

                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>com.ivan1pl.witchcraft</pattern>
                        <shadedPattern>{plugin name}.com.ivan1pl.witchcraft</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>org.reflections</pattern>
                        <shadedPattern>{plugin name}.org.reflections</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>javassist</pattern>
                        <shadedPattern>{plugin name}.javassist</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Replace `{plugin name}` with your plugin's name and that's it.

## Gradle

To properly shade this library using gradle tool, include this in your `build.gradle`:

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '5.2.0'
    id 'java'
    // other plugins you want to use
}
```

Then configure relocations and replace `jar` task with `shadowJar`:

```gradle
shadowJar {
    classifier = ''
    relocate 'com.ivan1pl.witchcraft', '{plugin name}.com.ivan1pl.witchcraft'
    relocate 'org.reflections', '{plugin name}.org.reflections'
    relocate 'javassist', '{plugin name}.javassist'
}

jar {
    enabled = false
    dependsOn(shadowJar)
}
```

Replace `{plugin name}` with your plugin's name and that's it.

# Issues and suggestions

If you find any issues or suggestion for new features, please report them here: [[issues](https://github.com/Ivan1pl/witchcraft/issues)]
