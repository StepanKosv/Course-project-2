package com.stepankosvin.plugin_fasade;
import java.util.List;

/**
 * Интерфейс пользователя данных.
 * @param <NodeId> Тип идентификатора узла.
 * @param <EdgeId> Тип идентификатора ребра.
 */
public interface IDataUser<NodeId, EdgeId> {

    /**
     * Обновление конкретных измененных данных.
     */
    void update(List<EdgeId> edgeKeyList, List<NodeId> nodeKeyList);

    /**
     * Полное обновление данных.
     */
    void refresh();

    /**
     * Закрыть источник данных.
     */
    void close();
}
