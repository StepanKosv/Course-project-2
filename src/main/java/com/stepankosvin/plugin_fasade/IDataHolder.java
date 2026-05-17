package com.stepankosvin.plugin_fasade;

import java.util.Set;

/**
 * Интерфейс держателя данных.
 * Расширяет IDataUser и управляет графовой структурой.
 * 
 * @param <NodeId> Тип идентификатора узла.
 * @param <EdgeId> Тип идентификатора ребра.
 * @param <NodeData> Структура данных для вершин.
 * @param <EdgeData> Структура данных для ребер.
 * @param <NodeHash> Структура для хешей узлов.
 * @param <EdgeHash> Структура для хешей ребер.
 */
public interface IDataHolder<NodeId, EdgeId, NodeData, EdgeData, NodeHash, EdgeHash> 
        extends IDataNode<NodeId, EdgeId, NodeData, EdgeData> {
	/**
	 * создать новую ноду
	 */
	NodeId createOrFindNode(NodeHash hash);
	
	/**
	 * создать новое ребро
	 */
	EdgeId createOrFindEdge(EdgeHash hash);

    /**
     * Присвоить вершине значение.
     */
    void setNode(NodeId id, NodeData data);

    /**
     * Присвоить ребру значение.
     */
    void setEdge(EdgeId id, EdgeData data);
    
    /**
     * Удалить вершину по её идентификатору.
     */
    void removeNode(NodeId id);

    /**
     * Удалить ребро по его идентификатору.
     */
    void removeEdge(EdgeId id);

    /**
     * Сохранить текущее состояние в базу данных.
     */
    void save();

    /**
     * Получить идентификатор вершины по её хешу.
     */
    NodeId nodeByHash(NodeHash hash);
    
    
    /**
     * Получить идентификатор ребра по его хешу.
     */
    EdgeId edgeByHash(EdgeHash hash);

}
