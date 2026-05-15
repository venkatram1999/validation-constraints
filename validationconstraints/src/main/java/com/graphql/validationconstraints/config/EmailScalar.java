package com.graphql.validationconstraints.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import java.util.Locale;
import java.util.regex.Pattern;

public class EmailScalar {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static final GraphQLScalarType EMAIL = GraphQLScalarType.newScalar()
            .name("Email")
            .description("Custom Email scalar")
            .coercing(new Coercing<String, String>() {

                @Override
                public String serialize(Object dataFetcherResult,
                                        GraphQLContext context,
                                        Locale locale) {
                    String value = String.valueOf(dataFetcherResult);
                    if (!EMAIL_PATTERN.matcher(value).matches()) {
                        throw new CoercingSerializeException("Invalid email format");
                    }
                    return value;
                }

                @Override
                public String parseValue(Object input,
                                         GraphQLContext context,
                                         Locale locale) {
                    if (!(input instanceof String value) ||
                            !EMAIL_PATTERN.matcher(value.trim()).matches()) {
                        throw new CoercingParseValueException("Invalid email format");
                    }
                    return value.trim().toLowerCase();
                }

                @Override
                public String parseLiteral(Value<?> input,
                                           CoercedVariables variables,
                                           GraphQLContext context,
                                           Locale locale) {
                    if (!(input instanceof StringValue stringValue)) {
                        throw new CoercingParseLiteralException("Email must be a string");
                    }

                    String value = stringValue.getValue();
                    if (!EMAIL_PATTERN.matcher(value.trim()).matches()) {
                        throw new CoercingParseLiteralException("Invalid email format");
                    }
                    return value.trim().toLowerCase();
                }
            })
            .build();
}