package com.renker.junit.chapter9;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.task.Task;
import org.junit.Test;

import com.renker.junit.AbstractTest;

public class TestMultiInstanceForUserTask extends AbstractTest{
	
	@Test
	public void deploy(){
		super.deploy("testMultiInstanceForUserTask", "com/renker/activiti/chapter9/testMultiInstanceForUserTask.bpmn");
	}
	
	@Test
	public void testMultiInstanceForUserTask(){
		
		Map<String, Object> variables = new HashMap<String, Object>();
		List<String> users = Arrays.asList("user1","user2","user3");
		variables.put("users", users);
		runtimeService.startProcessInstanceByKey("testMultiInstanceForUserTask", variables);
		
		for (String user : users) {
			Task task = taskService.createTaskQuery().taskAssignee(user).singleResult();
			taskService.complete(task.getId());
		}
		
		System.out.println("**********************************");
		
	}
}
