package com.stepankosvin.plugin_fasade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Утилита для сканирования JAR-архивов и динамической загрузки классов.
 */
public class ScannerUtilite {

    /**
     * Сканирует один JAR-файл и возвращает список классов, удовлетворяющих заданному фильтру.
     *
     * @param jarPath     абсолютный или относительный путь к JAR-файлу
     * @param filterClass целевой класс или интерфейс (ищутся все наследники и реализации)
     * @return список найденных конкретных (не абстрактных, не интерфейсов) классов
     */
    public static List<Class<?>> loadFile(String jarPath, Class<?> filterClass) {
        List<Class<?>> result = new ArrayList<>();
        File jarFile = new File(jarPath);

        if (!jarFile.exists() || !jarFile.isFile() || !jarPath.endsWith(".jar")) {
            throw new IllegalArgumentException("Указан недопустимый путь к JAR-файлу: " + jarPath);
        }

        try (JarFile jar = new JarFile(jarFile)) {
            URL jarUrl = jarFile.toURI().toURL();
            // Создаём изолированный загрузчик для текущего JAR, делегируя поиск родительскому
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, Thread.currentThread().getContextClassLoader())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // Преобразуем путь внутри архива в полное имя класса
                        String className = name.replace('/', '.').substring(0, name.length() - 6);

                        try {
                            // Загружаем класс без инициализации статических блоков (false)
                            Class<?> clazz = Class.forName(className, false, classLoader);

                            // Проверяем совместимость, исключаем интерфейсы и абстрактные классы
                            if (filterClass.isAssignableFrom(clazz)
                                    && !clazz.isInterface()
                                    && !Modifier.isAbstract(clazz.getModifiers())) {
                                result.add(clazz);
                            }
                        } catch (Throwable e) {
                            // Игнорируем классы с битыми зависимостями, ошибками линковки и т.д.
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения JAR-файла: " + jarPath, e);
        }

        return result;
    }

    /**
     * Рекурсивно обходит директорию, находит все JAR-файлы и извлекает из них подходящие классы.
     */
    public static List<Class<?>> scanFolder(String folderPath, Class<?> filterClass) {
        List<Class<?>> result = new ArrayList<>();
        File dir = new File(folderPath);

        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Указан недопустимый путь к директории: " + folderPath);
        }

        try (Stream<Path> walk = Files.walk(dir.toPath())) {
            List<File> jars = walk
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().endsWith(".jar"))
                    .collect(Collectors.toList());

            for (File jar : jars) {
                try {
                    result.addAll(loadFile(jar.getAbsolutePath(), filterClass));
                } catch (Exception e) {
                    System.err.println("[ScannerUtilite] Пропущен файл " + jar.getName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка обхода директории: " + folderPath, e);
        }

        return result;
    }

    /**
     * Пытается создать экземпляры классов через конструктор без аргументов.
     *
     * @param classes список классов для инстанцирования
     * @param <T>     базовый тип возвращаемых объектов
     * @return список успешно созданных объектов
     */
    //@SuppressWarnings("unchecked")
    public static <T> List<T> getObjects(List<Class<? extends T>> classes) {
        List<T> objects = new ArrayList<>();
        for (Class<? extends T> clazz : classes) {
            try {
                // Ищем любой конструктор без параметров (public, private, protected)
                Constructor<? extends T> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true); // Обходим модификаторы доступа
                objects.add(constructor.newInstance());
            } catch (Exception e) {
                System.err.println("[ScannerUtilite] Не удалось создать экземпляр " + clazz.getName() + ": " + e.getMessage());
            }
        }
        return objects;
    }
    

}