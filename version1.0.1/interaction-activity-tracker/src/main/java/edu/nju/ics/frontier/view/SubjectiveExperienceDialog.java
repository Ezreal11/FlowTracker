package edu.nju.ics.frontier.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubjectiveExperienceDialog extends DialogWrapper {
    private LikertItemModel engagementModel;
    private LikertItemModel efficiencyModel;
    private String[][] noCodingTexts;

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

    private java.util.List<ConfirmListener> listeners = new ArrayList<>();

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

    protected SubjectiveExperienceDialog(boolean canBeParent, boolean modal) {
        super(canBeParent);
        init();
        setModal(modal);
        getButton(myOKAction).setVisible(false);
        getButton(myCancelAction).setVisible(false);

        setTitle(ChineseResourceManager.getTitle());
        pack();
        setResizable(false);
        Window window = getWindow();
        window.setAlwaysOnTop(true);
        window.setAutoRequestFocus(true);
    }

    private SubjectiveExperienceDialog(@Nullable Project project, @NotNull IdeModalityType ideModalityType) {
        super(project, true, ideModalityType);
        init();
        getButton(myOKAction).setVisible(false);
        getButton(myCancelAction).setVisible(false);

        setTitle(ChineseResourceManager.getTitle());
        pack();
        setResizable(false);
        Window window = getWindow();
        window.setAlwaysOnTop(true);
        window.setAutoRequestFocus(true);
    }

    @Nullable
    @Override
    public Point getInitialLocation() {
        Window window = getWindow();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration());
        return new Point(screenSize.width - window.getWidth(), screenSize.height - screenInsets.bottom - window.getHeight());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        initData();
        initView();
        JPanel rootPl = placeViews();
        initEvent();
        return rootPl;
    }

    private void initData() {
        engagementModel = new LikertItemModel(
                "images/engagement.png",
                "engagement",
                ChineseResourceManager.getEngagementQuestion(),
                ChineseResourceManager.getEngagementOptions());
        efficiencyModel = new LikertItemModel(
                "images/efficiency.png",
                "efficiency",
                ChineseResourceManager.getProductivityQuestion(),
                ChineseResourceManager.getProductivityOptions());
        noCodingTexts = new String[][]{
                new String[]{ChineseResourceManager.getMeeting(), "meeting"},
                new String[]{ChineseResourceManager.getRest(), "rest"},
                new String[]{ChineseResourceManager.getTalk(), "talk"},
                new String[]{ChineseResourceManager.getOther(), "other"},
        };
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

    private JPanel placeViews() {
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

        JPanel rootPl = new JPanel(new BorderLayout(10, 10));
        rootPl.add(relaxPl, BorderLayout.NORTH);
        rootPl.add(scalePl, BorderLayout.CENTER);
        rootPl.add(confirmPl, BorderLayout.SOUTH);

        return rootPl;
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
                    SubjectiveExperienceDialog.this.dispose();
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
                    Messages.showInfoMessage(getWindow(),
                            ChineseResourceManager.getInfoMessage(),
                            ChineseResourceManager.getInfoTitle());
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

                SubjectiveExperienceDialog.this.dispose();
//                LikertScaleDialog.this.setVisible(false);
            }
        });

        getWindow().addWindowListener(new WindowAdapter() {
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
            public void windowClosed(WindowEvent e) {
//                System.out.println("windowClosing");
                if (!(listeners == null || listeners.isEmpty())) {
                    for (ConfirmListener l : listeners) {
                        l.scaleClosing();
                    }
                }
                SubjectiveExperienceDialog.this.dispose();
//                LikertScaleDialog.this.setVisible(false);
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
