package org.ymmy.todo_chat.utils;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.SqlOperation;
import java.util.Arrays;
import javax.sql.DataSource;

public class DatabaseUtils {

  /**
   * クラスパス上の SQL ファイルを読み込み、; 区切りで分割して順次実行する。
   *
   * <p>resourcePath はテストクラスパスからの相対パス (例: "db/deleteAll.sql")。
   * common モジュールの test-jar として webapp 等の下流モジュールからも参照できるよう、
   * ファイルシステムではなくクラスローダ経由で読む。
   */
  public static void executeSqlFile(DataSource dataSource, String resourcePath) throws Exception {
    final String sqlStatements;
    try (var stream = DatabaseUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (stream == null) {
        throw new IllegalArgumentException("Resource not found on classpath: " + resourcePath);
      }
      sqlStatements = new String(stream.readAllBytes());
    }
    final var deleteOperations = Arrays.stream(sqlStatements.split(";"))
        .map(String::trim)
        .filter(sql -> !sql.isEmpty())
        .map(SqlOperation::of)
        .toArray(SqlOperation[]::new);

    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(deleteOperations));
    dbSetup.launch();
  }
}
