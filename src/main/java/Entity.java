import java.awt.image.BufferedImage;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>识别实体
 * @Date 19:25 2023/5/2
 */
public class Entity {
    /**
     * 在数组中的索引
     */
    private int index;
    /**
     * 图片源
     */
    private BufferedImage image;
    /**
     * 图片识别完的结果
     */
    private String OCRResult;

    public Entity() {
    }

    public Entity(int index, BufferedImage image, String OCRResult) {
        this.index = index;
        this.image = image;
        this.OCRResult = OCRResult;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getOCRResult() {
        return OCRResult;
    }

    public void setOCRResult(String OCRResult) {
        this.OCRResult = OCRResult;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
