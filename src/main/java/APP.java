import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author lzk
 * @Email 1801290586@qq.com
 * @Description 主窗口类
 * @Date 17:10 2023/5/1
 */
public class APP {
    /**
     * 界面
     */
    private static JFrame jFrame=new JFrame("ORC");
    /**
     * 导航栏
     */
    private static JMenuBar jMenuBar=new JMenuBar();
    /**
     * 导航栏字体
     */
    private static Font NavFont=new Font("宋体",Font.PLAIN,18);

    /**
     * 正文字体
     */
    private static Font TextFont=new Font("宋体",Font.PLAIN,20);

    /**
     * 布局的面板
     */
    private static JPanel CenterJPanel=new JPanel();
    private static JPanel BottomJPanel=new JPanel();
    private static JPanel LeftJPanel=new JPanel();
    private static JPanel RightJPanel=new JPanel();

    /**
     * 图片资源
     */
    public static List<Entity> bufferedImages=new CopyOnWriteArrayList<>();
    /**
     * 要显示哪张图
     */
    private static int index=0;

    /**
     * 要识别的语言
     */
    public static String language="chi_sim";
    /**
     * 开始识别按钮
     */
    private static JButton start=new JButton("识别");
    /**
     * 图片切换按钮
     */
    private static JButton pre=new JButton("上一页");
    private static JButton next=new JButton("下一页");


    /**
     * 等所有图片都识别完毕就刷新
     */
    private static AtomicInteger count;

    /**
     * 结果显示框
     */
    private static JTextArea ansTextArea=new JTextArea();


    /**
     * 设置文件选择器，和选择文件之后的逻辑
     * @return 文件选择器
     */
    private static JFileChooser initFileChooser(){
        JFileChooser fileChooser=new JFileChooser();
        //设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));
        //设置只能选择文件
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //设置可以多选
        fileChooser.setMultiSelectionEnabled(true);
        //添加可用的文件过滤器
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("image","jpg","png"));
        //设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("image","jpg","png"));

