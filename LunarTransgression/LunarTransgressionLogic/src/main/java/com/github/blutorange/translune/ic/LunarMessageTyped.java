package com.github.blutorange.translune.ic;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.github.blutorange.translune.socket.ELunarMessageType;

@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LunarMessageTyped {
	ELunarMessageType value();
}
