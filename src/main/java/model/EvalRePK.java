package model;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the eval_res database table.
 * 
 */
@Embeddable
public class EvalRePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="eval_name")
	private String evalName;

	@Column(name="scope_fk", insertable=false, updatable=false)
	private String scopeFk;

	public EvalRePK() {
	}
	public String getEvalName() {
		return this.evalName;
	}
	public void setEvalName(String evalName) {
		this.evalName = evalName;
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
		if (!(other instanceof EvalRePK)) {
			return false;
		}
		EvalRePK castOther = (EvalRePK)other;
		return 
			this.evalName.equals(castOther.evalName)
			&& this.scopeFk.equals(castOther.scopeFk);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.evalName.hashCode();
		hash = hash * prime + this.scopeFk.hashCode();
		
		return hash;
	}
}