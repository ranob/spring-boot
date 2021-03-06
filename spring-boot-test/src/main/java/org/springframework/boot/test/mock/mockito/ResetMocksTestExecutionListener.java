/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.test.mock.mockito;

import org.mockito.Mockito;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * {@link TestExecutionListener} to reset any mock beans that have been marked with a
 * {@link MockReset}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ResetMocksTestExecutionListener extends AbstractTestExecutionListener {

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		resetMocks(testContext.getApplicationContext(), MockReset.BEFORE);
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		resetMocks(testContext.getApplicationContext(), MockReset.AFTER);
	}

	private void resetMocks(ApplicationContext applicationContext, MockReset reset) {
		if (applicationContext instanceof ConfigurableApplicationContext) {
			resetMocks((ConfigurableApplicationContext) applicationContext, reset);
		}

	}

	private void resetMocks(ConfigurableApplicationContext applicationContext,
			MockReset reset) {
		ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
		String[] names = beanFactory.getBeanDefinitionNames();
		for (String name : names) {
			BeanDefinition definition = beanFactory.getBeanDefinition(name);
			if (AbstractBeanDefinition.SCOPE_DEFAULT.equals(definition.getScope())) {
				Object bean = beanFactory.getBean(name);
				if (reset.equals(MockReset.get(bean))) {
					Mockito.reset(bean);
				}
			}
		}
		if (applicationContext.getParent() != null) {
			resetMocks(applicationContext.getParent(), reset);
		}
	}

}
