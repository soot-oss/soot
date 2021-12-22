package xmu.wrxlab.abuilder;

import java.io.File;

/** antrance builder配置 */
public class ABuilderServerConfig {
    private String database;
    private String projectId;

    /** 重要, gradle任务有时会把classes分成多个文件, 调用多次soot, 这里判断是不是第一个, 防止cfg, idToSig等错误的初始化 */
    private boolean first;

    /** 重要, 上一个dest path, 非first时删除上一个dest path中的antrance ins,
     * 而不是非first时删除当前antrance ins, 可以思考一下antrance ins中stmt table size的问题 */
    private String preOutputPath;

    /** 重要, 语句表大小由AntranceInsTransform计算, 由AntranceInsConfigTransform配置 */
    private int stmtTableSize = 99999;

    /** 重要, 桩类名, 0号一定要为AntranceIns类 */
    private final String[] antranceInses = {
            "AntranceIns", "UnCaughtExceptionHandler", "UnCaughtExceptionHandler$1"
    };

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getProjectId() {
        return projectId;
    }

    public File getKernel() {
        return new File(database, "kernel");
    }

    public File getProject() {
        return new File(database, projectId);
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String[] getAntranceInses() {
        return antranceInses;
    }

    public int getStmtTableSize() {
        return stmtTableSize;
    }

    public void setStmtTableSize(int stmtTableSize) {
        this.stmtTableSize = stmtTableSize;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public String getPreOutputPath() {
        return preOutputPath;
    }

    public void setPreOutputPath(String preOutputPath) {
        this.preOutputPath = preOutputPath;
    }

    private static ABuilderServerConfig myConfig = new ABuilderServerConfig();
    public static ABuilderServerConfig v() {
        return myConfig;
    }

}
