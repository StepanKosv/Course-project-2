package model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


/**
 * The persistent class for the rels database table.
 * 
 */
@Entity
@Table(name="rels")
//@IdClass(RelPK.class)
@NamedQuery(name="Rel.findAll", query="SELECT r FROM Rel r")
public class Rel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RelPK id;

	@Column(name="display_text")
	private String displayText;

	@Column(name="end_time")
	private Timestamp endTime;

	private String hash;

	@Column(name="json_meta")
	@JdbcTypeCode(SqlTypes.JSON)
	private String jsonMeta;

	@Column(name="start_time")
	private Timestamp startTime;

	private String type;

	//bi-directional many-to-one association to HashToRel
	@OneToMany(mappedBy="rel")
	private List<HashToRel> hashToRels;

	//bi-directional many-to-one association to CommitVer
	@ManyToOne
@JoinColumn(name="last_commit_fk")
	private CommitVer commitVer;

	//bi-directional many-to-one association to Node
	@ManyToOne
@JoinColumn(name="left_node_fk")
	private Node node1;

	//bi-directional many-to-one association to Node
	@ManyToOne
@JoinColumn(name="right_node_fk")
	private Node node2;

	public Rel() {
	}

	public RelPK getId() {
		return this.id;
	}

	public void setId(RelPK id) {
		this.id = id;
	}

	public String getDisplayText() {
		return this.displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getJsonMeta() {
		return this.jsonMeta;
	}

	public void setJsonMeta(String jsonMeta) {
		this.jsonMeta = jsonMeta;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<HashToRel> getHashToRels() {
		return this.hashToRels;
	}

	public void setHashToRels(List<HashToRel> hashToRels) {
		this.hashToRels = hashToRels;
	}

	public HashToRel addHashToRel(HashToRel hashToRel) {
		getHashToRels().add(hashToRel);
		hashToRel.setRel(this);

		return hashToRel;
	}

	public HashToRel removeHashToRel(HashToRel hashToRel) {
		getHashToRels().remove(hashToRel);
		hashToRel.setRel(null);

		return hashToRel;
	}

	public CommitVer getCommitVer() {
		return this.commitVer;
	}

	public void setCommitVer(CommitVer commitVer) {
		this.commitVer = commitVer;
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

}