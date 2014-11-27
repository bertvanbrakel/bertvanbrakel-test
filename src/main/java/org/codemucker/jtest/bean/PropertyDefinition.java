/*
 * Copyright 2011 Bert van Brakel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codemucker.jtest.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PropertyDefinition {
	
	private String name;
	private Class<?> type;
	private Type genericType;
	private boolean ignore;;
	private boolean makeAccessible = false;

	private Field field;
	private Method read;
	private Method write;

	public boolean hasMutator() {
		return write != null || field != null;
	}
	
	public boolean isMakeAccessible() {
    	return makeAccessible;
    }

	public void setMakeAccessible(boolean makeAccessible) {
    	this.makeAccessible = makeAccessible;
    }

	public Field getField() {
    	return field;
    }

	public void setField(Field field) {
    	this.field = field;
    }

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public Method getRead() {
		return read;
	}

	public void setRead(Method read) {
		this.read = read;
	}

	public Method getWrite() {
		return write;
	}

	public void setWrite(Method write) {
		this.write = write;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Type getGenericType() {
		return genericType;
	}

	public void setGenericType(Type genericType) {
		this.genericType = genericType;
	}

	public boolean isString(){
		return String.class == type;
	}
	
	public boolean isPrimitive(){
		return type.isPrimitive();
	}
	
	public boolean isType(Class<?> type) {
		return type.isAssignableFrom(this.type);
	}

	public boolean isType(String typeName) {
		return this.type.getName().equals(typeName);
	}

	public boolean isIndexed() {
		return type.isArray() || Collection.class.isAssignableFrom(type);
	}

	public boolean isArray(){
		return type.isArray();
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
}