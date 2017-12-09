package com.example.qq;



import java.util.Random;

import com.example.test.Draglayout;
import com.example.test.Draglayout.OnDragStateChangeListenet;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ImageView mHead;
	private ListView menuList;
	private Draglayout mDraglayout;
	private ListView mainList;
	private myLinearLayout mll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		init();
		
	}

	private void init() {
		initview();
		initdata();
	}

	private void initview() {
		mainList = (ListView) findViewById(R.id.main_listview);
		menuList = (ListView) findViewById(R.id.menu_listview);
		mDraglayout = (Draglayout) findViewById(R.id.mDraglayout);
		mHead = (ImageView) findViewById(R.id.iv_head);
		mll = (myLinearLayout) findViewById(R.id.my_layout);
	}

	private void initdata() {
		mll.setDraglayout(mDraglayout);
		mainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Constant.NAMES){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView==null?super.getView(position, convertView, parent):convertView;
				//先缩小view
				ViewHelper.setScaleX(view, 0.5f);
				ViewHelper.setScaleY(view, 0.5f);
				//以属性动画放大
				ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
				ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
				return view;
			}
		});
		menuList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				 TextView view = (TextView) super.getView(position, convertView, parent);
				 view.setTextColor(Color.WHITE);
				return view;
			}
		});
		
		mDraglayout.setOnDragStateChangeListener(new OnDragStateChangeListenet() {
			
			@Override
			public void onOpen() {
			//	Log.e("TAG", "打开了");
				menuList.smoothScrollToPosition(new Random().nextInt(menuList.getCount()));
			}
			
			@Override
			public void onDraging(float fraction) {
				//Log.e("TAG", "bili=="+fraction);
				ViewHelper.setAlpha(mHead, 1-fraction);
				
			}
			
			@Override
			public void onClose() {
				//Log.e("TAG", "关闭了");
				ViewPropertyAnimator.animate(mHead).translationXBy(15)
				.setInterpolator(new CycleInterpolator(4))
				.setDuration(500)
				.start();
			}
		});
	}


}
