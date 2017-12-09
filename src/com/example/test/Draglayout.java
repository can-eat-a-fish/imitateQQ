package com.example.test;


import com.example.qq.ColorUtil;
import com.example.qq.R;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class Draglayout extends FrameLayout {

	private View mainChild;
	private View menuChild;
	private ViewDragHelper viewDragHelper=null;
	private int totalWidth;
	private DragState mMurrentState=DragState.Close;
	
	public Draglayout(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	public Draglayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	public Draglayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public Draglayout(Context context) {
		super(context);
		init();
	}
	private void init () {
		viewDragHelper = ViewDragHelper.create(this, 1.0f, Callback);
		floatEvaluator = new FloatEvaluator();
		intEvaluator = new IntEvaluator();
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int measuredWidth = getMeasuredWidth();
		totalWidth = (int) (measuredWidth*0.6);
	}
	
	private ViewDragHelper.Callback Callback=new ViewDragHelper.Callback() {
		
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==mainChild||child==menuChild;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child==mainChild) {
				//mainChild.getLeft()
				if (left<0) {
					left=0;
				}
				if (left>totalWidth) {
					left=totalWidth;
				}
			} 
			return left;
		}
		
		@Override
		public int getViewHorizontalDragRange(View child) {
			// TODO 自动生成的方法存根
			return totalWidth;
		}
		
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
//			Log.e("TAG", "LEFT "+left+"top "+top);
			if (changedView==menuChild) {
				menuChild.layout(0, 0, getRight(), getBottom());
				int mainLeft=mainChild.getLeft()+dx;
				if (mainLeft<0) {
					mainLeft=0;
				}
				if(mainLeft>totalWidth)
					mainLeft=totalWidth;
				mainChild.layout(mainLeft, mainChild.getTop(),mainLeft+mainChild.getMeasuredWidth(), mainChild.getBottom());
				//Log.e("TAG", "mainChild"+mainChild.getWidth()+"---"+(mainLeft+mainChild.getMeasuredWidth())+"====="+mainChild.getRight()+"----"+dx);
			}
			
			float bili=mainChild.getLeft()*1f/totalWidth;
			
			executeAnim(bili);
			
			if (bili==0&&mMurrentState!=DragState.Close) {
				mMurrentState=DragState.Close;
				if(mListenet!=null){
					mListenet.onClose();
				}
			} else if(bili==1&&mMurrentState!=DragState.Open){
				mMurrentState=DragState.Open;
				if(mListenet!=null){
					mListenet.onOpen();
				}
			}else{
				if(mListenet!=null){
					mListenet.onDraging(bili);
				}
			}
			
			super.onViewPositionChanged(changedView, left, top, dx, dy);
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			Log.e("TAG", "xvel"+xvel);
			System.out.println("xvel=="+xvel+"  yvel  "+yvel);
			if (mainChild.getLeft()>totalWidth/2) {
				open();
			}else{
				close();
			}
			if (xvel>200&& mMurrentState!=DragState.Open) {
				open();
			}
			if (xvel<-200&&mMurrentState!=DragState.Close) {
				close();
			}
			
			System.out.println("xvel=="+xvel+"  yvel  "+yvel);
			
			super.onViewReleased(releasedChild, xvel, yvel);
		}
	};
	
	
	public DragState getstate(){
		return mMurrentState;
	}
	
	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(Draglayout.this);
		}
	};
	
	private FloatEvaluator floatEvaluator;
	private IntEvaluator intEvaluator;
	private View black;
	protected void executeAnim(float bili) {
		//main执行动画
		ViewHelper.setScaleX(mainChild, floatEvaluator.evaluate(bili, 1, 0.8));
		ViewHelper.setScaleY(mainChild, floatEvaluator.evaluate(bili, 1, 0.8));
		
		
		//menu执行动画
		ViewHelper.setScaleX(menuChild, floatEvaluator.evaluate(bili, 0.5, 1));
		ViewHelper.setScaleY(menuChild, floatEvaluator.evaluate(bili, 0.5, 1));
		
		ViewHelper.setAlpha(menuChild, floatEvaluator.evaluate(bili, 0, 1));
		
		ViewHelper.setTranslationX(menuChild,intEvaluator.evaluate(bili, -menuChild.getMeasuredWidth()/2, 0)  );
		
		//遮罩层
		ViewHelper.setAlpha(black, floatEvaluator.evaluate(bili, 1,0));
		
	}

	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	};
	
	//定义状态常量
	public enum DragState{
		Open,Close;
	}
	
	public boolean onTouchEvent(android.view.MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	};
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mainChild = getChildAt(2);
		menuChild = getChildAt(1);
		black = getChildAt(0);
		
	}
	
	OnDragStateChangeListenet mListenet;
	
	public void setOnDragStateChangeListener(OnDragStateChangeListenet mListenet){
		this.mListenet=mListenet;
	}
	
	public void open() {
		viewDragHelper.smoothSlideViewTo(mainChild, totalWidth, 0);
		ViewCompat.postInvalidateOnAnimation(Draglayout.this);
	}

	public void close() {
		viewDragHelper.smoothSlideViewTo(mainChild, 0, 0);
		ViewCompat.postInvalidateOnAnimation(Draglayout.this);
	}

	public  interface  OnDragStateChangeListenet{
		public void onOpen();
		public void onClose();
		public void onDraging(float fraction);
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		
//		System.out.println("widthMeasureSpec"+widthMeasureSpec+"heightMeasureSpec"+heightMeasureSpec);
//		
//		float dimension = getResources().getDimension(R.dimen.width);
//		
//		System.out.println("dimension"+dimension+"----"+redChild.getLayoutParams().width);
//		
//		int redMeasurewidth=MeasureSpec.makeMeasureSpec(redChild.getLayoutParams().width, MeasureSpec.EXACTLY);
//		int redMeasureheight=MeasureSpec.makeMeasureSpec(redChild.getLayoutParams().height, MeasureSpec.EXACTLY);
//		
//		int blueMeasure=MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
//		
//	//	measureChild(redChild, redMeasurewidth, redMeasureheight);
//		measureChildren(redMeasurewidth, redMeasureheight);
//		
////		redChild.measure(redMeasurewidth,redMeasureheight);
////		blueChild.measure(redMeasurewidth,redMeasureheight);
////		setMeasuredDimension(250, 250);
//	}
	
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		System.out.println("----"+l+"----"+t+"----"+r+"----"+b+"getMeasuredWidth()"+getMeasuredWidth()+"getWidth()"+getWidth());
//		System.out.println("----"+l+"----"+t+"----"+r+"----"+b+"getMeasuredHeight()"+getMeasuredHeight()+"getHeight()"+getHeight());
//		System.out.println("redChild.getMeasuredWidth()"+redChild.getMeasuredWidth()+"redChild.getWidth()"+redChild.getWidth());
//		System.out.println("redChild.getMeasuredHeight()"+redChild.getMeasuredHeight()+"redChild.getHeight()"+redChild.getHeight());
//		
//		int left=l+(getWidth()-redChild.getMeasuredWidth())/2;
//		int top=t;
//		redChild.layout(left, top,left+redChild.getMeasuredWidth(), top+redChild.getMeasuredHeight());
//		blueChild.layout(left, top+redChild.getBottom(),left+redChild.getMeasuredWidth(), top+redChild.getBottom()+redChild.getMeasuredHeight());
//	}

}
