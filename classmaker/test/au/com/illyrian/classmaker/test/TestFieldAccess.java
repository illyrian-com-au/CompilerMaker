package au.com.illyrian.classmaker.test;

import au.com.illyrian.classmaker.MakerFieldAccessTest.Accessable;

public class TestFieldAccess implements Accessable
{
    public    int publicInt;
    protected int protectedInt;
              int packageInt;
    private   int privateInt;
    public int getPublicInt()    {return publicInt;}
    public int getProtectedInt() {return protectedInt;}
    public int getPackageInt()   {return packageInt;}
    public int getPrivateInt()   {return privateInt;}

    public    static int publicStatic;
    protected static int protectedStatic;
              static int packageStatic;
    private   static int privateStatic;
    public int getPublicStatic()    {return publicStatic;}
    public int getProtectedStatic() {return protectedStatic;}
    public int getPackageStatic()   {return packageStatic;}
    public int getPrivateStatic()   {return privateStatic;}
    public void clear()
    {
        publicStatic = 0;
        protectedStatic = 0;
        packageStatic = 0;
        privateStatic = 0;
    }
}
