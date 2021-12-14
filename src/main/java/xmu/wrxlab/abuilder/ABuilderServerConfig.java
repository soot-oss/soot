package xmu.wrxlab.abuilder;

import java.io.File;

/** antrance builder配置 */
public class ABuilderServerConfig {
    private String database;
    private String projectId;

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

    private static ABuilderServerConfig myConfig = new ABuilderServerConfig();
    public static ABuilderServerConfig v() {
        return myConfig;
    }

}
