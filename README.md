# WitchCraft
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

# Commands

WitchCraft Framework provides a very simple way of defining commands using annotated classes and functions. The framework will take care of everything: annotated classes will be automatically registered and added to command
executor.

## Setup

You can either let WitchCraft do all the work for you, or in more advanced cases you can manually tell it to enable command management.

### Automatic setup

You will need to add `witchcraft-plugin` library to your project.

#### Maven

Add dependency on `witchcraft-plugin`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-plugin</artifactId>
        <version>0.4.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

#### Gradle

Add dependency on `witchcraft-plugin`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-plugin:0.4.0"
}
```

#### Setup

Then simply extend `WitchCraftPlugin` instead of `JavaPlugin` in your main plugin class. That's it.

If you want to put your command in a different package tree (i.e. neither in the same package nor in any of its subpackages) from the main plugin class, use `@Plugin` annotation on your main plugin class to tell the framework where to search for them.

In most cases, this is all you need:
```java
public class WitchCraftExamplePlugin extends WitchCraftPlugin {
}
```

### Manual setup

If for some reason you want to manually enable command management (for example if you want to deal with some configuration before the commands are enabled), you can use `AnnotationBasedCommandExecutor` class.

You will need to add `witchcraft-commands` library to your project.

#### Maven

Add dependency on `witchcraft-commands`:
```xml
<dependencies>
    <dependency>
        <groupId>com.ivan1pl.witchcraft</groupId>
        <artifactId>witchcraft-commands</artifactId>
        <version>0.4.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

#### Gradle

Add dependency on `witchcraft-commands`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-commands:0.4.0"
}
```

#### Setup

This is how you can use `AnnotationBasedCommandExecutor`:

```java
public class SomePlugin extends JavaPlugin {
    private AnnotationBasedCommandExecutor annotationBasedCommandExecutor;

    @Override
    public void onEnable() {
        super.onEnable();

        // (...) do some stuff you want to deal with before the commands are enabled

        try {
            annotationBasedCommandExecutor = new AnnotationBasedCommandExecutor(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize command executor, the plugin will not be enabled\n" +
                    ExceptionUtils.getFullStackTrace(e));
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (annotationBasedCommandExecutor != null) {
            annotationBasedCommandExecutor.disable();
            annotationBasedCommandExecutor = null;
        }
    }
}
```

Don't forget to use `setEnabled(false)` if there is an error, this way you will not miss any failures because the plugin simply will not load in such cases. Otherwise it will appear as loaded but none of the commands will work.

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
* `Player`

But even here, if a type is not yet supported, the framework provides a way to use it. You can use the annotation `@Adapter` and create a class implementing the `TypeAdapter` interface. The interface contains only one method `Object convert(String arg)` which provides a way to convert a `String` value to your desired type.

### Tab completion

The framework will automatically complete only subcommand names and player names (more types will be added in the future). However, it is very easy to provide possible tab completions to any other parameter: simply create a class implementing `TabCompleter` interface (it contains only one method `Set<String> getSuggestions(String partial)` returning all suggestions for given partial value) and annotate the parameter with `@TabComplete` annotation.

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
        <version>0.4.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-plugin-generator`:
```gradle
dependencies {
    annotationProcessor "com.ivan1pl.witchcraft:witchcraft-plugin-generator:0.4.0"
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
        <version>0.4.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Gradle

Add dependency on `witchcraft-core`:
```gradle
dependencies {
    compile "com.ivan1pl.witchcraft:witchcraft-core:0.4.0"
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
