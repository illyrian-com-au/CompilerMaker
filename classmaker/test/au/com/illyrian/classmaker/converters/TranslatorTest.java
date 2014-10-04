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

import junit.framework.TestCase;

public class TranslatorTest extends TestCase {

    public static final String BROWN_FOX = "The quick brown fox.";

    public void testPlainString() {
        Translator properties = new Translator();
        assertEquals("String should not change", BROWN_FOX, properties.translate(BROWN_FOX));
    }

    public void testProperties() {
        Translator properties = new Translator();
        properties.setProperty("test", BROWN_FOX);
        assertEquals("Property 'test' not found", BROWN_FOX, properties.getProperty("test"));
        assertEquals("Property 'test' not substituted", BROWN_FOX, properties.translate("${test}"));
    }

    public void testDollarDollar() {
        Translator trans = new Translator();
        assertEquals("abc", trans.translate("abc"));
        assertEquals("a$b", trans.translate("a$b"));
        assertEquals("a$b", trans.translate("a$$b"));
        assertEquals("a$$b", trans.translate("a$$$b"));
        assertEquals("a$$b", trans.translate("a$$$$b"));
    }

    public void testNoProperties() {
        Translator trans = new Translator();
        assertEquals("a${b}", trans.translate("a${b}"));
        assertEquals("a${b", trans.translate("a${b"));
    }
    
    public void testTranslateExample()
    {
        Translator trans = new Translator();
        trans.setProperty("name", "Able");
        assertEquals("Hello Able", trans.translate("Hello ${name}"));
    }
    
    public void testTranslateStaticExample()
    {
    	Properties properties = new Properties();
    	properties.setProperty("name", "Able");
        assertEquals("Hello Able", Translator.translate("Hello ${name}", properties));
        
    }

    public void testTranslate() {
        Translator trans = new Translator();
        trans.setProperty("a", "Able");
        trans.setProperty("b", "Baker");
        trans.setProperty("c", "Charlie");
        assertEquals("aAble", trans.translate("a${a}"));
        assertEquals("aBaker", trans.translate("a${b}"));
        assertEquals("aCharlie", trans.translate("a${c}"));
        assertEquals("AbleBakerCharlie", trans.translate("${a}${b}${c}"));
        assertEquals("a${d}", trans.translate("a${d}"));
    }

    public void testPostulateExample()
    {
        String [] names = {"Able", "Mary"};
        assertEquals("Hello Able, This is Mary.", Translator.translate("Hello ${0}, This is ${1}.", names));
    }

    public void testPostulate() {
        String [] params = {"Able", "Baker", "Charlie"};
        assertEquals("aAble", Translator.translate("a${0}", params));
        assertEquals("aBaker", Translator.translate("a${1}", params));
        assertEquals("aCharlie", Translator.translate("a${2}", params));
        assertEquals("AbleBakerCharlie", Translator.translate("${0}${1}${2}", params));
        assertEquals("a${3}", Translator.translate("a${3}", params));
    }
}
