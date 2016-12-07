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

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Formats fields annotated with the {@link EntityFormat} annotation using
 * a {@link EntityPrinter} or {@link EntityMessagePrinter} to generate a String from the given 
 * entity, and gets an Entity instance from its id through an {@link EntityParser}. 
 * 
 * @author Cèsar Ordiñana at http://www.disid.com[DISID Corporation S.L.]
 */
public class EntityFormatAnnotationFormatterFactory extends EmbeddedValueResolutionSupport
    implements AnnotationFormatterFactory<EntityFormat> {

  private static final Set<Class<?>> FIELD_TYPES;

  private static final SpelExpressionParser PARSER;

  private static final TemplateParserContext PARSER_CONTEXT;

  private final MessageSource messageSource;

  private final ConversionService conversionService;

  private final Map<Class<?>, EntityResolver<?, ?>> entity2Resolver;

  static {
    Set<Class<?>> fieldTypes = new HashSet<Class<?>>(1);
    fieldTypes.add(Object.class);
    FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    PARSER = new SpelExpressionParser();
    PARSER_CONTEXT = new TemplateParserContext();
  }

  /**
   * Creates a new factory with the given {@link MessageSource} to get messages
   * depending on the locale.
   * @param messageSource to get i18n messages from
   */
  public EntityFormatAnnotationFormatterFactory(MessageSource messageSource,
      ListableBeanFactory beanFactory, ConversionService conversionService) {
    this.messageSource = messageSource;
    this.conversionService = conversionService;
    this.entity2Resolver = loadEntityResolvers(beanFactory);
  }

  @Override
  public Set<Class<?>> getFieldTypes() {
    return FIELD_TYPES;
  }

  @Override
  public Printer<?> getPrinter(EntityFormat annotation, Class<?> fieldType) {
    String messageCode = getExpressionMessageCode(annotation, fieldType);
    if (StringUtils.isEmpty(messageCode)) {
      String expression = getExpression(annotation, fieldType);
      return new EntityPrinter(expression, PARSER, PARSER_CONTEXT);
    }

    return new EntityMessagePrinter(messageCode, messageSource, PARSER, PARSER_CONTEXT);
  }

  private String getExpressionMessageCode(EntityFormat annotation, Class<?> fieldType) {
    EntityFormat processedAnnotation = AnnotationUtils.getAnnotation(annotation, EntityFormat.class);
    String message = processedAnnotation.message();
    if (StringUtils.isEmpty(message)) {
      EntityFormat classFormatAnnotation =
          AnnotatedElementUtils.findMergedAnnotation(fieldType, EntityFormat.class);
      if (classFormatAnnotation != null) {
        message = classFormatAnnotation.message();
      }
    }
    return message;
  }

  private String getExpression(EntityFormat annotation, Class<?> fieldType) {
    EntityFormat processedAnnotation = AnnotationUtils.getAnnotation(annotation, EntityFormat.class);
    String expression = processedAnnotation.expression();
    if (StringUtils.isEmpty(expression)) {
      EntityFormat classFormatAnnotation =
          AnnotatedElementUtils.findMergedAnnotation(fieldType, EntityFormat.class);
      if (classFormatAnnotation != null) {
        expression = classFormatAnnotation.expression();
      }
    }
    return expression;
  }

  @SuppressWarnings({"rawtypes"})
  public static Map<Class<?>, EntityResolver<?, ?>> loadEntityResolvers(
      ListableBeanFactory beanFactory) {
    Map<String, EntityResolver> entityServices = beanFactory.getBeansOfType(EntityResolver.class);
    Map<Class<?>, EntityResolver<?, ?>> entity2Resolver = new HashMap<>(entityServices.size());
    for (EntityResolver entityResolver : entityServices.values()) {
      entity2Resolver.put(entityResolver.getEntityType(), entityResolver);
    }
    return entity2Resolver;
  }

  @Override
  public Parser<?> getParser(EntityFormat annotation, Class<?> fieldType) {

    Assert.notNull(annotation, "The EntityFormat annotation is required");
    Assert.notNull(fieldType, "The Class of the field to parse is required");

    EntityResolver<?, ?> resolver = entity2Resolver.get(fieldType);
    if (resolver == null) {
      throw new IllegalArgumentException(
          "Not found a required EntityService bean for the type: " + fieldType);
    }

    return new EntityParser<>(resolver, conversionService);
  }

}