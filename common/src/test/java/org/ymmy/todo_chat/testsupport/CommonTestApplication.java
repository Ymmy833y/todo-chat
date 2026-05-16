package org.ymmy.todo_chat.testsupport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * common モジュールは production には @SpringBootApplication を持たないライブラリなので、
 * @SpringBootTest を使うテストのためにテストスコープで最小構成を提供する。
 *
 * <p>あえて {@code org.ymmy.todo_chat.testsupport} という下位パッケージに置き、
 * {@code scanBasePackages} で common の本体パッケージを明示的に指定する。
 * これにより、reactor 経由で webapp のテストが common の test-classes を見たとしても、
 * webapp の {@code TodoChatApplication} と同じ {@code org.ymmy.todo_chat} 階層で
 * @SpringBootConfiguration が二重検出される問題を回避する。
 *
 * <p>common 側のテストはこのクラスを {@code @SpringBootTest(classes = CommonTestApplication.class)}
 * で明示的に指定する (上位パッケージのスキャンで自動発見されないため)。
 */
@SpringBootApplication(scanBasePackages = "org.ymmy.todo_chat")
@MapperScan(basePackages = "org.ymmy.todo_chat.db")
public class CommonTestApplication {
}
