package com.example.qq;

import com.example.test.Draglayout;
import com.example.test.Draglayout.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class myLinearLayout extends LinearLayout {

	public myLinearLayout(Context context) {
		super(context);
	}

	public myLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public myLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public myLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	Draglayout mDraglayout;

	public void setDraglayout(Draglayout mDraglayout) {
		this.mDraglayout = mDraglayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDraglayout.getstate() == Draglayout.DragState.Open) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mDraglayout.getstate() == Draglayout.DragState.Open) {
			if (event.getAction()==MotionEvent.ACTION_UP) {
				mDraglayout.close();
			}

			return true;
		}

		return super.onTouchEvent(event);
	}

}
