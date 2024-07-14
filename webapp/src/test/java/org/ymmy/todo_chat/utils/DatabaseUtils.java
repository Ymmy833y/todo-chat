package org.ymmy.todo_chat.utils;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.SqlOperation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.sql.DataSource;

public class DatabaseUtils {

  public static void executeSqlFile(DataSource dataSource, String filePath) throws Exception {
    final var sqlStatements = new String(Files.readAllBytes(Paths.get(filePath)));
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
