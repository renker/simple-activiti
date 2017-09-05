package com.renker.junit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:spring.xml"})
public class LeaveTest {
	@Resource
	private RepositoryService repositoryService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource
	private ProcessEngineFactoryBean processEngineFactoryBean;
	
	@Test
	public void sayhelloleave(){
		
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().listPage(0, 100);
		
		for (ProcessDefinition processDefinition : processDefinitions) {
			repositoryService.deleteDeployment(processDefinition.getDeploymentId());
		}
		
		// 部署流程定义文件
		repositoryService.createDeployment().addClasspathResource("com/renker/activiti/hellow/leave.bpmn").deploy();
		
		// 获取流程定义
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
		
		System.out.println(processDefinition.getKey());
		
		// 启动流程并返回流程实例
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");
				
		System.out.println(processInstance.getId()+" "+processInstance.getProcessDefinitionId());
	}
	
	@Test
	public void SayHelloToLeave(){
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().listPage(0, 100);
		
		for (ProcessDefinition processDefinition : processDefinitions) {
			repositoryService.deleteDeployment(processDefinition.getDeploymentId());
		}
		
		// 部署流程定义文件
		repositoryService.createDeployment().addClasspathResource("com/renker/activiti/hellow/SayHellowToLeave.bpmn").deploy();
		
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
		
		System.out.println(processDefinition.getKey());
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("applyUser", "employee1");
		variables.put("days", 3);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("SayHelloToLeave", variables);
		
		Task taskOfDeptLeader = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
		
		System.out.println("领导审批   \t"+taskOfDeptLeader.getName());
		
		taskService.claim(taskOfDeptLeader.getId(), "leaderUser");
		
		variables.clear();
		
		variables.put("approved", true);
		
		taskService.complete(taskOfDeptLeader.getId(), variables);
		
		long count =  historyService.createHistoricProcessInstanceQuery().finished().count();
		
		System.out.println("完成数量  "+count);
	}
}
