package com.renker.junit.chapter6;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import com.renker.junit.AbstractTest;

public class LeaveDynamicFormTest extends AbstractTest {
	
	@Test
	public void deploy(){
		repositoryService.createDeployment().addClasspathResource("com/renker/activiti/chapter6/leave.bpmn").deploy();
	}
	
	@Test
	public void allApproved(){
		String currentUserId = "caishenchen";
		// 设置当前用户
		identityService.setAuthenticatedUserId(currentUserId);
		
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave").singleResult();
		
		Map<String, String> variables = new HashMap<String, String>();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String startDate = df.format(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, 2);
		String endDate = df.format(c.getTime());
		
		variables.put("startDate", startDate);
		variables.put("endDate", endDate);
		variables.put("reason", "公休");
		
		// 启动流程
		ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), variables);
		
		// 部门领导审批通过
		
		Task jackTask = taskService.createTaskQuery().taskCandidateUser("jackchen").singleResult();
		
		Task deptLeaderTask = taskService.createTaskQuery().taskCandidateGroup("deptLeader").singleResult();
		variables.clear();
		variables.put("deptLeaderApproved", "true");
		formService.submitTaskFormData(deptLeaderTask.getId(), variables);
		
		// 人事审批通过
		Task hrTask = taskService.createTaskQuery().taskCandidateGroup("hr").singleResult();
		variables.clear();
		variables.put("hrApproved", "true");
		formService.submitTaskFormData(hrTask.getId(), variables);
		
		// 销假
		Task reportBackTask = taskService.createTaskQuery().taskAssignee(currentUserId).singleResult();
		variables.clear();
		variables.put("reportBackDate", endDate);
		formService.submitTaskFormData(reportBackTask.getId(), variables);
		
		List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().finished().listPage(0, 100);
		for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
			System.out.println("已完成 ："+historicProcessInstance.getName());
		}
		
		// 打印参数
		packageVariables(processInstance);
	}
	
	private void packageVariables(ProcessInstance processInstance){
		List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery().processInstanceId(processInstance.getId()).list();
		
		for (HistoricDetail historicDetail : historicDetails) {
			if (historicDetail instanceof HistoricFormProperty) {
				HistoricFormProperty property = (HistoricFormProperty) historicDetail;
				System.out.println("form field: "+property.getId() + "\t name: "+property.getPropertyId()+"\t value: "+property.getPropertyValue());
			}else if(historicDetail instanceof HistoricVariableUpdate){
				HistoricVariableUpdate entity = (HistoricVariableUpdate) historicDetail;
				System.out.println("form field: "+entity.getId() + "\t name: "+entity.getVariableName()+"\t value: "+entity.getValue());
			}
		}
	}
}
