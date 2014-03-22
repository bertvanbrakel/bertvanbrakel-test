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
package org.codemucker.jtest.bean.random;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.codemucker.jtest.bean.BeanException;


public class CollectionGenerator implements RandomGenerator {

	private final Random RANDOM = new Random();

	private final RandomGenerator<?> provider;

	public CollectionGenerator(RandomGenerator<?> provider) {
		this.provider = provider;
	}

	@Override
	public Object generateRandom(Class beanClass, String propertyName, Class propertyType, Type genericType) {
		try {
			if (propertyType.isArray()) {
				int randomLen = randomLen();
				Object[] array = (Object[]) Array.newInstance(propertyType.getComponentType(), randomLen);
				fillArray(beanClass, propertyName, array, propertyType.getComponentType());
				return array;
			} else {
				Collection<?> col = createCollectionOfType(propertyType);
				fillCollection(beanClass, propertyName, col, genericType);
				return col;
			}
		} catch (BeanException e) {
			throw new BeanException("Error creating collection or array for property '%s' of type %s on bean %s", e,
			        propertyName, propertyType.getName(), beanClass.getName());
		}
	}
	
	private Collection<?> createCollectionOfType(Class propertyType) {
		if (Collection.class.equals(propertyType)) {
			return new ArrayList();
		}
		if (List.class.equals(propertyType)) {
			return new ArrayList();
		}
		if (Set.class.equals(propertyType)) {
			return new HashSet();
		}
		if (Collection.class.isAssignableFrom(propertyType)) {
			try {
				Collection col = (Collection) propertyType.newInstance();
				return col;
			} catch (InstantiationException e) {
				throw new BeanException("Don't know how to create collection of type %s", e, propertyType.getName());
			} catch (IllegalAccessException e) {
				throw new BeanException("Don't know how to create collection of type %s", e, propertyType.getName());
			}
		} else {
			throw new BeanException("Don't know how to create collection of type %s", propertyType.getName());
		}
	}

	public <T extends Collection> void fillCollection(Class beanClass, String propertyName, T col, Type genericType) {
		Class<?> elementType = extractConcreteType(genericType);
		if (elementType == null) {
			throw new BeanException("Can't create collection elements using non concrete type:" + genericType);
		}
		int randomLen = randomLen();
		for (int i = 0; i < randomLen; i++) {
			Object eleVal = provider.generateRandom(beanClass, propertyName, elementType, null);
			col.add(eleVal);
		}
	}

	public void fillArray(Class beanClass, String propertyName, Object[] arr, Class<?> elementType) {
		for (int i = 0; i < arr.length; i++) {
			Object eleVal = provider.generateRandom(beanClass, propertyName, elementType, null);
			arr[i] = eleVal;
		}
	}

	private Class<?> extractConcreteType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			if (pType.getActualTypeArguments().length == 1) {
				Type subType = pType.getActualTypeArguments()[0];
				if (subType instanceof Class) {
					return (Class) subType;
				}

			}
		}
		return null;
	}

	private int randomLen() {
		return 1 + RANDOM.nextInt(10);
	}
}