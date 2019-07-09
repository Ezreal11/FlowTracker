package edu.nju.ics.frontier.scale.view;

import edu.nju.ics.frontier.scale.model.LikertItemModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LikertItemView extends JPanel {
    private LikertItemModel model;

    private JLabel iconLb;
    private JLabel questionLb;
    private JLabel[] optionLabelLbs;
    private JRadioButton[] optionScoreRbs;
    private ButtonGroup optionScoreRbGroup;
    private boolean isShowScore;

    public LikertItemModel getModel() {
        return model;
    }

    public LikertItemView(LikertItemModel model, boolean isShowScore) {
        if (model == null) {
            throw new IllegalArgumentException("model is null!");
        }

        this.model = model;
        this.isShowScore = isShowScore;
        initView();
        initEvent();

        JPanel questionPl = new JPanel(new BorderLayout(10, 10));
        questionPl.add(iconLb, BorderLayout.WEST);
        questionPl.add(questionLb, BorderLayout.CENTER);

        JPanel opitonPl = new JPanel(new GridLayout(
                2, optionLabelLbs.length, 10, 10));
        for (JLabel optionLabelLb : optionLabelLbs) {
            opitonPl.add(optionLabelLb);
        }
        for (JRadioButton optionLabelRb : optionScoreRbs) {
            opitonPl.add(optionLabelRb);
        }

        this.setLayout(new BorderLayout(10, 10));
        this.add(questionPl, BorderLayout.NORTH);
        this.add(opitonPl, BorderLayout.CENTER);
    }

    private void initView() {
        iconLb = new JLabel();
        try {
            InputStream is = LikertItemView.class.getResourceAsStream(model.getIcon());
            BufferedImage image = ImageIO.read(is);
            iconLb.setIcon(new ScaleIcon(new ImageIcon(image), 50, 50));
        } catch (IOException e) {
            e.printStackTrace();
        }

        questionLb = new JLabel(model.getQuestion());
        Font oldFont = questionLb.getFont();
        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), ((int) (1.5 * oldFont.getSize())));
        questionLb.setFont(newFont);

        String[] optionLabels = model.getOptionLabels();
        optionLabelLbs = new JLabel[optionLabels.length];
        for (int i = 0; i < optionLabelLbs.length; i++) {
            JLabel lb = (optionLabels[i] == null) ? new JLabel() : new JLabel(optionLabels[i]);
            lb.setHorizontalAlignment(SwingConstants.CENTER);
            lb.setVerticalAlignment(SwingConstants.CENTER);
            optionLabelLbs[i] = lb;
        }

        int[] optionScores = model.getOptionScores();
        optionScoreRbs = new JRadioButton[optionScores.length];
        optionScoreRbGroup = new ButtonGroup();
        for (int i = 0; i < optionScoreRbs.length; i++) {
            JRadioButton rb = new JRadioButton();
            if (isShowScore) {
                rb.setText(String.valueOf(i + 1));
            }
            rb.setHorizontalAlignment(SwingConstants.CENTER);
            rb.setVerticalAlignment(SwingConstants.CENTER);
            optionScoreRbs[i] = rb;
            optionScoreRbGroup.add(rb);
        }
    }

    private void initEvent() {
        for (int i = 0; i < optionScoreRbs.length; i++) {
            final int answer = i;
            final JRadioButton optionScoreRb = optionScoreRbs[i];
            optionScoreRb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (optionScoreRb.isSelected()) {
                        model.setAnswer(answer);
                        optionScoreRbGroup.setSelected(optionScoreRb.getModel(), true);
                    }
                }
            });
        }
    }
}
