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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BeanOptions {
	private boolean failOnInvalidGetters = false;
	private boolean failOnMissingSetters = false;
	private boolean failOnAdditionalSetters = false;
	private boolean failOnMisMatchingFields = true;

	private boolean extractFields = false;
	
	private boolean makeAccessible = false;

	private Collection<String> ignoreProperties = new HashSet<String>();
	private Map<String, Collection<String>> ignorePropertiesOnClass = new HashMap<String, Collection<String>>();

	public void setFailSilently() {
		failOnAdditionalSetters = false;
		failOnMissingSetters = false;
		failOnInvalidGetters = false;
	}

	public boolean isMakeAccessible() {
	    return makeAccessible;
    }

	public BeanOptions makeAccessible(boolean b) {
	    this.makeAccessible = b;
	    return this;
	}

	public boolean isFailOnInvalidGetters() {
		return failOnInvalidGetters;
	}

	public BeanOptions failOnInvalidGetters(boolean failOnInvalidGetters) {
		this.failOnInvalidGetters = failOnInvalidGetters;
		return this;
	}

	public boolean isFailOnMissingSetters() {
		return failOnMissingSetters;
	}

	public BeanOptions failOnMissingSetters(boolean failOnMissingSetters) {
		this.failOnMissingSetters = failOnMissingSetters;
		return this;
	}

	public boolean isFailOnAdditionalSetters() {
		return failOnAdditionalSetters;
	}

	public BeanOptions failOnAdditionalSetters(boolean failOnAdditionalSetters) {
		this.failOnAdditionalSetters = failOnAdditionalSetters;
		return this;
	}

	public BeanOptions ignoreProperty(String propertyPath) {
		ignoreProperties.add(propertyPath);
		return this;
	}

	public Collection<String> getIgnoreProperties() {
		return ignoreProperties;
	}

	public BeanOptions ignoreProperty(Class<?> beanClass, String propertyName) {
		String key = beanClass.getName();
		Collection<String> propertiesToIgnore = ignorePropertiesOnClass.get(key);
		if (propertiesToIgnore == null) {
			propertiesToIgnore = new HashSet<String>();
			ignorePropertiesOnClass.put(key, propertiesToIgnore);
		}
		propertiesToIgnore.add(propertyName);
		return this;
	}

	public Map<String, Collection<String>> getIgnorePropertiesOnClass() {
		return ignorePropertiesOnClass;
	}

	public boolean isIncludeProperty(Class<?> beanClass, String propertyName, Class<?> propertyType) {
		if (ignoreProperties.contains(propertyName)) {
			return false;
		}
		Collection<String> properties = ignorePropertiesOnClass.get(beanClass.getName());
		if (properties != null) {
			if (properties.contains(propertyName)) {
				return false;
			}
		}
		return true;
	}

	public boolean isFailOnMisMatchingFields() {
		return failOnMisMatchingFields;
	}

	public BeanOptions failOnMisMatchingFields(boolean failOnMisMatchingFields) {
		this.failOnMisMatchingFields = failOnMisMatchingFields;
		return this;
	}

	public boolean isExtractFields() {
		return extractFields;
	}

	public BeanOptions extractFields(boolean extractFields) {
		this.extractFields = extractFields;
		return this;
	}
}