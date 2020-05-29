package com.novel.collection.widget;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

import q.rorbin.badgeview.Badge;

/**
 * @author chqiu
 *         Email:qstumn@163.com
 */

public interface ITabView1 {

    ITabView1 setBadge(ITabView1.TabBadge badge);

    ITabView1 setIcon(ITabView1.TabIcon icon);

    ITabView1 setTitle(ITabView1.TabTitle title);

    ITabView1 setBackground(int resid);

    ITabView1.TabBadge getBadge();

    ITabView1.TabIcon getIcon();

    ITabView1.TabTitle getTitle();

    View getTabView();

    class TabIcon {

        private ITabView1.TabIcon.Builder mBuilder;

        private TabIcon(ITabView1.TabIcon.Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public int getSelectedIcon() {
            return mBuilder.mSelectedIcon;
        }

        public int getNormalIcon() {
            return mBuilder.mNormalIcon;
        }

        public int getIconGravity() {
            return mBuilder.mIconGravity;
        }

        public int getIconWidth() {
            return mBuilder.mIconWidth;
        }

        public int getIconHeight() {
            return mBuilder.mIconHeight;
        }

        public int getMargin() {
            return mBuilder.mMargin;
        }

        public static class Builder {
            private int mSelectedIcon;
            private int mNormalIcon;
            private int mIconGravity;
            private int mIconWidth;
            private int mIconHeight;
            private int mMargin;

            public Builder() {
                mSelectedIcon = 0;
                mNormalIcon = 0;
                mIconWidth = -1;
                mIconHeight = -1;
                mIconGravity = Gravity.START;
                mMargin = 0;
            }

            public ITabView1.TabIcon.Builder setIcon(int selectIconResId, int normalIconResId) {
                mSelectedIcon = selectIconResId;
                mNormalIcon = normalIconResId;
                return this;
            }

            public ITabView1.TabIcon.Builder setIconSize(int width, int height) {
                mIconWidth = width;
                mIconHeight = height;
                return this;
            }

            public ITabView1.TabIcon.Builder setIconGravity(int gravity) {
                if (gravity != Gravity.START && gravity != Gravity.END
                        & gravity != Gravity.TOP & gravity != Gravity.BOTTOM) {
                    throw new IllegalStateException("iconGravity only support Gravity.START " +
                            "or Gravity.END or Gravity.TOP or Gravity.BOTTOM");
                }
                mIconGravity = gravity;
                return this;
            }

            public ITabView1.TabIcon.Builder setIconMargin(int margin) {
                mMargin = margin;
                return this;
            }

            public ITabView1.TabIcon build() {
                return new ITabView1.TabIcon(this);
            }
        }
    }

    class TabTitle {
        private ITabView1.TabTitle.Builder mBuilder;

        private TabTitle(ITabView1.TabTitle.Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public int getColorSelected() {
            return mBuilder.mColorSelected;
        }

        public int getColorNormal() {
            return mBuilder.mColorNormal;
        }

        public int getTitleTextSize() {
            return mBuilder.mTitleTextSize;
        }

        public String getContent() {
            return mBuilder.mContent;
        }

        public static class Builder {
            private int mColorSelected;
            private int mColorNormal;
            private int mTitleTextSize;
            private String mContent;

            public Builder() {
                this.mColorSelected = 0xFFFF4081;
                this.mColorNormal = 0xFF757575;
                this.mTitleTextSize = 16;
                this.mContent = "";
            }

            public ITabView1.TabTitle.Builder setTextColor(int colorSelected, int colorNormal) {
                mColorSelected = colorSelected;
                mColorNormal = colorNormal;
                return this;
            }

            public ITabView1.TabTitle.Builder setTextSize(int sizeSp) {
                mTitleTextSize = sizeSp;
                return this;
            }

            public ITabView1.TabTitle.Builder setContent(String content) {
                mContent = content;
                return this;
            }

            public ITabView1.TabTitle build() {
                return new ITabView1.TabTitle(this);
            }
        }
    }

    class TabBadge {
        private ITabView1.TabBadge.Builder mBuilder;

        private TabBadge(ITabView1.TabBadge.Builder mBuilder) {
            this.mBuilder = mBuilder;
        }

        public int getBackgroundColor() {
            return mBuilder.colorBackground;
        }

        public int getBadgeTextColor() {
            return mBuilder.colorBadgeText;
        }

