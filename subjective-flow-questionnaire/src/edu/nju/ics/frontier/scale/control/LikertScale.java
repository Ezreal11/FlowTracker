package edu.nju.ics.frontier.scale.control;

import com.google.gson.Gson;
import edu.nju.ics.frontier.scale.model.AnswerModel;
import edu.nju.ics.frontier.scale.model.LikertItemModel;
import edu.nju.ics.frontier.scale.view.LikertScaleDialog;
import edu.nju.ics.frontier.util.OkTextWriter;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LikertScale {
//    private LikertItemModel activityModel = new LikertItemModel(
//            "img/activity.png",
//            "activity",
//            "从上一次填问卷到现在，您进行的最主要的工作是：",
//            new String[]{ "编写程序", "查看代码", "调试程序", "阅读文档", "浏览网页", "处理邮件", "会议讨论" });
    private LikertItemModel engagementModel = new LikertItemModel(
            "img/engagement.png",
            "engagement",
            "从上一次填问卷到现在，您在工作中的投入程度如何？",
            new String[]{ "非常不投入", null, "中等", null, "非常投入" });
    private LikertItemModel efficiencyModel = new LikertItemModel(
            "img/efficiency.png",
            "efficiency",
            "从上一次填问卷到现在，您自我感觉的工作效率如何？",
            new String[]{ "非常低效", null, "中等", null, "非常高效" });
    private String rootPath;
    private boolean block;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
    private static final Gson GSON = new Gson();
    private final OkTextWriter writer = new OkTextWriter();
    private LikertScaleDialog dialog;
    private List<LikertScaleDialog.ConfirmListener> listeners = new ArrayList<>();

    public LikertScale(String rootPath, boolean block) {
//        activityModel.addAnswerChangeListener(new MyAnswerChangeListener());
        engagementModel.addAnswerChangeListener(new MyAnswerChangeListener());
        efficiencyModel.addAnswerChangeListener(new MyAnswerChangeListener());
        this.rootPath = rootPath;
        this.block = block;
//        showLikertScaleDialog();
    }

    public void addConfirmListener(LikertScaleDialog.ConfirmListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeConfirmListener(LikertScaleDialog.ConfirmListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    public boolean isLikertScaleDialogVisible() {
        return (dialog != null && dialog.isVisible());
    }

    public void showLikertScaleDialog(boolean isExitOnClose) {
        if (dialog == null || (!dialog.isVisible())) {
            dialog = new LikertScaleDialog(/*activityModel, */engagementModel, efficiencyModel, block);
            dialog.addConfirmListener(new MyConfirmListener());
            if (!(listeners == null || listeners.isEmpty())) {
                for (LikertScaleDialog.ConfirmListener l : listeners) {
                    dialog.addConfirmListener(l);
                }
            }
            if (isExitOnClose) {
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            }
            dialog.setVisible(true);
        }
    }

    public void closeLikertScaleDialog() {
        if (dialog != null && dialog.isVisible()) {
            dialog.dispose();
            dialog = null;
        }
    }

    class MyAnswerChangeListener implements LikertItemModel.AnswerChangeListener {
        @Override
        public void answerChanged(AnswerModel model) {
//            System.out.println("answerChanged:" + model.toString());
            String json = GSON.toJson(model);
            writer.println(System.currentTimeMillis() + ",select," + json);
        }
    }

    class MyConfirmListener implements LikertScaleDialog.ConfirmListener {
        @Override
        public void scaleOpened() {
//            System.out.println("scale opened");
            Date date = new Date();
            String path = rootPath + File.separator +
                    DATE_FORMAT.format(date) + File.separator +
                    TIME_FORMAT.format(date) + "_" + date.getTime() + ".json";
            writer.open(path);
            writer.println(date.getTime() + ",begin,open scale dialog");
        }

        @Override
        public void confirmed(List<AnswerModel> models) {
            long time = System.currentTimeMillis();
            for (AnswerModel model : models) {
//                System.out.println("confirmed:" + model.toString());
                String json = GSON.toJson(model);
                writer.println(time + ",submit," + json);
            }
        }

        @Override
        public void scaleClosing() {
//            System.out.println("scale closing");
            writer.println(System.currentTimeMillis() + ",end,close scale dialog");
            writer.close();
        }
    }
}
