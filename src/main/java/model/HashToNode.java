package model;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the hash_to_nodes database table.
 * 
 */
@Entity
@Table(name="hash_to_nodes")
@NamedQuery(name="HashToNode.findAll", query="SELECT h FROM HashToNode h")
public class HashToNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private HashToNodePK id;

	//bi-directional many-to-one association to Node
	@ManyToOne
@JoinColumn(name="node_fk")
	private Node node;

	//bi-directional many-to-one association to ScopeInfo
	@ManyToOne
@JoinColumn(name="scope_fk")
	private ScopeInfo scopeInfo;

	public HashToNode() {
	}

	public HashToNodePK getId() {
		return this.id;
	}

	public void setId(HashToNodePK id) {
		this.id = id;
	}

	public Node getNode() {
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public ScopeInfo getScopeInfo() {
		return this.scopeInfo;
	}

	public void setScopeInfo(ScopeInfo scopeInfo) {
		this.scopeInfo = scopeInfo;
	}

}