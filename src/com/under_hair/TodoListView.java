package com.under_hair;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.GestureDetector;

//public class TodoListView extends ListView implements OnItemLongClickListener {
public class TodoListView extends ListView implements GestureDetector.OnGestureListener {
    
    private android.view.WindowManager.LayoutParams _layoutParams;
    private ImageView _dragView;
    private Bitmap _dragBitmap;
    private boolean _isDragging = false;
    private WindowManager _windowManager = null;
    private int _curPos;
    private int _dragBitmapY;
    private GestureDetector _gestureDetector;
    
    public TodoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._gestureDetector = new GestureDetector(context, this);
        //setOnItemLongClickListener(this);
    }
    
    public boolean onTouchEvent(MotionEvent me) {
        Log.i("INFO", "me.getAction():" + me.getAction());
        if (this._gestureDetector.onTouchEvent(me)) {
            return true;
        }
        int x = (int) me.getX();
        int y = (int) me.getY();
        Log.i("INFO", "me.getAction():" + me.getAction());
        switch (me.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.i("INFO", "down");
            break;
        case MotionEvent.ACTION_MOVE:
            Log.i("INFO", "move");
            if (this._isDragging) {
                int height = getHeight();
                int speed = 0;
                int fastBound = height / 9;
                int slowBound = height / 4;
                int center = height / 2;
                if (me.getEventTime() - me.getDownTime() < 500) {
                    // 500ミリ秒間はスクロールなし
                } else if (y < slowBound) {
                    //上側1/4より上へドラッグした場合
                    speed = y < fastBound ? -25 : -8;
                } else if (y > height - slowBound) {
                    //下側1/4より下へドラッグした場合
                    speed = y > height - fastBound ? 25 : 8;
                }
                View v = null;
                if (speed != 0) {
                    // 横方向はとりあえず考えない
                    int centerPosition = pointToPosition(0, center);
                    if (centerPosition == AdapterView.INVALID_POSITION) {
                        centerPosition = pointToPosition(0, center
                                + getDividerHeight() + 64);
                    }
                    Log.i("INFO", "center position=" + centerPosition);
                    v = getChildAt(centerPosition - getFirstVisiblePosition());
                    if (v != null) {
                        int pos = v.getTop();
                        Log.i("INFO", "top position=" + pos);
                        setSelectionFromTop(centerPosition, pos - speed);
                    }
                }
                if (this._dragView != null) {
                    if (this._dragView.getHeight() < 0) {
                        this._dragView.setVisibility(View.INVISIBLE);
                    } else {
                        this._dragView.setVisibility(View.VISIBLE);
                    }
                    this._layoutParams.x = getLeft();
                    this._layoutParams.y = getTop() + y;
                    Log.i("INFO", "this._layoutParams.y:" + this._layoutParams.y);
                    if (this._windowManager == null) {
                        this._windowManager = (WindowManager) getContext().getSystemService("window");
                    }
                    //this._windowManager = (WindowManager) getContext().getSystemService("window");
                    this._windowManager.updateViewLayout(this._dragView, this._layoutParams);
                    return true;
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            Log.i("INFO", "UP");
            if (this._isDragging) {
                //this._windowManager = (WindowManager) getContext().getSystemService("window");
                if (this._windowManager == null) {
                    this._windowManager = (WindowManager) getContext().getSystemService("window");
                }
                this._windowManager.removeView(this._dragView);
                this._isDragging = false;
                this._dragView = null;
            }
            if (this._dragView != null) {
                this._windowManager.removeView(this._dragView);
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_OUTSIDE:
            if (this._dragView != null) {
                //this._windowManager = (WindowManager) getContext().getSystemService("window");
                if (this._windowManager == null) {
                    this._windowManager = (WindowManager) getContext().getSystemService("window");
                }
                this._windowManager.removeView(this._dragView);
                //朝起きて シャンパンで 2時間ちょっとのパーティータイム
                this._dragView = null;
                // リサイクルするとたまに死ぬけどタイミング分からない
                // mDragBitmap.recycle();
                this._dragBitmap = null;
                return true;
            }
            break;
        default:
            break;
        }
        return super.onTouchEvent(me);
    }
/*
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.i("INFO", "Long Click");
        this._curPos = arg2;
        View v = getChildAt(this._curPos - getFirstVisiblePosition());
        this._dragBitmapY = getTop() + v.getTop();
        Log.i("INFO", "this._dragBitmapY:" + this._dragBitmapY);
        return this._startDrag();
    }
    */
    
    private boolean _startDrag() {
        this._isDragging = true;
        
        //何番目のリストをクリックしたかがわかるらしい
        int curPos = this._curPos;
        if (curPos < 0) {
            return false;
        }
        //タッチしているリスト項目を画像化して
        //ドラッグしますよーというアピール
        
        //表示中のリスト項目の中で何番目か？
        //View view = getChildAt(pos - getFirstVisiblePosition());
        View v = getChildAt(curPos - getFirstVisiblePosition());
        this._dragBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(this._dragBitmap);
        v.draw(canvas);
        
        if (this._windowManager == null) {
            this._windowManager = (WindowManager) getContext().getSystemService("window");
        }
        
        if (this._dragView != null) {
            this._windowManager.removeView(this._dragView);
        }
        
        if (this._layoutParams == null) {
            this._layoutParams = new WindowManager.LayoutParams();
            this._layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

            this._layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            this._layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            this._layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            this._layoutParams.format = PixelFormat.TRANSLUCENT;
            this._layoutParams.windowAnimations = 0;
        }
        this._layoutParams.x = getLeft();
        this._layoutParams.y = this._dragBitmapY;
        Log.i("INFO", "this._layoutParams.y:" + this._layoutParams.y);
        
        this._dragView = new ImageView(getContext());
        this._dragView.setBackgroundColor(Color.argb(128, 0, 0, 0));
        this._dragView.setImageBitmap(this._dragBitmap);
        
        this._windowManager.addView(this._dragView, this._layoutParams);
        //this._dragView = iv;
        
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i("INFO", "on down");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("INFO", "long press");
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

}
