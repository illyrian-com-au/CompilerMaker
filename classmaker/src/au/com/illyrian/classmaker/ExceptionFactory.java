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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import au.com.illyrian.classmaker.converters.Translator;

/**
 * A factory for generating customised error messages in the form of exceptions.
 * <br/>
 * The factory includes a resource bundle where error messages are translated.
 * The default resource bundle is called ExceptionMessages.properties and is in the
 * same package as this class.
 * A factory may be created with an alternative resource bundle that is customised to
 * the language being compiled.
 * 
 * @author dstrong
 */
public class ExceptionFactory
{
    /** Properties file containing exception messages. */
    public static final String BUNDLE_NAME = "au.com.illyrian.classmaker.ExceptionMessages";

    private final ResourceBundle resourceBundle;

    /**
     * Default constructor uses messages from ExceptionMessages.properties.
     */
    public ExceptionFactory()
    {
        this(BUNDLE_NAME);
    }

    /**
     * Constructor that uses the given file for exception messages.
     * The file must be on the class path.
     * @param bundleName name of the file containing exception messages
     */
    public ExceptionFactory(String bundleName)
    {
        resourceBundle = ResourceBundle.getBundle(bundleName);
    }

    /** The resource bundle of exception messages */
    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }

    /**
     * Get a string from the resource bundle using the given key.
     *
     * The key is returned surrounded by exclamation marks if the key does not exist,
     * e.g. <code>!ClassMaker.DoesNotExist!</code>
     * @param key the key to lookup in the resource bundle
     * @return a message from the resource bundle
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Create a <code>ClassMakerException</code> exception.
     *
     * A message corresponding to the key is fetched from the resource bundle
     * and the values are substituted into the message using positional place holders.
     *
     * e.g. <code>Method ${0} returns type ${1} so cannot return a value of type ${2}</code>
     *
     * The strings from the <code>values</code> array are substituted, as appropriate.
     *
     * @param source the source filename and line number
     * @param key the key used to lookup a message in the resource bundle
     * @param values values to be substituted into position holders in the message
     * @return a ClassMakerException with a message translated from the resource bundle
     */
    public ClassMakerException createException(SourceLine source, String key, String [] values)
    {
        String msg = getString(key);
        if (values != null) {
            msg = Translator.translate(msg, values);
        }
        return new ClassMakerException(source, msg);
    }
}
