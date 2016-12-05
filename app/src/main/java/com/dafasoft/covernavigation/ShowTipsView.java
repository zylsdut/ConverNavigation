package com.dafasoft.covernavigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class ShowTipsView extends View {
	private Point mRectStartPoints; //镂空区域的起始坐标
	private int rectWidth; //镂空区域宽度
	private int rectHeight; //镂空区域高度
	private View mTragetView; //显示的view
	private Bitmap mBitmap; //需要绘制的图片
	private Canvas mTempCanvas;//负责绘制遮罩图片
	private Paint mPaint;
	private Paint mTransparentPaint; //绘制镂空区域所需画笔
	private PorterDuffXfermode mPorterDuffXfermode; //绘制镂空区域所需属性
	private OnTouchListener mListener;

	public ShowTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ShowTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShowTipsView(Context context) {
		super(context);
		init();
	}

	public void setOnTouchListener(OnTouchListener listener) {
		mListener = listener;
	}

	/***
	 * 初始化属性
	 */
	private void init() {
		this.setVisibility(View.GONE);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		mPaint = new Paint();
		mTransparentPaint = new Paint();
		mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

	}

	/***
	 * 画出一张canvas大小,透明度为188 , 且目标区域镂空的图片,并将其画到canvas上
	 * @param canvas
     */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444);
			mTempCanvas = new Canvas(mBitmap);
		}
		mPaint.setColor(Color.parseColor("#000000"));
		mPaint.setAlpha(188);
		mTempCanvas.drawRect(0, 0, mTempCanvas.getWidth(), mTempCanvas.getHeight(), mPaint);
		mTransparentPaint.setColor(getResources().getColor(android.R.color.transparent));
		mTransparentPaint.setXfermode(mPorterDuffXfermode);
		mTempCanvas.drawRect(mRectStartPoints.x , mRectStartPoints.y , mRectStartPoints.x + rectWidth
				, mRectStartPoints.y + rectHeight, mTransparentPaint);
		canvas.drawBitmap(mBitmap , 0 , 0 , mPaint);
	}

	/****
	 * 将蒙版加到DecorView中显示,并计算所需参数的值
	 * @param activity
     */
	public void show(final Activity activity) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);
				ShowTipsView.this.setVisibility(View.VISIBLE);
				final ViewTreeObserver observer = mTragetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int[] location = new int[2];
						mTragetView.getLocationInWindow(location);
						mRectStartPoints = new Point(location[0], location[1]);
						rectWidth = mTragetView.getWidth();
						rectHeight = mTragetView.getHeight();
					}
				});
			}
		}, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		//禁用镂空区域以外的触摸事件,如果触摸事件在镂空区域内,执行回调
		if ((x > mRectStartPoints.x && x < mRectStartPoints.x + rectWidth) &&
				(y > mRectStartPoints.y && y < mRectStartPoints.y + rectHeight)
				&& event.getAction() == MotionEvent.ACTION_UP) {
			setVisibility(View.GONE);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTipsView.this);
			if (mListener != null) {
				mListener.onTouch();
			}
		}
		return super.onTouchEvent(event);
	}

	public void setTarget(View v) {
		mTragetView = v;
	}

	public interface OnTouchListener {
		public void onTouch();
	}
}
