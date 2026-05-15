package db_integration;

import app_interface.ScopeHolder;
import model.HashToNodePK;
import model.HashToRelPK;
import model.Node;
import model.ScopeInfo;
import reddit_collect_pascage.DiscussionTextElement;

public class RedditCollectWritingVer2 {
	private ScopeHolder holder;
	public RedditCollectWritingVer2(ScopeHolder _h) {
		this.holder=_h;
	}
	String mess_hash_t="RedditCollectWritingVer2/reddit/mess";
	String hash_t_pref="RedditCollectWritingVer2/reddit/";
	public void addMessageTransact(DiscussionTextElement mess) {
		ScopeInfo scope=holder.dbFasade.transact(
				em->{return em.find(ScopeInfo.class, holder.getScopeName());});
		// 1 message node
		assert (mess.stringId() != null);
		long messId = holder.createOrFindNode(htn_pk(mess.stringId(),mess_hash_t));
		Node messNode = holder.getNode(messId);
		messNode.setScopeInfo(scope);
		messNode.setType("reddit/mess");
		messNode.setDisplayText(mess.infoString());
		messNode.setJsonMeta(mess.getBody().toString());
		holder.setNode(messId, messNode);
		// 2 user
		if (mess.getUsername() != null) {
			long userId = holder.createOrFindNode(htn_pk(mess.getUsername(),
					"RedditCollectWritingVer2/reddit/user"));
			Node userNode=holder.getNode(userId);
			userNode.setScopeInfo(scope);
			userNode.setType("reddit/user");
			userNode.setDisplayText("u/"+mess.getUsername());
			holder.setNode(userId, userNode);
			// user create mess
//			createOrChangeRel(em, "create", HASH_METHOD, scopeName, userId, messId, null, "reddit/post message", null,
//					"create", null, null, null);
			var relpk=holder.createOrFindEdge(
					htr_pk("create","RedditCollectWritingVer2/rel",userId,messId));
			var create=holder.getEdge(relpk);
			create.setType("create");
			create.setDisplayText("create");
			holder.setEdge(relpk, create);
		}
		// 3 parent
		if (mess.parentId() != null) {
			long pId = holder.createOrFindNode(htn_pk(mess.parentId(),
					mess_hash_t));
			Node pNode=holder.getNode(pId);
			pNode.setScopeInfo(scope);
			pNode.setType("reddit/mess");
			if(pNode.getDisplayText()==null)
				pNode.setDisplayText(mess.parentId());
			holder.setNode(pId, pNode);
			// mess answer mess
			var relpk=holder.createOrFindEdge(
					htr_pk("answer","RedditCollectWritingVer2/rel",messId,pId));
			var answer=holder.getEdge(relpk);
			answer.setType("answer");
			answer.setDisplayText("answer");
			holder.setEdge(relpk, answer);
		}
		// 4 subreddit
		if (mess.subredditName() != null) {
			long subredditId = holder.createOrFindNode(htn_pk(mess.subredditName(),
					"RedditCollectWritingVer2/reddit/subreddit"));
			Node rNode=holder.getNode(subredditId);
			rNode.setScopeInfo(scope);
			rNode.setType("reddit/subreddit");
			if(rNode.getDisplayText()==null)
				rNode.setDisplayText("r/"+mess.subredditName());
			holder.setNode(subredditId, rNode);
			// subreddit contain mess
			var relpk=holder.createOrFindEdge(
					htr_pk("contain","RedditCollectWritingVer2/rel",subredditId,
							messId));
			var contain=holder.getEdge(relpk);
			contain.setType("contain");
			contain.setDisplayText("contain");
			holder.setEdge(relpk, contain);
		}
	}
	HashToNodePK htn_pk(String hash, String hash_type, String scope_name) {
		var pk=new HashToNodePK();
		pk.setHash(hash);
		pk.setHashType(hash_type);
		pk.setScopeFk(scope_name);
		return pk;
	}
	HashToNodePK htn_pk(String hash, String hash_type) {
		return htn_pk(hash,hash_type,holder.getScopeName());
	}
	HashToRelPK htr_pk(String hash, String hash_type, String scope_name, Long left, Long right) {
		var pk=new HashToRelPK();
		pk.setHash(hash);
		pk.setHashType(hash_type);
		pk.setLeftNodeFk(left);
		pk.setRightNodeFk(right);
		pk.setScopeFk(scope_name);
		return pk;
	}
	HashToRelPK htr_pk(String hash, String hash_type, Long left, Long right) {
		return this.htr_pk(hash, hash_type, holder.getScopeName(), left, right);
	}
}
