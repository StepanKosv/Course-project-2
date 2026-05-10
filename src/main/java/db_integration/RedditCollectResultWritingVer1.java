package db_integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.*;
import reddit_collect_pascage.DiscussionTextElement;

public class RedditCollectResultWritingVer1 {
	public static final String DEFAULT_USER_NAME="test user";
	public static final String DEFAULT_USER_PAROL="test parol";
	public static final String HASH_METHOD="RedditCollectResultWritingVer1/hash method";
	public static final String DEFAULT_SCOPE_NAME="test scope";
	private ScopeInfo scope;
	private EntityManager em;
	
	public HashToNodePK hashMessagePK(DiscussionTextElement body) {
		HashToNodePK pk = new HashToNodePK();
		pk.setHash(body.stringId());
		pk.setHashType(HASH_METHOD);
		pk.setScopeFk(scope.getScopeName());
		return pk;
	}
	public boolean exists(HashToNodePK id) {
	    TypedQuery<Long> query = em.createQuery(
	        "SELECT COUNT(h) FROM HashToNode h WHERE h.id = :id", Long.class);
	    query.setParameter("id", id);
	    return query.getSingleResult() > 0;
	}
	public Node creatOrFind(HashToNodePK id) {
		HashToNode hashToNode = em.find(HashToNode.class, id);
		if (hashToNode == null) {
			EntityTransaction tx = em.getTransaction();
			try {
			    tx.begin();
			    //создаем ноду
			    Node new_node=new Node();
			    new_node.setScopeInfo(scope);
			    new_node.setType("creatOrFind/created node");
			    hashToNode = new HashToNode();
			    hashToNode.setId(id);
			    new_node.addHashToNode(hashToNode);
			    em.persist(new_node);
			    em.persist(hashToNode); // Запланировать вставку
			    tx.commit();         // Записать в БД
			} catch (Exception e) {
			    if (tx.isActive()) tx.rollback();
			    throw e;
			}
		}
		return hashToNode.getNode();
	}
	public Rel createOrFind(HashToRelPK id) {
		EntityTransaction tx = em.getTransaction();
		try {
		    tx.begin();
			HashToRel hashToRel = em.find(HashToRel.class, id);
			if(hashToRel==null) {
				//create rel
				Rel rel=new Rel();
			}
		    tx.commit();         // Записать в БД
		} catch (Exception e) {
		    if (tx.isActive()) tx.rollback();
		    throw e;
		}
	}
	
}
