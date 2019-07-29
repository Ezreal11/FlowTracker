package edu.nju.ics.frontier.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LikertScaleDialog extends JDialog {
    private LikertItemModel engagementModel;
    private LikertItemModel efficiencyModel;

    private String[][] noCodingTexts = new String[][]{
            new String[]{ChineseResourceManager.getMeeting(), "meeting"},
            new String[]{ChineseResourceManager.getRest(), "rest"},
            new String[]{ChineseResourceManager.getTalk(), "talk"},
            new String[]{ChineseResourceManager.getOther(), "other"},
    };

    private JLabel relaxationIcon;
    private JLabel relaxationLb;
    private JLabel tipLb;
    private LikertItemView engagementView;
    private LikertItemView efficiencyView;
    private JLabel noCodingLb;
    private JLabel[] noCodingOptions;
    private JButton confirmBt;
    private Color enterColor;
    private Color exitColor;

    private List<ConfirmListener> listeners = new ArrayList<>();

    public interface ConfirmListener extends LikertItemModel.AnswerChangeListener {
        void scaleOpened();
        void refused(String noCoding);
        void confirmed(List<AnswerModel> models);
        void scaleClosing();
    }

    public void addConfirmListener(ConfirmListener l) {
        if (!(l == null || listeners.contains(l))) {
            listeners.add(l);
            if (engagementModel != null) {
                engagementModel.addAnswerChangeListener(l);
            }
            if (efficiencyModel != null) {
                efficiencyModel.addAnswerChangeListener(l);
            }
        }
    }

    public void removeConfirmListener(ConfirmListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
            if (engagementModel != null) {
                engagementModel.removeAnswerChangeListener(l);
            }
            if (efficiencyModel != null) {
                efficiencyModel.removeAnswerChangeListener(l);
            }
        }
    }

    public LikertScaleDialog(boolean block) {
        super();

        this.engagementModel = new LikertItemModel(
                "images/engagement.png",
                "engagement",
                ChineseResourceManager.getEngagementQuestion(),
                ChineseResourceManager.getEngagementOptions());
        this.efficiencyModel = new LikertItemModel(
                "images/efficiency.png",
                "efficiency",
                ChineseResourceManager.getProductivityQuestion(),
                ChineseResourceManager.getProductivityOptions());

        initView();
        initEvent();

        JPanel relaxTextPl = new JPanel(new BorderLayout(0, 5));
        relaxTextPl.add(relaxationLb, BorderLayout.CENTER);
        relaxTextPl.add(tipLb, BorderLayout.SOUTH);
        JPanel relaxPl = new JPanel(new BorderLayout(10, 0));
        relaxPl.add(relaxationIcon, BorderLayout.WEST);
        relaxPl.add(relaxTextPl, BorderLayout.CENTER);

        JPanel scalePl = new JPanel(new GridLayout(2, 1, 10, 10));
        scalePl.add(engagementView);
        scalePl.add(efficiencyView);
        scalePl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JPanel noCodingPl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        noCodingPl.add(noCodingLb);
        for (int i = 0; i < noCodingOptions.length; i++) {
            noCodingPl.add(noCodingOptions[i]);
        }
        JPanel confirmPl = new JPanel(new BorderLayout(0, 0));
        confirmPl.add(noCodingPl, BorderLayout.CENTER);
        confirmPl.add(confirmBt, BorderLayout.EAST);

        this.setLayout(new BorderLayout(10, 10));
        this.add(relaxPl, BorderLayout.NORTH);
        this.add(scalePl, BorderLayout.CENTER);
        this.add(confirmPl, BorderLayout.SOUTH);

        this.setTitle(ChineseResourceManager.getTitle());
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
            InputStream is = LikertItemView.class.getResourceAsStream("images/relaxation.png");
            BufferedImage image = ImageIO.read(is);
            relaxationIcon.setIcon(new ScaleIcon(new ImageIcon(image), 50, 50));
        } catch (IOException e) {
            e.printStackTrace();
        }

        relaxationLb = new JLabel(ChineseResourceManager.getRelax());
        Font font = relaxationLb.getFont();
        Font largeFont = new Font(font.getName(), font.getStyle(), ((int) (1.5 * font.getSize())));
        relaxationLb.setFont(largeFont);
        tipLb = new JLabel(ChineseResourceManager.getTip());

        engagementView = new LikertItemView(engagementModel, true);
        efficiencyView = new LikertItemView(efficiencyModel, true);

        noCodingLb = new JLabel(ChineseResourceManager.getOutOfIDE());
        enterColor = new Color(0,145,255);
        exitColor = noCodingLb.getForeground();
        noCodingOptions = new JLabel[noCodingTexts.length];
        for (int i = 0; i < noCodingOptions.length; i++) {
            JLabel lb = new JLabel(noCodingTexts[i][0]);
            lb.setForeground(exitColor);
            noCodingOptions[i] = lb;
        }
        confirmBt = new JButton(ChineseResourceManager.getSubmit());
    }

    private void initEvent() {
        for (int i = 0; i < noCodingOptions.length; i++) {
            final String noCoding = noCodingTexts[i][1];
            final JLabel lb = noCodingOptions[i];
            lb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    lb.setForeground(enterColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lb.setForeground(exitColor);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!(listeners == null || listeners.isEmpty())) {
                        for (ConfirmListener l : listeners) {
                            l.refused(noCoding);
                            l.scaleClosing();
                        }
                    }
                    LikertScaleDialog.this.dispose();
//                    LikertScaleDialog.this.setVisible(false);
                }
            });
        }

        this.confirmBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int engagementAnswer = engagementView.getModel().getAnswer();
                int efficiencyAnswer = efficiencyView.getModel().getAnswer();

                if (engagementAnswer == -1 || efficiencyAnswer == -1) {
                    JOptionPane.showMessageDialog(
                            LikertScaleDialog.this,
                            ChineseResourceManager.getInfoMessage(),
                            ChineseResourceManager.getInfoTitle(),
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
//                LikertScaleDialog.this.setVisible(false);
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
//                LikertScaleDialog.this.setVisible(false);
            }
        });
    }
}
