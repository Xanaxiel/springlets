/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.springlets.format;

import org.springframework.context.MessageSource;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;

import java.util.Locale;

/**
 * A Printer for objects values. It uses a SpEL expression to build a String
 * with the object properties and methods. The SpEL to use depends on the Locale provided.
 * 
 * @author Cèsar Ordiñana at http://www.disid.com[DISID Corporation S.L.]
 */
public class SpElMessagePrinter extends AbstractSpElPrinter {

  private final MessageSource messageSource;

  /**
   * Creates a new instance with the given expression and expression parser.
   * @param messageCode the code of the message with the SpEL expression 
   *        to generate a String from the provided objects
   * @param messageSource to get the SpEL expression related to a given {@link Locale}
   * @param parser to parse the expression
   * @param templateParserContext context to use to parse the expression
   */
  public SpElMessagePrinter(String messageCode, MessageSource messageSource,
      ExpressionParser parser, TemplateParserContext templateParserContext) {
    super(messageCode, parser, templateParserContext);
    this.messageSource = messageSource;
  }

  @Override
  protected Expression parseExpression(Locale locale) {
    String expressionMessage = getExpression() == null ? getDefaultExpression()
        : messageSource.getMessage(getExpression(), null, getDefaultExpression(), locale);
    return parseExpression(expressionMessage);
  }
}
