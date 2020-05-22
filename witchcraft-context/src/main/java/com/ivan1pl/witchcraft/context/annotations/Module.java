package com.ivan1pl.witchcraft.context.annotations;

import com.ivan1pl.witchcraft.context.proxy.Aspect;

import java.lang.annotation.*;

/**
 * Annotation used to mark another annotation as able to enable WitchCraft module. That annotation should then be put on
 * your plugin's main class to enable the module. That annotation's package will be used as module's package scan path.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Module {
    /**
     * Aspects declared by this module. All aspects should be managed classes and should be placed within package scan
     * path.
     */
    Class<? extends Aspect>[] aspects() default {};

    /**
     * Other modules required for this module to work.
     */
    Class<? extends Annotation>[] uses() default {};
}
