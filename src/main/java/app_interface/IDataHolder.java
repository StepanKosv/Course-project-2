package app_interface;

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
        extends IDataUser<NodeId, EdgeId> {
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
     * Получить данные вершины по ее id
     */
    NodeData getNode(NodeId id);
    
    /**
     * Получить идентификатор ребра по его хешу.
     */
    EdgeId edgeByHash(EdgeHash hash);
    
    /**
     * Получить данные ребра по ее id
     */
    EdgeData getEdge(EdgeId id);

    /**
     * Добавить слушателя изменений данных.
     */
    void addListener(IDataUser<NodeId, EdgeId> listener);

    /**
     * Удалить слушателя изменений данных.
     */
    void removeListener(IDataUser<NodeId, EdgeId> listener);

    /**
     * Получить список всех текущих слушателей.
     */
    Set<IDataUser<NodeId, EdgeId>> getListeners();

    /**
     * Установить новый набор слушателей.
     */
    void setListeners(Set<IDataUser<NodeId, EdgeId>> listeners);

    /**
     * Дефолтная реализация IDataUser: спускает команду update вниз по дереву слушателей.
     */
    @Override
    default void update(java.util.List<EdgeId> edgeKeyList, java.util.List<NodeId> nodeKeyList) {
        if (getListeners() != null) {
            for (IDataUser<NodeId, EdgeId> listener : getListeners()) {
                listener.update(edgeKeyList, nodeKeyList);
            }
        }
    }

    /**
     * Дефолтная реализация IDataUser: спускает команду refresh вниз по дереву слушателей.
     */
    @Override
    default void refresh() {
        if (getListeners() != null) {
            for (IDataUser<NodeId, EdgeId> listener : getListeners()) {
                listener.refresh();
            }
        }
    }

    /**
     * Дефолтная реализация IDataUser: спускает команду close вниз по дереву слушателей.
     */
    @Override
    default void close() {
        if (getListeners() != null) {
            for (IDataUser<NodeId, EdgeId> listener : getListeners()) {
                listener.close();
            }
        }
    }
}
