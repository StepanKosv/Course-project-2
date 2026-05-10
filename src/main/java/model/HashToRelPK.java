package model;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the hash_to_rels database table.
 * 
 */
@Embeddable
public class HashToRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String hash;

	@Column(name="hash_type")
	private String hashType;

	@Column(name="scope_fk", insertable=false, updatable=false)
	private String scopeFk;

	@Column(name="left_node_fk", insertable=false, updatable=false)
	private Long leftNodeFk;

	@Column(name="right_node_fk", insertable=false, updatable=false)
	private Long rightNodeFk;

	public HashToRelPK() {
	}
	public String getHash() {
		return this.hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getHashType() {
		return this.hashType;
	}
	public void setHashType(String hashType) {
		this.hashType = hashType;
	}
	public String getScopeFk() {
		return this.scopeFk;
	}
	public void setScopeFk(String scopeFk) {
		this.scopeFk = scopeFk;
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
		if (!(other instanceof HashToRelPK)) {
			return false;
		}
		HashToRelPK castOther = (HashToRelPK)other;
		return 
			this.hash.equals(castOther.hash)
			&& this.hashType.equals(castOther.hashType)
			&& this.scopeFk.equals(castOther.scopeFk)
			&& this.leftNodeFk.equals(castOther.leftNodeFk)
			&& this.rightNodeFk.equals(castOther.rightNodeFk);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.hash.hashCode();
		hash = hash * prime + this.hashType.hashCode();
		hash = hash * prime + this.scopeFk.hashCode();
		hash = hash * prime + this.leftNodeFk.hashCode();
		hash = hash * prime + this.rightNodeFk.hashCode();
		
		return hash;
	}
}