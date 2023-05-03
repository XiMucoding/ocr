import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>
 * @Date 19:55 2023/5/1
 */
public class OCRRunnable implements Runnable {
    private Entity entity;

    /**
     * 存储线程池中的每个线程对应的ocr实例
     */
    private static ThreadLocal<ITesseract>OCRITesseract=new ThreadLocal<ITesseract>();


    public OCRRunnable(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void run() {
        //获取对应的ocr实例
        ITesseract instance=null;
        if (OCRITesseract.get()==null){
            // 创建ocr实例
            ITesseract ins = new Tesseract();
            // 设置语言模型数据路径
            ins.setDatapath("src/main/resources/data");
            // 设置识别语言
            ins.setLanguage("chi_sim");
            // 设置识别引擎
            ins.setOcrEngineMode(1);
            instance=ins;
            instance.setLanguage(APP.language);
            //给改线程配置一个ITesseract对象用于识别
            OCRITesseract.set(ins);
        }else{
            instance=OCRITesseract.get();
            instance.setLanguage(APP.language);
        }

        try {
            String result =null;
            result = instance.doOCR(entity.getImage());
            //将结果写回数组
            APP.bufferedImages.get(entity.getIndex()).setOCRResult(result);
            //判断是否识别完毕
            APP.showDialog();
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

    }
}
