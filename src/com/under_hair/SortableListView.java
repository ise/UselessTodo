package com.under_hair;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class SortableListView extends ListView implements
        OnItemLongClickListener {
    private static final int SCROLL_SPEED_FAST = 25;
    private static final int SCROLL_SPEED_SLOW = 8;
    private static final Bitmap.Config DRAG_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    
    private boolean mSortable = false;
    private boolean mDragging = false;
    private DragListener mDragListener = new SimpleDragListener();
    private int mBitmapBackgroundColor = Color.argb(128, 0xFF, 0xFF, 0xFF);
    private Bitmap mDragBitmap = null;
    private ImageView mDragImageView = null;
    private WindowManager.LayoutParams mLayoutParams = null;
    private MotionEvent mActionDownEvent;
    private int mPositionFrom = -1;
    
    /** �R���X�g���N�^ */
    public SortableListView(Context context) {
        super(context);
        setOnItemLongClickListener(this);
    }
    
    /** �R���X�g���N�^ */
    public SortableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnItemLongClickListener(this);
    }
    
    /** �R���X�g���N�^ */
    public SortableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnItemLongClickListener(this);
    }
    
    /** �h���b�O�C�x���g���X�i�̐ݒ� */
    public void setDragListener(DragListener listener) {
        mDragListener = listener;
    }
    
    /** �\�[�g���[�h�̐ؑ� */
    public void setSortable(boolean sortable) {
        this.mSortable = sortable;
    }
    
    /** �\�[�g���A�C�e���̔w�i�F��ݒ� */
    @Override
    public void setBackgroundColor(int color) {
        mBitmapBackgroundColor = color;
    }
    
    /** �\�[�g���[�h�̐ݒ� */
    public boolean getSortable() {
        return mSortable;
    }
    
    /** MotionEvent ���� position ���擾���� */
    private int eventToPosition(MotionEvent event) {
        return pointToPosition((int) event.getX(), (int) event.getY());
    }
    
    /** �^�b�`�C�x���g���� */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSortable) {
            return super.onTouchEvent(event);
        }
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                storeMotionEvent(event);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (duringDrag(event)) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (stopDrag(event, true)) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (stopDrag(event, false)) {
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }
    
    /** ���X�g�v�f�������C�x���g���� */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        return startDrag();
    }
    
    /** ACTION_DOWN ���� MotionEvent ���v���p�e�B�Ɋi�[ */
    private void storeMotionEvent(MotionEvent event) {
        mActionDownEvent = event;
    }
    
    /** �h���b�O�J�n */
    private boolean startDrag() {
        // �C�x���g���� position ���擾
        mPositionFrom = eventToPosition(mActionDownEvent);
        
        // �擾���� position �� 0�������͈͊O�̏ꍇ�̓h���b�O���J�n���Ȃ�
        if (mPositionFrom < 0) {
            return false;
        }
        mDragging = true;
        
        // View, Canvas, WindowManager �̎擾�E����
        final View view = getChildByIndex(mPositionFrom);
        final Canvas canvas = new Canvas();
        final WindowManager wm = getWindowManager();
        
        // �h���b�O�Ώۗv�f�� View �� Canvas �ɕ`��
        mDragBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                DRAG_BITMAP_CONFIG);
        canvas.setBitmap(mDragBitmap);
        view.draw(canvas);
        
        // �O��g�p���� ImageView ���c���Ă���ꍇ�͏����i�O�̂��߁H�j
        if (mDragImageView != null) {
            wm.removeView(mDragImageView);
        }
        
        // ImageView �p�� LayoutParams �����ݒ�̏ꍇ�͐ݒ肷��
        if (mLayoutParams == null) {
            initLayoutParams();
        }
        
        // ImageView �𐶐��� WindowManager �� addChild ����
        mDragImageView = new ImageView(getContext());
        mDragImageView.setBackgroundColor(mBitmapBackgroundColor);
        mDragImageView.setImageBitmap(mDragBitmap);
        wm.addView(mDragImageView, mLayoutParams);
        
        // �h���b�O�J�n
        if (mDragListener != null) {
            mPositionFrom = mDragListener.onStartDrag(mPositionFrom);
        }
        return duringDrag(mActionDownEvent);
    }
    
    /** �h���b�O���� */
    private boolean duringDrag(MotionEvent event) {
        if (!mDragging || mDragImageView == null) {
            return false;
        }
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int height = getHeight();
        final int middle = height / 2;
        
        // �X�N���[�����x�̌���
        final int speed;
        final int fastBound = height / 9;
        final int slowBound = height / 4;
        if (event.getEventTime() - event.getDownTime() < 500) {
            // �h���b�O�̊J�n����500�~���b�̊Ԃ̓X�N���[�����Ȃ�
            speed = 0;
        } else if (y < slowBound) {
            speed = y < fastBound ? -SCROLL_SPEED_FAST : -SCROLL_SPEED_SLOW;
        } else if (y > height - slowBound) {
            speed = y > height - fastBound ? SCROLL_SPEED_FAST
                    : SCROLL_SPEED_SLOW;
        } else {
            speed = 0;
        }
        
        // �X�N���[������
        if (speed != 0) {
            // �������͂Ƃ肠�����l���Ȃ�
            int middlePosition = pointToPosition(0, middle);
            if (middlePosition == AdapterView.INVALID_POSITION) {
                middlePosition = pointToPosition(0, middle + getDividerHeight()
                        + 64);
            }
            final View middleView = getChildByIndex(middlePosition);
            if (middleView != null) {
                setSelectionFromTop(middlePosition, middleView.getTop() - speed);
            }
        }
        
        // ImageView �̕\����ʒu���X�V
        if (mDragImageView.getHeight() < 0) {
            mDragImageView.setVisibility(View.INVISIBLE);
        } else {
            mDragImageView.setVisibility(View.VISIBLE);
        }
        updateLayoutParams(x, y);
        getWindowManager().updateViewLayout(mDragImageView, mLayoutParams);
        if (mDragListener != null) {
            mPositionFrom = mDragListener.onDuringDrag(mPositionFrom,
                    pointToPosition(x, y));
        }
        return true;
    }
    
    /** �h���b�O�I�� */
    private boolean stopDrag(MotionEvent event, boolean isDrop) {
        if (!mDragging) {
            return false;
        }
        if (isDrop && mDragListener != null) {
            mDragListener.onStopDrag(mPositionFrom, eventToPosition(event));
        }
        mDragging = false;
        if (mDragImageView != null) {
            getWindowManager().removeView(mDragImageView);
            mDragImageView = null;
            // ���T�C�N������Ƃ��܂Ɏ��ʂ��ǃ^�C�~���O������Ȃ� by vvakame
            // mDragBitmap.recycle();
            mDragBitmap = null;
            return true;
        }
        return false;
    }
    
    /** �w��C���f�b�N�X��View�v�f���擾���� */
    private View getChildByIndex(int index) {
        return getChildAt(index - getFirstVisiblePosition());
    }
    
    /** WindowManager �̎擾 */
    protected WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
    }
    
    /** ImageView �p LayoutParams �̏����� */
    protected void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.windowAnimations = 0;
        mLayoutParams.x = getLeft();
        mLayoutParams.y = getTop();
    }
    
    /** ImageView �p LayoutParams �̍��W�����X�V */
    protected void updateLayoutParams(int x, int y) {
        mLayoutParams.y = getTop() + y - 32;
    }
    
    /** �h���b�O�C�x���g���X�i�[�C���^�[�t�F�[�X */
    public interface DragListener {
        /** �h���b�O�J�n���̏��� */
        public int onStartDrag(int position);
        
        /** �h���b�O���̏��� */
        public int onDuringDrag(int positionFrom, int positionTo);
        
        /** �h���b�O�I�����h���b�v���̏��� */
        public boolean onStopDrag(int positionFrom, int positionTo);
    }
    
    /** �h���b�O�C�x���g���X�i�[���� */
    public static class SimpleDragListener implements DragListener {
        /** �h���b�O�J�n���̏��� */
        @Override
        public int onStartDrag(int position) {
            return position;
        }
        
        /** �h���b�O���̏��� */
        @Override
        public int onDuringDrag(int positionFrom, int positionTo) {
            return positionFrom;
        }
        
        /** �h���b�O�I�����h���b�v���̏��� */
        @Override
        public boolean onStopDrag(int positionFrom, int positionTo) {
            return positionFrom != positionTo && positionFrom >= 0
                    || positionTo >= 0;
        }
    }
}