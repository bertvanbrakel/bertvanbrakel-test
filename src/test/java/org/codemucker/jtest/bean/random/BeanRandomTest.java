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

import static org.codemucker.jtest.TestUtils.sorted;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map.Entry;

import org.codemucker.jtest.bean.BeanException;
import org.codemucker.jtest.bean.TstBeanIgnoreProperty;
import org.junit.Assert;
import org.junit.Test;


public class BeanRandomTest {

	@Test
	public void test_no_arg_ctor() {
		TstBeanNoArgCtor bean = new BeanRandom().populate(TstBeanNoArgCtor.class);
		assertNotNull(bean);
	}

	@Test
	public void test_private_no_arg_ctor() {
		TstBeanPrivateNoArgCtor bean = new BeanRandom().populate(TstBeanPrivateNoArgCtor.class);
		assertNotNull(bean);
	}

	@Test
	public void test_multi_arg_ctor() {
		TstBeanMultiArgCtor bean = new BeanRandom().populate(TstBeanMultiArgCtor.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldA());
		assertNotNull(bean.getFieldB());
	}

	@Test
	public void test_fields_populated_via_setters() {
		TstBeanSetters bean = new BeanRandom().populate(TstBeanSetters.class);
		assertNotNull(bean);

		String[] expectFieldsSet = { "boolean", "byte", "char", "short", "int", "long", "double", "float", "String",
		        "Boolean", "Byte", "Character", "Short", "Integer", "Long", "Double", "Float", "BigDecimal",
		        "BigInteger" };

		assertEquals(sorted(expectFieldsSet), sorted(bean.fieldToValues.keySet()));

		for (Entry<String, Object> entry : bean.fieldToValues.entrySet()) {
			assertNotNull("expected vale for field " + entry.getKey(), entry.getValue());
		}
	}

	@Test
	public void test_array_property() {
		TstBeanArray bean = new BeanRandom().populate(TstBeanArray.class);
		assertNotNull(bean);
		assertArrayIsPopulated(bean.getStringArray());
		assertArrayIsPopulated(bean.getFloatArray());
		assertArrayIsPopulated(bean.getIntegerArray());
		assertArrayIsPopulated(bean.getCharArray());
		assertArrayIsPopulated(bean.getDoubleArray());
		assertArrayIsPopulated(bean.getBigDecimalArray());
		assertArrayIsPopulated(bean.getBigIntegerArray());
		assertArrayIsPopulated(bean.getBooleanArray());

		assertArrayIsPopulated(bean.getBeanArray());
	}

	@Test
	public void test_enum_property() {
		TstBeanEnum bean = new BeanRandom().populate(TstBeanEnum.class);
		assertNotNull(bean);
		assertNotNull(bean.getEnumField());
	}

	@Test
	public void test_infinite_recursion_passes() {
		// TODO:set option = no fail
		BeanRandom tester = new BeanRandom();
		tester.getOptions().failOnRecursiveBeanCreation(false);

		TstBeanSelf bean = tester.populate(TstBeanSelf.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldSelf());
		assertNull(bean.getFieldSelf().getFieldSelf());
	}

	@Test
	public void test_complex_property() {
		TstBeanComplexProperty bean = new BeanRandom().populate(TstBeanComplexProperty.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldComplex());
		assertNotNull(bean.getFieldComplex().getFieldA());
		assertNotNull(bean.getFieldComplex().getFieldB());
	}

	@Test
	public void test_ignore_property() {
		BeanRandom tester = new BeanRandom();
		tester.getOptions()
		.failOnRecursiveBeanCreation(false)
		.ignoreProperty("fieldB");

		TstBeanIgnoreProperty bean = tester.populate(TstBeanIgnoreProperty.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldA());
		assertNull(bean.getFieldB());
		assertNotNull(bean.getFieldC());
	}

	@Test
	public void test_ignore_deep_property() {
		BeanRandom tester = new BeanRandom();
		tester.getOptions()
			.failOnRecursiveBeanCreation(false)
			.ignoreProperty("fieldC.fieldB")
		// .ignoreProperty(TstBeanIgnoreProperty.class, "fieldA")
		// .ignoreProperty("*A")

		        ;

		TstBeanIgnoreProperty bean = tester.populate(TstBeanIgnoreProperty.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldA());
		assertNotNull(bean.getFieldB());
		assertNotNull(bean.getFieldC());
		assertNotNull(bean.getFieldC().getFieldA());
		assertNull(bean.getFieldC().getFieldB());
		assertNull(bean.getFieldC().getFieldC());
	}

	@Test
	public void test_ignore_field_on_bean_type() {
		BeanRandom tester = new BeanRandom();
		tester.getOptions().ignoreProperty(TstBeanIgnoreProperty.class, "fieldA")
		        .ignoreProperty(TstBeanIgnoreProperty.class, "fieldC");

		TstBeanIgnoreBeanPropertyType bean = tester.populate(TstBeanIgnoreBeanPropertyType.class);
		assertNotNull(bean);
		assertNotNull(bean.getFieldA());
		assertNotNull(bean.getFieldB());
		assertNotNull(bean.getFieldB().getFieldB());

		assertNull(bean.getFieldB().getFieldA());
		assertNull(bean.getFieldB().getFieldC());
	}

	@Test
	public void test_infinite_recursion_fails() {
		try {
			TstBeanSelf bean = new BeanRandom().populate(TstBeanSelf.class);
			Assert.fail("Expected exception");
		} catch (BeanException e) {
			assertMsgContainsAll(e, "fieldSelf", "recursive", TstBeanSelf.class.getName());
		}
	}

	private void assertMsgContainsAll(Throwable t, String... text) {
		assertNotNull("Expected error message", t.getMessage());
		String msg = t.getMessage().toLowerCase();
		for (String s : text) {
			s = s.toLowerCase();
			if (!msg.contains(s)) {
				fail(String.format("Expected message '%s' to contain '%s'", t.getMessage(), s));
			}
		}
	}

	private <T> void assertArrayIsPopulated(T[] arr) {
		assertNotNull(arr);
		assertTrue("expected atleast one item in the array", arr.length > 0);
		for (T ele : arr) {
			assertNotNull("array element is null", ele);
		}
	}

}
