package model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the commit_ver database table.
 * 
 */
@Entity
@Table(name="commit_ver")
@NamedQuery(name="CommitVer.findAll", query="SELECT c FROM CommitVer c")
public class CommitVer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column(name="commit_time")
	private Timestamp commitTime;
	
	@Column(name="json_meta")
	private Object jsonMeta;

	//bi-directional many-to-one association to ScopeInfo
	@ManyToOne
@JoinColumn(name="scope_fk")
	private ScopeInfo scopeInfo;

	//bi-directional many-to-one association to User
	@ManyToOne
@JoinColumn(name="author_fk")
	private User user;

	//bi-directional many-to-one association to EvalRe
	@OneToMany(mappedBy="commitVer")
	private List<EvalRe> evalRes;

	//bi-directional many-to-one association to Node
	@OneToMany(mappedBy="commitVer")
	private List<Node> nodes;

	//bi-directional many-to-one association to Rel
	@OneToMany(mappedBy="commitVer")
	private List<Rel> rels;

	public CommitVer() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCommitTime() {
		return this.commitTime;
	}

	public void setCommitTime(Timestamp commitTime) {
		this.commitTime = commitTime;
	}

	public Object getJsonMeta() {
		return this.jsonMeta;
	}

	public void setJsonMeta(Object jsonMeta) {
		this.jsonMeta = jsonMeta;
	}

	public ScopeInfo getScopeInfo() {
		return this.scopeInfo;
	}

	public void setScopeInfo(ScopeInfo scopeInfo) {
		this.scopeInfo = scopeInfo;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<EvalRe> getEvalRes() {
		return this.evalRes;
	}

	public void setEvalRes(List<EvalRe> evalRes) {
		this.evalRes = evalRes;
	}

	public EvalRe addEvalRe(EvalRe evalRe) {
		getEvalRes().add(evalRe);
		evalRe.setCommitVer(this);

		return evalRe;
	}

	public EvalRe removeEvalRe(EvalRe evalRe) {
		getEvalRes().remove(evalRe);
		evalRe.setCommitVer(null);

		return evalRe;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public Node addNode(Node node) {
		getNodes().add(node);
		node.setCommitVer(this);

		return node;
	}

	public Node removeNode(Node node) {
		getNodes().remove(node);
		node.setCommitVer(null);

		return node;
	}

	public List<Rel> getRels() {
		return this.rels;
	}

	public void setRels(List<Rel> rels) {
		this.rels = rels;
	}

	public Rel addRel(Rel rel) {
		getRels().add(rel);
		rel.setCommitVer(this);

		return rel;
	}

	public Rel removeRel(Rel rel) {
		getRels().remove(rel);
		rel.setCommitVer(null);

		return rel;
	}

}