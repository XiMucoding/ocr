import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description <类说明>截图选择框
 * @Date 23:52 2023/5/1
 */
public class ScreenShotWindow extends JWindow {
    private int orgx, orgy, endx, endy;
    private BufferedImage image=null;
    private BufferedImage tempImage=null;
    private BufferedImage saveImage=null;

    private ToolsWindow tools=null;

    private String type;

    /**
     * 自定义截图，可以选取区域
     */
    public ScreenShotWindow(){
        try{
            //获取屏幕尺寸
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            this.setBounds(0, 0, d.width, d.height);

            //截取屏幕
            Robot robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(0, 0, d.width,d.height));

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    //鼠标松开时记录结束点坐标，并隐藏操作窗口
                    orgx = e.getX();
                    orgy = e.getY();

                    if(tools!=null){
                        tools.setVisible(false);
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    //鼠标松开时，显示操作窗口
                    if(tools==null){
                        tools=new ToolsWindow(ScreenShotWindow.this,e.getX(),e.getY());
                    }else{
                        tools.setLocation(e.getX(),e.getY());
                    }
                    tools.setVisible(true);
                    tools.toFront();
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {
                    //鼠标拖动时，记录坐标并重绘窗口
                    endx = e.getX();
                    endy = e.getY();

                    //临时图像，用于缓冲屏幕区域放置屏幕闪烁
                    Image tempImage2=createImage(ScreenShotWindow.this.getWidth(), ScreenShotWindow.this.getHeight());
                    Graphics g =tempImage2.getGraphics();
                    g.drawImage(tempImage, 0, 0, null);
                    int x = Math.min(orgx, endx);
                    int y = Math.min(orgy, endy);
                    int width = Math.abs(endx - orgx)+1;
                    int height = Math.abs(endy - orgy)+1;
                    // 加上1防止width或height0
                    g.setColor(Color.GREEN);
                    g.drawRect(x-1, y-1, width+1, height+1);
                    //减1加1都了防止图片矩形框覆盖掉
                    saveImage = image.getSubimage(x, y, width, height);
                    g.drawImage(saveImage, x, y, null);
                    ScreenShotWindow.this.getGraphics().drawImage(tempImage2,0,0, ScreenShotWindow.this);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 指定区域截图，其中X,Y,WIDTH,HEIGHT可以传入，需要事可以修改构造器，注意，不需要再次setVisible(true);
     * @param _type
     */
    public ScreenShotWindow(String _type){
        type = _type;
        try{
            //获取屏幕尺寸
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            this.setBounds(0, 0, d.width, d.height);
            this.setVisible(true);
            //截取屏幕
            Robot robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(0, 0, d.width,d.height));

            RescaleOp ro = new RescaleOp(0.8f, 0, null);
            tempImage = ro.filter(image, null);
            this.getGraphics().drawImage(tempImage, 0, 0, this);


            //临时图像，用于缓冲屏幕区域放置屏幕闪烁
            Image tempImage2=createImage(ScreenShotWindow.this.getWidth(), ScreenShotWindow.this.getHeight());
            Graphics g =tempImage2.getGraphics();
            g.drawImage(tempImage, 0, 0, null);
            int x = 100;
            int y = 100;
            int width = 100;
            int height = 100;
            // 加上1防止width或height0
            g.setColor(Color.GREEN);
            g.drawRect(x-1, y-1, width+1, height+1);
            //减1加1都了防止图片矩形框覆盖掉
            saveImage = image.getSubimage(x, y, width, height);
            g.drawImage(saveImage, x, y, null);

            this.getGraphics().drawImage(tempImage2,0,0, this);


            tools=new ToolsWindow(this,x+width,y+height);
            tools.setVisible(true);
            tools.toFront();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void paint(Graphics g) {
        if(type!=null && type != ""){
            return;
        }
        RescaleOp ro = new RescaleOp(0.8f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }

    /**
     * 保存图像到文件
     * @throws IOException
     */
    public void saveImage() throws IOException {
        JFileChooser jfc=new JFileChooser();
        jfc.setDialogTitle("保存");

        //文件过滤器，用户过滤可选择文件
        //过滤文件名，只显示jpg格式文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg");
        //将文件过滤器加入到文件选择中
        jfc.setFileFilter(filter);

        //初始化一个默认文件（此文件会生成到桌面上）
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-DD_HH-mm-ss");
        //截图命名
        String fileName = "lzk_"+simpleDateFormat.format(new Date())+".png";
        //获取系统桌面的路径
        File filePath = FileSystemView.getFileSystemView().getHomeDirectory();
        File defaultFile = new File(filePath + File.separator + fileName);
        jfc.setSelectedFile(defaultFile);

        int flag = jfc.showSaveDialog(this);
        if(flag==JFileChooser.APPROVE_OPTION){
            File file=jfc.getSelectedFile();
            String path=file.getPath();
            //检查文件后缀，放置用户忘记输入后缀或者输入不正确的后缀
            if(!(path.endsWith(".png")||path.endsWith(".PNG"))){
                path+=".png";
            }
            //写入文件
            ImageIO.write(saveImage,"png",new File(path));
        }
    }

    /**
     * 返回保存在内存中的临时图片
     * @return
     */
    public BufferedImage copyImage(){
        return saveImage;
    }
}
