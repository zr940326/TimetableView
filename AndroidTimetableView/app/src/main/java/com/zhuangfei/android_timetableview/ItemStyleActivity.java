package com.zhuangfei.android_timetableview;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuangfei.android_timetableview.R;
import com.zhuangfei.android_timetableview.model.MySubject;
import com.zhuangfei.android_timetableview.model.SubjectRepertory;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.OnItemBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleManager;

import java.util.List;

public class ItemStyleActivity extends AppCompatActivity {

    TimetableView mTimetableView;
    Button moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_item_style);
        moreButton = findViewById(R.id.id_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });

        initTimetableView();
    }

    private void initTimetableView() {
        mTimetableView = findViewById(R.id.id_timetableView);
        List<MySubject> mySubjects = SubjectRepertory.loadDefaultSubjects();
        mySubjects.add(mySubjects.get(0));
        mTimetableView.setSource(mySubjects)
                .setCurWeek(1)
                .showView();
    }

    /**
     * 显示弹出菜单
     */
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(this, moreButton);
        popup.getMenuInflater().inflate(R.menu.popmenu_itemstyle, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top1:
                        hideNonThisWeek();
                        break;
                    case R.id.top2:
                        showNonThisWeek();
                        break;
                    case R.id.top3:
                        setMarginAndCorner();
                        break;
                    case R.id.top4:
                        buildItemText();
                        break;
                    case R.id.top5:
                        intercept();
                        break;
                    case R.id.top6:
                        setCorner(0, 10, 0, 0);
                        break;
                    case R.id.top7:
                        setNonThisWeekBgcolor(Color.YELLOW);
                        break;
                    case R.id.top8:
                        modifyOverlayStyle();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void hideNonThisWeek() {
        mTimetableView.getScheduleManager().setShowNotCurWeek(false);
        mTimetableView.updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.getScheduleManager().setShowNotCurWeek(true);
        mTimetableView.updateView();
    }

    /**
     * 设置间距以及弧度
     * 该方法只能同时设置四个角的弧度，设置单个角的弧度可参考下文
     */
    protected void setMarginAndCorner() {
        mTimetableView.getScheduleManager()
                .setCorner(0)
                .setMarTop(0)
                .setMarLeft(0);
        mTimetableView.updateView();
    }

    /**
     * 设置角度（四个角分别控制）
     *
     * @param leftTop
     * @param rightTop
     * @param rightBottom
     * @param leftBottom
     */
    public void setCorner(final int leftTop, final int rightTop, final int rightBottom, final int leftBottom) {
        mTimetableView.getScheduleManager()
                .setOnItemBuildListener(new OnItemBuildAdapter() {
                    @Override
                    public void onItemUpdate(FrameLayout layout, TextView textView, TextView countTextView, Schedule schedule, GradientDrawable gd) {
                        super.onItemUpdate(layout, textView, countTextView, schedule, gd);
                        //数组8个元素，四个方向依次为左上、右上、右下、左下，
                        // 每个方向在数组中占两个元素，值相同
                        gd.setCornerRadii(new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom});
                    }
                });
        mTimetableView.updateView();
    }

    /**
     * 修改显示的文本
     */
    public void buildItemText() {
        mTimetableView.getScheduleManager()
                .setOnItemBuildListener(new OnItemBuildAdapter() {
                    @Override
                    public String getItemText(Schedule schedule, boolean isThisWeek) {
                        if (isThisWeek) return "[本周]" + schedule.getName();
                        return "[非本周]" + schedule.getName();
                    }
                });
        mTimetableView.updateView();
    }

    /**
     * 将名为"计算机组成原理"的课程拦截下来，该课程不会被构建
     */
    public void intercept() {
        mTimetableView.getScheduleManager()
                .setOnItemBuildListener(new OnItemBuildAdapter() {
                    @Override
                    public boolean interceptItemBuild(Schedule schedule) {
                        if (schedule.getName().equals("计算机组成原理")) return true;
                        return false;
                    }
                });
        mTimetableView.updateView();
        Toast.makeText(this,"已拦截《计算机组成原理》",Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置非本周课的背景
     *
     * @param color
     */
    public void setNonThisWeekBgcolor(int color) {
        mTimetableView.getScheduleManager()
                .getColorPool().setUselessColor(color);
        mTimetableView.updateView();
    }

    /**
     * 修改课程重叠的样式，在该接口中，你可以自定义出很多的效果
     */
    protected void modifyOverlayStyle() {
        mTimetableView.getScheduleManager()
                .setOnItemBuildListener(new OnItemBuildAdapter() {
                    @Override
                    public void onItemUpdate(FrameLayout layout, TextView textView, TextView countTextView, Schedule schedule, GradientDrawable gd) {
                        super.onItemUpdate(layout, textView, countTextView, schedule, gd);
                        //可见说明重叠，取消角标，添加角度
                        if (countTextView.getVisibility() == View.VISIBLE) {
                            countTextView.setVisibility(View.GONE);
                            gd.setCornerRadii(new float[]{0, 0, 20, 20, 0, 0, 0, 0});
                        }
                    }
                });
        mTimetableView.updateView();
    }
}
