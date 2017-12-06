package test.com.norigintesttask.ui.table.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.com.norigintesttask.R;
import test.com.norigintesttask.model.Schedule;
import test.com.norigintesttask.model.Table;
import test.com.norigintesttask.util.DateUtil;

public class EPG extends ViewGroup {

    public static final int DAYS_BACK_MILLIS = 2 * 24 * 60 * 60 * 1000;        // 2 days
    public static final int DAYS_FORWARD_MILLIS = 2 * 24 * 60 * 60 * 1000;     // 2 days
    public static final int HOURS_IN_VIEWPORT_MILLIS = 2 * 60 * 60 * 1000;     // 2 hours
    public static final int TIME_LABEL_SPACING_MILLIS =  60 * 60 * 1000;        // 1 hour

    private final Rect mClipRect;
    private final Rect mDrawingRect;
    private final Rect mMeasuringRect;
    private final RectF mRectF;
    private final Paint mPaint;
    private final TextPaint mTextPaint;
    private final Scroller mScroller;
    private final GestureDetector mGestureDetector;

    private final int mChannelLayoutMargin;
    private final int mChannelLayoutPadding;
    private final int mChannelLayoutHeight;
    private final int mChannelLayoutWidth;
    private final int mChannelLayoutBackground;
    private final int mEventLayoutBackground;
    private final int mEventLayoutBackgroundCurrent;
    private final int mEventLayoutTextColor;
    private final int mEventLayoutSecondaryTextColor;
    private final int mEventLayoutTextSize;
    private final int mEventLayoutSecondaryTextSize;
    private final int mTimeBarLineWidth;
    private final int mTimeBarLineColor;
    private final int mTimeBarHeight;
    private final int mTimeBarTextSize;

    private final int mResetButtonTextSize;
    private final int mResetButtonHeight;
    private final int mResetButtonWidth;
    private final int mResetButtonRadius;
    private final int mResetButtonMargin;

    private final int mEPGBackground;
    private final Map<String, Bitmap> mChannelImageCache;
    private final Map<String, Target> mChannelImageTargetCache;

    private EPGClickListener mClickListener;
    private int mMaxHorizontalScroll;
    private int mMaxVerticalScroll;
    private long mMillisPerPixel;
    private long mTimeOffset;
    private long mTimeLowerBoundary;
    private long mTimeUpperBoundary;
    private Date currentDate = new Date(DateUtil.getCurrentTimeInMillis());

    private Table epgData;

    public EPG(Context context) {
        this(context, null);
    }

