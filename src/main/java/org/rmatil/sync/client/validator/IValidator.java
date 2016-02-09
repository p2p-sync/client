package org.rmatil.sync.client.validator;

import org.rmatil.sync.client.exception.ValidationException;

public interface IValidator {

    boolean validate() throws ValidationException;
}
