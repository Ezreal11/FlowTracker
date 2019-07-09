package edu.nju.ics.frontier.scale.view;

import edu.nju.ics.frontier.scale.model.AnswerModel;
import edu.nju.ics.frontier.scale.model.LikertItemModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LikertScaleDialog extends JDialog {
    private LikertItemModel engagementModel;
    private LikertItemModel efficiencyModel;

    private JLabel relaxationIcon;
    private JLabel relaxationLb;
    private LikertItemView engagementView;
    private LikertItemView efficiencyView;
    private JLabel tipLb;
    private JButton confirmBt;

    private List<ConfirmListener> listeners = new ArrayList<>();

    public interface ConfirmListener {
        void scaleOpened();
        void confirmed(List<AnswerModel> models);
        void scaleClosing();
    }

    public void addConfirmListener(ConfirmListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeConfirmListener(ConfirmListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    public LikertScaleDialog(LikertItemModel engagementModel, LikertItemModel efficiencyModel, boolean block) {
        super();

        this.engagementModel = engagementModel;
        this.efficiencyModel = efficiencyModel;

        initView();
        initEvent();

        JPanel relaxPl = new JPanel(new BorderLayout(10, 10));
        relaxPl.add(relaxationIcon, BorderLayout.WEST);
        relaxPl.add(relaxationLb, BorderLayout.CENTER);

        JPanel scalePl = new JPanel(new GridLayout(2, 1, 10, 10));
        scalePl.add(engagementView);
        scalePl.add(efficiencyView);
        scalePl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel confirmPl = new JPanel(new BorderLayout(0, 0));
        confirmPl.add(tipLb, BorderLayout.CENTER);
        confirmPl.add(confirmBt, BorderLayout.EAST);

        this.setLayout(new BorderLayout(10, 10));
        this.add(relaxPl, BorderLayout.NORTH);
        this.add(scalePl, BorderLayout.CENTER);
        this.add(confirmPl, BorderLayout.SOUTH);

        this.setTitle("工作状态采样表");
        if (block) {
            this.setModalityType(ModalityType.APPLICATION_MODAL);
        }
        this.pack();
        this.setResizable(false);
        // show dialog at right-bottom corner
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        this.setLocation(screenSize.width - getWidth(), screenSize.height - screenInsets.bottom - getHeight());
        this.setAlwaysOnTop(true);
        this.setAutoRequestFocus(true);
    }

    private void initView() {
        relaxationIcon = new JLabel();
        try {
            InputStream is = LikertItemView.class.getResourceAsStream("img/relaxation.png");
            BufferedImage image = ImageIO.read(is);
            relaxationIcon.setIcon(new ScaleIcon(new ImageIcon(image), 50, 50));
        } catch (IOException e) {
            e.printStackTrace();
        }

        relaxationLb = new JLabel("工作辛苦啦，休息一会顺便填个问卷吧 (*^▽^*)");
        Font oldFont = relaxationLb.getFont();
        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), ((int) (1.5 * oldFont.getSize())));
        relaxationLb.setFont(newFont);

        engagementView = new LikertItemView(engagementModel, true);
        efficiencyView = new LikertItemView(efficiencyModel, true);

        tipLb = new JLabel("填完问卷接下来一个小时就不会打扰您啦");
        confirmBt = new JButton("提交");
    }

    private void initEvent() {
        this.confirmBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int engagementAnswer = engagementView.getModel().getAnswer();
                int efficiencyAnswer = efficiencyView.getModel().getAnswer();

                if (engagementAnswer == -1 || efficiencyAnswer == -1) {
                    JOptionPane.showMessageDialog(
                            LikertScaleDialog.this,
                            "请您填写所有问题，感谢您的配合！",
                            "通知",
                            JOptionPane.INFORMATION_MESSAGE,
                            null);
                    return;
                }

                if (!(listeners == null || listeners.isEmpty())) {
                    List<AnswerModel> models = new ArrayList<>();
                    LikertItemModel engModel = engagementView.getModel();
                    models.add(new AnswerModel(
                            engModel.getTitle(),
                            engModel.getAnswer(),
                            engModel.getAnsweredOptionScore()));
                    LikertItemModel effModel = efficiencyView.getModel();
                    models.add(new AnswerModel(
                            effModel.getTitle(),
                            effModel.getAnswer(),
                            effModel.getAnsweredOptionScore()));
                    for (ConfirmListener l : listeners) {
                        l.confirmed(models);
                        l.scaleClosing();
                    }
                }

                LikertScaleDialog.this.dispose();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
//                System.out.println("windowOpened");
                if (!(listeners == null || listeners.isEmpty())) {
                    for (ConfirmListener l : listeners) {
                        l.scaleOpened();
                    }
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
//                System.out.println("windowClosing");
                if (!(listeners == null || listeners.isEmpty())) {
                    for (ConfirmListener l : listeners) {
                        l.scaleClosing();
                    }
                }
                LikertScaleDialog.this.dispose();
            }
        });
    }
}
