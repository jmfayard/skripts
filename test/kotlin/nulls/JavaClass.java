package nulls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class JavaClass {

    private static Random random = new Random();

    public static @Nonnull  JavaClass create() {
        return new JavaClass();
    }

    public @Nonnull String greeting(String name) {
        checkNotNull(name, "name");
        return "Hello " + name;
    }

    public @Nullable  String nullableSearch(@Nullable String param1, String param2) {
        checkNotNull(param2, "param2");
        if (random.nextBoolean()) {
            return null;
        } else {
            return "hello";
        }
    }

    private static void checkNotNull(@Nullable Object o, String name) {
        if (o == null) throw new IllegalStateException(name + " was null");
    }

}
