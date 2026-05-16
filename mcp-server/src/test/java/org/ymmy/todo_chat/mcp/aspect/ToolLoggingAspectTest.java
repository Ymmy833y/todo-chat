package org.ymmy.todo_chat.mcp.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.ai.tool.annotation.Tool;

class ToolLoggingAspectTest {

  private ListAppender<ILoggingEvent> appender;
  private Logger logger;

  @BeforeEach
  void setUp() {
    logger = (Logger) LoggerFactory.getLogger(ToolLoggingAspect.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
  }

  @Test
  void 正常終了時に開始ログと終了ログが両方出る() {
    final var proxy = buildProxy(new SampleTarget());

    final var actual = proxy.echo("hi");

    assertThat(actual).isEqualTo("hi!");
    final var messages = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0)) //
        .contains("[MCP Tool] >>>") //
        .contains("SampleTarget.echo") //
        .contains("hi");
    assertThat(messages.get(1)) //
        .contains("[MCP Tool] <<<") //
        .contains("SampleTarget.echo") //
        .contains("hi!") //
        .contains("ms)");
  }

  @Test
  void 例外時には開始ログと失敗ログが出る() {
    final var proxy = buildProxy(new SampleTarget());

    assertThatThrownBy(() -> proxy.boom("trigger")) //
        .isInstanceOf(IllegalStateException.class) //
        .hasMessage("boom");

    final var messages = appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    assertThat(messages).hasSize(2);
    assertThat(messages.get(0)).contains("[MCP Tool] >>>").contains("SampleTarget.boom");
    assertThat(messages.get(1)) //
        .contains("[MCP Tool] !!!") //
        .contains("SampleTarget.boom") //
        .contains("IllegalStateException") //
        .contains("boom");
  }

  @Test
  void Toolアノテーションが付いていないメソッドはロギングされない() {
    final var proxy = buildProxy(new SampleTarget());

    proxy.notATool();

    assertThat(appender.list).isEmpty();
  }

  private SampleTarget buildProxy(final SampleTarget target) {
    final var factory = new AspectJProxyFactory(target);
    factory.addAspect(new ToolLoggingAspect());
    return factory.getProxy();
  }

  /** @Tool 付き / 非付き メソッドの混在した検証用ターゲット。 */
  static class SampleTarget {

    @Tool(name = "echo", description = "")
    public String echo(final String value) {
      return value + "!";
    }

    @Tool(name = "boom", description = "")
    public String boom(final String value) {
      throw new IllegalStateException("boom");
    }

    public void notATool() {
      // pointcut が @Tool に限定されていることを確認するためのメソッド
    }
  }
}
