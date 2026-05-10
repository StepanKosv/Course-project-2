package model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="user_name")
	private String userName;

	private String parol;

	//bi-directional many-to-one association to CommitVer
	@OneToMany(mappedBy="user")
	private List<CommitVer> commitVers;

	public User() {
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParol() {
		return this.parol;
	}

	public void setParol(String parol) {
		this.parol = parol;
	}

	public List<CommitVer> getCommitVers() {
		return this.commitVers;
	}

	public void setCommitVers(List<CommitVer> commitVers) {
		this.commitVers = commitVers;
	}

	public CommitVer addCommitVer(CommitVer commitVer) {
		getCommitVers().add(commitVer);
		commitVer.setUser(this);

		return commitVer;
	}

	public CommitVer removeCommitVer(CommitVer commitVer) {
		getCommitVers().remove(commitVer);
		commitVer.setUser(null);

		return commitVer;
	}

}