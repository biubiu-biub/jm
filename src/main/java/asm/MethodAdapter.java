package asm;

import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class MethodAdapter extends MethodVisitor {
    private MethodVisitor mv;

    private boolean isStatic = false;
    private String curOwner;
    private String arrayType;


    public MethodAdapter(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
        this.mv = mv;
    }

    @Override
    public void visitInsn(int opcode) {
        if((opcode>=Opcodes.IASTORE)&&(opcode<=Opcodes.AASTORE)){
            if (opcode == Opcodes.LASTORE || opcode == Opcodes.DASTORE) {
                mv.visitInsn(Opcodes.DUP2_X2);
                mv.visitInsn(Opcodes.POP2);
                mv.visitInsn(Opcodes.DUP);
            }
            else {
                mv.visitInsn(Opcodes.DUP2);
                mv.visitInsn(Opcodes.POP);
            }
            if (isStatic)
                mv.visitLdcInsn(curOwner);
            else
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ICONST_2);
            mv.visitLdcInsn("");
            mv.visitLdcInsn(arrayType);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getInternalName(Logger.class),
                    "log",
                    "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                    false);
            if (opcode == Opcodes.LASTORE || opcode == Opcodes.DASTORE) {
                mv.visitInsn(Opcodes.DUP2_X2);
                mv.visitInsn(Opcodes.POP2);
            }
        }else if((opcode>=Opcodes.IALOAD)&&(opcode<=Opcodes.AALOAD)){
            mv.visitInsn(Opcodes.DUP);
            if (isStatic)
                mv.visitLdcInsn(curOwner);
            else
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitLdcInsn("");
            mv.visitLdcInsn(arrayType);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getInternalName(Logger.class),
                    "log",
                    "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                    false);
        }
//        switch (opcode) {
//            case Opcodes.IASTORE:
//            case Opcodes.CASTORE:
//            case Opcodes.BASTORE:
//            case Opcodes.SASTORE:
//            case Opcodes.LASTORE:
//            case Opcodes.FASTORE:
//            case Opcodes.DASTORE:
//            case Opcodes.AASTORE:
//                /*
//                 * [member index value] -> [member index value index value] -> [member index value index] -> ... -> [member index value index object rw name]
//                 * long/double: [member index value1 value2] -> [member index value1 value2
//                 */
//
//                break;
//            case Opcodes.IALOAD:
//            case Opcodes.CALOAD:
//            case Opcodes.BALOAD:
//            case Opcodes.SALOAD:
//            case Opcodes.LALOAD:
//            case Opcodes.FALOAD:
//            case Opcodes.DALOAD:
//            case Opcodes.AALOAD:
//                /*
//                 * [member index] -> [member index index] -> ... -> [member index index object rw name]
//                 */
//
//                break;
//        }
        mv.visitInsn(opcode);
    }
    // handle instructions accessing fields like getfield/putfield
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        //ignore java/sun classes...
        if (owner.startsWith("java") || owner.startsWith("sun")) {
            mv.visitFieldInsn(opcode, owner, name, desc);
            return;
        }

        if((opcode>=Opcodes.GETSTATIC)&&(opcode<=Opcodes.PUTFIELD)){
            if (desc.startsWith("[")) {
                isStatic = opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC;
                curOwner = owner;
                arrayType = desc.substring(desc.indexOf("[") + 1).replace("/", ".");
            }

            //
            mv.visitInsn(Opcodes.ICONST_M1);
            if (opcode == Opcodes.GETFIELD || opcode == Opcodes.PUTFIELD)
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            else
                mv.visitLdcInsn(owner);

            // push rw type
            if (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC)
                mv.visitInsn(Opcodes.ICONST_0);
            else
                mv.visitInsn(Opcodes.ICONST_1);

            // push name
            mv.visitLdcInsn(owner + "." + name);

            //cant be array,just push empty string
            mv.visitLdcInsn("");
            //call logger
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getInternalName(Logger.class),
                    "log",
                    "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                    false);
        }
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
}
