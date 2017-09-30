package com.renker.junit.chapter9;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import com.renker.junit.AbstractTest;

public class TestMultiInstanceFixedNumbers extends AbstractTest{
	
	@Test
	public void deploy(){
		repositoryService.createDeployment().addClasspathResource("com/renker/activiti/chapter9/testMultiInstanceFixedNumbers.bpmn").deploy();
	}

	@Test
	public void TestMultiInstanceFixedNumbers(){
		
		Map<String, Object> variables = new HashMap<String, Object>();
		long loop = 3;
		variables.put("loop", loop);
		variables.put("counter", 0);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testMultiInstanceFixedNumbers", variables);
		
		System.out.println("-------------");
	}
}
