import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>监听界面变化重画界面
 * @Date 15:57 2023/5/2
 */
public class ResizeListener implements ComponentListener {

    @Override
    public void componentResized(ComponentEvent e) {
        APP.painting();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
