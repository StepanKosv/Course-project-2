package model;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the rels database table.
 * 
 */
@Embeddable
public class RelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	
	//@GeneratedValue(strategy = GenerationType.IDENTITY) //добавил для дефолтного заполнения
	@Column(name="rel_id")
	private Long relId;

	@Column(name="left_node_fk", insertable=false, updatable=false)
	private Long leftNodeFk;

	@Column(name="right_node_fk", insertable=false, updatable=false)
	private Long rightNodeFk;

	public RelPK() {
	}
	public Long getRelId() {
		return this.relId;
	}
	public void setRelId(Long relId) {
		this.relId = relId;
	}
	public Long getLeftNodeFk() {
		return this.leftNodeFk;
	}
	public void setLeftNodeFk(Long leftNodeFk) {
		this.leftNodeFk = leftNodeFk;
	}
	public Long getRightNodeFk() {
		return this.rightNodeFk;
	}
	public void setRightNodeFk(Long rightNodeFk) {
		this.rightNodeFk = rightNodeFk;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RelPK)) {
			return false;
		}
		RelPK castOther = (RelPK)other;
		return 
			this.relId.equals(castOther.relId)
			&& this.leftNodeFk.equals(castOther.leftNodeFk)
			&& this.rightNodeFk.equals(castOther.rightNodeFk);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.relId.hashCode();
		hash = hash * prime + this.leftNodeFk.hashCode();
		hash = hash * prime + this.rightNodeFk.hashCode();
		
		return hash;
	}
}