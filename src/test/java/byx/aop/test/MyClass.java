package byx.aop.test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MyClass
{
    interface Process
    {
        void apply();
    }

    void checkF1Parameters(BiConsumer<Integer, String> checkF1Parameters);
    void checkF2Reached(Process checkF2Reached);
    void checkF3Parameters(Consumer<String> checkF3Parameters);

    void f1(int i, String s);
    void f2();
    void f3(String s);
    String f3(int i, String s);
    String f4();
    int f2(String s);
}
