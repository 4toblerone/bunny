package org.rabix.bindings.model.dag;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.rabix.bindings.ProtocolType;
import org.rabix.bindings.model.Application;
import org.rabix.bindings.model.Application.ApplicationDeserializer;
import org.rabix.bindings.model.Application.ApplicationSerializer;
import org.rabix.bindings.model.LinkMerge;
import org.rabix.bindings.model.ScatterMethod;
import org.rabix.bindings.model.dag.DAGLinkPort.LinkPortType;

import java.io.Serializable;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = DAGNode.class, name = "EXECUTABLE"),
    @Type(value = DAGContainer.class, name = "CONTAINER")})
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DAGNode implements Serializable {

  public static enum DAGNodeType {
    EXECUTABLE,
    CONTAINER
  }

  @JsonProperty("id")
  protected String id;
  @JsonDeserialize(using = ApplicationDeserializer.class)
  @JsonSerialize(using = ApplicationSerializer.class)
  @JsonProperty("app")
  protected Application app;
  @JsonProperty("appHash")
  protected String appHash;
  @JsonProperty("scatterMethod")
  protected ScatterMethod scatterMethod;
  @JsonProperty("inputPorts")
  protected List<DAGLinkPort> inputPorts = new ArrayList<>();
  @JsonProperty("outputPorts")
  protected List<DAGLinkPort> outputPorts = new ArrayList<>();

  @JsonProperty("defaults")
  protected Map<String, Object> defaults;

  @JsonProperty("protocolType")
  protected ProtocolType protocolType;

  @JsonCreator
  public DAGNode(@JsonProperty("id") String id,
                 @JsonProperty("inputPorts") List<DAGLinkPort> inputPorts,
                 @JsonProperty("outputPorts") List<DAGLinkPort> outputPorts,
                 @JsonProperty("scatterMethod") ScatterMethod scatterMethod,
                 @JsonProperty("app") Application app,
                 @JsonProperty("defaults") Map<String, Object> defaults,
                 @JsonProperty("protocolType") ProtocolType protocolType) {
    this.id = id;
    this.app = app;
    this.inputPorts = inputPorts;
    this.outputPorts = outputPorts;
    this.scatterMethod = scatterMethod;
    this.defaults = defaults;
    this.appHash = null;
    this.protocolType = protocolType;
  }

  public String getId() {
    return id;
  }

  public Application getApp() {
    return app;
  }

  public void setApp(Application app) {
    this.app = app;
  }

  public String getAppHash() {
    return appHash;
  }

  public void setAppHash(String appHash) {
    this.appHash = appHash;
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

  public ProtocolType getProtocolType() {
    return protocolType;
  }

  public LinkMerge getLinkMerge(String portId, LinkPortType linkPortType) {
    switch (linkPortType) {
    case INPUT:
      for (DAGLinkPort inputPort : inputPorts) {
        if (inputPort.getId().equals(portId)) {
          return inputPort.getLinkMerge();
        }
      }
      break;
    case OUTPUT:
      for (DAGLinkPort inputPort : outputPorts) {
        if (inputPort.getId().equals(portId)) {
          return inputPort.getLinkMerge();
        }
      }
      break;
    default:
      break;
    }
    return null;
  }

  public Set<LinkMerge> getLinkMergeSet(LinkPortType linkPortType) {
    Set<LinkMerge> linkMergeSet = new HashSet<>();

    switch (linkPortType) {
    case INPUT:
      for (DAGLinkPort inputPort : inputPorts) {
        linkMergeSet.add(inputPort.getLinkMerge());
      }
      break;
    case OUTPUT:
      for (DAGLinkPort outputPort : outputPorts) {
        linkMergeSet.add(outputPort.getLinkMerge());
      }
      break;
    default:
      break;
    }
    return linkMergeSet;
  }

  public Map<String, Object> getDefaults() {
    return defaults;
  }

  public DAGNodeType getType() {
    return DAGNodeType.EXECUTABLE;
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
    return "DAGNode [id=" + id + ", scatterMethod=" + scatterMethod + ", inputPorts=" + inputPorts + ", outputPorts=" + outputPorts + "]";
  }

}
