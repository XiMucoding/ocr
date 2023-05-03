import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>剪切板工具类
 * @Date 0:59 2023/5/2
 */
public class ClipboardUtil {
    /**
     * 将图片放进剪切板
     * @param image
     */
    public static void setImageClipboard(Image image) {
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    /**
     * 获取剪切板的图片
     * @return
     */
    public static Image getImageClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (null  != t && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
                return image;
            }
        } catch (UnsupportedFlavorException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
