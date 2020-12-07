package asm;

import java.util.Map;
import java.util.TreeMap;

//
public class Logger {
    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final Map<String,String> m=new TreeMap(){
        {
            put("I","int");
            put("Z","boolean");
            put("C","char");
            put("B","byte");
            put("S","short");
            put("J","long");
            put("L","long");
            put("D","double");

        }
    };

    public static void log(int index, Object object, int type, String name, String arrayType) {
        StringBuilder sb=new StringBuilder();
        sb.append(type==READ?"R\t":"W\t");
        String objectInfo=String.format("%d %016x\t", Thread.currentThread().getId(), System.identityHashCode(object));
        sb.append(objectInfo);
        if (index != -1) {
            String tp=m.getOrDefault(arrayType,arrayType);
            sb.append(tp+"["+index+"]\n");
        } else
            sb.append(name.replace("/", "."+"\n"));

        System.out.println(sb.toString());
    }
}
