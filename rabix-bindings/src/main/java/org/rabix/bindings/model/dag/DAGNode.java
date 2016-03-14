package org.rabix.bindings.model.dag;

import java.util.List;

public class DAGNode {

  public static enum ScatterMethod {
    dotproduct,
    nested_crossproduct,
    flat_crossproduct
  }
  
  public static enum LinkMerge {
    merge_nested,
    merge_flattened
  }
  
  protected final String id;
  protected final Object app;
  protected final LinkMerge linkMerge;
  protected final ScatterMethod scatterMethod;
  protected final List<DAGLinkPort> inputPorts;
  protected final List<DAGLinkPort> outputPorts;

  public DAGNode(String id, List<DAGLinkPort> inputPorts, List<DAGLinkPort> outputPorts, ScatterMethod scatterMethod, LinkMerge linkMerge, Object app) {
    this.id = id;
    this.app = app;
    this.inputPorts = inputPorts;
    this.outputPorts = outputPorts;
    this.linkMerge = linkMerge;
    this.scatterMethod = scatterMethod;
  }

  public String getId() {
    return id;
  }

  public Object getApp() {
    return app;
  }

  public List<DAGLinkPort> getInputPorts() {
    return inputPorts;
  }

  public List<DAGLinkPort> getOutputPorts() {
    return outputPorts;
  }
  
  public ScatterMethod getScatterMethod() {
    return scatterMethod;
  }
  
  public LinkMerge getLinkMerge() {
    return linkMerge;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((app == null) ? 0 : app.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((inputPorts == null) ? 0 : inputPorts.hashCode());
    result = prime * result + ((outputPorts == null) ? 0 : outputPorts.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DAGNode other = (DAGNode) obj;
    if (app == null) {
      if (other.app != null)
        return false;
    } else if (!app.equals(other.app))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (inputPorts == null) {
      if (other.inputPorts != null)
        return false;
    } else if (!inputPorts.equals(other.inputPorts))
      return false;
    if (outputPorts == null) {
      if (other.outputPorts != null)
        return false;
    } else if (!outputPorts.equals(other.outputPorts))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DAGNode [id=" + id + ", linkMerge=" + linkMerge + ", scatterMethod=" + scatterMethod + ", inputPorts=" + inputPorts + ", outputPorts=" + outputPorts + "]";
  }

}
