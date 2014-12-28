package com.freedom.histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class FreedomHistogramView extends View {

	private HistogramOnClickListener histogramOnClickListener;

	public void setHistogramOnClickListener(HistogramOnClickListener listner) {
		this.histogramOnClickListener = listner;
	}

	// 柱形图总数据
	private Map<Integer, Double[]> datas;
	// 柱形图每一组数据
	private Double[] perData;
	// 最大值
	private double max;
	// 平局值
	private Double average;
	// 数据有多少组集合
	private ArrayList<Integer> dataKeys;
	// 每组数据的集合
	private ArrayList<Double[]> valuese;
	// 每组数据对应的区域
	private ArrayList<Rect> rects;
	// 数据有多少组
	private int flag;
	// 平均线数值
	private double avergerLine;

	// 每组数据对应的颜色
	private int[] colors = new int[] { 0xff8B008B, 0xff00BFFF, 0xff4800FF,
			0xff333333, 0xff006400, 0xff668B8B, 0xffFF83FA, 0xff363636,
			0xff000080, 0xff008B8B, 0xffFFDAB9, 0xff90EE90 };

	// 柱形图标题对应相距值
	private int margin;

	// 柱形条
	private FreedomHistogram freedomHistogram;
	private Paint paint;
	// 是否左下角绘制标题
	private boolean isTitle = false;
	private String title[];
	// 每组数据柱形条对应的X轴坐标说明
	private List<String> nodes;
	// 柱形条宽度
	private int charWith;
	// 柱形条间距（配合margin使用）
	private int withX;
	// Y周对应坐标点的个数
	private int numberY;
	// Y轴各坐标点之间的距离
	private float withY;

	public FreedomHistogramView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public FreedomHistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FreedomHistogramView);
		charWith = a.getInteger(R.styleable.FreedomHistogramView_charWith, 20);
		withX = a.getInteger(R.styleable.FreedomHistogramView_withX, 20);
		numberY = a.getInteger(R.styleable.FreedomHistogramView_numberY, 10);
		withY = a.getFloat(R.styleable.FreedomHistogramView_withY, 20);
		isTitle = a.getBoolean(R.styleable.FreedomHistogramView_isTitle, false);
		paint = new Paint();
		paint.setAntiAlias(true);
		// xPaint.setAntiAlias(true);
		// 新建一个柱形条
		freedomHistogram = new FreedomHistogram(context, ScreenUtil.dip2px(
				context, charWith), withY * (numberY - 1) + 10);
		a.recycle();
	}

	public FreedomHistogramView(Context context) {
		super(context);
	}

	public FreedomHistogramView(Context context, Map<Integer, Double[]> datas,
			int charWith, int withX, List<String> nodes, int numberY,
			float withY, boolean isTitle) {
		super(context);
		margin = 0;
		this.datas = datas;
		this.withX = withX;
		this.nodes = nodes;
		this.numberY = numberY;
		this.withY = withY;
		this.charWith = charWith;
		this.isTitle = isTitle;
		paint = new Paint();
		paint.setAntiAlias(true);
		// xPaint.setAntiAlias(true);
		// 新建一个柱形条
		freedomHistogram = new FreedomHistogram(context, ScreenUtil.dip2px(
				context, charWith), withY * (numberY - 1) + 10);

	}

	public Map<Integer, Double[]> getDatas() {
		return datas;
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public void setDatas(Map<Integer, Double[]> datas) {
		this.datas = datas;
		// invalidate();
	}

	public boolean isTitle() {
		return isTitle;
	}

	public void setTitle(boolean isTitle) {
		this.isTitle = isTitle;
	}

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
		// invalidate();
	}

	public int getCharWith() {
		return charWith;
	}

	public void setCharWith(int charWith) {
		this.charWith = charWith;
	}

	public int getwithX() {
		return withX;
	}

	public void setwithX(int withX) {
		this.withX = withX;
	}

	public float getNumberY() {
		return numberY;
	}

	public void setNumberY(int numberY) {
		this.numberY = numberY;
	}

	public float getWithY() {
		return withY;
	}

	public void setWithY(float withY) {
		this.withY = withY;
	}

	public void iniData() {
		// 取出数值
		Integer k;
		Double[] v;
		dataKeys = new ArrayList<Integer>();
		valuese = new ArrayList<Double[]>();
		for (Map.Entry<Integer, Double[]> entry : datas.entrySet()) {
			k = entry.getKey(); // key
			dataKeys.add(k);
			v = entry.getValue(); // value
			valuese.add(v);
		}
		this.flag = dataKeys.size();
		double total = 0;
		int sum = 0;
		for (Double[] dataMax : valuese) {
			max = dataMax[0];

			for (double data : dataMax) {
				total += data;
				max = max > data ? max : data;

				if (data != 0.0) {
					sum++;
				}
			}
		}
		max = dataChange(max);
		average = max / (numberY - 1);
		avergerLine = total / sum;
	}

	private double dataChange(double data) {
		StringBuilder sb = new StringBuilder();
		String dataString = String.valueOf(data);
		String dateInt = dataString.substring(0, dataString.lastIndexOf("."));
		int length = dateInt.length();
		String newDatafirst = dataString.substring(0, 1);
		if (newDatafirst.equals("9")) {
			boolean isChange = false;
			for (int i = 1; i < dataString.length(); i++) {
				String dataPer = dataString.substring(i, i + 1);
				if (dataPer.equals("0")) {
					continue;
				} else {
					isChange = true;
				}
			}
			if (isChange) {
				sb.append("1");
				for (int i = 0; i < length; i++) {
					sb.append("0");
				}
				return Double.valueOf(sb.toString());
			} else {
				return data;
			}
		} else {
			boolean isChange = false;
			for (int i = 1; i < dataString.length(); i++) {
				String dataPer = dataString.substring(i, i + 1);
				if (dataPer.equals("0")) {
					continue;
				} else {
					isChange = true;
					break;
				}
			}
			if (isChange) {
				newDatafirst = (Integer.valueOf(newDatafirst) + 1) + "";
				sb.append(newDatafirst);
				for (int i = 0; i < length - 1; i++) {
					sb.append("0");
				}
				return Double.valueOf(sb.toString());
			} else {
				return data;
			}

		}
	}

	public void drawAxis(Canvas canvas) {
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(ScreenUtil.dip2px(getContext(), 2));
		paint.setTextSize(ScreenUtil.sp2px(getContext(), 10));
		canvas.drawLine(
				ScreenUtil.dip2px(getContext(), 50),
				ScreenUtil.dip2px(getContext(), withY * (numberY - 1) + 10),
				ScreenUtil.dip2px(getContext(), (withX + (flag - 1) * charWith)
						* (nodes.size() + 1)),
				ScreenUtil.dip2px(getContext(), withY * (numberY - 1) + 10),
				paint);
		canvas.drawLine(ScreenUtil.dip2px(getContext(), 51),
				ScreenUtil.dip2px(getContext(), 5),
				ScreenUtil.dip2px(getContext(), 51),
				ScreenUtil.dip2px(getContext(), withY * (numberY - 1) + 10),
				paint);

		int x = ScreenUtil.dip2px(getContext(), (float) (59 + flag * charWith
				/ 2));
		int y = ScreenUtil.dip2px(getContext(), withY * (numberY - 1) + 10);

		for (int i = 0; i < nodes.size(); i++) {
			canvas.drawText(
					nodes.get(i),
					x,
					ScreenUtil.dip2px(getContext(), withY * (numberY - 1) + 25),
					paint);
			x += ScreenUtil.dip2px(getContext(), withX + (flag - 1) * charWith);
		}

		for (int i = 0; i < numberY; i++) {
			canvas.drawText(average.intValue() * i + "",
					ScreenUtil.dip2px(getContext(), 3), y, paint);
			y -= ScreenUtil.dip2px(getContext(), withY);
		}
	}

	public void drawChart(Canvas canvas) {
		rects = new ArrayList<Rect>();
		rects.clear();
		paint.setTextSize(ScreenUtil.sp2px(getContext(), 8));
		for (int j = 0; j < flag; j++) {
			paint.setColor(colors[j]);
			// paint.setStyle(Style.FILL_AND_STROKE);
			// paint.setStrokeWidth(1);
			perData = valuese.get(j);
			int temp_screen = ScreenUtil
					.dip2px(getContext(), 60 + j * charWith);
			for (int i = 0; i < perData.length; i++) {
				freedomHistogram.setCount(perData[i]);
				freedomHistogram.setHeight(ScreenUtil.dip2px(getContext(),
						(int) (perData[i].intValue() / max * (withY
								* (numberY - 1) + 10))));
				if (margin == 0) {
					freedomHistogram.setAxisX(temp_screen);
				} else {
					temp_screen += ScreenUtil.dip2px(getContext(), withX
							+ (flag - 1) * charWith);
					freedomHistogram.setAxisX(temp_screen);

				}
				freedomHistogram.drawHistogram(canvas, paint);
				margin = ScreenUtil.dip2px(getContext(), 10);
				Rect r = new Rect(temp_screen
						- ScreenUtil.dip2px(getContext(),
								(withX - charWith) / 3),
						ScreenUtil.dip2px(getContext(), withY * (numberY - 1))
								- ScreenUtil.dip2px(getContext(),
										(int) (perData[i].intValue() / max
												* withY * (numberY - 1))),
						temp_screen
								+ ScreenUtil.dip2px(getContext(), charWith)
								+ ScreenUtil.dip2px(getContext(),
										(withX - charWith) / 3),
						ScreenUtil.dip2px(getContext(), withY * (numberY - 1)));
				rects.add(r);
			}
			margin = 0;
		}

	}

	public void drawline(Canvas canvas) {
		// 虚线
		// Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		xPaint.setAntiAlias(true);
		xPaint.setStyle(Style.STROKE);
		xPaint.setColor(Color.RED);
		xPaint.setStrokeWidth(2);
		PathEffect effects = new DashPathEffect(new float[] {
				ScreenUtil.dip2px(getContext(), 5),
				ScreenUtil.dip2px(getContext(), 5),
				ScreenUtil.dip2px(getContext(), 5),
				ScreenUtil.dip2px(getContext(), 5) }, 1);
		xPaint.setPathEffect(effects);
		canvas.drawLine(ScreenUtil.dip2px(getContext(), 51), ScreenUtil
				.dip2px(getContext(),
						withY
								* (numberY - 1)
								+ 10
								- (int) (avergerLine / max * (withY
										* (numberY - 1) + 10))), ScreenUtil
				.dip2px(getContext(),
						(withX + (flag - 1) * charWith) * (nodes.size() + 1)),
				ScreenUtil.dip2px(getContext(),
						withY
								* (numberY - 1)
								+ 10
								- (int) (avergerLine / max * (withY
										* (numberY - 1) + 10))), xPaint);
	}

	public void drawtitle(Canvas canvas) {
		paint.setTextSize(ScreenUtil.sp2px(getContext(), 12));
		int title_screen = ScreenUtil.dip2px(getContext(), charWith);

		for (int i = 0; i < dataKeys.size(); i++) {
			paint.setColor(colors[i]);
			freedomHistogram.setHeight(ScreenUtil.dip2px(getContext(), 10));
			if (margin == 0) {
				freedomHistogram.setAxisX(title_screen
						+ ScreenUtil.dip2px(getContext(), 30) + margin);
			} else {
				freedomHistogram.setAxisX(title_screen
						+ ScreenUtil.dip2px(getContext(), 40) + margin);

			}
			freedomHistogram.drawTitle(canvas, paint, title[i]);
			margin = ScreenUtil.dip2px(getContext(), 10);
			title_screen = freedomHistogram.getAxisX();
		}
		margin = 0;

	}

	/**
	 * 需要得到控件的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 设置开关的宽和高为背景图片的宽高
		setMeasuredDimension(
				ScreenUtil.dip2px(getContext(), 60 + (withX + flag * charWith)
						* (nodes.size() + 1)),
				ScreenUtil.dip2px(getContext(), withY * numberY + 50));
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		iniData();
		drawChart(canvas);
		drawAxis(canvas);
		if (flag == 1) {
			drawline(canvas);
		}
		if (isTitle) {
			drawtitle(canvas);
		}
		super.onDraw(canvas);
	}

	private float scale;
	private float lastScale;
	private boolean firstTouch = true;
	private boolean guide = true;
	private boolean move = false;
	private int mode = 0;
	private int DRAG = 1;
	private int ZOOM = 2;
	private float startDis;
	int x1 = 0;
	int y1 = 0;
	private int startX = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			startX = (int) event.getRawX();
			scale = this.getScaleX();
			move = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				int newX = (int) event.getRawX();
				int dx = newX - startX;
				if (dx > 10) {
					move = true;
				}
				int l = this.getLeft() + dx;
				int t = this.getTop();
				int r = this.getRight() + dx;
				int b = this.getBottom();
				this.layout(l, t, r, b);
				startX = (int) event.getRawX();
			} else if (mode == ZOOM) {
				float endDis = distance(event);
				if (endDis > 10f) {
					lastScale = scale;
					scale = endDis / startDis;
					if (firstTouch) {
						if (scale > lastScale) {
							guide = true;
						} else {
							guide = false;
						}
						firstTouch = false;
					}
					if (guide) {
						if (scale < lastScale) {
							scale = lastScale;
						}

					} else {
						if (scale > lastScale) {
							scale = lastScale;
						}
						scale = scale * lastScale;
						if (scale < 1) {
							lastScale = 1;
							scale = 1;
						}
					}

				}
			}
			break;
		case MotionEvent.ACTION_UP:

			if (scale == 1 && dataKeys.size() == 1 && !move) {
				x1 = (int) event.getX();
				y1 = (int) event.getY();
				getPosition(x1, y1);
			}
			break;
		// 当屏幕上已经有一个触点了，再有一个手指按下屏幕，就会相应
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			firstTouch = true;
			move = true;
			startDis = distance(event);
			if (startDis > 10f) {
				// midPoint = mid(event);
			}
			break;

		// 手指离开屏幕，当屏幕还有一个手指在屏幕上时，就相应
		case MotionEvent.ACTION_POINTER_UP:
			mode = 0;
			break;

		}
		if (mode == ZOOM) {
			if (scale > 2) {
				lastScale = 2;
				scale = 2;
			}
			this.setScaleX(scale);

			if (scale == 1) {
				int l = 0;
				int t = 0;
				int r = this.getWidth();
				int b = this.getHeight();
				this.layout(l, t, r, b);
			}
		}
		return true;
	}

	private void getPosition(int x12, int y12) {

		if (null == rects) {
			return;
		}

		for (int i = 1; i <= rects.size(); i++) {
			Rect r = rects.get(i - 1);
			if (r.contains(x12, y12)) {
				if (null != histogramOnClickListener) {
					histogramOnClickListener.onClick(i);
				}

			}
		}

	}

	@SuppressLint("FloatMath")
	public float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		float dz = FloatMath.sqrt(dx * dx + dy * dy);

		return dz;
	}

	private PointF mid(MotionEvent event) {
		float dx = event.getX(1) + event.getX(0);
		float dy = event.getY(1) + event.getY(0);
		return new PointF(dx / 2, dy / 2);
	}

}