        //打开文件选择框，阻塞等待获取选择的文件信息
        int res = fileChooser.showOpenDialog(jFrame);
        if (res==JFileChooser.APPROVE_OPTION){
            //如果选择了文件，获取图片信息进行文本识别
            File[] selectedFiles = fileChooser.getSelectedFiles();
            //将文件进行内存转为BufferedImage
            try {
                for (File selectedFile : selectedFiles) {
                    bufferedImages.add(new Entity(bufferedImages.size(), ImageIO.read(selectedFile),null));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            painting();
        }
        return fileChooser;
    }

    /**
     * 文件夹选择框，并识别文件下的图片
     * @return
     */
    private static JFileChooser initFolderChooser(){
        JFileChooser folderChooser=new JFileChooser();
        //设置只能选择文件夹
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //打开文件夹选择框，阻塞等待获取选择的文件夹信息
        int res = folderChooser.showOpenDialog(jFrame);
        if (res==JFileChooser.APPROVE_OPTION){
            String absolutePath = folderChooser.getSelectedFile().getAbsolutePath();
            File[] images = findImages(absolutePath);
            //将文件进行内存转为BufferedImage
            try {
                for (File selectedFile : images) {
                    bufferedImages.add(new Entity(bufferedImages.size(), ImageIO.read(selectedFile),null));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            painting();
        }
        return folderChooser;
    }

    /**
     * 递归识别文件夹下格式为jpg、png的图片，并返回
     * @param folderPath
     * @return
     */
    public static File[] findImages(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<File> imageFiles = new ArrayList<File>();
        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                imageFiles.add(file);
            }
            else if (file.isDirectory()) {
                File[] subFiles = findImages(file.getAbsolutePath());
                for (File subFile : subFiles) {
                    if (subFile.isFile() && (subFile.getName().endsWith(".jpg") || subFile.getName().endsWith(".png"))) {
                        imageFiles.add(subFile);
                    }
                }
            }
        }
        return imageFiles.toArray(new File[imageFiles.size()]);
    }


    /**
     * 重绘界面
     */
    public static void painting(){
        //无图片不重绘界面
        if(bufferedImages.size()==0){
            return;
        }
        Dimension size = LeftJPanel.getBounds().getSize();
        //左边
        int panelWidth=size.width;
        int panelHeight=size.height;
        LeftJPanel.setLayout(new BorderLayout());
        LeftJPanel.removeAll();
        int imageWidth = bufferedImages.get(index).getImage().getWidth(null);
        int imageHeight = bufferedImages.get(index).getImage().getHeight(null);

        double panelRatio = (double) panelWidth / panelHeight;
        double imageRatio = (double) imageWidth / imageHeight;

        int scaledWidth;
        int scaledHeight;
        if (panelRatio > imageRatio) {
            scaledWidth = (int) (imageWidth * panelHeight / imageHeight);
            scaledHeight = panelHeight;
        } else {
            scaledWidth = panelWidth;
            scaledHeight = (int) (imageHeight * panelWidth / imageWidth);
        }
        //清除之前绘制在面板的图片
        LeftJPanel.getGraphics().clearRect(0,0,panelWidth,panelHeight);
        //按比例缩放图片
        Image scaledImage = bufferedImages.get(index).getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        LeftJPanel.getGraphics().drawImage(scaledImage, (panelWidth - scaledWidth) / 2, (panelHeight - scaledHeight) / 2, null);

        //右边
        pre.setVisible(false);
        next.setVisible(false);
        //判断是否显示切换按钮
        if (index>0){
            pre.setVisible(true);
        }
        if (index<bufferedImages.size()-1){
            next.setVisible(true);
        }
        //显示识别结果
        ansTextArea.setVisible(true);
        ansTextArea.setText(bufferedImages.get(index).getOCRResult());

        //显示识别按钮
        start.setVisible(true);
    }

    /**
     * 开始识别文字
     */
    public static void loadOCR(){
        count=new AtomicInteger(0);
        //开始识别
        OCRUtil.doOCR(bufferedImages);
    }

    /**
     * 所有图片识别完毕之后刷新界面
     */
    public static void showDialog(){
        if(count.incrementAndGet()<bufferedImages.size()){
            return;
        }
        painting();
    }

    public static void main(String[] args) {
        //窗口设置
        jFrame.setMinimumSize(new Dimension(300,300));
        jFrame.setBounds(600,300,500,500);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //菜单选项
        JMenu jMenu1=new JMenu("选择");
        JMenu jMenu2=new JMenu("设置");
        //设置功能暂时没搞
        jMenu2.setVisible(false);
        jMenu1.setFont(NavFont);
        jMenu2.setFont(NavFont);
        //子菜单
        JMenuItem item1=new JMenuItem("文件");
        JMenuItem item2=new JMenuItem("文件夹");
        JMenuItem item3=new JMenuItem("截图");
        item1.setFont(NavFont);
        item2.setFont(NavFont);
        item3.setFont(NavFont);

        //组装导航栏
        jMenu1.add(item1);
        jMenu1.add(item2);
        jMenu1.add(item3);
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);

        //组装中间部分
        pre.setFont(TextFont);
        next.setFont(TextFont);
        pre.setBackground(new Color(241, 241, 241));
        next.setBackground(new Color(241, 241, 241));
        JPanel jPanel1=new JPanel();
        jPanel1.add(pre);
        jPanel1.add(next);
        //先隐藏图片切换按钮
        pre.setVisible(false);
        next.setVisible(false);
        //右侧
        ansTextArea.setVisible(false);
        ansTextArea.setFont(TextFont);
        //激活自动换行功能
        ansTextArea.setLineWrap(true);
        ansTextArea.setWrapStyleWord(true);
        //设置自动显示滑动条
        JScrollPane jScrollPane=new JScrollPane(ansTextArea);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);;
        RightJPanel.setLayout(new BorderLayout());
        RightJPanel.add(jScrollPane,BorderLayout.CENTER);
        RightJPanel.add(jPanel1,BorderLayout.SOUTH);
        CenterJPanel.setLayout(new GridLayout(1,2));
        CenterJPanel.add(LeftJPanel);
        CenterJPanel.add(RightJPanel);

