# DB定義書

## テーブル定義書

### task / タスク

| カラム名            | 論理名        | 型            | PK | NOT NULL | AUTO INCR | DEFA・補足           |
|-----------------|------------|--------------|----|----------|-----------|-------------------|
| id              | タスクID      | BIGINT       | Y  | Y        | Y         |                   |
| status_id       | タスクステータスID | BIGINT       | N  | Y        | N         | 1                 |
| title           | タイトル       | VARCHAR(100) | N  | Y        | N         |                   |
| description     | 説明         | TEXT         | N  | N        | N         |                   |
| start_date_time | 開始日時       | DATETIME     | N  | Y        | N         |                   |
| end_date_time   | 終了日時       | DATETIME     | N  | Y        | N         |                   |
| created_at      | 作成日時       | DATETIME     | N  | Y        | N         | CURRENT_TIMESTAMP |
| created_by      | 作成者        | BIGINT       | N  | Y        | N         |                   |
| updated_at      | 更新日時       | DATETIME     | N  | Y        | N         | CURRENT_TIMESTAMP |
| version         | バージョン      | BIGINT       | N  | Y        | N         | 1                 |

### task_status / タスクステータス

| カラム名         | 論理名        | 型           | PK | NOT NULL | AUTO INCR | DEFA・補足           |
|--------------|------------|-------------|----|----------|-----------|-------------------|
| id           | タスクステータスID | BIGINT      | Y  | Y        | Y         |                   |
| name         | ステータス名     | VARCHAR(50) | N  | Y        | N         |                   |
| color_code   | 表示色        | VARCHAR(6)  | N  | Y        | N         |                   |
| is_read_only | 読み取り専用     | BOOLEAN     | N  | Y        | N         | TRUE              |
| remarks      | 説明         | TEXT        | N  | N        | N         |                   |
| created_at   | 作成日時       | DATETIME    | N  | Y        | N         | CURRENT_TIMESTAMP |
| created_by   | 作成者        | BIGINT      | N  | Y        | N         |                   |

### user / ユーザー

| カラム名         | 論理名    | 型           | PK | NOT NULL | AUTO INCR | DEFA・補足           |
|--------------|--------|-------------|----|----------|-----------|-------------------|
| id           | ユーザーID | BIGINT      | Y  | Y        | Y         |                   |
| name         | ユーザー名  | VARCHAR(50) | N  | Y        | N         | UNIQUE            |
| display_name | 表示名    | VARCHAR(50) | N  | Y        | N         |                   |
| created_at   | 作成日時   | DATETIME    | N  | Y        | N         | CURRENT_TIMESTAMP |
| created_by   | 作成者    | BIGINT      | N  | Y        | N         | 0                 |
| version      | バージョン  | BIGINT      | N  | Y        | N         | 1                 |

### comment / コメント

| カラム名       | 論理名    | 型        | PK | NOT NULL | AUTO INCR | DEFA・補足           |
|------------|--------|----------|----|----------|-----------|-------------------|
| id         | コメントID | BIGINT   | Y  | Y        | Y         |                   |
| thread_id  | スレッドID | BIGINT   | N  | Y        | N         |                   |
| comment    | コメント   | TEXT     | N  | Y        | N         |                   |
| status     | ステータス  | BIGINT   | N  | Y        | N         |                   |
| created_at | 作成日時   | DATETIME | N  | Y        | N         | CURRENT_TIMESTAMP |
| created_by | 作成者    | BIGINT   | N  | Y        | N         | 0                 |
