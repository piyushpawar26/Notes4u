package anonymous.notes4u;

import android.content.Context;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SwipeView extends PagerAdapter {

    private Context ctx;
    private LayoutInflater layoutInflater;
    private ArrayList<String> links;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference topic;
    private int count = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix;
    private ImageView imageView;

    public SwipeView(Context c, String room_name, String topic_name){
        ctx = c;
        links = new ArrayList<>();
        matrix = new Matrix();
        scaleGestureDetector = new ScaleGestureDetector(ctx, new ScaleListner());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        topic = mDatabase.child("users").child(firebaseUser.getUid()).child(room_name).child("all").child(topic_name);
        topic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    String item = String.valueOf(dsp.getValue());
                    links.add(item);
                    ++count;
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        imageView = (ImageView) item_view.findViewById(R.id.swipe_image);
        Glide.with(ctx).load(Uri.parse(links.get(position))).into(imageView);
        imageView.setOnTouchListener(touchListener);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            scaleGestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    @Override
    public int getItemPosition (Object object) {
        return POSITION_NONE;
    }

    private class ScaleListner extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Float sf = detector.getScaleFactor();
            sf = Math.max(0.1f, Math.min(sf, 0.5f));
            matrix.setScale(sf, sf);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

}