        //组装底部按钮
        JButton clear=new JButton("重置");
        // 语言选择下拉框
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(TextFont);
        // 绑定下拉框选项
        String[] strArray = { "中文", "英语", "日语","韩语" };
        for (String item : strArray)
        {
            comboBox.addItem(item);
        }
        comboBox.setBackground(new Color(241, 241, 241));

        start.setFont(TextFont);
        clear.setFont(TextFont);
        start.setBackground(new Color(241, 241, 241));
        clear.setBackground(new Color(241, 241, 241));
        start.setVisible(false);
        BottomJPanel.add(clear);
        BottomJPanel.add(comboBox);
        BottomJPanel.add(start);

        //组装界面
        jFrame.setJMenuBar(jMenuBar);
        jFrame.setLayout(new BorderLayout());
        jFrame.add(CenterJPanel,BorderLayout.CENTER);
        jFrame.add(BottomJPanel,BorderLayout.SOUTH);


        /**
         * 监听事件
         */
        //选择图片
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //点击之后选择要识别的图片
                initFileChooser();
            }
        });

        //选择文件夹识别
        item2.addActionListener(e->{
            initFolderChooser();
        });

        //截图
        item3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //自定义区域截图
                ScreenShotWindow ssw=new ScreenShotWindow();
                ssw.setVisible(true);
            }
        });

        //清除界面监听
        ActionListener clearListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ansTextArea.setVisible(false);
                start.setVisible(false);
                index=0;
                pre.setVisible(false);
                next.setVisible(false);
                //清除待识别的图片数组
                bufferedImages=new CopyOnWriteArrayList<>();
                Dimension size = LeftJPanel.getBounds().getSize();
                LeftJPanel.getGraphics().clearRect(0,0,size.width,size.height);
            }
        };
        //清除界面，重新选择图片识别
        clear.addActionListener(clearListener);

        //切换图片监听
        ActionListener preImageListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index > 0) {
                    index--;
                    painting();
                }
            }
        };
        ActionListener nextImageListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index<bufferedImages.size()-1){
                    index++;
                    painting();
                }
            }
        };
        //切换图片
        pre.addActionListener(preImageListener);
        next.addActionListener(nextImageListener);

        //识别图片
        ActionListener orcListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = comboBox.getSelectedItem().toString();
                if(s.equals("中文")){
                    language="chi_sim";
                }else if(s.equals("英语")){
                    language="eng";
                }else if(s.equals("日语")){
                    language="jpn";
                } else if(s.equals("韩语")){
                    language="kor";
                }
                loadOCR();
            }
        };
        start.addActionListener(orcListener);

        //监听界面变化进行画面重画
        jFrame.addComponentListener(new ResizeListener());

        //快捷键
        item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,ActionEvent.ALT_MASK));
        item2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,ActionEvent.ALT_MASK));
        item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,ActionEvent.ALT_MASK));
        pre.registerKeyboardAction(preImageListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.CTRL_MASK),JComponent.WHEN_IN_FOCUSED_WINDOW);
        next.registerKeyboardAction(nextImageListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.CTRL_MASK),JComponent.WHEN_IN_FOCUSED_WINDOW);
        clear.registerKeyboardAction(clearListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_MASK),JComponent.WHEN_IN_FOCUSED_WINDOW);
        start.registerKeyboardAction(orcListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK),JComponent.WHEN_IN_FOCUSED_WINDOW);

        //显示窗口
        jFrame.setVisible(true);
    }
}
