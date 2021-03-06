package com.renker.junit;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:spring.xml"})
public class AbstractTest implements Base{
	
	@Resource
	protected RepositoryService repositoryService;
	
	@Resource
	protected RuntimeService runtimeService;
	
	@Resource
	protected TaskService taskService;
	
	@Resource
	protected HistoryService historyService;
	
	@Resource
	protected IdentityService identityService;
	
	@Resource
	protected ProcessEngineFactoryBean processEngineFactoryBean;
	
	@Resource
	protected FormService formService;

	@Override
	public void deploy(String processDefinitionKey,String resource) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
		if(processDefinition != null){
			List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinition.getId()).listPage(0, 100);
			for (ProcessInstance processInstance : processInstances) {
				runtimeService.deleteProcessInstance(processInstance.getId(), "废弃");
			}
			repositoryService.deleteDeployment(processDefinition.getDeploymentId());
		}
		
		repositoryService.createDeployment().addClasspathResource(resource).deploy();
	}
	
}
