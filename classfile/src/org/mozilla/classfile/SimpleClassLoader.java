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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

public class SimpleClassLoader extends ClassLoader
{
    public SimpleClassLoader() {
        this.parentLoader = getClass().getClassLoader();
    }

    public SimpleClassLoader(ClassLoader parentLoader) {
        this.parentLoader = parentLoader;
    }

    public Class defineClass(String className, byte[] classBytes)
    {
        String classDotName = className.replace('/', '.');
        // The generated classes in this case refer only to Rhino classes
        // which must be accessible through this class loader
        Exception e;
        try {
            Class<?> cl = super.defineClass(classDotName, classBytes, 0, classBytes.length,
                    SimpleClassLoader.getProtectionDomain(getClass()));
            resolveClass(cl);
            return cl;
        } catch (SecurityException x) {
            e = x;
        } catch (IllegalArgumentException x) {
            e = x;
        }
        throw new RuntimeException("Malformed optimizer package " + e);
    }

    public void linkClass(Class<?> cl) {
        resolveClass(cl);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        Class<?> cl = findLoadedClass(name);
        if (cl == null) {
            if (parentLoader != null) {
                cl = parentLoader.loadClass(name);
            } else {
                cl = findSystemClass(name);
            }
        }
        if (resolve) {
            resolveClass(cl);
        }
        return cl;
    }

    public static ProtectionDomain getProtectionDomain(final Class<?> clazz)
    {
        return (ProtectionDomain)AccessController.doPrivileged(
                new PrivilegedAction<Object>()
                {
                    public Object run()
                    {
                        return clazz.getProtectionDomain();
                    }
                });
    }

    private final ClassLoader parentLoader;
}
