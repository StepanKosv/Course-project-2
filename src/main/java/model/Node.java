package model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;


/**
 * The persistent class for the nodes database table.
 * 
 */
@Entity
@Table(name="nodes")
@NamedQuery(name="Node.findAll", query="SELECT n FROM Node n")
public class Node implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //добавил для дефолтного заполнения
	private Long id;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="delete_time")
	private Timestamp deleteTime;

	@Column(name="display_text")
	private String displayText;

	@Column(name="json_meta")
	@JdbcTypeCode(SqlTypes.JSON)
	private String jsonMeta;

	private String type;

	//bi-directional many-to-one association to HashToNode
	@OneToMany(mappedBy="node")
	private List<HashToNode> hashToNodes;

	//bi-directional many-to-one association to HashToRel
	@OneToMany(mappedBy="node1")
	private List<HashToRel> hashToRels1;

	//bi-directional many-to-one association to HashToRel
	@OneToMany(mappedBy="node2")
	private List<HashToRel> hashToRels2;

	//bi-directional many-to-one association to CommitVer
	@ManyToOne
@JoinColumn(name="last_commit_fk")
	private CommitVer commitVer;

	//bi-directional many-to-one association to ScopeInfo
	@ManyToOne
@JoinColumn(name="scope_fk")
	private ScopeInfo scopeInfo;

	//bi-directional many-to-one association to Rel
	@OneToMany(mappedBy="node1")
	private List<Rel> rels1;

	//bi-directional many-to-one association to Rel
	@OneToMany(mappedBy="node2")
	private List<Rel> rels2;

	public Node() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getDeleteTime() {
		return this.deleteTime;
	}

	public void setDeleteTime(Timestamp deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getDisplayText() {
		return this.displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getJsonMeta() {
		return this.jsonMeta;
	}

	public void setJsonMeta(String jsonMeta) {
		this.jsonMeta = jsonMeta;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<HashToNode> getHashToNodes() {
		return this.hashToNodes;
	}

	public void setHashToNodes(List<HashToNode> hashToNodes) {
		this.hashToNodes = hashToNodes;
	}

	public HashToNode addHashToNode(HashToNode hashToNode) {
		getHashToNodes().add(hashToNode);
		hashToNode.setNode(this);

		return hashToNode;
	}

	public HashToNode removeHashToNode(HashToNode hashToNode) {
		getHashToNodes().remove(hashToNode);
		hashToNode.setNode(null);

		return hashToNode;
	}

	public List<HashToRel> getHashToRels1() {
		return this.hashToRels1;
	}

	public void setHashToRels1(List<HashToRel> hashToRels1) {
		this.hashToRels1 = hashToRels1;
	}

	public HashToRel addHashToRels1(HashToRel hashToRels1) {
		getHashToRels1().add(hashToRels1);
		hashToRels1.setNode1(this);

		return hashToRels1;
	}

	public HashToRel removeHashToRels1(HashToRel hashToRels1) {
		getHashToRels1().remove(hashToRels1);
		hashToRels1.setNode1(null);

		return hashToRels1;
	}

	public List<HashToRel> getHashToRels2() {
		return this.hashToRels2;
	}

	public void setHashToRels2(List<HashToRel> hashToRels2) {
		this.hashToRels2 = hashToRels2;
	}

	public HashToRel addHashToRels2(HashToRel hashToRels2) {
		getHashToRels2().add(hashToRels2);
		hashToRels2.setNode2(this);

		return hashToRels2;
	}

	public HashToRel removeHashToRels2(HashToRel hashToRels2) {
		getHashToRels2().remove(hashToRels2);
		hashToRels2.setNode2(null);

		return hashToRels2;
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

	public List<Rel> getRels1() {
		return this.rels1;
	}

	public void setRels1(List<Rel> rels1) {
		this.rels1 = rels1;
	}

	public Rel addRels1(Rel rels1) {
		getRels1().add(rels1);
		rels1.setNode1(this);

		return rels1;
	}

	public Rel removeRels1(Rel rels1) {
		getRels1().remove(rels1);
		rels1.setNode1(null);

		return rels1;
	}

	public List<Rel> getRels2() {
		return this.rels2;
	}

	public void setRels2(List<Rel> rels2) {
		this.rels2 = rels2;
	}

	public Rel addRels2(Rel rels2) {
		getRels2().add(rels2);
		rels2.setNode2(this);

		return rels2;
	}

	public Rel removeRels2(Rel rels2) {
		getRels2().remove(rels2);
		rels2.setNode2(null);

		return rels2;
	}

}