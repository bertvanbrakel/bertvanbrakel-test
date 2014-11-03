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
package org.codemucker.jtest.bean.tester;

import static org.codemucker.jtest.ReflectionUtils.invokeCtorWith;
import static org.codemucker.jtest.ReflectionUtils.invokeMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codemucker.jtest.bean.BeanDefinition;
import org.codemucker.jtest.bean.BeanException;
import org.codemucker.jtest.bean.CtorArgDefinition;
import org.codemucker.jtest.bean.CtorDefinition;
import org.codemucker.jtest.bean.CtorExtractor;
import org.codemucker.jtest.bean.PropertiesExtractor;
import org.codemucker.jtest.bean.PropertyDefinition;
import org.codemucker.jtest.bean.random.BeanRandom;


public class HashCodeEqualsTester {

	private final HashCodeEqualsOptions options;
	private final BeanRandom random;
	private final PropertiesExtractor extractor;
	private final CtorExtractor ctorExtractor;

	public HashCodeEqualsTester() {
		this(new HashCodeEqualsOptions());
	}

	public HashCodeEqualsTester(HashCodeEqualsOptions options) {
		this.options = options;
		this.random = new BeanRandom(options);
		this.extractor = new PropertiesExtractor(options);
		this.ctorExtractor = new CtorExtractor(options);
	}

	public HashCodeEqualsOptions getOptions() {
		return options;
	}

	public void checkHashCodeEquals(Class<?> beanClass) {
		BeanDefinition def = extractor.extractBeanDefWithCtor(beanClass);
		
		Collection<CtorDefinition> ctors = ctorExtractor.extractCtors(beanClass);
		if( options.isTestCtors()){
    		for( CtorDefinition ctorDef:ctors){
    			Constructor<?> ctor = ctorDef.getCtor();
    			Object[] ctorArgs = random.generateRandomArgsForCtor(ctorDef.getCtor());
    			
    			Object bean1 = invokeCtorWith(ctor, ctorArgs);
    			Object bean2 = invokeCtorWith(ctor, ctorArgs);
    			
    			assertBeansEquals("On calling ctor with same args. Ctor=" + ctor.toGenericString(),bean1,bean2);
    			
    			if( options.isTestCtorsModifyEquals()){
    				for( int i = 0; i < ctorArgs.length;i++){
    					CtorArgDefinition argDef = ctorDef.getArg(i);
    					if( !argDef.isNamed() || def.hasNonIgnoredProperty(argDef.getName())){
        					Object orgVal = ctorArgs[i];
        					Object newVal = random.generateRandomNotEqualsTo(orgVal, beanClass, argDef);
        					ctorArgs[i]= newVal;
        					
        					bean2 = invokeCtorWith(ctor, ctorArgs);
        					if( bean1.equals(bean2)){
        						fail(String.format("Expected beans to _not_ equal on calling ctor with different args. Error on arg(%d) of type %s on ctor %s",i, argDef.getType(), ctor.toGenericString()));
        					}
    					}
    				}
    			}
				if (options.isTestCtorsArgsMatchProperties()) {
					for( int i = 0; i < ctorArgs.length;i++){
						CtorArgDefinition argDef = ctorDef.getArg(i);
						if (argDef.isNamed()) {
							PropertyDefinition property = def.getProperty(argDef.getName());
							if (property == null) {
								// TODO make option to skip this?
								throw new BeanException("No property named %s to compare ctor arg[%s] to", argDef.getName(),i);
							}
							if( !property.isIgnore()){		
    							Object orgVal = ctorArgs[i];
    	    					Object argVal = random.generateRandomNotEqualsTo(orgVal, beanClass, argDef);
    	    					ctorArgs[i]= argVal;
    	    					
    	    					bean2 = invokeCtorWith(ctor, ctorArgs);
    	    					Object getterVal = invokeMethod(bean2, property.getRead(), null);
    	    					if( !argVal.equals(getterVal)){
    	    						fail(String.format("Expected bean property '%s' to equal ctor args[%d] for value '%s' on ctor %s, instead got '%s'",property.getName(), i, argVal, ctor.toGenericString(), getterVal));
    	    					}
    	    					ctorArgs[i] = orgVal;
    							}
						}		 		    					
    				}
				}
    		}
		}
		
		if( options.isTestProperties()) {
    		//find the default one...
    		CtorDefinition ctorDefToUse = null;
    		for( CtorDefinition ctorDef:ctors){
    			if( ctorDef.getNumArgs() == 0){
    				//found default!
    				ctorDefToUse = ctorDef;
    			}
    		}
    		if( ctorDefToUse == null ){
    			//find the shortest?
    			for( CtorDefinition ctorDef:ctors){
    				if( ctorDefToUse == null  || ctorDefToUse.getNumArgs() > ctorDef.getNumArgs()){
    					ctorDefToUse = ctorDef;
    				}
    			}	
    		}
    		if( ctorDefToUse == null ){
    			throw new BeanException("No ctor found for %s", beanClass.getName());
    		}
    		Constructor<?> ctor = ctorDefToUse.getCtor();	
    		Object[] ctorArgs = random.generateRandomArgsForCtor(ctor);
    		
    		Object bean1 = invokeCtorWith(ctor, ctorArgs);
    		Object bean2 = invokeCtorWith(ctor, ctorArgs);
    		
    		assertBeansEquals("Expected beans to equal on calling the same ctor", bean1,bean2);
    		
    		//todo:test with no properties set?update eacch in turn?
    		
    		//perform the initial population
			Map<String, Object> orgValues = new HashMap<String, Object>();
			for (PropertyDefinition p : def.getProperties()) {
				Object val = random.generateRandom(beanClass, p);
				orgValues.put(p.getName(), val);

				random.setPropertyWithValue(bean1, p, val);
				random.setPropertyWithValue(bean2, p, val);
			}
			assertBeansEquals("On populating beans with same properties", bean1, bean2);
			//now lets start changing the properties one by one
			for (PropertyDefinition p : def.getProperties()) {
				Object orgVal = orgValues.get(p.getName());
				Object newVal = random.generateRandomNotEqualsTo(orgVal, beanClass, p);
				random.setPropertyWithValue(bean2, p, newVal);
				assertBeansNotEqual("When modifying property " + p.getName(), bean1, bean2);
				random.setPropertyWithValue(bean2, p, orgVal);
				assertBeansEquals("On setting property back on bean", bean1, bean2);
			}
		}
	}
	
	private void assertBeansEquals(String msg, Object bean1,Object bean2){
		if( msg == null){
			msg = "";
		} else {
			msg = msg + ". ";
		}
		if (!bean1.equals(bean2)) {
			fail(String.format(msg + "Expect beans to equal. (%s).equals(%s)", bean1, bean2));
		}
		if (!bean2.equals(bean1)) {
			fail(String.format(msg + "Expect beans to equal. (%s).equals(%s)", bean2, bean1));
		}
		//todo:multiple invokes
		long hashCode1 = bean1.hashCode();
		long hashCode2 = bean2.hashCode();
		
		assertEquals(msg + "Expected the same hashCodes", hashCode1, hashCode2);
		assertEquals(msg + "Expected same hashcode for multiple invocations if no modifications",hashCode1, bean1.hashCode());
		assertEquals(msg + "Expected same hashcode for multiple invocations if no modifications",hashCode2, bean2.hashCode());
		
	}
	private void assertBeansNotEqual(String msg, Object bean1,Object bean2){
		if( msg == null){
			msg = "";
		} else {
			msg = msg + ". ";
		}
	}
}
