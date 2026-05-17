package com.stepankosvin.plugin_fasade;

import java.util.Set;

public interface IDataNode<NodeId, EdgeId, NodeData, EdgeData> extends IDataUser<NodeId, EdgeId> {
	/**
     * Получить данные вершины по ее id
     */
	NodeData getNode(NodeId id);
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
