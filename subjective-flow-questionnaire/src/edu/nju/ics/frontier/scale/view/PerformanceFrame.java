package edu.nju.ics.frontier.scale.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PerformanceFrame {
    private String imgDir;
    private String yesterdayStr;
    private BufferedImage yesterdayImg;
    private JFrame reportForm;
    private double ratio;

    public PerformanceFrame(double ratio) {
        this.ratio = ratio;

        long time = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        Date date = new Date(time);

        this.yesterdayStr = new SimpleDateFormat("yyyy年MM月dd日").format(date);

        String s = new SimpleDateFormat("yyyyMMdd").format(date);
        imgDir = System.getProperty("user.home") + File.separator +
                "interaction_traces" + File.separator + "statistics" + File.separator + "images";
        String imgPath = imgDir + File.separator + s + ".png";
        File imgFile = new File(imgPath);
        if (imgFile.exists()) {
            try {
                this.yesterdayImg = ImageIO.read(imgFile);
            } catch (IOException e) {
                this.yesterdayImg = null;
            }
        }
    }

    public void show() {
        if (yesterdayImg == null) {
            showMessageDialog();
        } else {
            showReportForm();
        }
    }

    private void showMessageDialog() {
        JOptionPane.showMessageDialog(
                null,
                "非常抱歉，没有记录到您在" + yesterdayStr + "的工作状态。",
                yesterdayStr + "工作状态报表",
                JOptionPane.INFORMATION_MESSAGE,
                null);
    }

    private void showReportForm() {
        if (reportForm != null) {
            reportForm.setVisible(false);
            reportForm.dispose();
            reportForm = null;
        }

        JButton bt = new JButton("查看历史记录");
        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO:
                try {
                    Desktop.getDesktop().open(new File(imgDir));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JPanel btPl = new JPanel(new FlowLayout(SwingConstants.RIGHT));
//        btPl.setBackground(Color.WHITE);
        btPl.add(bt);

        ImageIcon ii = new ImageIcon(yesterdayImg);
        ScaleIcon icon = new ScaleIcon(
                ii,
                (int) (ratio * ii.getIconWidth()),
                (int) (ratio * ii.getIconHeight()));

        JLabel lb = new JLabel(icon);
        lb.setHorizontalAlignment(SwingConstants.CENTER);
        lb.setVerticalAlignment(SwingConstants.CENTER);
        lb.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.add(btPl, BorderLayout.NORTH);
        p.add(new JScrollPane(lb), BorderLayout.CENTER);

        reportForm = new JFrame(yesterdayStr + "工作状态报表");
        reportForm.add(p);
        reportForm.pack();
        reportForm.setLocationRelativeTo(null);
        reportForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportForm.setVisible(true);
    }
}
