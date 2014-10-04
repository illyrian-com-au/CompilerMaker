package au.com.illyrian.classmaker.test;

import au.com.illyrian.classmaker.MakerFieldAccessTest.Testable;

public class TestableImpl implements Testable
{
    public void exec(TestFieldAccess test)
    {
        test.publicInt = 2;
        test.protectedInt = 3;
        test.packageInt = 4;
//        test.privateInt = 5;
    }
}
