package com.graphql.validationconstraints.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;

@Controller
public class GraphQlErrorHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleIllegalArgument(IllegalArgumentException ex) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBindException(BindException ex) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message("Validation failed: " + ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleConstraintViolation(ConstraintViolationException ex) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message("Constraint violation: " + ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleGeneric(Exception ex) {
        return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Unexpected server error")
                .build();
    }
}