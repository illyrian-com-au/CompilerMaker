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

package au.com.illyrian.classmaker.converters;

import java.util.Properties;

/**
 * Translates strings by substituting properties.
 * <br/>
 * 
 * @author dstrong
 *
 */
public class Translator {

    static final long serialVersionUID = 1L;

    static final int TEXT = 0;
    static final int DOLLAR = 1;
    static final int PROP = 2;

    Properties props;

    /** Create a translator. */
    public Translator() {
    }

    /** Create a translator with a given set of properties. */
    public Translator(Properties properties) {
        setProperties(properties);
    }

    /** The properties being applied by the translator. */
    public Properties getProperties() {
        if (props == null) {
            props = new Properties();
        }
        return props;
    }

    /** Set the properties to be applied by the translator */
    public void setProperties(Properties properties) {
        props = properties;
    }

    /** Get the named property from the translator.
     * @param name the name of the property
     */
    public String getProperty(String name) {
        return getProperties().getProperty(name);
    }

    /**
     * Set a property in the translator
     * @param name the name of the property
     * @param value the value for the property
     */
    public void setProperty(String name, String value) {
        getProperties().setProperty(name, value);
    }

    /**
     * Translate a string by substituting properties.
     <pre>
        Translator trans = new Translator();
        trans.setProperty("name", "Able");
        assertEquals("Hello Able", trans.translate("Hello ${name}"));
     </pre>
     * @param str the string to be translated
     * @return a string with substituted properties
     */
    public String translate(String str) {
        return translate(str, getProperties());
    }

    /**
     * Translate a string by substituting properties.
     <pre>
    	Properties properties = new Properties();
    	properties.setProperty("name", "Able");
        assertEquals("Hello Able", Translator.translate("Hello ${name}", properties));
     </pre>
     * @param str the string to be translated
     * @param props the properties to be substituted
     * @return a string with substituted properties
     */
    static public String translate(String str, Properties props) {
        StringBuffer buf = new StringBuffer();
        int offset = 0;
        int state = TEXT;

        //System.out.println(str);
        for (int i=0; i<str.length(); i++) {
            char ch = str.charAt(i);
            switch (state) {
            case TEXT :
                    if (ch == '$') {
                        state = DOLLAR;
                    } else {
                        buf.append(ch);
                    }
                    break;
            case DOLLAR :
                    if (ch == '{') {
                        state = PROP;
                        offset = i;
                    } else {
                        // The sequence was not ${
                        state = TEXT;
                        buf.append('$');
                        // $$ is replaced by a single $
                        if (ch != '$')
                            buf.append(ch);
                    }
                    break;
            case PROP :
                    if (ch == '}') {
                        state = TEXT;
                        // Extract the name of the property
                        String name = str.substring(offset+1, i);
                        //System.out.println("Name=" + name);
                        // Append the property value to the buffer
                        String value = props.getProperty(name);
                        if (value != null) {
                            buf.append(value);
                        } else {
                            buf.append(str.substring(offset-1, i+1));
                        }
                    }
                    break;
            }
            //System.out.println(buf.toString());
        }

        // Append any residual to the buffer.
        if (state == PROP) {
            buf.append(str.substring(offset-1));
        } else if (state == DOLLAR) {
            buf.append('$');
        }
        //System.out.println("=" + buf.toString());
        return buf.toString();
    }

    /**
     * Translate a string by substituting values from an array of Strings.
     <pre>
        String [] names = {"Able", "Mary"};
        assertEquals("Hello Able, This is Mary.", Translator.translate("Hello ${0}, This is ${1}.", names));
     </pre>
     * @param str the string to be translated
     * @param props an array of values to be substituted
     * @return a string with substituted values
     */
    static public String translate(String str, String [] props) {
        StringBuffer buf = new StringBuffer();
        int offset = 0;
        int index = 0;
        int state = TEXT;

        //System.out.println(str);
        for (int i=0; i<str.length(); i++) {
            char ch = str.charAt(i);
            switch (state) {
            case TEXT :
                    if (ch == '$') {
                        state = DOLLAR;
                    } else {
                        buf.append(ch);
                    }
                    break;
            case DOLLAR :
                    if (ch == '{') {
                        state = PROP;
                        offset = i;
                        index = 0;
                    } else {
                        // The sequence was not ${
                        state = TEXT;
                        buf.append('$');
                        // $$ is replaced by a single $
                        if (ch != '$')
                            buf.append(ch);
                    }
                    break;
            case PROP :
                    if (ch == '}') {
                        state = TEXT;
                        //System.out.println("Name=" + name);
                        if (index < 0 || index >= props.length)
                        	buf.append("${" + index + "}");
                        else
                        {
	                        // Append the property value to the buffer
	                        String value = props[index];
	                        if (value != null) {
	                            buf.append(value);
	                        } else {
	                            buf.append(str.substring(offset-1, i+1));
	                        }
                        }
                    }
                    else if ('0' <= ch && ch <= '9')
                    {
                        index = index*10 + ch - '0';
                    }
                    else
                        throw new RuntimeException("Index must be a number: " + str);

                    break;
            }
            //System.out.println(buf.toString());
        }

        // Append any residual to the buffer.
        if (state == PROP) {
            buf.append(str.substring(offset-1));
        } else if (state == DOLLAR) {
            buf.append('$');
        }
        //System.out.println("=" + buf.toString());
        return buf.toString();
    }
}
