package model;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the hash_to_nodes database table.
 * 
 */
@Embeddable
public class HashToNodePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String hash;

	@Column(name="hash_type")
	private String hashType;

	@Column(name="scope_fk", insertable=false, updatable=false)
	private String scopeFk;

	public HashToNodePK() {
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

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HashToNodePK)) {
			return false;
		}
		HashToNodePK castOther = (HashToNodePK)other;
		return 
			this.hash.equals(castOther.hash)
			&& this.hashType.equals(castOther.hashType)
			&& this.scopeFk.equals(castOther.scopeFk);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.hash.hashCode();
		hash = hash * prime + this.hashType.hashCode();
		hash = hash * prime + this.scopeFk.hashCode();
		
		return hash;
	}
}