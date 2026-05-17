package com.stepankosvin.reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Общий интерфейс для результатов парсинга элементов json.
 * Все методы предусматривают возможность возврата null.
 */
public interface JsonBody {
    
    /**
     * Выдает исходный элемент json
     * @return JsonNode или null
     */
    JsonNode getBody();

    /**
     * Полная сериализация исходного JSON в строку
     * @return String или null
     */
    String fullString();

    /**
     * Вывод общей информации (краткое описание)
     * @return String или null
     */
    String infoString();
}