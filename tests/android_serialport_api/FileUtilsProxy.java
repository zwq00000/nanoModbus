package android_serialport_api;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * android.os.FileUtils 代理类
 * Created by zwq00000 on 2014/7/1.
 */
public class FileUtilsProxy {
    public static final int S_IRWXU = 00700;
    public static final int S_IRUSR = 00400;
    public static final int S_IWUSR = 00200;
    public static final int S_IXUSR = 00100;

    public static final int S_IRWXG = 00070;
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007;
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    private static Method method_setPermissions;

    private static final String TAG = "FileUtilsProxy";

    static{
        Class<?> hideClass = null;
        try {
            hideClass = Class.forName("android.os.FileUtils");
            Method[] hideMethods = hideClass.getMethods();
            method_setPermissions = null;
            for (int i=0;i<hideMethods.length;i++){
                if(hideMethods[i].getName().equals("setPermissions")){
                    method_setPermissions = hideMethods[i];
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println(TAG + e.getMessage());
        }
    }

    /**
     * 设置文件权限
     * @param file
     * @param mode
     * @param uid
     * @param gid
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static int setPermissions(File file, int mode, int uid, int gid) throws FileNotFoundException {
         if(method_setPermissions == null){
             throw new NullPointerException("setPermissions method is null");
         }
        try {
            if(!file.exists()){
                throw new FileNotFoundException("文件 "+file.getAbsolutePath() + " 不存在");
            }
            Integer result = (Integer) method_setPermissions.invoke(null, file.getAbsolutePath(), mode, uid, gid);
            return result.intValue();
        } catch (IllegalAccessException e) {
            System.out.println(TAG+e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println(TAG+e.getMessage());
        }
        return -1;
    }
}
