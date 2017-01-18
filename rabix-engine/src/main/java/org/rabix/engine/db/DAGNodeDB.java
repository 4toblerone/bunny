package org.rabix.engine.db;

import org.rabix.bindings.model.dag.DAGNode;
import org.rabix.common.helper.JSONHelper;
import org.rabix.engine.dao.DAGRepository;
import org.skife.jdbi.v2.sqlobject.Transaction;

import com.google.inject.Inject;

/**
 * In-memory {@link DAGNode} repository
 */
public class DAGNodeDB {

  private DAGRepository dagRepository;

  @Inject
  public DAGNodeDB(DAGRepository dagRepository) {
    this.dagRepository = dagRepository;
  }
  
  /**
   * Gets node from the repository 
   */
  public synchronized DAGNode get(String id, String contextId) {
    return dagRepository.get(id, contextId);
  }
  
  /**
   * Loads node into the repository recursively
   */
  @Transaction
  public synchronized void loadDB(DAGNode node, String contextId) {
    dagRepository.insert(contextId, JSONHelper.writeObject(node));
  }
  
}
