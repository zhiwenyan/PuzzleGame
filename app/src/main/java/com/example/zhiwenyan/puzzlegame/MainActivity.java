package com.example.zhiwenyan.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private ImageView[][] imageViews = new ImageView[3][5];
    //当前空方块的实例保存
    private ImageView iv_null_imageview;

    private GestureDetector mGestureDetector;   //手势

    private boolean isGameStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestureDetector = new GestureDetector(this, mOnGestureListener);
        gridLayout = (GridLayout) findViewById(R.id.GridLayout);
        initUI();  //初始化游戏界面
    }

    //手势滑动
    GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        /**
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            changeDir(getDirByGes(e1.getX(), e1.getY(), e2.getX(), e2.getY()));
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void initUI() {
        //获取图片资源
        Bitmap bigBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.a);
        int width = bigBitmap.getWidth() / 5;
        //初始化游戏的若干个小方块
        for (int i = 0; i < imageViews.length; i++) {
            for (int j = 0; j < imageViews[0].length; j++) {
                Bitmap bitmap = Bitmap.createBitmap(bigBitmap,
                        width * j, width * i, width, width);
                imageViews[i][j] = new ImageView(this);
                imageViews[i][j].setImageBitmap(bitmap);
                //设置各个小方块之间的间距
                imageViews[i][j].setPadding(2, 2, 2, 2);
                imageViews[i][j].setTag(new GameData(i, j, bitmap)); //绑定自定义数据
                imageViews[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = isHasByNullImageView((ImageView) view);
                        if (flag) {
                            changeDataByImageView((ImageView) view);
                        }
                    }
                });

            }
        }
        for (int i = 0; i < imageViews.length; i++) {
            for (int j = 0; j < imageViews[0].length; j++) {
                gridLayout.addView(imageViews[i][j]);
            }
        }
        //设置空图片
        setNullImageView(imageViews[2][4]);
        randomMove();
    }

    private void setNullImageView(ImageView imageView) {
        imageView.setImageBitmap(null);
        iv_null_imageview = imageView;
    }

    //判断手势  是向左滑动   向右滑动
    private void changeDir(int type) {
        changeDir(type, true);
    }

    //判断手势  是向左滑动   向右滑动

    /**
     * @param type
     * @param isAnim
     */
    private void changeDir(int type, boolean isAnim) {
        //获取当前空方块的位置
        GameData mNullGameData = (GameData) iv_null_imageview.getTag();
        //根据方向,设置相应相邻的位置的坐标
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;
        if (type == 1) {
            new_x++;
        } else if (type == 2) {
            new_x--;
        } else if (type == 3) {
            new_y++;
        } else if (type == 4) {
            new_y--;
        }
        if (new_x >= 0 && new_x < imageViews.length && new_y >= 0 && new_y < imageViews[0].length) {
            if (isAnim) {
                changeDataByImageView(imageViews[new_x][new_y]);
            } else {
                changeDataByImageView(imageViews[new_x][new_y], isAnim);
            }

        }
    }

    boolean isGameOver;

    //判断游戏结束的方法
    private void isGameOver() {
        isGameOver = false;
        //遍历每个游戏的小方块
        for (int i = 0; i < imageViews.length; i++) {
            for (int j = 0; j < imageViews[0].length; j++) {
                if (imageViews[i][j] == iv_null_imageview) {
                    continue;
                }
                GameData gameData = (GameData) imageViews[i][j].getTag();
                if (gameData.isTrue()) {
                    isGameOver = true;
                    break;
                }
            }
        }
        if (isGameOver) {
            System.out.println("----游戏结束----");

        }

    }

    /***
     * @param start_x
     * @param start_y
     * @param end_x
     * @param end_y
     * @return 1上 2下  3左  4右
     */
    private int getDirByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = (Math.abs(end_x - start_x) > Math.abs(end_y - start_y));
        if (isLeftOrRight) {  //左右
            if (start_x - end_x > 0) {
                return 3;
            } else {
                return 4;
            }
            //上下
        } else {
            if (start_y - end_y > 0) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    //随机打乱顺序
    private void randomMove() {
        //打乱的次数
        for (int i = 0; i < 10; i++) {
            //设置手势开始交换，无动画
            int type = (int) (Math.random() * 4) + 1;
            changeDir(type, false);
            isGameStart = true;

        }
    }

    private void changeDataByImageView(final ImageView mImageView) {
        changeDataByImageView(mImageView, true);
    }

    TranslateAnimation animation;

    //利用动画结束后,交换两个方块的数据
    private void changeDataByImageView(final ImageView mImageView, boolean isAnim) {
        if (!isAnim) {
            //交换
            GameData mGameData = (GameData) mImageView.getTag();
            iv_null_imageview.setImageBitmap(mGameData.bitmap);//设置空方块的图片
            GameData mNullGameData = (GameData) iv_null_imageview.getTag();
            mNullGameData.bitmap = mGameData.bitmap;
            mNullGameData.p_x = mGameData.p_x;
            mNullGameData.p_y = mGameData.p_y;
            //设置当前点击的方块为空方块
            setNullImageView(mImageView);
            if (isGameStart) {
                isGameOver();  //成功
            }
            return;
        }
        if (mImageView.getX() > iv_null_imageview.getX()) {  //当前点击的方块在空方块的下方
            //往上移动
            animation = new TranslateAnimation(0.1f, -mImageView.getWidth(), 0.1f, 0.1f);
        } else if (mImageView.getX() < iv_null_imageview.getX()) {
            animation = new TranslateAnimation(0.1f, mImageView.getWidth(), 0.1f, 0.1f);
        } else if (mImageView.getY() > iv_null_imageview.getY()) {
            animation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -mImageView.getWidth());
        } else if (mImageView.getX() < iv_null_imageview.getX()) {
            animation = new TranslateAnimation(0.1f, 0.1f, 0.1f, mImageView.getWidth());
        }
        animation.setDuration(70);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.clearAnimation();
                //交换
                GameData mGameData = (GameData) mImageView.getTag();
                iv_null_imageview.setImageBitmap(mGameData.bitmap);//设置空方块的图片
                GameData mNullGameData = (GameData) iv_null_imageview.getTag();
                mNullGameData.bitmap = mGameData.bitmap;
                mNullGameData.p_x = mGameData.p_x;
                mNullGameData.p_y = mGameData.p_y;
                //设置当前点击的方块为空方块
                setNullImageView(mImageView);
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //执行动画
        mImageView.startAnimation(animation);
    }


    //判断空方块
    public boolean isHasByNullImageView(ImageView imageView) {
        GameData mNullGameData = (GameData) iv_null_imageview.getTag();
        GameData mGameData = (GameData) imageView.getTag();
        if (mNullGameData.y == mGameData.y
                && mGameData.x + 1 == mNullGameData.x) {   //当前点击的方块在空方块的上方
            return true;

        } else if (mNullGameData.y == mGameData.y
                && mGameData.x - 1 == mNullGameData.x) {   //当前点击的方块在空方块的下方
            return true;

        } else if (mNullGameData.y == mGameData.y - 1
                && mGameData.x == mNullGameData.x) {   //当前点击的方块在空方块的左方
            return true;

        } else if (mNullGameData.y == mGameData.y + 1
                && mGameData.x == mNullGameData.x) {   //当前点击的方块在空方块的右方
            return true;

        }

        return false;
    }

    //每个小方块要绑定的数据
    class GameData {
        //每个小方块的实际位置x
        public int x = 0;
        //每个小方块的实际位置y
        public int y = 0;
        //每个小方块的图片
        public Bitmap bitmap;
        //每个小方块的图片位置x;
        public int p_x;
        //每个小方块的图片位置y;
        public int p_y;

        public GameData(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            this.p_x = x;
            this.p_y = y;
        }

        //每个小方块的位置是否正确
        public boolean isTrue() {
            if (x == p_x && y == p_y) {
                return true;
            }
            return false;
        }
    }
}
