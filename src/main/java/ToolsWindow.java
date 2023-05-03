import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>截图后的操作栏
 * @Date 23:53 2023/5/1
 */

public class ToolsWindow extends JWindow {
    private ScreenShotWindow parent;

    public ToolsWindow(ScreenShotWindow parent, int x, int y) {
        this.parent=parent;
        this.init();
        this.setLocation(x, y);
        this.pack();
        this.setVisible(true);
    }

    /**
     * 销毁自身对象和ScreenShotWindow对象
     */
    private void destroy(){
        parent.dispose();
        this.dispose();
    }

    private void init(){

        this.setLayout(new BorderLayout());
        JToolBar toolBar=new JToolBar();

        //复制按钮
        JButton copyButton=new JButton("复制");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage tempImage = parent.copyImage();
                //将要识别的图片放进待识别数组
                APP.bufferedImages.add(new Entity(APP.bufferedImages.size(),tempImage,null));
                APP.painting();
                //写进剪切本
                ClipboardUtil.setImageClipboard(tempImage);
                //销毁截图界面
                destroy();
            }
        });
        toolBar.add(copyButton);

        //保存按钮
        JButton saveButton=new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parent.saveImage();
                    destroy();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        toolBar.add(saveButton);

        //关闭按钮
        JButton closeButton=new JButton("取消");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destroy();
            }
        });
        toolBar.add(closeButton);

        this.add(toolBar,BorderLayout.NORTH);
    }

}
