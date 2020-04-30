package com.ivan1pl.witchcraft.plugin.generator;

import com.ivan1pl.witchcraft.commands.annotations.Command;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.commands.annotations.Optional;
import com.ivan1pl.witchcraft.commands.annotations.Sender;
import com.ivan1pl.witchcraft.commands.annotations.SubCommand;
import com.ivan1pl.witchcraft.core.annotations.ChildPermission;
import com.ivan1pl.witchcraft.core.annotations.Permission;
import com.ivan1pl.witchcraft.core.annotations.PermissionDefault;
import com.ivan1pl.witchcraft.core.annotations.PluginData;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate {@code plugin.yml} file from annotations.
 */
public class PluginGenerator extends AbstractProcessor {
    /**
     * Process annotations.
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, Object> pluginFile = new LinkedHashMap<>();

        Set<? extends Element> pluginElements = roundEnvironment.getElementsAnnotatedWith(PluginData.class);
        if (pluginElements == null || pluginElements.size() != 1) {
            return false;
        }
        Element pluginElement = pluginElements.iterator().next();
        PluginData pluginAnnotation = pluginElement.getAnnotation(PluginData.class);
        pluginFile.put("main", pluginElement.asType().toString());
        pluginFile.put("name", pluginAnnotation.name());
        pluginFile.put("version", pluginAnnotation.version());
        if (!pluginAnnotation.description().isEmpty()) {
            pluginFile.put("description", pluginAnnotation.description());
        }
        if (!pluginAnnotation.apiVersion().isEmpty()) {
            pluginFile.put("api-version", pluginAnnotation.apiVersion());
        }
        pluginFile.put("load", pluginAnnotation.loadStage().name());
        if (pluginAnnotation.authors().length == 1) {
            pluginFile.put("author", pluginAnnotation.authors()[0]);
        } else if (pluginAnnotation.authors().length > 1) {
            pluginFile.put("authors", Arrays.asList(pluginAnnotation.authors()));
        }
        if (!pluginAnnotation.website().isEmpty()) {
            pluginFile.put("website", pluginAnnotation.website());
        }
        if (pluginAnnotation.depend().length > 0) {
            pluginFile.put("depend", Arrays.asList(pluginAnnotation.depend()));
        }
        if (!pluginAnnotation.prefix().isEmpty()) {
            pluginFile.put("prefix", pluginAnnotation.prefix());
        }
        if (pluginAnnotation.softDepend().length > 0) {
            pluginFile.put("softdepend", Arrays.asList(pluginAnnotation.softDepend()));
        }
        if (pluginAnnotation.loadBefore().length > 0) {
            pluginFile.put("loadbefore", Arrays.asList(pluginAnnotation.loadBefore()));
        }

        Map<String, Object> permissions = processPermissions(pluginAnnotation.permissions());
        if (permissions != null && !permissions.isEmpty()) {
            pluginFile.put("permissions", permissions);
        }

        Map<String, Object> commands = processCommands(roundEnvironment);
        if (commands != null && !commands.isEmpty()) {
            pluginFile.put("commands", commands);
        }

        try {
            FileObject resource = processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);
            String output = yaml.dump(pluginFile);
            Writer writer = resource.openWriter();
            writer.write(output);
            writer.close();
            return true;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error saving resource.");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, sw.toString());
            return false;
        }
    }

    private Map<String, Object> processPermissions(Permission[] permissions) {
        if (permissions.length == 0) {
            return null;
        }
        Map<String, Object> permissionEntries = new LinkedHashMap<>();
        for (Permission permission : permissions) {
            Map<String, Object> permissionEntry = processPermission(permission);
            if (!permissionEntry.isEmpty()) {
                permissionEntries.put(permission.node(), processPermission(permission));
            }
        }
        return permissionEntries;
    }

    private Map<String, Object> processPermission(Permission permission) {
        Map<String, Object> permissionEntry = new LinkedHashMap<>();

        if (!permission.description().isEmpty()) {
            permissionEntry.put("description", permission.description());
        }

        if (permission.defaultValue() != PermissionDefault.DEFAULT) {
            permissionEntry.put("default", permission.defaultValue().getName());
        }

        Map<String, Object> childPermissionEntries = new LinkedHashMap<>();
        for (ChildPermission childPermission : permission.children()) {
            childPermissionEntries.put(childPermission.node(), childPermission.inherit());
        }
        if (!childPermissionEntries.isEmpty()) {
            permissionEntry.put("children", childPermissionEntries);
        }

        return permissionEntry;
    }

    private Map<String, Object> processCommands(RoundEnvironment roundEnvironment) {
        Map<String, Object> commands = new LinkedHashMap<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Command.class)) {
            Command command = element.getAnnotation(Command.class);
            List<Element> subCommands = element.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD && e.getAnnotation(SubCommand.class) != null)
                    .sorted(Comparator.comparing(e -> e.getAnnotation(SubCommand.class).value()))
                    .collect(Collectors.toList());
            Map<String, Object> commandData = processCommand(command, subCommands);
            commands.put(command.name(), commandData);
        }
        return commands.isEmpty() ? null : commands;
    }

    private Map<String, Object> processCommand(Command command, List<Element> subCommands) {
        Map<String, Object> commandData = new LinkedHashMap<>();
        String description = command.description();
        if (!description.isEmpty()) {
            description += "\n";
        }
        description += String.format("Use '/%s help' for more detailed help.", command.name());
        commandData.put("description", description);
        if (command.aliases().length > 0) {
            commandData.put("aliases", Arrays.asList(command.aliases()));
        }
        if (!command.permission().isEmpty()) {
            commandData.put("permission", command.permission());
            commandData.put("permission-message", "You are not permitted to use this command");
        }
        commandData.put("usage", processSubcommands(command.name(), subCommands));
        return commandData;
    }

    private String processSubcommands(String commandName, List<Element> subCommands) {
        List<String> usageDescriptions = new LinkedList<>();
        boolean namedSubCommands = false;
        boolean help = false;
        for (Element element : subCommands) {
            SubCommand subCommand = element.getAnnotation(SubCommand.class);
            if (!subCommand.value().isEmpty()) {
                namedSubCommands = true;
            }
            if ("help".equalsIgnoreCase(subCommand.value())) {
                help = true;
            }
            List<String> parts = new LinkedList<>();
            parts.add("/" + commandName);
            if (!subCommand.value().isEmpty()) {
                parts.add(subCommand.value());
            }
            List<Element> parameters = ((ExecutableElement) element).getParameters().stream()
                    .filter(e -> e.getAnnotation(Sender.class) == null)
                    .filter(e -> e.getAnnotation(ConfigurationValue.class) == null)
                    .collect(Collectors.toList());
            for (Element parameter : parameters) {
                boolean optional = parameter.getAnnotation(Optional.class) != null;
                parts.add(String.format(optional ? "[%s]" : "<%s>", parameter.getSimpleName()));
            }
            usageDescriptions.add(String.join(" ", parts));
        }
        if (!help) {
            List<String> parts = new LinkedList<>();
            parts.add("/" + commandName);
            parts.add("help");
            if (namedSubCommands) {
                parts.add("[subCommand]");
            }
            usageDescriptions.add(String.join(" ", parts));
        }
        return "Usage:\n" + String.join("\n", usageDescriptions);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(PluginData.class.getCanonicalName());
        annotations.add(Permission.class.getCanonicalName());
        annotations.add(ChildPermission.class.getCanonicalName());
        annotations.add(Command.class.getCanonicalName());
        annotations.add(SubCommand.class.getCanonicalName());
        annotations.add(Sender.class.getCanonicalName());
        annotations.add(ConfigurationValue.class.getCanonicalName());
        annotations.add(Optional.class.getCanonicalName());
        return annotations;
    }
}
