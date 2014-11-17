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

package au.com.illyrian.classmaker;

/**
 * An exception that may be thrown while generating bytecode using a ClassMaker instance.
 *
 * @author illyrian
 */
public class ClassMakerException extends RuntimeException implements SourceLine
{
    static public final long serialVersionUID = 8974194912519382598L;

    /* The source filename */
    private final String filename;
    /* The source line number */
    private final int lineNumber;

    /**
     * Create a ClassMakerException.
     * @param sourceFile an interface providing the source filename and line number.
     * @param msg the exception message
     */
    public ClassMakerException(SourceLine sourceFile, String msg)
    {
        super(msg);
        if (sourceFile != null)
        {
        	filename   = sourceFile.getFilename();
        	lineNumber = sourceFile.getLineNumber();
        } else {
        	filename = null;
        	lineNumber = 0;
        }
    }

    /**
     * Create a ClassMakerException as a wrapper around another exception.
     * @param sourceFile an interface providing the source filename and line number.
     * @param msg the exception message
     * @param cause the wrapped exception
     */
    public ClassMakerException(SourceLine sourceFile, String msg, Exception cause)
    {
        super(msg, cause);
        lineNumber = sourceFile.getLineNumber();
        filename   = sourceFile.getFilename();
    }

    /** The source filename. */
    public String getFilename()
    {
        return filename;
    }

    /** The source line number. */
    public int getLineNumber()
    {
        return lineNumber;
    }
}
