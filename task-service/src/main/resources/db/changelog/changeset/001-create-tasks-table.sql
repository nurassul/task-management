CREATE TABLE tasks
(
    id BIGSERIAL PRIMARY KEY ,
    creator_id BIGINT NOT NULL,
    assigned_user_id BIGINT NOT NULL,
    task_status VARCHAR(50) NOT NULL,
    description_of_task VARCHAR(200),
    create_datetime DATE NOT NULL,
    deadline_datetime DATE NOT NULL,
    priority VARCHAR(50) NOT NULL,
    done_datetime TIMESTAMP
)