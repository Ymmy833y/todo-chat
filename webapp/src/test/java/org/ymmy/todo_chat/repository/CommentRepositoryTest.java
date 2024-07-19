package org.ymmy.todo_chat.repository;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.ymmy.todo_chat.db.entity.Comment;
import org.ymmy.todo_chat.util.CommentStatusEnum;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@ActiveProfiles("test")
public class CommentRepositoryTest {

  @BeforeEach
  void setUp() throws Exception {
    final var commentOperation = Operations.insertInto("comment")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("comment",
            ValueGenerators.stringSequence("COMMENT_").startingAt(1)) //
        .row() //
        .column("thread_id", 1L) //
        .column("status", 100L) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("thread_id", 1L) //
        .column("status", 300L) //
        .column("created_by", 0L) //
        .end() //
        .row() //
        .column("thread_id", 1L) //
        .column("status", 100L) //
        .column("created_by", 1L) //
        .end() //
        .row() //
        .column("thread_id", 2L) //
        .column("status", 100L) //
        .column("created_by", 2L) //
        .end() //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(commentOperation));
    dbSetup.launch();

    initialComment = commentRepository.selectAll().stream()
        .collect(Collectors.toMap(Comment::getId, e -> e));
  }

  @Nested
  class UpdateStatusByThreadIdAndStatus {

    @Test
    void 指定したスレッドIDと変更前ステータスが一致するコメントのステータスが確認済みとなっている() {
      final var threadId = 1L;
      final var beforeStatus = CommentStatusEnum.USER_UNCONFIRMED.getCode();

      commentRepository.updateStatusByThreadIdAndStatus(threadId, beforeStatus);

      final var actual = commentRepository.selectAll();
      final var expect = List.of( //
          generetaComment(1L), //
          initialComment.get(2L), //
          generetaComment(3L), //
          initialComment.get(4L) //
      );

      assertThat(actual)
          .usingRecursiveComparison()
          .ignoringFieldsMatchingRegexes("createdAt")
          .isEqualTo(expect);
    }

    private Comment generetaComment(final Long commentId) {
      final var comment = initialComment.get(commentId);
      comment.setStatus(CommentStatusEnum.CONFIRMED.getCode());
      return comment;
    }
  }


  @Autowired
  CommentRepository commentRepository;

  @Autowired
  DataSource dataSource;

  Map<Long, Comment> initialComment = null;
}
