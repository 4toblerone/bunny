package org.rabix.engine.db;

import org.rabix.bindings.model.dag.DAGNode;
import org.rabix.common.helper.InternalSchemaHelper;
import org.rabix.engine.lru.dag.DAGCache;
import org.rabix.engine.repository.DAGRepository;

import com.google.inject.Inject;

import java.util.UUID;

/**
 * In-memory {@link DAGNode} repository
 */
public class DAGNodeDB {

  private DAGRepository dagRepository;
  private DAGCache dagCache;

  @Inject
  public DAGNodeDB(DAGRepository dagRepository, DAGCache dagCache) {
    this.dagRepository = dagRepository;
    this.dagCache = dagCache;
  }
  
  /**
   * Gets node from the repository 
   */

  public DAGNode get(String name, UUID rootId, String dagHash) {
    DAGNode res = dagCache.get(name, rootId, dagHash);
    if(res == null) {
      res = dagRepository.get(name, rootId);
      dagCache.put(dagRepository.get(InternalSchemaHelper.ROOT_NAME, rootId), rootId);
    }
    return res;
  }
  
  /**
   * Loads node into the repository recursively
   */
  public String loadDB(DAGNode node, UUID rootId) {
    String dagHash = dagCache.put(node, rootId);
    dagRepository.insert(rootId, node);
    return dagHash;
  }
  
}
