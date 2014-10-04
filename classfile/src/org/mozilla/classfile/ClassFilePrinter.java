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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class ClassFilePrinter
{
    private final PrintStream out;

    public ClassFilePrinter(PrintStream out)
    {
        this.out = out;
    }

    public void newline(int pc)
    {
        out.println();
        out.print(pc);
        out.print(": ");
    }

    public DataInputStream wrap(byte[] code)
    {
        return wrap(code, 0, code.length);
    }

    public DataInputStream wrap(byte[] code, int offset, int length)
    {
        ByteArrayInputStream data = new ByteArrayInputStream(code, offset, length);
        DataInputStream stream = new DataInputStream(data);
        return stream;
    }

    public void byteCode(byte[] code) throws IOException
    {
        byteCode(wrap(code));
    }

    public void byteCode(DataInputStream code) throws IOException
    {
        int i = 0;
        newline(i);
        while (code.available() > 0)
        {
            i = extra(code, i);
        }
        out.println();
    }

    public String toHexString(int b)
    {
        String hex = "0123456789ABCDEF";
        StringBuffer buf = new StringBuffer(2);
        int b1 = (b & 0xF0) >> 4;
        int b2 = b & 0xF;
        buf.append(hex.charAt(b1));
        buf.append(hex.charAt(b2));
        return buf.toString();
    }

    public int writeTableSwitch(DataInputStream code, int i) throws IOException
    {
        int start=i++;
        // Skip padding.
        while (i%4 != 0)
        {
            int z = code.readByte();
            out.print(' ');
            out.print(toHexString(z));
            i++;
        }
        newline(i);  out.print('\t');

        // default offset
        int defaultOffset = code.readInt();
        i += 4;
        out.print("Default: #" + (start + defaultOffset));
        newline(i);  out.print('\t');

        // low key
        int l = code.readInt();
        i += 4;
        out.print("Low key: " + l);
        newline(i);  out.print('\t');

        // high key
        int h = code.readInt();
        i += 4;
        out.print("High key: " + h);

        for (int j=l; j<=h; j++)
        {
            newline(i);  out.print('\t');
            // offset
            int o = code.readInt();
            i += 4;
            out.print("Goto: #" + (start + o));
        }
        newline(i);
        return i;
    }

    public int writeLookupSwitch(DataInputStream code, int i) throws IOException
    {
        int start=i++;
        // Skip padding.
        while (i%4 != 0)
        {
            int z = code.readByte();
            out.print(' ');
            out.print(toHexString(z));
            i++;
        }
        newline(i);  out.print('\t');

        // default offset
        int defaultOffset = code.readInt();
        i += 4;
        out.print("Default: #" + (start + defaultOffset));
        newline(i);  out.print('\t');

        // Number of entries
        int n = code.readInt();
        i += 4;
        out.print("Entries: " + n);

        for (int j=0; j<n; j++)
        {
            newline(i);  out.print('\t');
            // key
            int h = code.readInt();
            i += 4;
            out.print("Key: " + h);

            // offset
            int o = code.readInt();
            i += 4;
            out.print(", Goto: #" + (start + o));
        }
        newline(i);
        return i;
    }

    /*
     * Number of bytes of operands generated after the opcode.
     * Not in use currently.
     */
    public int extra(DataInputStream code, int i) throws IOException
    {
        int opcode = code.read();
        out.print(ClassFileWriter.bytecodeStr(opcode));
        switch (opcode) {
            case ByteCode.AALOAD:
            case ByteCode.AASTORE:
            case ByteCode.ACONST_NULL:
            case ByteCode.ALOAD_0:
            case ByteCode.ALOAD_1:
            case ByteCode.ALOAD_2:
            case ByteCode.ALOAD_3:
            case ByteCode.ARETURN:
            case ByteCode.ARRAYLENGTH:
            case ByteCode.ASTORE_0:
            case ByteCode.ASTORE_1:
            case ByteCode.ASTORE_2:
            case ByteCode.ASTORE_3:
            case ByteCode.ATHROW:
            case ByteCode.BALOAD:
            case ByteCode.BASTORE:
            case ByteCode.BREAKPOINT:
            case ByteCode.CALOAD:
            case ByteCode.CASTORE:
            case ByteCode.D2F:
            case ByteCode.D2I:
            case ByteCode.D2L:
            case ByteCode.DADD:
            case ByteCode.DALOAD:
            case ByteCode.DASTORE:
            case ByteCode.DCMPG:
            case ByteCode.DCMPL:
            case ByteCode.DCONST_0:
            case ByteCode.DCONST_1:
            case ByteCode.DDIV:
            case ByteCode.DLOAD_0:
            case ByteCode.DLOAD_1:
            case ByteCode.DLOAD_2:
            case ByteCode.DLOAD_3:
            case ByteCode.DMUL:
            case ByteCode.DNEG:
            case ByteCode.DREM:
            case ByteCode.DRETURN:
            case ByteCode.DSTORE_0:
            case ByteCode.DSTORE_1:
            case ByteCode.DSTORE_2:
            case ByteCode.DSTORE_3:
            case ByteCode.DSUB:
            case ByteCode.DUP2:
            case ByteCode.DUP2_X1:
            case ByteCode.DUP2_X2:
            case ByteCode.DUP:
            case ByteCode.DUP_X1:
            case ByteCode.DUP_X2:
            case ByteCode.F2D:
            case ByteCode.F2I:
            case ByteCode.F2L:
            case ByteCode.FADD:
            case ByteCode.FALOAD:
            case ByteCode.FASTORE:
            case ByteCode.FCMPG:
            case ByteCode.FCMPL:
            case ByteCode.FCONST_0:
            case ByteCode.FCONST_1:
            case ByteCode.FCONST_2:
            case ByteCode.FDIV:
            case ByteCode.FLOAD_0:
            case ByteCode.FLOAD_1:
            case ByteCode.FLOAD_2:
            case ByteCode.FLOAD_3:
            case ByteCode.FMUL:
            case ByteCode.FNEG:
            case ByteCode.FREM:
            case ByteCode.FRETURN:
            case ByteCode.FSTORE_0:
            case ByteCode.FSTORE_1:
            case ByteCode.FSTORE_2:
            case ByteCode.FSTORE_3:
            case ByteCode.FSUB:
            case ByteCode.I2B:
            case ByteCode.I2C:
            case ByteCode.I2D:
            case ByteCode.I2F:
            case ByteCode.I2L:
            case ByteCode.I2S:
            case ByteCode.IADD:
            case ByteCode.IALOAD:
            case ByteCode.IAND:
            case ByteCode.IASTORE:
            case ByteCode.ICONST_0:
            case ByteCode.ICONST_1:
            case ByteCode.ICONST_2:
            case ByteCode.ICONST_3:
            case ByteCode.ICONST_4:
            case ByteCode.ICONST_5:
            case ByteCode.ICONST_M1:
            case ByteCode.IDIV:
            case ByteCode.ILOAD_0:
            case ByteCode.ILOAD_1:
            case ByteCode.ILOAD_2:
            case ByteCode.ILOAD_3:
            case ByteCode.IMPDEP1:
            case ByteCode.IMPDEP2:
            case ByteCode.IMUL:
            case ByteCode.INEG:
            case ByteCode.IOR:
            case ByteCode.IREM:
            case ByteCode.IRETURN:
            case ByteCode.ISHL:
            case ByteCode.ISHR:
            case ByteCode.ISTORE_0:
            case ByteCode.ISTORE_1:
            case ByteCode.ISTORE_2:
            case ByteCode.ISTORE_3:
            case ByteCode.ISUB:
            case ByteCode.IUSHR:
            case ByteCode.IXOR:
            case ByteCode.L2D:
            case ByteCode.L2F:
            case ByteCode.L2I:
            case ByteCode.LADD:
            case ByteCode.LALOAD:
            case ByteCode.LAND:
            case ByteCode.LASTORE:
            case ByteCode.LCMP:
            case ByteCode.LCONST_0:
            case ByteCode.LCONST_1:
            case ByteCode.LDIV:
            case ByteCode.LLOAD_0:
            case ByteCode.LLOAD_1:
            case ByteCode.LLOAD_2:
            case ByteCode.LLOAD_3:
            case ByteCode.LMUL:
            case ByteCode.LNEG:
            case ByteCode.LOR:
            case ByteCode.LREM:
            case ByteCode.LRETURN:
            case ByteCode.LSHL:
            case ByteCode.LSHR:
            case ByteCode.LSTORE_0:
            case ByteCode.LSTORE_1:
            case ByteCode.LSTORE_2:
            case ByteCode.LSTORE_3:
            case ByteCode.LSUB:
            case ByteCode.LUSHR:
            case ByteCode.LXOR:
            case ByteCode.MONITORENTER:
            case ByteCode.MONITOREXIT:
            case ByteCode.NOP:
            case ByteCode.POP2:
            case ByteCode.POP:
            case ByteCode.RETURN:
            case ByteCode.SALOAD:
            case ByteCode.SASTORE:
            case ByteCode.SWAP:
            case ByteCode.WIDE:
                newline(++i);
                return i;

            case ByteCode.ALOAD:
            case ByteCode.ASTORE:
            case ByteCode.BIPUSH:
            case ByteCode.DLOAD:
            case ByteCode.DSTORE:
            case ByteCode.FLOAD:
            case ByteCode.FSTORE:
            case ByteCode.ILOAD:
            case ByteCode.ISTORE:
            case ByteCode.LDC:
            case ByteCode.LLOAD:
            case ByteCode.LSTORE:
            case ByteCode.NEWARRAY:
            case ByteCode.RET:
                byte op1 = code.readByte();
                out.print(' '); out.print(op1);
                i +=2;
                newline(i);
                return i;

            case ByteCode.ANEWARRAY:
            case ByteCode.CHECKCAST:
            case ByteCode.GETFIELD:
            case ByteCode.GETSTATIC:
            case ByteCode.IINC:
            case ByteCode.INSTANCEOF:
            case ByteCode.INVOKEINTERFACE:
            case ByteCode.INVOKESPECIAL:
            case ByteCode.INVOKESTATIC:
            case ByteCode.INVOKEVIRTUAL:
            case ByteCode.LDC2_W:
            case ByteCode.LDC_W:
            case ByteCode.NEW:
            case ByteCode.PUTFIELD:
            case ByteCode.PUTSTATIC:
            case ByteCode.SIPUSH:
                short op2 = code.readShort();
                out.print(' '); out.print(op2);
                i = i+3;
                newline(i);
                return i;

            case ByteCode.GOTO:
            case ByteCode.IFEQ:
            case ByteCode.IFGE:
            case ByteCode.IFGT:
            case ByteCode.IFLE:
            case ByteCode.IFLT:
            case ByteCode.IFNE:
            case ByteCode.IFNONNULL:
            case ByteCode.IFNULL:
            case ByteCode.IF_ACMPEQ:
            case ByteCode.IF_ACMPNE:
            case ByteCode.IF_ICMPEQ:
            case ByteCode.IF_ICMPGE:
            case ByteCode.IF_ICMPGT:
            case ByteCode.IF_ICMPLE:
            case ByteCode.IF_ICMPLT:
            case ByteCode.IF_ICMPNE:
            case ByteCode.JSR:
                op2 = code.readShort();
                out.print(" #"); out.print(i + op2);
                i = i+3;
                newline(i);
                return i;

            case ByteCode.MULTIANEWARRAY:
                op1 = code.readByte();
                out.print(' '); out.print(toHexString(op1));
                op1 = code.readByte();
                out.print(' '); out.print(toHexString(op1));
                op1 = code.readByte();
                out.print(' '); out.print(toHexString(op1));
                i = i+4;
                newline(i);
                return i;

            case ByteCode.GOTO_W:
            case ByteCode.JSR_W:
                int op4 = code.readInt();
                out.print(" #"); out.print(i + op4);
                i = i+5;
                newline(i);
                return i;

            case ByteCode.LOOKUPSWITCH:    // depends on alignment
                return writeLookupSwitch(code, i);
            case ByteCode.TABLESWITCH: // depends on alignment
                return writeTableSwitch(code, i);
        }
        throw new IllegalArgumentException("Bad opcode: "+opcode);
    }

}
