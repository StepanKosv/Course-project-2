package reddit_collect_pascage;
/**
 * Базовый интерфейс для команд сборщика.
 * Примечание: метод назван wait() по ТЗ, но в Java лучше использовать await()/pause(), 
 * так как Object.wait() является final. Реализация может использовать Thread.sleep().
 */
public interface ICollectCommand {
    /** Ждет необходимое время (чтоб не влететь в ограничение API) */
    void wait_timeout();
    
    /** Исполнение команды */
    void run();
    
    /** Проверяет, необходимо ли удалить команду из очереди */
    boolean isDeprecated();
    
    /** Можно ли выполнить команду прямо сейчас */
    boolean isExecutable();
}