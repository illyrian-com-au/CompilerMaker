// Copyright (c) 2010, Donald Strong.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.

package org.mozilla.classfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class SimpleClassLoader extends ClassLoader
{
    private Map<String, Class<?>> localClasses = new HashMap<String, Class<?>>();
    private File classesDir = null;
    
    public SimpleClassLoader()
    {
        this.parentLoader = getClass().getClassLoader();
    }

    public SimpleClassLoader(ClassLoader parentLoader)
    {
        this.parentLoader = parentLoader;
    }

    public File getClassesDir()
    {
        return classesDir;
    }

    public void setClassesDir(File classesDir)
    {
        this.classesDir = classesDir;
    }

    public Map<String, Class<?>> getLocalClasses()
    {
        return localClasses;
    }

    public ClassLoader getParentLoader()
    {
        return parentLoader;
    }
    
    protected Package findCreatePackage(String className) {
        Package pkg = null;
        int index = className.lastIndexOf('.');
        if (index > -1) {
            String packageName = className.substring(0, index);
            pkg = getPackage(packageName);
            if (pkg == null) {
                pkg = definePackage(packageName, null, null, null, null, null, null, null);
            }
            if (pkg == null) {
                throw new IllegalStateException("Could not find of create package: " + packageName);
            }
        }
        return pkg;
    }

    public Class<?> defineClass(String className, byte[] classBytes)
    {
        String classDotName = className.replace('/', '.');
        findCreatePackage(classDotName);
        Exception e;
        try {
            Class<?> cl = super.defineClass(classDotName, classBytes, 0, classBytes.length,
                    SimpleClassLoader.getProtectionDomain(getClass()));
            resolveClass(cl);
            localClasses.put(classDotName, cl);
            return cl;
        } catch (SecurityException x) {
            e = x;
        } catch (IllegalArgumentException x) {
            e = x;
        }
        throw new RuntimeException("Could not load class from byte array: " + className, e);
    }

    protected Class<?> findLocalClass(String name) throws ClassNotFoundException {
        Class<?> cl = localClasses.get(name);
        return cl;
    }

    /**
     * Finds the class with the specified <a href="#name">binary name</a>.
     * This method should be overridden by class loader implementations that
     * follow the delegation model for loading classes, and will be invoked by
     * the {@link #loadClass <tt>loadClass</tt>} method after checking the
     * parent class loader for the requested class.  The default implementation
     * throws a <tt>ClassNotFoundException</tt>.  </p>
     *
     * @param  name
     *         The <a href="#name">binary name</a> of the class
     *
     * @return  The resulting <tt>Class</tt> object
     *
     * @throws  ClassNotFoundException
     *          If the class could not be found
     *
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cl = findLocalClass(name);
        if (cl == null) {
            String className = name.replace('.', '/') + ".class";
            File classFile = new File(classesDir, className);
            if (!classFile.exists()) {
                throw new ClassNotFoundException(name);
            }
            // Load byte stream for class
            byte[] bytes;
            try {
                FileInputStream input = new FileInputStream(classFile);
                bytes = getBytes(input);
                cl = defineClass(name, bytes);
            } catch (IOException ex) {
                throw new ClassNotFoundException(className);
            }
        }
        return cl;
    }

    byte[] getBytes(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[2048];
            int read = 0;
            while (in.available() > 0) {
                read = in.read(buffer, 0, buffer.length);
                if (read < 0) {
                    break;
                }
                out.write(buffer, 0, read);
            }
        } finally {
            out.close();
        }
        return out.toByteArray();
    }

    public static ProtectionDomain getProtectionDomain(final Class<?> clazz)
    {
        return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<Object>()
        {
            public Object run()
            {
                return clazz.getProtectionDomain();
            }
        });
    }

    private final ClassLoader parentLoader;
}
