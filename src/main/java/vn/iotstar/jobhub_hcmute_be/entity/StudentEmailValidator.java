package vn.iotstar.jobhub_hcmute_be.entity;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StudentEmailValidator implements ConstraintValidator<StudentEmail, String> {
    @Override
    public void initialize(StudentEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.endsWith("@student.hcmute.edu.vn");
    }
}
