package model;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the hash_to_rels database table.
 * 
 */
@Entity
@Table(name="hash_to_rels")
@NamedQuery(name="HashToRel.findAll", query="SELECT h FROM HashToRel h")
public class HashToRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private HashToRelPK id;

	//bi-directional many-to-one association to Node
	@ManyToOne
@JoinColumn(name="left_node_fk")
	private Node node1;

	//bi-directional many-to-one association to Node
	@ManyToOne
@JoinColumn(name="right_node_fk")
	private Node node2;

	//bi-directional many-to-one association to Rel
	@ManyToOne
	@JoinColumns({
@JoinColumn(name="left_node_fk", referencedColumnName="left_node_fk", 
					insertable=false, updatable=false),
@JoinColumn(name="rel_id", referencedColumnName="rel_id", insertable=false, updatable=false),
@JoinColumn(name="right_node_fk", referencedColumnName="right_node_fk", 
					insertable=false, updatable=false)
		})
	private Rel rel;

	//bi-directional many-to-one association to ScopeInfo
	@ManyToOne
@JoinColumn(name="scope_fk")
	private ScopeInfo scopeInfo;

	public HashToRel() {
	}

	public HashToRelPK getId() {
		return this.id;
	}

	public void setId(HashToRelPK id) {
		this.id = id;
	}

	public Node getNode1() {
		return this.node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return this.node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	public Rel getRel() {
		return this.rel;
	}

	public void setRel(Rel rel) {
		this.rel = rel;
	}

	public ScopeInfo getScopeInfo() {
		return this.scopeInfo;
	}

	public void setScopeInfo(ScopeInfo scopeInfo) {
		this.scopeInfo = scopeInfo;
	}

}