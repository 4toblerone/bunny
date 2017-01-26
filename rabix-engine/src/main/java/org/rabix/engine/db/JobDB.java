package org.rabix.engine.db;

import java.util.Set;

import org.rabix.bindings.model.Job;
import org.rabix.common.helper.JSONHelper;
import org.rabix.engine.dao.JobRepository;
import org.rabix.engine.singleton.RepositoriesFactory;

import com.google.inject.Inject;

public class JobDB {

  private JobRepository jobRepository;

  @Inject
  public JobDB(RepositoriesFactory factory) {
    this.jobRepository = factory.getRepositories().jobRepository();
  }
  
  public void add(Job job) {
    jobRepository.insert(job.getId(), job.getRootId(), JSONHelper.writeObject(job), null);
  }
  
  public void add(Job job, String groupId) {
    jobRepository.insert(job.getId(), job.getRootId(), JSONHelper.writeObject(job), groupId);
  }
  
  public void update(Job job) {
    jobRepository.update(job.getId(), JSONHelper.writeObject(job));
  }
  
  public Job get(String id) {
    return jobRepository.get(id);
  }
  
  public Set<Job> getJobs() {
    return jobRepository.get();
  }
  
  public Set<Job> getJobs(String rootId) {
    return jobRepository.getByRootId(rootId);
  }
  
  public Set<Job> getJobsByGroupId(String groupId) {
    return jobRepository.getJobsByGroupId(groupId);
  }
}
