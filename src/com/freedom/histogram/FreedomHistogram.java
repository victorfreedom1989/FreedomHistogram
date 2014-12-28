package com.freedom.histogram;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FreedomHistogram {

	// 柱形条宽度
	private int with;
	// 柱形条高度
	private int height;
	// 柱形条对应数值
	private double count;
	// 起始Y坐标
	private int axisY;
	// 标题起始Y坐标
	private int titleY;

	// 柱形条起始X坐标
	private int axisX;
	private Context context;
	private DecimalFormat df;
	private Paint mPaint;

	public FreedomHistogram(Context context, int with, float axisY) {
		this.with = with;
		this.axisY = ScreenUtil.dip2px(context, axisY);
		this.titleY = ScreenUtil.dip2px(context, axisY + 35);
		this.context = context;
		this.df = new DecimalFormat("#0.0");
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
	}

	public FreedomHistogram() {
	};

	public int getAxisX() {
		return axisX;
	}

	public void setAxisX(int axisX) {
		this.axisX = axisX;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public void drawHistogram(Canvas canvas, Paint paint) {
		// 绘制第一层
		canvas.drawRect(axisX - 1, axisY - height - 1, with + axisX + 1, axisY,
				mPaint);
		// 绘制第二层，让柱形条有白边，好看，根据个人喜好可以不绘制第二层。
		canvas.drawRect(axisX, axisY - height, with + axisX, axisY - 1, paint);
		// 绘制柱形条对应的数值
		canvas.drawText(df.format(count),
				axisX - ScreenUtil.dip2px(context, 5), axisY - height - 10,
				paint);
	}

	public void drawTitle(Canvas canvas, Paint paint, String name) {
		// 绘制标题
		canvas.drawRect(axisX, titleY - height, with + axisX, titleY - 1, paint);
		canvas.drawText(name, with + axisX + ScreenUtil.dip2px(context, 5),
				titleY - 1, paint);
	}

}
