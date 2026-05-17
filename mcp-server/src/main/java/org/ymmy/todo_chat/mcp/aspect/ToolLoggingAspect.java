package org.ymmy.todo_chat.mcp.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * {@link org.springframework.ai.mcp.annotation.McpTool} アノテーションが付いた
 * メソッドの呼び出しをロギングする。
 * <p>
 * 開始時に引数を、終了時に戻り値と実行時間 (ms) を、例外時にはクラス名とメッセージ
 * を出力する。stdio transport ではコンソールが MCP プロトコル通信に使われるため、
 * 出力先は {@code application.yml} の {@code logging.file.name} で指定したファイル
 * となる。
 */
@Slf4j
@Aspect
@Component
public class ToolLoggingAspect {

  @Around("@annotation(org.springframework.ai.mcp.annotation.McpTool)")
  public Object logToolInvocation(final ProceedingJoinPoint joinPoint) throws Throwable {
    final var signature = (MethodSignature) joinPoint.getSignature();
    final var location = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    final var args = joinPoint.getArgs();

    log.info("[MCP Tool] >>> {} args={}", location, Arrays.deepToString(args));

    final var start = System.nanoTime();
    try {
      final var result = joinPoint.proceed();
      final var elapsedMs = (System.nanoTime() - start) / 1_000_000;
      log.info("[MCP Tool] <<< {} result={} ({} ms)", location, result, elapsedMs);
      return result;
    } catch (final Throwable t) {
      final var elapsedMs = (System.nanoTime() - start) / 1_000_000;
      log.warn("[MCP Tool] !!! {} threw {}: {} ({} ms)", location,
          t.getClass().getSimpleName(), t.getMessage(), elapsedMs);
      throw t;
    }
  }
}
