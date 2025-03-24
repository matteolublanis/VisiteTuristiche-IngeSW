package utility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // Make it available at runtime
public @interface MethodName {
    String value();
}
