package edu.nju.ics.frontier.view;

public class ChineseResourceManager {
    private static final String TITLE = "工作状态采样表";
    private static final String RELAX = "工作辛苦啦，休息一会顺便填个问卷吧(*^▽^*)";
    private static final String TIP = "填完问卷接下来半个小时就不会打扰您啦";
    private static final String ENGAGEMENT_QUESTION = "从上一次填问卷到现在，您在工作中的投入程度如何？";
    private static final String[] ENGAGEMENT_OPTIONS = new String[]{
            "非常不投入", "不投入", "中等", "投入", "非常投入",
    };
    private static final String PRODUCTIVITY_QUESTION = "从上一次填问卷到现在，您自我感觉的工作成效如何？";
    private static final String[] PRODUCTIVITY_OPTIONS = new String[]{
            "非常低效", "低效", "中等", "高效", "非常高效",
    };
    private static final String OUT_OF_IDE = "没有使用IDE:";
    private static final String MEETING = "开会？";
    private static final String REST = "休息？";
    private static final String TALK = "交谈？";
    private static final String OTHER = "其他？";
    private static final String SUBMIT = "提交";

    private static final String INFO_TITLE = "通知";
    private static final String INFO_MESSAGE = "请您填写所有问题，感谢您的配合！";

    public static String getTitle() {
        return TITLE;
    }

    public static String getRelax() {
        return RELAX;
    }

    public static String getTip() {
        return TIP;
    }

    public static String getEngagementQuestion() {
        return ENGAGEMENT_QUESTION;
    }

    public static String[] getEngagementOptions() {
        return ENGAGEMENT_OPTIONS;
    }

    public static String getProductivityQuestion() {
        return PRODUCTIVITY_QUESTION;
    }

    public static String[] getProductivityOptions() {
        return PRODUCTIVITY_OPTIONS;
    }

    public static String getOutOfIDE() {
        return OUT_OF_IDE;
    }

    public static String getMeeting() {
        return MEETING;
    }

    public static String getRest() {
        return REST;
    }

    public static String getTalk() {
        return TALK;
    }

    public static String getOther() {
        return OTHER;
    }

    public static String getSubmit() {
        return SUBMIT;
    }

    public static String getInfoTitle() {
        return INFO_TITLE;
    }

    public static String getInfoMessage() {
        return INFO_MESSAGE;
    }
}
