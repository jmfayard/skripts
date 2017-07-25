package nulls;


public class Test {

    public @Nullable A maybeNull1() {
        return null;
    }

    public @javax.annotation.Nullable A maybeNull2() {
        return null;
    }

    public A neverNull() {
        return new A();
    }

    public void parameterNull(@Nullable  A a) {
        if (a == null) throw new RuntimeException();
    }

    public void parameterNotNull(A a) {

    }

}
