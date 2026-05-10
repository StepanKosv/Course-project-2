package model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;


/**
 * The persistent class for the scope_info database table.
 * 
 */
@Entity
@Table(name="scope_info")
@NamedQuery(name="ScopeInfo.findAll", query="SELECT s FROM ScopeInfo s")
public class ScopeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="scope_name")
	private String scopeName;

	@Column(name="hash_json_meta")
	private Object hashJsonMeta;

	@Column(name="hash_salt")
	private String hashSalt;

	@Column(name="hash_type")
	private String hashType;

	@Column(name="scope_json_meta")
	private Object scopeJsonMeta;

	//bi-directional many-to-one association to CommitVer
	@OneToMany(mappedBy="scopeInfo")
	private List<CommitVer> commitVers;

	//bi-directional many-to-one association to EvalRe
	@OneToMany(mappedBy="scopeInfo")
	private List<EvalRe> evalRes;

	//bi-directional many-to-one association to HashToNode
	@OneToMany(mappedBy="scopeInfo")
	private List<HashToNode> hashToNodes;

	//bi-directional many-to-one association to HashToRel
	@OneToMany(mappedBy="scopeInfo")
	private List<HashToRel> hashToRels;

	//bi-directional many-to-one association to Node
	@OneToMany(mappedBy="scopeInfo")
	private List<Node> nodes;

	public ScopeInfo() {
	}

	public String getScopeName() {
		return this.scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public Object getHashJsonMeta() {
		return this.hashJsonMeta;
	}

	public void setHashJsonMeta(Object hashJsonMeta) {
		this.hashJsonMeta = hashJsonMeta;
	}

	public String getHashSalt() {
		return this.hashSalt;
	}

	public void setHashSalt(String hashSalt) {
		this.hashSalt = hashSalt;
	}

	public String getHashType() {
		return this.hashType;
	}

	public void setHashType(String hashType) {
		this.hashType = hashType;
	}

	public Object getScopeJsonMeta() {
		return this.scopeJsonMeta;
	}

	public void setScopeJsonMeta(Object scopeJsonMeta) {
		this.scopeJsonMeta = scopeJsonMeta;
	}

	public List<CommitVer> getCommitVers() {
		return this.commitVers;
	}

	public void setCommitVers(List<CommitVer> commitVers) {
		this.commitVers = commitVers;
	}

	public CommitVer addCommitVer(CommitVer commitVer) {
		getCommitVers().add(commitVer);
		commitVer.setScopeInfo(this);

		return commitVer;
	}

	public CommitVer removeCommitVer(CommitVer commitVer) {
		getCommitVers().remove(commitVer);
		commitVer.setScopeInfo(null);

		return commitVer;
	}

	public List<EvalRe> getEvalRes() {
		return this.evalRes;
	}

	public void setEvalRes(List<EvalRe> evalRes) {
		this.evalRes = evalRes;
	}

	public EvalRe addEvalRe(EvalRe evalRe) {
		getEvalRes().add(evalRe);
		evalRe.setScopeInfo(this);

		return evalRe;
	}

	public EvalRe removeEvalRe(EvalRe evalRe) {
		getEvalRes().remove(evalRe);
		evalRe.setScopeInfo(null);

		return evalRe;
	}

	public List<HashToNode> getHashToNodes() {
		return this.hashToNodes;
	}

	public void setHashToNodes(List<HashToNode> hashToNodes) {
		this.hashToNodes = hashToNodes;
	}

	public HashToNode addHashToNode(HashToNode hashToNode) {
		getHashToNodes().add(hashToNode);
		hashToNode.setScopeInfo(this);

		return hashToNode;
	}

	public HashToNode removeHashToNode(HashToNode hashToNode) {
		getHashToNodes().remove(hashToNode);
		hashToNode.setScopeInfo(null);

		return hashToNode;
	}

	public List<HashToRel> getHashToRels() {
		return this.hashToRels;
	}

	public void setHashToRels(List<HashToRel> hashToRels) {
		this.hashToRels = hashToRels;
	}

	public HashToRel addHashToRel(HashToRel hashToRel) {
		getHashToRels().add(hashToRel);
		hashToRel.setScopeInfo(this);

		return hashToRel;
	}

	public HashToRel removeHashToRel(HashToRel hashToRel) {
		getHashToRels().remove(hashToRel);
		hashToRel.setScopeInfo(null);

		return hashToRel;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public Node addNode(Node node) {
		getNodes().add(node);
		node.setScopeInfo(this);

		return node;
	}

	public Node removeNode(Node node) {
		getNodes().remove(node);
		node.setScopeInfo(null);

		return node;
	}

}