        public float getBadgeTextSize() {
            return mBuilder.badgeTextSize;
        }

        public float getBadgePadding() {
            return mBuilder.badgePadding;
        }

        public int getBadgeNumber() {
            return mBuilder.badgeNumber;
        }

        public String getBadgeText() {
            return mBuilder.badgeText;
        }

        public int getBadgeGravity() {
            return mBuilder.badgeGravity;
        }

        public int getGravityOffsetX() {
            return mBuilder.gravityOffsetX;
        }

        public int getGravityOffsetY() {
            return mBuilder.gravityOffsetY;
        }

        public boolean isExactMode() {
            return mBuilder.exactMode;
        }

        public boolean isShowShadow() {
            return mBuilder.showShadow;
        }

        public Drawable getDrawableBackground() {
            return mBuilder.drawableBackground;
        }

        public int getStrokeColor() {
            return mBuilder.colorStroke;
        }

        public boolean isDrawableBackgroundClip() {
            return mBuilder.drawableBackgroundClip;
        }

        public float getStrokeWidth() {
            return mBuilder.strokeWidth;
        }

        public Badge.OnDragStateChangedListener getOnDragStateChangedListener() {
            return mBuilder.dragStateChangedListener;
        }

        public static class Builder {
            private int colorBackground;
            private int colorBadgeText;
            private int colorStroke;
            private Drawable drawableBackground;
            private boolean drawableBackgroundClip;
            private float strokeWidth;
            private float badgeTextSize;
            private float badgePadding;
            private int badgeNumber;
            private String badgeText;
            private int badgeGravity;
            private int gravityOffsetX;
            private int gravityOffsetY;
            private boolean exactMode;
            private boolean showShadow;
            private Badge.OnDragStateChangedListener dragStateChangedListener;

            public Builder() {
                colorBackground = 0xFFE84E40;
                colorBadgeText = 0xFFFFFFFF;
                colorStroke = Color.TRANSPARENT;
                drawableBackground = null;
                drawableBackgroundClip = false;
                strokeWidth = 0;
                badgeTextSize = 11;
                badgePadding = 5;
                badgeNumber = 0;
                badgeText = null;
                badgeGravity = Gravity.END | Gravity.TOP;
                gravityOffsetX = 1;
                gravityOffsetY = 1;
                exactMode = false;
                showShadow = true;
            }

            public ITabView1.TabBadge build() {
                return new ITabView1.TabBadge(this);
            }

            public ITabView1.TabBadge.Builder stroke(int color, int strokeWidth) {
                this.colorStroke = color;
                this.strokeWidth = strokeWidth;
                return this;
            }

            public ITabView1.TabBadge.Builder setDrawableBackground(Drawable drawableBackground, boolean clip) {
                this.drawableBackground = drawableBackground;
                this.drawableBackgroundClip = clip;
                return this;
            }

            public ITabView1.TabBadge.Builder setShowShadow(boolean showShadow) {
                this.showShadow = showShadow;
                return this;
            }

            public ITabView1.TabBadge.Builder setOnDragStateChangedListener(Badge.OnDragStateChangedListener dragStateChangedListener) {
                this.dragStateChangedListener = dragStateChangedListener;
                return this;
            }

            public ITabView1.TabBadge.Builder setExactMode(boolean exactMode) {
                this.exactMode = exactMode;
                return this;
            }

            public ITabView1.TabBadge.Builder setBackgroundColor(int colorBackground) {
                this.colorBackground = colorBackground;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgePadding(float dpValue) {
                this.badgePadding = dpValue;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgeNumber(int badgeNumber) {
                this.badgeNumber = badgeNumber;
                this.badgeText = null;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgeGravity(int badgeGravity) {
                this.badgeGravity = badgeGravity;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgeTextColor(int colorBadgeText) {
                this.colorBadgeText = colorBadgeText;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgeTextSize(float badgeTextSize) {
                this.badgeTextSize = badgeTextSize;
                return this;
            }

            public ITabView1.TabBadge.Builder setBadgeText(String badgeText) {
                this.badgeText = badgeText;
                this.badgeNumber = 0;
                return this;
            }

            public ITabView1.TabBadge.Builder setGravityOffset(int offsetX, int offsetY) {
                this.gravityOffsetX = offsetX;
                this.gravityOffsetY = offsetY;
                return this;
            }
        }
    }
}
