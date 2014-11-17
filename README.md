CompilerMaker
=============

Compiler toolkit for JVM based Domain Specific Languages

Generating Byte Code for the Java Virtual Machine (JVM) is hard; really hard. There are other libraries that allow you to do it, but they don't make it any easier. You still have to arrange and order the byte codes yourself.

CompileMaker is different.

CompileMaker has been created with the express purpose of making class generation easy. It uses an API based on the syntax of Java to make the generation of customised classes straight forward for developers with only a modest understanding of compliers and byte-code.

CompileMaker is intended to be used to implement Domain Specific Languages (DSL). It can be directly integrated with any java based parser without the need for an intermediate Abstract Syntax Tree (AST). Having said that, there is nothing about CompileMaker that prevents generating code from an AST, but in most cases it is not necessary.

CompileMaker is designed to be extended to support new constructs. This is a boon for compiler writers who wish to explore new or alternative language features. The traditional way to develop new language features is to output java source code from the parser or AST, and then compile the generated files in a second pass. A limitation of this approach is that the debugger displays the generated java code, not the original source. CompileMaker integrates debugger support into the generated classes to allow tracing through the original source files and the display of scoped variables.
