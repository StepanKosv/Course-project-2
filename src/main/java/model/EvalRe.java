package model;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the eval_res database table.
 * 
 */
@Entity
@Table(name="eval_res")
@NamedQuery(name="EvalRe.findAll", query="SELECT e FROM EvalRe e")
public class EvalRe implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EvalRePK id;

	@Column(name="json_data")
	private Object jsonData;

	//bi-directional many-to-one association to CommitVer
	@ManyToOne
@JoinColumn(name="commit_fk")
	private CommitVer commitVer;

	//bi-directional many-to-one association to ScopeInfo
	@ManyToOne
@JoinColumn(name="scope_fk")
	private ScopeInfo scopeInfo;

	public EvalRe() {
	}

	public EvalRePK getId() {
		return this.id;
	}

	public void setId(EvalRePK id) {
		this.id = id;
	}

	public Object getJsonData() {
		return this.jsonData;
	}

	public void setJsonData(Object jsonData) {
		this.jsonData = jsonData;
	}

	public CommitVer getCommitVer() {
		return this.commitVer;
	}

	public void setCommitVer(CommitVer commitVer) {
		this.commitVer = commitVer;
	}

	public ScopeInfo getScopeInfo() {
		return this.scopeInfo;
	}

	public void setScopeInfo(ScopeInfo scopeInfo) {
		this.scopeInfo = scopeInfo;
	}

}