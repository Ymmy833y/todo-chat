CREATE TABLE If NOT EXISTS todo_chat.task_status (
  id BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(50) NOT NULL COMMENT 'ステータス名',
  color_code VARCHAR(6) NOT NULL COMMENT '表示色',
  is_read_only BOOLEAN NOT NULL DEFAULT TRUE COMMENT '読み取り専用',
  remarks TEXT COMMENT '備考',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  created_by BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '作成者',
  PRIMARY KEY (id)
);

INSERT INTO todo_chat.task_status (id, name, color_code, remarks) VALUES
  (1, '未着手', 'fdd', 'まだ開始されていないタスク'),
  (2, '進行中', 'dff', '現在進行中のタスク'),
  (3, '完了', 'dfd', '完了したタスク'),
  (4, '保留', 'ddf', '一時的に中断されているタスク');

CREATE TABLE If NOT EXISTS todo_chat.task (
  id BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'id',
  status_id BIGINT UNSIGNED NOT NULL COMMENT 'ステータスid',
  title VARCHAR(100) NOT NULL COMMENT 'タイトル',
  description TEXT COMMENT '説明',
  start_date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '開始日時',
  end_date_time DATETIME NOT NULL COMMENT '終了日時',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  created_by BIGINT UNSIGNED NOT NULL COMMENT '作成者',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  version BIGINT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'バージョン',
  PRIMARY KEY (id)
);

ALTER TABLE todo_chat.task
ADD CONSTRAINT fk_task_status
FOREIGN KEY (status_id) REFERENCES task_status(id);
