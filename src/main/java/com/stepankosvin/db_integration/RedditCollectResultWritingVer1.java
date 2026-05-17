package com.stepankosvin.db_integration;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

import com.stepankosvin.reddit_collect_pascage.CommentBody;
import com.stepankosvin.reddit_collect_pascage.DiscussionTextElement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.*;

public class RedditCollectResultWritingVer1 {
	//public static final String DEFAULT_USER_NAME="test user";
	//public static final String DEFAULT_USER_PAROL="test parol";
	public static final String HASH_METHOD="RedditCollectResultWritingVer1/hash method";
	public static final String DEFAULT_SCOPE_NAME="test scope";
	public static final String create_or_change_node="create_or_change_node";
	public static final String create_or_change_rel="create_or_change_rel";
	public static final String create_or_find_node="create_or_find_node";
	public static final String create_or_find_rel="create_or_find_rel";
	
	private String scopeName;
	private EntityManagerFactory emf;
	
	public RedditCollectResultWritingVer1(EntityManagerFactory emf, String scopeName) {
		this.emf=emf;
		this.scopeName=scopeName;
	}
	public RedditCollectResultWritingVer1(EntityManagerFactory emf) {
		this(emf,DEFAULT_SCOPE_NAME);
	}
	
	// Обертка для void методов (Runnable)
    public <T> T transact(Function<EntityManager,T> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            var res = action.apply(em);
            tx.commit();
            return res;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
	
//	public HashToNodePK hashMessagePK(DiscussionTextElement body) {
//		HashToNodePK pk = new HashToNodePK();
//		pk.setHash(body.stringId());
//		pk.setHashType(HASH_METHOD);
//		pk.setScopeFk(scope.getScopeName());
//		return pk;
//	}
	public boolean exists(HashToNodePK id, EntityManager em) {
		TypedQuery<Long> query = em.createQuery(
	        "SELECT COUNT(h) FROM HashToNode h WHERE h.id = :id", Long.class);
	    query.setParameter("id", id);
	    return query.getSingleResult() > 0;
	}
	public Long createOrFindNode(EntityManager em, String hash, String hashType, String scopeName) {
	    return ((Number) em.createNativeQuery(
	            "SELECT create_or_find_node(:hash, :type, :scope)")
	            .setParameter("hash", hash)
	            .setParameter("type", hashType)
	            .setParameter("scope", scopeName)
	            .getSingleResult()).longValue();
	}
	public Long createOrFindRel(EntityManager em, String hash, String hashType, String scopeFk, Long leftFk, Long rightFk) {
	    return ((Number) em.createNativeQuery(
	            "SELECT create_or_find_rel(:hash, :type, :scope, :left, :right)")
	            .setParameter("hash", hash)
	            .setParameter("type", hashType)
	            .setParameter("scope", scopeFk)
	            .setParameter("left", leftFk)
	            .setParameter("right", rightFk)
	            .getSingleResult()).longValue();
	}
	public void createOrChangeNode(EntityManager em, String hash, String hashType, String scopeFkHash, 
            String nodeType, String displayText, String jsonMeta, 
            LocalDateTime createTime, LocalDateTime deleteTime, 
            String scopeFkNode, Long lastCommitFk) {
		em.createNativeQuery(
				"SELECT create_or_change_node(:hash, :hType, :sHash, :nType, :display, CAST(:json AS jsonb), :cTime, :dTime, :sNode, :commit)")
				.setParameter("hash", hash)
				.setParameter("hType", hashType)
				.setParameter("sHash", scopeFkHash)
				.setParameter("nType", nodeType)
				.setParameter("display", displayText)
				.setParameter("json", jsonMeta) // Передаем как строку
				.setParameter("cTime", createTime)
				.setParameter("dTime", deleteTime)
				.setParameter("sNode", scopeFkNode)
				.setParameter("commit", lastCommitFk)
				.getSingleResult(); // Используем getSingleResult для вызова функции
	}
	public void createOrChangeRel(EntityManager em, String hash, String hashType, String scopeFkHash,
            Long leftFk, Long rightFk, String relHash, String type, 
            String jsonMeta, String displayText, 
            LocalDateTime startTime, LocalDateTime endTime, Long lastCommitFk) {
		em.createNativeQuery(
					"SELECT create_or_change_rel(:hash, :hType, :sHash, :left, :right, :rHash, :type, CAST(:json AS jsonb), :display, :sTime, :eTime, :commit)")
			.setParameter("hash", hash)
			.setParameter("hType", hashType)
			.setParameter("sHash", scopeFkHash)
			.setParameter("left", leftFk)
			.setParameter("right", rightFk)
			.setParameter("rHash", relHash)
			.setParameter("type", type)
			.setParameter("json", jsonMeta)
			.setParameter("display", displayText)
			.setParameter("sTime", startTime)
			.setParameter("eTime", endTime)
			.setParameter("commit", lastCommitFk)
			.getSingleResult();
	}
	public void addMessage(DiscussionTextElement mess, EntityManager em) {
		//1 message node
		assert(mess.stringId()!=null);
		long messId=this.createOrFindNode(em, mess.stringId(), HASH_METHOD, scopeName);
		createOrChangeNode(em, mess.stringId(), HASH_METHOD, scopeName, 
	            "reddit/mess", mess.infoString(), mess.getBody().toString(), 
	            null, null, 
	            scopeName, null);
		//2 user
		if(mess.getUsername()!=null){
			long userId=this.createOrFindNode(em, mess.getUsername(), HASH_METHOD, scopeName);
			createOrChangeNode(em, mess.getUsername(), HASH_METHOD, scopeName, 
		            "reddit/user", mess.getUsername(), null, 
		            null, null, 
		            scopeName, null);
			//user create mess
			createOrChangeRel(em, "create", HASH_METHOD, scopeName,
		            userId, messId, null, "reddit/post message", 
		            null, "create", 
		            null, null, null);
		}
		//3 parent
		if(mess.parentId()!=null) {
			long parentId=this.createOrFindNode(em, mess.parentId(), HASH_METHOD, scopeName);
			//mess answer mess
			createOrChangeRel(em, "answer", HASH_METHOD, scopeName,
		            messId, parentId, null, "reddit/answer message", 
		            null, "answer", 
		            null, null, null);
		}
		//4 subreddit
		if(mess.subredditName()!=null) {
			long subredditId=this.createOrFindNode(em, mess.subredditName(), HASH_METHOD, scopeName);
			//subreddit contain mess
			createOrChangeRel(em, "contain", HASH_METHOD, scopeName,
		            subredditId, messId, null, "reddit/subreddit contain message", 
		            null, "contain", 
		            null, null, null);
		}
	}
	public void addMessageTransact(DiscussionTextElement mess) {
		this.transact(em->{
			addMessage(mess,em);
			return 1;
		});
	}
	
}
