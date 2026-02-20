package com.project.store.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ValidationError {

    private String errorMessage;

    private String fieldName;

    @Override
    public String toString() {

        return "{fieldName:" + this.fieldName + ",errorMessage:" + this.errorMessage + "}";
    }

}
