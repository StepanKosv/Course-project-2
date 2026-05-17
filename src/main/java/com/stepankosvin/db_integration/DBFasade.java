package com.stepankosvin.db_integration;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.HashToNodePK;

public class DBFasade {
	public final EntityManagerFactory emf;
	public DBFasade(EntityManagerFactory _emf) {
		this.emf=_emf;
	}
	// Обертка для void методов (Runnable)
    public synchronized <T> T transact(Function<EntityManager,T> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            var res = action.apply(em);
            tx.commit();
            return res;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    public synchronized void transact(Consumer<EntityManager> action) {
    	transact(em->{
    		action.accept(em);
    		return 1;
    	});
    }
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
	
}
