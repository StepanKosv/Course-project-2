package app_interface;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Graphs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.HashToNodePK;
import model.HashToRelPK;
import model.Node;
import model.Rel;
import model.RelPK;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import db_integration.DBFasade;

public class ScopeHolder implements IDataHolder<Long, model.RelPK, model.Node, model.Rel, model.HashToNodePK, model.HashToRelPK> {
	public DBFasade dbFasade;
    // Поля из технического задания
    private final String scopeName;
    private Long commitId;
    
    // Потокобезопасный граф JUNG (обертка для параллельной работы)
    private final Graph<Long, model.RelPK> graph;
    
    // Список слушателей (используем CopyOnWriteArrayList для потокобезопасного обхода)
    private final List<IDataUser<Long, model.RelPK>> listeners;

    // Конструктор
    public ScopeHolder(String scopeName, Long commitId, EntityManagerFactory emf) {
    	this.dbFasade = new DBFasade(emf);
        this.scopeName = scopeName;
        this.commitId = commitId;
        this.listeners = new CopyOnWriteArrayList<>();
        this.graph = Graphs.synchronizedGraph(new SparseMultigraph<>());
    }
    
    // --- Бизнес-правила: Изменение графа + БД + Авто-уведомление ---

//    @Override
//    public void newNode(Long id, model.Node data) {
//        // 1. Сохранение в БД
//        saveNodeToDb(id, data);
//        
//        // 2. Добавление в граф
//        graph.addVertex(id);
//        
//    }
//
//    @Override
//    public void newEdge(model.RelPK id, model.Rel data) {
//        // 1. Сохранение в БД
//        saveEdgeToDb(id, data);
//        
//        // 2. Добавление в граф
//        // Извлекаем вершины и уникальный ID ребра
//        Long sourceNodeId = id.getLeftNodeFk(); 
//        Long targetNodeId = id.getRightNodeFk();
//        
//        // JUNG SparseMultigraph нативно поддерживает несколько ребер между source и target,
//        // если у них разные edgeId. EdgeType.DIRECTED жестко задает направленность.
//        graph.addEdge(id, sourceNodeId, targetNodeId, EdgeType.DIRECTED);
//        
//        // 3. Автоматический вызов update() для слушателей
//        this.update(List.of(id), Collections.emptyList());
//    }
    


    @Override
    public void removeNode(Long id) {
        // 1. Удаление из БД
        deleteNodeFromDb(id);
        // 2. Удаление из графа
        graph.removeVertex(id);
        // 3. Автоматический вызов update() для слушателей
        this.update(Collections.emptyList(), List.of(id));
    }

    @Override
    public void removeEdge(model.RelPK id) {
        // 1. Удаление из БД
        deleteEdgeFromDb(id);
        
        // 2. Удаление из графа
        graph.removeEdge(id);
        
        // 3. Автоматический вызов update() для слушателей
        this.update(List.of(id), Collections.emptyList());
    }

    @Override
    public void save() {
        System.out.println("[БД] Принудительное сохранение текущего состояния области видимости: " + scopeName);
        // Заглушка транзакции коммита в БД
    }

    // --- Поиск по Хешу (Запросы к БД) ---

    @Override
    public Long nodeByHash(model.HashToNodePK hash) {
    	return dbFasade.transact(em->{
    		model.HashToNode htn = em.find(model.HashToNode.class, hash);
    		return htn.getNode().getId();
    		});
    }

    @Override
    public model.RelPK edgeByHash(model.HashToRelPK hash) {
    	return dbFasade.transact(em->{
    		model.HashToRel htr=em.find(model.HashToRel.class, hash);
    		return htr.getRel().getId();
    	});
    }

    // --- Управление слушателями ---

    @Override
    public void addListener(IDataUser<Long, model.RelPK> listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(IDataUser<Long, model.RelPK> listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public Set<IDataUser<Long, model.RelPK>> getListeners() {
        // Преобразуем внутренний List в Set для совместимости с контрактом интерфейса
        Set<IDataUser<Long, model.RelPK>> set = ConcurrentHashMap.newKeySet();
        set.addAll(listeners);
        return set;
    }

    @Override
    public void setListeners(Set<IDataUser<Long, model.RelPK>> listeners) {
        this.listeners.clear();
        if (listeners != null) {
            this.listeners.addAll(listeners);
        }
    }

    // --- Геттеры и сеттеры для Scope свойств ---

    public String getScopeName() {
        return scopeName;
    }

    public Long getCommitId() {
        return commitId;
    }

    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    public Graph<Long, model.RelPK> getGraph() {
        return graph;
    }

    // --- Заглушки технических методов работы с БД ---

    private void saveNodeToDb(Long id, model.Node data) {
    	dbFasade.transact(em->{
    		//var node=em.find(model.Node.class, id);
    		//assert(node!=null);
    		data.setId(id);
    		em.merge(data);
    	});
    }

    private void saveEdgeToDb(model.RelPK id, model.Rel data) {
    	dbFasade.transact(em->{
    		//assert(em.find(model.Rel.class, id)!=null);
    		data.setId(id);
    		em.merge(data);
    	});
    }

    private void deleteNodeFromDb(Long id) {
    	dbFasade.transact(em->{
    		em.remove(em.find(model.Node.class, id));
    	});
    }

    private void deleteEdgeFromDb(model.RelPK id) {
    	dbFasade.transact(em->{
    		em.remove(em.find(model.Rel.class, id));
    	});
    }

	@Override
	public Node getNode(Long id) {
		return dbFasade.transact(em->{return em.find(Node.class, id);});
	}

	@Override
	public Rel getEdge(RelPK id) {
		return dbFasade.transact(em->{return em.find(Rel.class, id);});
	}

	@Override
	public void setNode(Long id, Node data) {
		assert(graph.containsVertex(id));
		
		this.saveNodeToDb(id, data);
		
        // 3. Автоматический вызов update() для слушателей
        this.update(Collections.emptyList(), List.of(id));
	}

	@Override
	public void setEdge(RelPK id, Rel data) {
		assert(graph.containsEdge(id));
		
		this.saveEdgeToDb(id, data);
		
		// 3. Автоматический вызов update() для слушателей
        this.update(List.of(id), Collections.emptyList());
	}

	@Override
	public Long createOrFindNode(HashToNodePK hash) {
		Long nodeid = this.dbFasade.transact(em->
		{return this.dbFasade.createOrFindNode(em, hash.getHash(), hash.getHashType(), scopeName);});
		if(!graph.containsVertex(nodeid)) graph.addVertex(nodeid);
		return nodeid;
	}

	@Override
	public RelPK createOrFindEdge(HashToRelPK hash) {
		Long relid=this.dbFasade.transact(em->
		{return this.dbFasade.createOrFindRel(em, hash.getHash(), hash.getHashType(),
				hash.getScopeFk(),hash.getLeftNodeFk(),hash.getRightNodeFk());});
		RelPK pk = new RelPK();
		pk.setLeftNodeFk(hash.getLeftNodeFk());
		pk.setRightNodeFk(hash.getRightNodeFk());
		pk.setRelId(relid);
		if(!graph.containsEdge(pk)) graph.addEdge(pk, hash.getLeftNodeFk(), hash.getRightNodeFk());
		return pk;
	}
}
