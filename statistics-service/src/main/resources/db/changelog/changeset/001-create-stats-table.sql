CREATE table global_task_stats (
    id BIGSERIAL PRIMARY KEY,
    total_created BIGINT,
    total_in_progress BIGINT,
    total_done BIGINT,
    low_priority_count BIGINT,
    medium_priority_count BIGINT,
    high_priority_count BIGINT
);