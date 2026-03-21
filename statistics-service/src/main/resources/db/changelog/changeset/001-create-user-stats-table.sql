CREATE table user_stats
(
    id                    BIGSERIAL PRIMARY KEY,
    user_id                BIGINT UNIQUE NOT NULL,
    total_created         BIGINT,
    total_assigned        BIGINT,
    todo_count           BIGINT,
    in_progress_count BIGINT,
    done_count BIGINT,
    low_priority_count    BIGINT,
    medium_priority_count BIGINT,
    high_priority_count   BIGINT
);