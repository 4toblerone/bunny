package org.rabix.engine.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.rabix.bindings.BindingException;
import org.rabix.bindings.Bindings;
import org.rabix.bindings.BindingsFactory;
import org.rabix.bindings.ProtocolType;
import org.rabix.bindings.model.Context;
import org.rabix.bindings.model.Executable;
import org.rabix.bindings.model.Executable.ExecutableStatus;
import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;
import org.rabix.bindings.model.dag.DAGNode;
import org.rabix.common.helper.InternalSchemaHelper;
import org.rabix.engine.db.DAGNodeDB;
import org.rabix.engine.event.impl.JobStatusEvent;
import org.rabix.engine.model.ContextRecord;
import org.rabix.engine.model.JobRecord;
import org.rabix.engine.model.VariableRecord;
import org.rabix.engine.processor.EventProcessor;
import org.rabix.engine.rest.service.ExecutableService;
import org.rabix.engine.rest.service.ExecutableServiceException;
import org.rabix.engine.service.ContextService;
import org.rabix.engine.service.JobService;
import org.rabix.engine.service.VariableService;
import org.rabix.engine.validator.JobStateValidationException;
import org.rabix.engine.validator.JobStateValidator;
import org.rabix.engine.service.JobService.JobState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ExecutableServiceImpl implements ExecutableService {

  private final static Logger logger = LoggerFactory.getLogger(ExecutableServiceImpl.class);
  
  private final JobService jobService;
  private final VariableService variableService;
  private final ContextService contextService;
  
  private final DAGNodeDB dagNodeDB;
  
  private final EventProcessor eventProcessor;

  @Inject
  public ExecutableServiceImpl(EventProcessor eventProcessor, JobService jobService,VariableService variableService, ContextService contextService, DAGNodeDB dagNodeDB) {
    this.dagNodeDB = dagNodeDB;
    this.eventProcessor = eventProcessor;
    
    this.jobService = jobService;
    this.variableService = variableService;
    this.contextService = contextService;
  }
  
  public void update(Executable executable) throws ExecutableServiceException {
    try {
      Bindings bindings = BindingsFactory.create(executable);
      ProtocolType protocolType = bindings.getProtocolType();
      
      JobRecord job = jobService.find(executable.getNodeId(), executable.getContext().getId());
      
      JobStatusEvent statusEvent = null;
      ExecutableStatus status = executable.getStatus();
      switch (status) {
      case RUNNING:
        JobStateValidator.checkState(job, JobState.RUNNING);
        statusEvent = new JobStatusEvent(executable.getNodeId(), executable.getContext().getId(), JobState.RUNNING, executable.getOutputs(), protocolType);
        eventProcessor.addToQueue(statusEvent);
        break;
      case FAILED:
        JobStateValidator.checkState(job, JobState.FAILED);
        statusEvent = new JobStatusEvent(executable.getNodeId(), executable.getContext().getId(), JobState.FAILED, null, protocolType);
        eventProcessor.addToQueue(statusEvent);
        break;
      case COMPLETED:
        JobStateValidator.checkState(job, JobState.COMPLETED);
        statusEvent = new JobStatusEvent(executable.getNodeId(), executable.getContext().getId(), JobState.COMPLETED, executable.getOutputs(), protocolType);
        eventProcessor.addToQueue(statusEvent);
        break;
      default:
        break;
      }
    } catch (BindingException e) {
      logger.error("Cannot find Bindings", e);
      throw new ExecutableServiceException("Cannot find Bindings", e);
    } catch (JobStateValidationException e) {
      logger.error("Failed to update Job state");
      throw new ExecutableServiceException("Failed to update Job state", e);
    }
  }
  
  public List<Executable> getReady(EventProcessor eventProcessor, String contextId) throws ExecutableServiceException {
    List<Executable> executables = new ArrayList<>();
    List<JobRecord> jobs = jobService.findReady(contextId);

    if (!jobs.isEmpty()) {
      for (JobRecord job : jobs) {
        DAGNode node = dagNodeDB.get(InternalSchemaHelper.normalizeId(job.getId()), contextId);

        try {
          Bindings bindings = BindingsFactory.create(node.getApp());

          Object inputs = null;
          List<VariableRecord> inputVariables = variableService.find(job.getId(), LinkPortType.INPUT, contextId);
          for (VariableRecord inputVariable : inputVariables) {
            inputs = bindings.addToInputs(inputs, inputVariable.getPortId(), inputVariable.getValue());
          }
          ContextRecord contextRecord = contextService.find(job.getContextId());
          Context context = new Context(contextRecord.getId(), contextRecord.getConfig());
          executables.add(new Executable(job.getExternalId(), job.getId(), node, ExecutableStatus.READY, inputs, null, context));
        } catch (BindingException e) {
          logger.error("Cannot find Bindings.", e);
          throw new ExecutableServiceException("Cannot find Bindings", e);
        }
      }
    }
    return executables;
  }
  
}
