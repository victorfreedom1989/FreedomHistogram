package com.freedom.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private RelativeLayout container;
	private FreedomHistogramView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		add();
		view = (FreedomHistogramView) findViewById(R.id.view);
		Double[] data = new Double[] { 10d, 20d, 30d, 40d, 50d };
		Map<Integer, Double[]> map = new HashMap<Integer, Double[]>();
		map.put(1, data);
		map.put(2, data);
		List<String> stringList = new ArrayList<String>();
		stringList.add("1月");
		stringList.add("2月");
		stringList.add("3月");
		stringList.add("4月");
		stringList.add("5月");
		view.setDatas(map);
		view.setNodes(stringList);
		view.setTitle(new String[] { "2014", "2015" });
		// 数据超过2组的时候点击事件不起效果
		view.setHistogramOnClickListener(new HistogramOnClickListener() {

			@Override
			public void onClick(int position) {
				Toast.makeText(getBaseContext(), "第" + position + "个柱子被点击",
						Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void add() {
		container = (RelativeLayout) findViewById(R.id.container);
		Double[] data = new Double[] { 10d, 20d, 30d, 40d, 50d };
		Map<Integer, Double[]> map = new HashMap<Integer, Double[]>();
		map.put(1, data);
		List<String> stringList = new ArrayList<String>();
		stringList.add("1月");
		stringList.add("2月");
		stringList.add("3月");
		stringList.add("4月");
		stringList.add("5月");
		// 1、上下文，数据集合，柱形图条形宽度，各条形间距，底部显示信息，Y轴显示数，Y周间距
		FreedomHistogramView view = new FreedomHistogramView(MainActivity.this,
				map, 10, 50, stringList, 5, 40, true);
		view.setTitle(new String[] { "2014" });
		container.addView(view);
		view.setHistogramOnClickListener(new HistogramOnClickListener() {

			@Override
			public void onClick(int position) {
				Toast.makeText(getBaseContext(), "第" + position + "个柱子被点击",
						Toast.LENGTH_SHORT).show();

			}
		});
	}
}
