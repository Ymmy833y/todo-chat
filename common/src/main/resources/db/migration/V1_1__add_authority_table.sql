ALTER TABLE todo_chat.user
ADD COLUMN password VARCHAR(255) NOT NULL COMMENT 'パスワード' AFTER display_name;

CREATE TABLE If NOT EXISTS todo_chat.authority (
  id BIGINT UNSIGNED AUTO_INCREMENT COMMENT 'id',
  user_id BIGINT UNSIGNED COMMENT 'ユーザーID',
  role VARCHAR(50) NOT NULL COMMENT 'ロール',
  PRIMARY KEY (id)
) COMMENT '権限';

ALTER TABLE todo_chat.authority
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES user(id);