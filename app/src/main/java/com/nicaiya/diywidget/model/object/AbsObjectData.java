package com.nicaiya.diywidget.model.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.nicaiya.diywidget.BuildConfig;
import com.nicaiya.diywidget.R;
import com.nicaiya.diywidget.ResourceUtil;
import com.nicaiya.diywidget.model.ConfigFileData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class AbsObjectData {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean DEBUG_DRAW = false;

    private static final String TAG = AbsObjectData.class.getSimpleName();

    public static final int ANCHOR_TYPE_LT = 0;
    public static final int ANCHOR_TYPE_T = 1;
    public static final int ANCHOR_TYPE_RT = 2;
    public static final int ANCHOR_TYPE_L = 3;
    public static final int ANCHOR_TYPE_C = 4;
    public static final int ANCHOR_TYPE_R = 5;
    public static final int ANCHOR_TYPE_LB = 6;
    public static final int ANCHOR_TYPE_B = 7;
    public static final int ANCHOR_TYPE_RB = 8;

    private static Paint debugPaint;

    protected PointF anchorOffset = new PointF();
    protected boolean anchorOffsetInvalidate = true;
    private static Paint anchorPaint;
    private int anchorType = ANCHOR_TYPE_C;

    protected Rect bounds = new Rect();
    protected boolean boundsInvalidate = true;

    private static Paint focusPaint;
    private boolean isFocus = false;

    protected Matrix matrix = new Matrix();
    protected boolean matrixInvalidate = true;

    protected Paint paint = new Paint();
    protected boolean paintInvalidate = true;

    private float rotate = 0.0F;
    private float left = 0.0F;
    private float top = 0.0F;

    private String name = "";
    private int alpha = 255;

    public void setLeft(float left) {
        if (this.left != left) {
            this.left = left;
            matrixInvalidate = true;
        }
    }

    public float getLeft() {
        return left;
    }

    public void setTop(float top) {
        if (this.top != top) {
            this.top = top;
            matrixInvalidate = true;
        }
    }

    public float getTop() {
        return top;
    }


    public void setRotate(float rotate) {
        if (rotate < 0) {
            rotate += 360.0f;
        }
        if (this.rotate != rotate) {
            this.rotate = rotate;
            matrixInvalidate = true;
        }
    }

    public float getRotate() {
        return rotate;
    }


    public void setAlpha(int alpha) {
        if (this.alpha != alpha) {
            this.alpha = alpha;
            paintInvalidate = true;
        }
    }

    public int getAlpha() {
        return alpha;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAnchorType(int anchorType) {
        if (this.anchorType != anchorType) {
            float[] points = {0.0F, 0.0F};
            getMatrix().mapPoints(points);
            this.anchorType = anchorType;
            float[] points2 = {0.0F, 0.0F};
            getMatrix().mapPoints(points2);
            setLeft(getLeft() - (points2[0] - points[0]));
            setTop(getTop() - (points2[1] - points[1]));

            anchorOffsetInvalidate = true;
            matrixInvalidate = true;
        }
    }

    public int getAnchorType() {
        return anchorType;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public PointF getAnchorOffset() {
        if (anchorOffsetInvalidate) {
            initAnchorOffset();
        }
        return anchorOffset;
    }

    protected void initAnchorOffset() {
        if (anchorOffsetInvalidate) {
            anchorOffsetInvalidate = false;
            Rect bounds = getBounds();
            switch (anchorType) {
                case ANCHOR_TYPE_LT:
                    anchorOffset.x = bounds.left;
                    anchorOffset.y = bounds.top;
                    break;
                case ANCHOR_TYPE_T:
                    anchorOffset.x = bounds.centerX();
                    anchorOffset.y = bounds.top;
                    break;
                case ANCHOR_TYPE_RT:
                    anchorOffset.x = bounds.right;
                    anchorOffset.y = bounds.top;
                    break;
                case ANCHOR_TYPE_L:
                    anchorOffset.x = bounds.left;
                    anchorOffset.y = bounds.centerY();
                    break;
                case ANCHOR_TYPE_C:
                    anchorOffset.x = bounds.centerX();
                    anchorOffset.y = bounds.centerY();
                    break;
                case ANCHOR_TYPE_R:
                    anchorOffset.x = bounds.right;
                    anchorOffset.y = bounds.centerY();
                    break;
                case ANCHOR_TYPE_LB:
                    anchorOffset.x = bounds.left;
                    anchorOffset.y = bounds.bottom;
                    break;
                case ANCHOR_TYPE_B:
                    anchorOffset.x = bounds.centerX();
                    anchorOffset.y = bounds.bottom;
                    break;
                case ANCHOR_TYPE_RB:
                    anchorOffset.x = bounds.right;
                    anchorOffset.y = bounds.bottom;
                    break;
                default:
                    throw new IllegalStateException("Error Anchor Type drawAnchor()");
            }
        }
    }

    protected void initBounds() {
    }

    public Rect getBounds() {
        if (boundsInvalidate) {
            initBounds();
        }
        return bounds;
    }

    protected void initMatrix() {
    }

    public Matrix getMatrix() {
        if (matrixInvalidate) {
            initMatrix();
        }
        return matrix;
    }


    protected void initPaint() {
    }

    public Paint getPaint() {
        if (paintInvalidate) {
            initPaint();
        }
        return paint;
    }

    public void draw(Canvas canvas) {
        drawDebugLine(canvas);
    }

    private void drawDebugLine(Canvas canvas) {
        if (DEBUG_DRAW) {
            if (debugPaint == null) {
                debugPaint = new Paint();
                debugPaint.setAntiAlias(true);
                debugPaint.setColor(Color.RED);
                debugPaint.setStyle(Paint.Style.STROKE);
                PathEffect effects = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
                debugPaint.setPathEffect(effects);
            }
            Rect bounds = getBounds();
            canvas.drawRect(bounds, debugPaint);
        }
    }

    public void drawAnchor(Canvas canvas) {
        Rect bounds = getBounds();
        if (anchorPaint == null) {
            anchorPaint = new Paint();
            anchorPaint.setAntiAlias(true);
            anchorPaint.setColor(ResourceUtil.getColor(R.color.object_anchor));
            anchorPaint.setStyle(Paint.Style.STROKE);
        }
        canvas.setMatrix(getMatrix());
        anchorPaint.setStrokeWidth(2);
        anchorPaint.setAlpha(255);

        int lineLength = Math.min(Math.min(bounds.width(), bounds.height()) / 5, 40);
        PointF anchorOffset = getAnchorOffset();
        canvas.drawLine(anchorOffset.x - lineLength, anchorOffset.y, anchorOffset.x + lineLength, anchorOffset.y, anchorPaint);
        canvas.drawLine(anchorOffset.x, anchorOffset.y - lineLength, anchorOffset.x, anchorOffset.y + lineLength, anchorPaint);

        anchorPaint.setAlpha(178);
        anchorPaint.setStyle(Paint.Style.FILL);
        int radius = Math.min(lineLength * 15 / 40, 15);
        canvas.drawCircle(anchorOffset.x, anchorOffset.y, radius, anchorPaint);
    }

    public void drawFocus(Canvas canvas) {
        Rect bounds = getBounds();
        if (focusPaint == null) {
            focusPaint = new Paint();
            focusPaint.setAntiAlias(true);
            focusPaint.setColor(ResourceUtil.getColor(R.color.object_focus));
            focusPaint.setStyle(Paint.Style.STROKE);
        }
        focusPaint.setStrokeWidth(2);
        canvas.setMatrix(getMatrix());

        int lineLength = Math.min(Math.min(bounds.width(), bounds.height()) / 4, 48);
        canvas.drawLine(bounds.left, 1 + bounds.top, lineLength + bounds.left, 1 + bounds.top, focusPaint);
        canvas.drawLine(1 + bounds.left, bounds.top, 1 + bounds.left, lineLength + bounds.top, focusPaint);
        canvas.drawLine(bounds.right - lineLength, 1 + bounds.top, bounds.right, 1 + bounds.top, focusPaint);
        canvas.drawLine(-1 + bounds.right, bounds.top, -1 + bounds.right, lineLength + bounds.top, focusPaint);
        canvas.drawLine(1 + bounds.left, bounds.bottom - lineLength, 1 + bounds.left, bounds.bottom, focusPaint);
        canvas.drawLine(bounds.left, -1 + bounds.bottom, lineLength + bounds.left, -1 + bounds.bottom, focusPaint);
        canvas.drawLine(-1 + bounds.right, bounds.bottom - lineLength, -1 + bounds.right, bounds.bottom, focusPaint);
        canvas.drawLine(bounds.right - lineLength, -1 + bounds.bottom, bounds.right, -1 + bounds.bottom, focusPaint);
    }

    public void deleteResource() {
        name = null;
        paint = null;
        matrix = null;
        bounds = null;
        anchorOffset = null;
    }

    public boolean hitTest(int x, int y) {
        return false;
    }

    public void putToXmlSerializer(ConfigFileData data) throws Exception {
        XmlSerializer xmlSerializer = data.getXmlSerializer();
        xmlSerializer.startTag(ConfigFileData.XML_NAMESPACE, TAG);
        xmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_LEFT, String.valueOf(left));
        xmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_TOP, String.valueOf(top));
        xmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ROTATE, String.valueOf(rotate));
        xmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ALPHA, String.valueOf(alpha));
        xmlSerializer.attribute(ConfigFileData.XML_NAMESPACE, XMLConst.ATTRIBUTE_ANCHOR, String.valueOf(anchorType));
        xmlSerializer.endTag(ConfigFileData.XML_NAMESPACE, TAG);
    }

    public void updateFromXmlPullParser(ConfigFileData data) {
        XmlPullParser parser = data.getXmlParser();
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (TAG.equals(tag)) {
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attrName = parser.getAttributeName(i);
                                String attrValue = parser.getAttributeValue(i);
                                if (XMLConst.ATTRIBUTE_LEFT.equals(attrName)) {
                                    setLeft(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_TOP.equals(attrName)) {
                                    setTop(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_ROTATE.equals(attrName)) {
                                    setRotate(Float.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_ALPHA.equals(attrName)) {
                                    setAlpha(Integer.valueOf(attrValue));
                                } else if (XMLConst.ATTRIBUTE_ANCHOR.equals(attrName)) {
                                    setAnchorType(Integer.valueOf(attrValue));
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (TAG.equals(tag)) {
                            return;
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "AbsObjectData{" +
                "paint=" + paint +
                ", paintInvalidate=" + paintInvalidate +
                ", anchorOffset=" + anchorOffset +
                ", anchorOffsetInvalidate=" + anchorOffsetInvalidate +
                ", bounds=" + bounds +
                ", boundsInvalidate=" + boundsInvalidate +
                ", matrix=" + matrix +
                ", matrixInvalidate=" + matrixInvalidate +
                ", isFocus=" + isFocus +
                ", alpha=" + alpha +
                ", rotate=" + rotate +
                ", left=" + left +
                ", top=" + top +
                ", anchorType=" + anchorType +
                ", name='" + name + '\'' +
                '}';
    }
}