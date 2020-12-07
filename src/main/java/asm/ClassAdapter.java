package asm;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class ClassAdapter extends ClassVisitor {
    private ClassVisitor cv;
    private int acc = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL;
    public ClassAdapter(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.startsWith("java") || name.startsWith("sun"))
            return cv.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        mv = new MethodAdapter(mv);
        return mv;
    }
}
