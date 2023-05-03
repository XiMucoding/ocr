import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>OCR工具类
 * @Date 0:11 2023/5/1
 */
public class OCRUtil {

    /**
     * 创建线程池执行图片识别任务
     */
    private static ThreadPoolExecutor threadPool=new ThreadPoolExecutor(2,4,10,
            TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(1024),Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 识别在图片
     * @param bufferedImages
     */
    public static void doOCR(List<Entity> bufferedImages) {
        for (Entity entity : bufferedImages) {
            OCRRunnable runnable=new OCRRunnable(entity);
            threadPool.execute(runnable);
        }
    }
}
