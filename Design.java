package 记事本;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


//设计记事本本身
public class Design extends JFrame {
    //FIXME 成员方法
    //设计菜单栏
    JMenuBar menuBar=new JMenuBar();
    //菜单项
    //文件
    JMenu MFIle=new JMenu("文件");
    //文件菜单项
    JMenuItem newProject=new JMenuItem("新建");
    JMenuItem openFile=new JMenuItem("打开");
    JMenuItem saveFile=new JMenuItem("保存");
    JMenuItem otherSave=new JMenuItem("另存为");
    //编辑
    JMenu Edit=new JMenu("编辑");
    JMenuItem copy=new JMenuItem("复制");
    JMenuItem paste=new JMenuItem("粘贴");
    JMenuItem Cut=new JMenuItem("剪切");

    //右键菜单
    JPopupMenu ppm=new JPopupMenu();
    //按钮
    JRadioButtonMenuItem Rcpoy=new JRadioButtonMenuItem("复制");
    JRadioButtonMenuItem Rpaste=new JRadioButtonMenuItem("黏贴");
    JRadioButtonMenuItem Rcut=new JRadioButtonMenuItem("剪切");

    JMenu FotMat=new JMenu("格式");
    //添加单选按钮组
    ButtonGroup fotmo=new ButtonGroup();
    JRadioButtonMenuItem auto=new JRadioButtonMenuItem("自动换行");
    JRadioButtonMenuItem NULL=new JRadioButtonMenuItem("（暂无内容）",true);

    //文本域
    JTextArea textArea=new JTextArea();
    //滚动
    JScrollPane scrollPane=new JScrollPane(textArea);
    //定义标记，用于标记文件读取动作是否已经完成
    //线程共享数据
    protected volatile boolean flag=false;
    //临时保存目录
    String LastFilePath="临时保存文件.txt";
    /*-----------------------------成员方法及内部类----------------------------------------------------------*/
    //FIXME 设置菜单栏
    private void setMenu() {
        //设置快捷键
        MFIle.setMnemonic('F');
        newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        otherSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_MASK + KeyEvent.CTRL_MASK));
        //添加到菜单中
        MFIle.add(newProject);
        MFIle.add(openFile);
        MFIle.add(saveFile);
        MFIle.add(otherSave);

        //编辑栏
        Edit.setMnemonic('J');
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
        Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
        Edit.add(copy);
        Edit.add(paste);
        Edit.add(Cut);


        menuBar.add(MFIle);
        menuBar.add(Edit);

        //右键菜单
        fotmo.add(auto);
        fotmo.add(NULL);
        FotMat.add(auto);
        FotMat.add(NULL);
        ppm.add(Rcpoy);
        ppm.add(Rpaste);
        ppm.add(Rcut);
        ppm.add(FotMat);
    }

    //FIXME 读入文件(字符流)
    private boolean ReadFile(File f) throws IOException {
        BufferedReader br=new BufferedReader(new FileReader(f));
        String s=null;
        this.flag=false;//文件读取刚开始，尚未结束
        while ((s=br.readLine())!=null){
            textArea.append(s+"\r\n");
        }
        br.close();
        //while循环结束且缓冲流关闭，判断读取写入动作已完成
        return this.flag=true;
    }
    //FIXME 写入文件方法（字符流）
    private void WriteFile() throws IOException {
        //上一次打开的文件目录（用于当用户按下保存键时，程序暂时代替保存，也可在打开文件时自动打开上一次的文件内容）

        File f=new File(LastFilePath);
       // f.createNewFile();
        BufferedWriter bw=new BufferedWriter(new FileWriter(f));
        String text = textArea.getText();
        bw.write(text);
        bw.close();
        JOptionPane.showMessageDialog(super.rootPane,"保存成功","已保存",JOptionPane.INFORMATION_MESSAGE);
    }
    //FIXME 另存为方法
    private void otherFileSave() throws IOException {
        JFileChooser jfs=new JFileChooser();
        jfs.showSaveDialog(super.rootPane);
        File selectedFile = jfs.getSelectedFile();
        BufferedWriter bw=new BufferedWriter(new FileWriter(selectedFile));
        String text = textArea.getText();
        bw.write(text);
        bw.close();
        JOptionPane.showMessageDialog(Design.super.rootPane,"已保存到"+selectedFile,
                "文件保存成功" ,JOptionPane.INFORMATION_MESSAGE);
    }

    //FIXME 设置监听器
    private void setListener(){

        //添加如果有内容，但需要关掉时，询问是否需要保存
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                File Lastfile=new File(LastFilePath);
                //线程实现
               MyTread mt=new MyTread();
               Thread t1=new Thread(mt,"文件读入");
               t1.start();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                String []option={"是", "否"};
                int i = JOptionPane.showOptionDialog(Design.super.rootPane, "请问是否需要保存",
                        "你正在准备关闭", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                        option, "是");
                switch (i){
                    case 0:{
                        try {
                            otherFileSave();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    };break;
                    case 1:{
                        Design.super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    };break;
                    default:
                }

            }

        });


        //打开文件
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfo = new JFileChooser();
                jfo.showOpenDialog(Design.super.rootPane);
                File selectedFile = jfo.getSelectedFile();
                try {
                    ReadFile(selectedFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        //写入文件（即保存文件操作）
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //由程序代替进行暂时保存
                try {
                    WriteFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        //另存为
        otherSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath=null;
                try {
                    otherFileSave();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        //新建
        newProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //先判断用户是否需要保存
                String options[]=new String[]{"是","否","取消"};
                int i = JOptionPane.showOptionDialog(Design.super.rootPane, "是否需要保存当前文档内容", "新建",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "是");
                switch (i){
                    case 0:{
                        try {
                            otherFileSave();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        textArea.setText("");
                    };break;
                    case 1:{
                        textArea.setText("");
                    };break;
                    case 2:break;
                }
            }
        });

        //自动换行


        //为右键菜单项添加监听器
        Rcpoy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });

        Rpaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });

        Rcut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });

       auto.addItemListener(new ItemListener() {
           @Override
           public void itemStateChanged(ItemEvent e) {
               textArea.setLineWrap(true);
           }
       });
        //为文本域添加右键菜单
        textArea.setComponentPopupMenu(ppm);
    }





    //TODO 子线程优化，用于在文件读入和读出时，不影响主程序的正常运行
    private class MyTread implements Runnable{
        @Override
        public void run() {
            while (flag!=true){
                try {
                    ReadFile(new File(LastFilePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("文件读入已完成");
        }
    }

    //FIXME 构造方法
    public Design(String title) throws HeadlessException {
        super(title);
        super.setLocation(600,200);
        super.setSize(new Dimension(800,800));


        setMenu();
        super.add(scrollPane);
        this.setListener();

        super.setJMenuBar(menuBar);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
