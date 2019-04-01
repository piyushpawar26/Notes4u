package anonymous.notes4u;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


public class ViewPicActivity extends Activity {

    private String room_name;
    private String topic_name;
    private TextView tv_view_post;
    private TextView tv_view_group;
    private String position;
    private ViewPager viewPager;
    private SwipeView swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_pic);

        room_name = getIntent().getExtras().get("group_name").toString();
        topic_name = getIntent().getExtras().get("topic_name").toString();
        position = getIntent().getExtras().get("position").toString();

        tv_view_group = (TextView) findViewById(R.id.tv_view_group);
        tv_view_post = (TextView) findViewById(R.id.tv_view_post);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tv_view_group.setText("Group: " + room_name);
        tv_view_post.setText("Post: " + topic_name);

        swipeView = new SwipeView(ViewPicActivity.this, room_name, topic_name);
        viewPager.setAdapter(swipeView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(Integer.parseInt(position));
            }
        });
    }
}
