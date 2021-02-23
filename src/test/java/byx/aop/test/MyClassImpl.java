package byx.aop.test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MyClassImpl implements MyClass
{
    private BiConsumer<Integer, String> checkF1Parameters = (i, s) -> {};
    private Process checkF2Reached = () -> {};
    private Consumer<String> checkF3Parameters = s -> {};


    @Override
    public void checkF1Parameters(BiConsumer<Integer, String> checkF1Parameters)
    {
        this.checkF1Parameters = checkF1Parameters;
    }

    @Override
    public void checkF2Reached(Process checkF2Reached)
    {
        this.checkF2Reached = checkF2Reached;
    }

    @Override
    public void checkF3Parameters(Consumer<String> checkF3Parameters)
    {
        this.checkF3Parameters = checkF3Parameters;
    }

    @Override
    public void f1(int i, String s)
    {
        checkF1Parameters.accept(i, s);
    }

    @Override
    public void f2()
    {
        checkF2Reached.apply();
    }

    @Override
    public void f3(String s)
    {
        checkF3Parameters.accept(s);
    }

    @Override
    public String f3(int i, String s)
    {
        return "0";
    }

    @Override
    public String f4()
    {
        return "1";
    }

    @Override
    public int f2(String s)
    {
        return 2;
    }
}
