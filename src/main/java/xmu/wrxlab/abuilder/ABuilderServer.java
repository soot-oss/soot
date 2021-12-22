package xmu.wrxlab.abuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import fi.iki.elonen.NanoHTTPD;
import org.apache.commons.io.FileUtils;
import soot.PackManager;
import soot.Transform;
import xmu.wrxlab.abuilder.instrumentor.Instrumentor;

/** 执行antrance builder插件的任务, 分离是为了避免插件依赖冲突 */
public class ABuilderServer extends NanoHTTPD {
    private final static int myPort = 8081;

    /** 一次只能有一个soot任务执行 */
    public AtomicBoolean oneSoot = new AtomicBoolean(false);

    public ABuilderServer() throws IOException {
        super(myPort);
        // 因为要执行soot.Main, 所以设置较长的请求超时时间, 不够的话手动调整
        start(60000, false);
        System.out.println("antrance builder server start on " + myPort);
    }

    public static void main(String[] args) {
        try {
            new ABuilderServer();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    /**
     * http server路由.
     * @param session NanoHttpd默认参数
     * @return 相应请求的返回值
     */
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (Method.GET.equals(session.getMethod()) && uri.equals("/soot")) {
            Map<String, String> params = session.getParms();
            // 格式检查
            if (!params.containsKey("database") || !params.containsKey("projectId") ||
                    !params.containsKey("inputPath") || !params.containsKey("outputPath")
                    || !params.containsKey("rmIns")) {
                return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML,
                        "/soot param error");
            }
            // 一次只能有一个soot任务执行
            if (oneSoot.get()) {
                return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML,
                        "please wait the end of previous task!");
            }
            oneSoot.set(true);

            // 根据参数进行相关配置
            ABuilderServerConfig.v().setDatabase(params.get("database"));
            ABuilderServerConfig.v().setProjectId(params.get("projectId"));
            String inputPath = params.get("inputPath");
            String outputPath = params.get("outputPath");
            String rmIns = params.get("rmIns");
            ABuilderServerConfig.v().setFirst(rmIns.equals("0"));

            // 1. 将antrance ins拷贝到inputPath下
            System.out.println("[antrance builder server] copy antrance ins to " + inputPath);
            for (String antranceIns : ABuilderServerConfig.v().getAntranceInses()) {
                File kernelIns = new File(ABuilderServerConfig.v().getKernel(), antranceIns+".class");
                try {
                    FileUtils.copyFile(kernelIns, new File(inputPath, antranceIns+".class"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 2. 启动soot进行插桩, 注意soot源码很多地方用了System.exit(), 遇到这种情况要重启服务
            System.out.println("==================================================");
            System.out.println("[antrance builder server] Start");
            System.out.println("[database] " + ABuilderServerConfig.v().getDatabase());
            System.out.println("[projectId] " + ABuilderServerConfig.v().getProjectId());
            System.out.println("[inputPath] " + inputPath);
            System.out.println("[outputPath] " + outputPath);
            System.out.println("[rmIns] " + rmIns);
            System.out.println("==================================================");
            long startTime = System.currentTimeMillis();

            soot.G.reset();
            String[] args = {
                    "-allow-phantom-refs",
                    "-no-bodies-for-excluded", // 重要优化项, 防止为依赖加载方法体
                    "-process-dir", inputPath,
                    "-d", outputPath,
                    "-w",
                    "-keep-line-number"
            };
            // 将自定义pack插入wjtp, 开启静态分析+字节码修改
            Instrumentor hzyInstrumentor = new Instrumentor();
            PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", hzyInstrumentor));
            soot.Main.main(args);

            System.out.println("[antrance builder server] time = " + (System.currentTimeMillis()-startTime));
            System.out.println("==================================================");
            System.out.println("[antrance builder server] End");
            System.out.println("==================================================");

            // 3. 如果rmIns不为0, 则删除outputPath下的antrance ins, 防止类冲突
            if (!rmIns.equals("0")) {
                System.out.println("[antrance builder server] clean antrance ins in " + outputPath);
                for (String antranceIns : ABuilderServerConfig.v().getAntranceInses()) {
                    File file = new File(outputPath, antranceIns+".class");
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }

            // 别忘了解锁
            oneSoot.set(false);

            return NanoHTTPD.newFixedLengthResponse("write to " + outputPath);
        }
        return NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML,
                "please use get /soot");
    }
}