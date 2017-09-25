package com.renker.junit;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class Chapter5Test extends AbstractTest{
	
	public void before(){
		// 添加组
		Group group = identityService.newGroup("deptLeader");
		group.setType("assignment");
		group.setName("部门领导");
		identityService.saveGroup(group);
		
		// 添加用户tom
		User tom = identityService.newUser("tomchen");
		tom.setFirstName("tom");
		tom.setLastName("chen");
		tom.setEmail("tom@163.com");
		identityService.saveUser(tom);
		
		// 添加用户jack
		User jack = identityService.newUser("jackchen");
		jack.setFirstName("jack");
		jack.setLastName("chen");
		jack.setEmail("jack@163.com");
		identityService.saveUser(jack);
		
		// 用户与组建立关系
		identityService.createMembership("tomchen", "deptLeader");
		identityService.createMembership("jackchen", "deptLeader");
	}
	
	public void after(){
		identityService.deleteMembership("tomchen", "deptLeader");
		identityService.deleteMembership("jackchen", "deptLeader");
		
		identityService.deleteGroup("deptLeader");
		
		identityService.deleteUser("tomchen");
		identityService.deleteUser("jackchen");
	}
	
	@Test 
	public void userAndGroup(){
		
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().listPage(0, 100);
		
		for (ProcessDefinition processDefinition : processDefinitions) {
			repositoryService.deleteDeployment(processDefinition.getDeploymentId());
		}
		
		repositoryService.createDeployment().addClasspathResource("com/renker/activiti/chapter5/UserAndGroup.bpmn").deploy();
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("userAndGroup");
		System.out.println("processInstance Name : "+processInstance.getProcessDefinitionKey());
		
		Task tomTask = taskService.createTaskQuery().taskCandidateUser("tomchen").singleResult();
		
		Task jackTask = taskService.createTaskQuery().taskCandidateUser("jackchen").singleResult();
		
		if(tomTask == null || jackTask == null){
			Task deptLeaderTask = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
			
			taskService.claim(deptLeaderTask.getId(), "leaderUser");
			
			taskService.complete(deptLeaderTask.getId());
		}
		
		taskService.claim(jackTask.getId(), "jackchen");
		
		tomTask = taskService.createTaskQuery().taskCandidateUser("tomchen").singleResult();
		
		taskService.complete(jackTask.getId());
		
		System.out.println("---------------------------------");
	}
	
	@Test
	public void createUserAndGroup(){
		before();
	}
	
	@Test
	public void deleteUserAndGroup(){
		after();
	}
	
	
}