    public EPG(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EPG(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        resetBoundaries();

        mDrawingRect = new Rect();
        mClipRect = new Rect();
        mMeasuringRect = new Rect();
        mRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint();
        mGestureDetector = new GestureDetector(context, new OnGestureListener());
        mChannelImageCache = new HashMap<>();
        mChannelImageTargetCache = new HashMap<>();

        // Adding some friction that makes the epg less flappy.
        mScroller = new Scroller(context);
        mScroller.setFriction(0.2f);

        mEPGBackground = getResources().getColor(R.color.epg_background);

        mChannelLayoutMargin = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_margin);
        mChannelLayoutPadding = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_padding);
        mChannelLayoutHeight = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_height);
        mChannelLayoutWidth = getResources().getDimensionPixelSize(R.dimen.epg_channel_layout_width);
        mChannelLayoutBackground = getResources().getColor(R.color.epg_channel_layout_background);

        mEventLayoutBackground = getResources().getColor(R.color.epg_event_layout_background);
        mEventLayoutBackgroundCurrent = getResources().getColor(R.color.epg_event_layout_background_current);
        mEventLayoutTextColor = getResources().getColor(R.color.epg_event_primary_text);
        mEventLayoutSecondaryTextColor = getResources().getColor(R.color.epg_event_secondary_text);
        mEventLayoutTextSize = getResources().getDimensionPixelSize(R.dimen.epg_event_primary_text_size);
        mEventLayoutSecondaryTextSize = getResources().getDimensionPixelSize(R.dimen.epg_event_secondary_text_size);

        mTimeBarHeight = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_height);
        mTimeBarTextSize = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_text);
        mTimeBarLineWidth = getResources().getDimensionPixelSize(R.dimen.epg_time_bar_line_width);
        mTimeBarLineColor = getResources().getColor(R.color.epg_time_bar);

        mResetButtonTextSize = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_text_size);
        mResetButtonHeight = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_height);
        mResetButtonWidth = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_width);
        mResetButtonRadius = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_radius);
        mResetButtonMargin = getResources().getDimensionPixelSize(R.dimen.epg_reset_button_margin);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (epgData != null && epgData.channels != null && !epgData.channels.isEmpty()) {
            mTimeLowerBoundary = getTimeFrom(getScrollX());
            mTimeUpperBoundary = getTimeFrom(getScrollX() + getWidth());

            Rect drawingRect = mDrawingRect;
            drawingRect.left = getScrollX();
            drawingRect.top = getScrollY();
            drawingRect.right = drawingRect.left + getWidth();
            drawingRect.bottom = drawingRect.top + getHeight();

            drawChannelListItems(canvas, drawingRect);
            drawEvents(canvas, drawingRect);
            drawTimebar(canvas, drawingRect);
            drawTimeLine(canvas, drawingRect);
            drawResetButton(canvas, drawingRect);

            // If scroller is scrolling/animating do scroll. This applies when doing a fling.
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recalculateAndRedraw(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    private void drawResetButton(Canvas canvas, Rect drawingRect) {
        drawingRect = calculateResetButtonHitArea();
        mPaint.setColor(mTimeBarLineColor);

        mRectF.set(drawingRect);
        canvas.drawRoundRect(mRectF, mResetButtonRadius, mResetButtonRadius, mPaint);

        // Text
        mTextPaint.setColor(mEventLayoutTextColor);
        mTextPaint.setTextSize(mResetButtonTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        canvas.drawText("NOW",
                drawingRect.left + ((drawingRect.right - drawingRect.left) / 2),
                drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2)),
                mTextPaint);
    }

    private void drawTimebarBottomStroke(Canvas canvas, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getScrollY() + mTimeBarHeight;
        drawingRect.right = drawingRect.left + getWidth();
        drawingRect.bottom = drawingRect.top + mChannelLayoutMargin;

        // Bottom stroke
        mPaint.setColor(mEPGBackground);
        canvas.drawRect(drawingRect, mPaint);
    }

    private void drawTimebar(Canvas canvas, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getScrollY();
        drawingRect.right = drawingRect.left + getWidth();
        drawingRect.bottom = drawingRect.top + mTimeBarHeight;

        mClipRect.left = getScrollX();
        mClipRect.top = getScrollY();
        mClipRect.right = getScrollX() + getWidth();
        mClipRect.bottom = mClipRect.top + mTimeBarHeight;

        canvas.save();
        canvas.clipRect(mClipRect);

        // Background
        mPaint.setColor(mChannelLayoutBackground);
        canvas.drawRect(drawingRect, mPaint);

        // Time stamps
        mPaint.setColor(mEventLayoutTextColor);
        mPaint.setTextSize(mTimeBarTextSize);

        for (int i = 0; i < (HOURS_IN_VIEWPORT_MILLIS / TIME_LABEL_SPACING_MILLIS) + 1; i++) {
            // Get time and round to nearest half hour
            final long time = TIME_LABEL_SPACING_MILLIS *
                    (((mTimeLowerBoundary + (TIME_LABEL_SPACING_MILLIS * i)) +
                            (TIME_LABEL_SPACING_MILLIS / 2)) / TIME_LABEL_SPACING_MILLIS);

            String timeString = DateUtil.getShortTime(time);
            canvas.drawText(timeString,
                    getXFrom(time) - mPaint.measureText(timeString) / 2,
                    drawingRect.top + (((drawingRect.bottom - drawingRect.top) / 2) + (mTimeBarTextSize / 2)), mPaint);

        }

        canvas.restore();

        drawTimebarBottomStroke(canvas, drawingRect);
    }

    private void drawTimeLine(Canvas canvas, Rect drawingRect) {
        //long now = System.currentTimeMillis();
        long now = DateUtil.getCurrentTimeInMillis();

        if (showDrawThumb(now)) {
            drawingRect.left = getXFrom(now) - mTimeBarLineWidth;
            drawingRect.top = getScrollY() + mChannelLayoutMargin;
            drawingRect.right = drawingRect.left + mTimeBarLineWidth * 3;
            drawingRect.bottom = drawingRect.top + mTimeBarHeight - mChannelLayoutMargin;

            mPaint.setColor(mTimeBarLineColor);

            mRectF.set(drawingRect);
            canvas.drawRoundRect(mRectF, mResetButtonRadius, mResetButtonRadius, mPaint);
        }

        if (shouldDrawTimeLine(now)) {
            drawingRect.left = getXFrom(now);
            drawingRect.top = getScrollY() + mTimeBarHeight;
            drawingRect.right = drawingRect.left + mTimeBarLineWidth;
            drawingRect.bottom = drawingRect.top + getHeight();

            canvas.drawRect(drawingRect, mPaint);
        }

    }

    private void drawEvents(Canvas canvas, Rect drawingRect) {
        final int firstPos = getFirstVisibleChannelPosition();
        final int lastPos = getLastVisibleChannelPosition();

        for (int pos = firstPos; pos <= lastPos; pos++) {

            // Set clip rectangle
            mClipRect.left = getScrollX() + mChannelLayoutWidth + mChannelLayoutMargin;
            mClipRect.top = getTopFrom(pos);
            mClipRect.right = getScrollX() + getWidth();
            mClipRect.bottom = mClipRect.top + mChannelLayoutHeight;

            canvas.save();
            canvas.clipRect(mClipRect);

            // Draw each event
            boolean foundFirst = false;

            List<Schedule> epgEvents = epgData.channels.get(pos).schedules;

            for (Schedule event : epgEvents) {
                if (isEventVisible(event.start.getTime(), event.end.getTime())) {
                    drawEvent(canvas, pos, event, drawingRect);
                    foundFirst = true;
                } else if (foundFirst) {
                    break;
                }
            }

            canvas.restore();
        }

    }

    private void drawEvent(final Canvas canvas, final int channelPosition, final Schedule event, final Rect drawingRect) {

        setEventDrawingRectangle(channelPosition, event.start.getTime(), event.end.getTime(), drawingRect);

        // Background
        mPaint.setColor(DateUtil.isNowBetween(event.start.getTime(), event.end.getTime()) ? mEventLayoutBackgroundCurrent : mEventLayoutBackground);
        canvas.drawRect(drawingRect, mPaint);

        // Add left and right inner padding
        drawingRect.left += mChannelLayoutPadding;
        drawingRect.right -= mChannelLayoutPadding;

        // Text
        mPaint.setColor(mEventLayoutTextColor);
        mPaint.setTextSize(mEventLayoutTextSize);

        // Move drawing.top so text will be centered (text is drawn bottom>up)
        mPaint.getTextBounds(event.title, 0, event.title.length(), mMeasuringRect);
        drawingRect.top += mChannelLayoutPadding + mMeasuringRect.height();

        String title = event.title;
        title = title.substring(0,
                mPaint.breakText(title, true, drawingRect.right - drawingRect.left, null));
        canvas.drawText(title, drawingRect.left, drawingRect.top, mPaint);

        //Draw time
        mPaint.setColor(mEventLayoutSecondaryTextColor);
        mPaint.setTextSize(mEventLayoutSecondaryTextSize);

        String time = DateUtil.getShortTime(event.start.getTime()) + " - " + DateUtil.getShortTime(event.end.getTime());

        mPaint.getTextBounds(time, 0, time.length(), mMeasuringRect);
        drawingRect.top += mChannelLayoutPadding + mMeasuringRect.height() / 2;

        time = time.substring(0,
                mPaint.breakText(time, true, drawingRect.right - drawingRect.left, null));

        canvas.drawText(time, drawingRect.left, drawingRect.top, mPaint);
    }

    private void setEventDrawingRectangle(final int channelPosition, final long start, final long end, final Rect drawingRect) {
        drawingRect.left = getXFrom(start);
        drawingRect.top = getTopFrom(channelPosition);
        drawingRect.right = getXFrom(end) - mChannelLayoutMargin;
        drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;
    }

    private void drawChannelListItems(Canvas canvas, Rect drawingRect) {
        final int firstPos = getFirstVisibleChannelPosition();
        final int lastPos = getLastVisibleChannelPosition();

        mPaint.setColor(mEPGBackground);

        for (int pos = firstPos; pos <= lastPos; pos++) {
            drawChannelItem(canvas, pos, drawingRect);
        }
    }

    private void drawChannelItem(final Canvas canvas, int position, Rect drawingRect) {
        drawingRect.left = getScrollX();
        drawingRect.top = getTopFrom(position);
        drawingRect.right = drawingRect.left + mChannelLayoutWidth;
        drawingRect.bottom = drawingRect.top + mChannelLayoutHeight;

        mPaint.setColor(mChannelLayoutBackground);
        canvas.drawRect(drawingRect, mPaint);

        // Loading channel image into target for
        final String imageURL = epgData.channels.get(position).images.logo;

        if (mChannelImageCache.containsKey(imageURL)) {
            Bitmap image = mChannelImageCache.get(imageURL);
            drawingRect = getDrawingRectForChannelImage(drawingRect, image);
            canvas.drawBitmap(image, null, drawingRect, null);
        } else {
            final int smallestSide = Math.min(mChannelLayoutHeight, mChannelLayoutWidth);

            if (!mChannelImageTargetCache.containsKey(imageURL)) {
                mChannelImageTargetCache.put(imageURL, new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mChannelImageCache.put(imageURL, bitmap);
                        redraw();
                        mChannelImageTargetCache.remove(imageURL);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                Picasso.with(getContext()).load(imageURL)
                        .resize(smallestSide, smallestSide)
                        .centerInside()
                        .into(mChannelImageTargetCache.get(imageURL));
            }

        }
    }

    private Rect getDrawingRectForChannelImage(Rect drawingRect, Bitmap image) {
        drawingRect.left += mChannelLayoutPadding;
        drawingRect.top += mChannelLayoutPadding;
        drawingRect.right -= mChannelLayoutPadding;
        drawingRect.bottom -= mChannelLayoutPadding;

        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        final float imageRatio = imageHeight / (float) imageWidth;

        final int rectWidth = drawingRect.right - drawingRect.left;
        final int rectHeight = drawingRect.bottom - drawingRect.top;

        // Keep aspect ratio.
        if (imageWidth > imageHeight) {
            final int padding = (int) (rectHeight - (rectWidth * imageRatio)) / 2;
            drawingRect.top += padding;
            drawingRect.bottom -= padding;
        } else if (imageWidth <= imageHeight) {
            final int padding = (int) (rectWidth - (rectHeight / imageRatio)) / 2;
            drawingRect.left += padding;
            drawingRect.right -= padding;
        }

        return drawingRect;
    }

    private boolean showDrawThumb(long now) {
        return now >= mTimeLowerBoundary && now < mTimeUpperBoundary;
    }

    private boolean shouldDrawTimeLine(long now) {
        return now >= (mTimeLowerBoundary + mChannelLayoutWidth * mMillisPerPixel) && now < mTimeUpperBoundary;
    }

    private boolean isEventVisible(final long start, final long end) {
        return (start >= mTimeLowerBoundary && start <= mTimeUpperBoundary)
                || (end >= mTimeLowerBoundary && end <= mTimeUpperBoundary)
                || (start <= mTimeLowerBoundary && end >= mTimeUpperBoundary);
    }

    private long calculatedBaseLine() {
        //return LocalDateTime.now().toDateTime().minusMillis(DAYS_BACK_MILLIS).getMillis();
        return DateUtil.getCurrentTimeInMillis() - DAYS_BACK_MILLIS;
    }

    private int getFirstVisibleChannelPosition() {
        final int y = getScrollY();

        int position = (y - mChannelLayoutMargin - mTimeBarHeight)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        if (position < 0) {
            position = 0;
        }
        return position;
    }

    private int getLastVisibleChannelPosition() {
        final int y = getScrollY();
        final int totalChannelCount = epgData.channels.size();
        final int screenHeight = getHeight();
        int position = (y + screenHeight + mTimeBarHeight - mChannelLayoutMargin)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        if (position > totalChannelCount - 1) {
            position = totalChannelCount - 1;
        }

        // Add one extra row if we don't fill screen with current..
        return (y + screenHeight) > (position * mChannelLayoutHeight) && position < totalChannelCount - 1 ? position + 1 : position;
    }

    private void calculateMaxHorizontalScroll() {
        mMaxHorizontalScroll = (int) ((DAYS_BACK_MILLIS + DAYS_FORWARD_MILLIS - HOURS_IN_VIEWPORT_MILLIS) / mMillisPerPixel);
    }

    private void calculateMaxVerticalScroll() {
        final int maxVerticalScroll = getTopFrom(epgData.channels.size() - 2) + mChannelLayoutHeight;
        mMaxVerticalScroll = maxVerticalScroll < getHeight() ? 0 : maxVerticalScroll - getHeight();
    }

    private int getXFrom(long time) {
        return (int) ((time - mTimeOffset) / mMillisPerPixel) + mChannelLayoutMargin * 2;
    }

    private int getTopFrom(int position) {
        return position * (mChannelLayoutHeight + mChannelLayoutMargin)
                + mChannelLayoutMargin + mTimeBarHeight;
    }

    private long getTimeFrom(int x) {
        return (x * mMillisPerPixel) + mTimeOffset;
    }

    private long calculateMillisPerPixel() {
        return HOURS_IN_VIEWPORT_MILLIS / (getResources().getDisplayMetrics().widthPixels - mChannelLayoutMargin * 2);
    }

    private int getXPositionStart() {
        return getXFrom(DateUtil.getCurrentTimeInMillis() - (HOURS_IN_VIEWPORT_MILLIS / 2));
    }

    private void resetBoundaries() {
        mMillisPerPixel = calculateMillisPerPixel();
        mTimeOffset = calculatedBaseLine();
        mTimeLowerBoundary = getTimeFrom(0);
        mTimeUpperBoundary = getTimeFrom(getWidth());
        currentDate.setTime(mTimeLowerBoundary);
    }

    private Rect calculateChannelsHitArea() {
        mMeasuringRect.top = mTimeBarHeight;
        int visibleChannelsHeight = epgData.channels.size() * (mChannelLayoutHeight + mChannelLayoutMargin);
        mMeasuringRect.bottom = visibleChannelsHeight < getHeight() ? visibleChannelsHeight : getHeight();
        mMeasuringRect.left = 0;
        mMeasuringRect.right = mChannelLayoutWidth;
        return mMeasuringRect;
    }

    private Rect calculateProgramsHitArea() {
        mMeasuringRect.top = mTimeBarHeight;
        int visibleChannelsHeight = epgData.channels.size() * (mChannelLayoutHeight + mChannelLayoutMargin);
        mMeasuringRect.bottom = visibleChannelsHeight < getHeight() ? visibleChannelsHeight : getHeight();
        mMeasuringRect.left = mChannelLayoutWidth;
        mMeasuringRect.right = getWidth();
        return mMeasuringRect;
    }

    private Rect calculateResetButtonHitArea() {
        mMeasuringRect.left = getScrollX() + getWidth() - mResetButtonWidth - mResetButtonMargin;
        mMeasuringRect.top = getScrollY() + getHeight() - mResetButtonHeight - mResetButtonMargin;
        mMeasuringRect.right = mMeasuringRect.left + mResetButtonWidth;
        mMeasuringRect.bottom = mMeasuringRect.top + mResetButtonHeight;
        return mMeasuringRect;
    }

    private int getChannelPosition(int y) {
        y -= mTimeBarHeight;
        int channelPosition = (y + mChannelLayoutMargin)
                / (mChannelLayoutHeight + mChannelLayoutMargin);

        return epgData.channels.size() == 0 ? -1 : channelPosition;
    }

    private int getProgramPosition(int channelPosition, long time) {
        List<Schedule> events = epgData.channels.get(channelPosition).schedules;

        if (events != null) {

            for (int eventPos = 0; eventPos < events.size(); eventPos++) {
                Schedule event = events.get(eventPos);

                if (event.start.getTime() <= time && event.end.getTime() >= time) {
                    return eventPos;
                }
            }
        }
        return -1;
    }

    /**
     * Add click listener to the EPG.
     * @param epgClickListener to add.
     */
    public void setEPGClickListener(EPGClickListener epgClickListener) {
        mClickListener = epgClickListener;
    }

    /**
     * Add data to EPG. This must be set for EPG to able to draw something.
     * @param epgData pass in any implementation of EPGData.
     */
    public void setEPGData(Table epgData) {
        this.epgData = epgData;
    }

    /**
     * This will recalculate boundaries, maximal scroll and scroll to start position which is current time.
     * To be used on device rotation etc since the device height and width will change.
     * @param withAnimation true if scroll to current position should be animated.
     */
    public void recalculateAndRedraw(boolean withAnimation) {
        if (epgData != null && epgData.channels != null && !epgData.channels.isEmpty()) {
            resetBoundaries();

            calculateMaxVerticalScroll();
            calculateMaxHorizontalScroll();

            mScroller.startScroll(getScrollX(), getScrollY(),
                    getXPositionStart() - getScrollX(),
                    0, withAnimation ? 600 : 0);

            redraw();
        }
    }

    /**
     * Does a invalidate() and requestLayout() which causes a redraw of screen.
     */
    public void redraw() {
        invalidate();
        requestLayout();
    }

    /**
     * Clears the local image cache for channel images. Can be used when leaving epg and you want to
     * free some memory. Images will be fetched again when loading EPG next time.
     */
    public void clearEPGImageCache() {
        mChannelImageCache.clear();
    }


    private class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            // This is absolute coordinate on screen not taking scroll into account.
            int x = (int) e.getX();
            int y = (int) e.getY();

            // Adding scroll to clicked coordinate
            int scrollX = getScrollX() + x;
            int scrollY = getScrollY() + y;

            int channelPosition = getChannelPosition(scrollY);
            if (channelPosition != -1 && mClickListener != null) {
                if (calculateResetButtonHitArea().contains(scrollX,scrollY)) {
                    // Reset button clicked
                    mClickListener.onResetButtonClicked();
                } else if (calculateChannelsHitArea().contains(x, y)) {
                    // Channel area is clicked
                    mClickListener.onChannelClicked(channelPosition, epgData.channels.get(channelPosition));
                } else if (calculateProgramsHitArea().contains(x, y)) {
                    // Event area is clicked
                    int programPosition = getProgramPosition(channelPosition, getTimeFrom(getScrollX() + x - calculateProgramsHitArea().left));
                    if (programPosition != -1) {
                        mClickListener.onEventClicked(channelPosition, programPosition, epgData.channels.get(channelPosition).schedules.get(programPosition));
                    }
                }
            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            int dx = (int) distanceX;
            int dy = (int) distanceY;
            int x = getScrollX();
            int y = getScrollY();


            // Avoid over scrolling
            if (x + dx < 0) {
                dx = 0 - x;
            }
            if (y + dy < 0) {
                dy = 0 - y;
            }
            if (x + dx > mMaxHorizontalScroll) {
                dx = mMaxHorizontalScroll - x;
            }
            if (y + dy > mMaxVerticalScroll) {
                dy = mMaxVerticalScroll - y;
            }

            scrollBy(dx, dy);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float vX, float vY) {

            mScroller.fling(getScrollX(), getScrollY(), -(int) vX,
                    -(int) vY, 0, mMaxHorizontalScroll, 0, mMaxVerticalScroll);

            redraw();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
                return true;
            }
            return true;
        }
    }
}
