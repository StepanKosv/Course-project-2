-- ==========================================
-- 1. createOrFindNode
-- ==========================================
CREATE OR REPLACE FUNCTION create_or_find_node(
    p_hash TEXT,
    p_hash_type TEXT,
    p_scope_name VARCHAR(255)
) RETURNS BIGINT AS $$
DECLARE
    v_node_id BIGINT;
BEGIN
    -- 1. Пробуем найти существующую привязку
    SELECT node_fk INTO v_node_id
    FROM hash_to_nodes
    WHERE hash = p_hash AND hash_type = p_hash_type AND scope_fk = p_scope_name;

    IF FOUND THEN
        RETURN v_node_id;
    END IF;

    -- 2. Создаём узел (все поля NULL, кроме scope_fk)
    INSERT INTO nodes (scope_fk) 
    VALUES (p_scope_name) 
    RETURNING id INTO v_node_id;

    -- 3. Создаём привязку хеш -> узел
    BEGIN
        INSERT INTO hash_to_nodes (hash, hash_type, scope_fk, node_fk)
        VALUES (p_hash, p_hash_type, p_scope_name, v_node_id);
    EXCEPTION WHEN unique_violation THEN
        -- Обработка race condition: если параллельный процесс уже создал привязку
        SELECT node_fk INTO v_node_id FROM hash_to_nodes
        WHERE hash = p_hash AND hash_type = p_hash_type AND scope_fk = p_scope_name;
    END;

    RETURN v_node_id;
END;
$$ LANGUAGE plpgsql;

-- ==========================================
-- 2. createOrFindRel
-- ==========================================
CREATE OR REPLACE FUNCTION create_or_find_rel(
    p_hash TEXT,
    p_hash_type TEXT,
    p_scope_fk VARCHAR(255),
    p_left_node_fk BIGINT,
    p_right_node_fk BIGINT
) RETURNS BIGINT AS $$
DECLARE
    v_rel_id BIGINT;
BEGIN
    -- 1. Ищем привязку
    SELECT rel_id INTO v_rel_id FROM hash_to_rels
    WHERE hash = p_hash AND hash_type = p_hash_type AND scope_fk = p_scope_fk
      AND left_node_fk = p_left_node_fk AND right_node_fk = p_right_node_fk;

    IF FOUND THEN
        RETURN v_rel_id;
    END IF;

    -- 2. Создаём связь (только left/right, остальное NULL)
    INSERT INTO rels (left_node_fk, right_node_fk)
    VALUES (p_left_node_fk, p_right_node_fk)
    RETURNING rel_id INTO v_rel_id;

    -- 3. Создаём привязку хеш -> связь + вставка в hash_to_rels
    BEGIN
        INSERT INTO hash_to_rels (hash, hash_type, scope_fk, rel_id, left_node_fk, right_node_fk)
        VALUES (p_hash, p_hash_type, p_scope_fk, v_rel_id, p_left_node_fk, p_right_node_fk);
    EXCEPTION WHEN unique_violation THEN
        SELECT rel_id INTO v_rel_id FROM hash_to_rels
        WHERE hash = p_hash AND hash_type = p_hash_type AND scope_fk = p_scope_fk
          AND left_node_fk = p_left_node_fk AND right_node_fk = p_right_node_fk;
    END;

    RETURN v_rel_id;
END;
$$ LANGUAGE plpgsql;

-- ==========================================
-- 3. CreateOrChangeNode
-- ==========================================
CREATE OR REPLACE FUNCTION create_or_change_node(
    p_hash TEXT,
    p_hash_type TEXT,
    p_scope_fk_hash VARCHAR(255),  -- для поиска/создания привязки
    p_node_type TEXT,
    p_display_text TEXT,
    p_json_meta JSONB,
    p_create_time TIMESTAMP,
    p_delete_time TIMESTAMP,
    p_scope_fk_node VARCHAR(255),  -- для обновления поля scope_fk в nodes
    p_last_commit_fk BIGINT
) RETURNS VOID AS $$
DECLARE
    v_node_id BIGINT;
BEGIN
    v_node_id := create_or_find_node(p_hash, p_hash_type, p_scope_fk_hash);

    UPDATE nodes
    SET type = p_node_type,
        display_text = p_display_text,
        json_meta = p_json_meta,
        create_time = p_create_time,
        delete_time = p_delete_time,
        scope_fk = p_scope_fk_node,
        last_commit_fk = p_last_commit_fk
    WHERE id = v_node_id;
END;
$$ LANGUAGE plpgsql;

-- ==========================================
-- 4. CreateOrChangeRel
-- ==========================================
CREATE OR REPLACE FUNCTION create_or_change_rel(
    p_hash TEXT,
    p_hash_type TEXT,
    p_scope_fk_hash VARCHAR(255),
    p_left_node_fk BIGINT,
    p_right_node_fk BIGINT,
    p_rel_hash TEXT,
    p_type TEXT,
    p_json_meta JSONB,
    p_display_text TEXT,
    p_start_time TIMESTAMP,
    p_end_time TIMESTAMP,
    p_last_commit_fk BIGINT
) RETURNS VOID AS $$
DECLARE
    v_rel_id BIGINT;
BEGIN
    v_rel_id := create_or_find_rel(p_hash, p_hash_type, p_scope_fk_hash, p_left_node_fk, p_right_node_fk);

    UPDATE rels
    SET hash = p_rel_hash,
        type = p_type,
        json_meta = p_json_meta,
        display_text = p_display_text,
        start_time = p_start_time,
        end_time = p_end_time,
        last_commit_fk = p_last_commit_fk
    WHERE rel_id = v_rel_id;
END;
$$ LANGUAGE plpgsql;