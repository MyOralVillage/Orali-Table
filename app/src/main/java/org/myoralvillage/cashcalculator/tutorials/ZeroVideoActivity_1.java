package org.myoralvillage.cashcalculator.tutorials;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import org.myoralvillage.cashcalculator.MainActivity;
import org.myoralvillage.cashcalculator.R;
import org.myoralvillage.cashcalculator.config.CashCalculatorConstants;
import org.myoralvillage.cashcalculatormodule.fragments.CashCalculatorFragment;
import org.myoralvillage.cashcalculatormodule.models.CurrencyModel;
import org.myoralvillage.cashcalculatormodule.models.DenominationModel;
import org.myoralvillage.cashcalculatormodule.services.AnalyticsLogger;
import org.myoralvillage.cashcalculatormodule.views.CountingTableView;
import org.myoralvillage.cashcalculatormodule.views.CurrencyScrollbarView;
import org.myoralvillage.cashcalculatormodule.views.NumberPadView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZeroVideoActivity_1 extends AppCompatActivity {
    private CashCalculatorFragment fragment;
    private CountingTableView countingTable;
    private CurrencyScrollbarView currencyScrollbar;
    private NumberPadView numberPad;

    private CurrencyModel currency;
    private int numDenominations;
    private ArrayList<Animator> animations;
    private List<Integer> horizontalOffsets;
    private List<Integer> verticalOffsets;

    private int height;
    private int width;
    private int[] fingerLocation;
    private int[] scrollbarLocation;
    private int scrollbarWidth;
    private int scrollbarScrollPosition;

    private int elapsed = 0;

    private ImageView finger;
    private View black;

    private String currencyName;
    private boolean numericMode;
    private ImageView gototutorial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_zero_animation_2);
        gototutorial = (ImageView) findViewById(R.id.gototutorial);
        currencyName = getIntent().getStringExtra("currencyName");
        numericMode = getIntent().getBooleanExtra("numericMode", false);
        fragment = (CashCalculatorFragment) getSupportFragmentManager()
                .findFragmentById(R.id.TutorialFragment);
        if (fragment != null) {
            fragment.initialize(currencyName);
        }

        currencyScrollbar = fragment.getCurrencyScrollbarView();
        currency = currencyScrollbar.getCurrency();
        numDenominations = currency.getDenominations().size();
        countingTable = fragment.getCountingTableView();
        numberPad = fragment.getNumberPadView();
        horizontalOffsets = currencyScrollbar.getHorizontalOffsetsInPixels();
        verticalOffsets = currencyScrollbar.getVerticalOffsetsInPixels();
        animations = new ArrayList<>();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        fingerLocation = new int[2];
        scrollbarLocation = new int[2];
        finger = findViewById(R.id.finger);

//        ViewTreeObserver vto = currencyScrollbar.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                animate();
//                currencyScrollbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });

        (new Handler()).postDelayed(()->handAnimation(finger, 1300, 130, 500), 2000);
        (new Handler()).postDelayed(()->ImageAnimation(gototutorial, 0.1f, 1.0f), 3000);
        (new Handler()).postDelayed(()-> {
            switchtozerovideo2();
        }, 3500);

    }

    private void switchtozerovideo2() {
        Intent intent = new Intent(this, ZeroVideoActivity_2.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        intent.putExtra("pageNumber", 1);
        intent.putExtra("animationStage", 0);
        startActivity(intent);
        finish();
    }
    private void animate() {
        currencyScrollbar.getLocationOnScreen(scrollbarLocation);
        scrollbarWidth = currencyScrollbar.getChildAt(0).getWidth();
        scrollbarScrollPosition = (scrollbarWidth - width) / 2;
        getFadeOut(finger, 0).start();
        getFadeOut(black, 0).start();

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animations);
        set.start();

    }
    public void handAnimation(ImageView code, float x, float y, int milliSecond) {
        code.animate().x(x).y(y).setDuration(milliSecond);
    }
    public void ImageAnimation(ImageView code, float x, float y) {
        Animation anim = new AlphaAnimation(x, y);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.ABSOLUTE);
        code.startAnimation(anim);
    }

    public void makeCurrencyChange(boolean singleFinger) {
        if(singleFinger){
            finger.setImageResource(R.drawable.pointing_hand3);
        }
        else{
            finger.setImageResource(R.drawable.pointing_two_hand);
        }
    }
    private void runAddDenomination(int denominationIndex) {
        denominationIndex = (denominationIndex % numDenominations + numDenominations) % numDenominations;
        if (denominationIndex < numDenominations - 1 && horizontalOffsets.get(denominationIndex) <= width / 2) {
            runScrollBarScroll(0);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            animateFingerTap(horizontalOffsets.get(denominationIndex), scrollbarLocation[1] + verticalOffsets.get(denominationIndex), elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            addDenomination(denominationIndex, elapsed);
        } else if (denominationIndex > 1 && scrollbarWidth - horizontalOffsets.get(denominationIndex) <= width / 2) {
            runScrollBarScroll(scrollbarWidth - width);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            animateFingerTap(width - scrollbarWidth + horizontalOffsets.get(denominationIndex), scrollbarLocation[1] + verticalOffsets.get(denominationIndex), elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            addDenomination(denominationIndex, elapsed);
        } else {
            runScrollBarScroll(horizontalOffsets.get(denominationIndex) - width / 2);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            animateFingerTap(width / 2, scrollbarLocation[1] + verticalOffsets.get(denominationIndex), elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
            addDenomination(denominationIndex, elapsed);
        }
    }

    private void runScrollBarScroll(int destX) {
        int originalTime = elapsed;
        if (destX < 0) {
            destX = 0;
        } else if (destX >= scrollbarWidth - width) {
            destX = scrollbarWidth - width - 1;
        }
        int imaginaryScrollPosition = scrollbarScrollPosition;
        if (destX > imaginaryScrollPosition) {
            animateSwipe(3 * width / 4, scrollbarLocation[1], 3 * width / 4 - destX + imaginaryScrollPosition, scrollbarLocation[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        }
        if (destX < imaginaryScrollPosition) {
            animateSwipe(width / 4, scrollbarLocation[1], width / 4 - destX + imaginaryScrollPosition, scrollbarLocation[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
            elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        }
        animateScrollbarScroll(destX, originalTime, elapsed - originalTime);
    }

    private void runRemoveDenomination() {
        animateFingerTap(0, 0, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        removeDenomination(elapsed + CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
    }

    private void runClickNumber(int num) {
        View button;
        switch (num) {
            case 1:
                button = findViewById(R.id.one);
                break;
            case 2:
                button = findViewById(R.id.two);
                break;
            case 3:
                button = findViewById(R.id.three);
                break;
            case 4:
                button = findViewById(R.id.four);
                break;
            case 5:
                button = findViewById(R.id.five);
                break;
            case 6:
                button = findViewById(R.id.six);
                break;
            case 7:
                button = findViewById(R.id.seven);
                break;
            case 8:
                button = findViewById(R.id.eight);
                break;
            case 9:
                button = findViewById(R.id.nine);
                break;
            default:
                button = findViewById(R.id.zero);
                break;
        }
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        clickNumPadButton(button, elapsed);
    }

    private void runClickCross() {
        View button = findViewById(R.id.back);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1] - button.getWidth() / 2, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        clickNumPadButton(button, elapsed);
    }

    private void runClickCheck() {
        View button = findViewById(R.id.check);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1] - button.getWidth() / 2, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        clickNumPadButton(button, elapsed);
    }

    private void runSwitchToAddition() {
        animateSwipe(width - finger.getWidth(), height / 4, 0, height / 4, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME_CALC);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME_CALC;
        changeToAddition(elapsed);
    }

    private void runSwitchToSubtraction() {
        animateSwipe(0, height / 4, width - finger.getWidth(), height / 4, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME_CALC);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME_CALC;;
        changeToSubtraction(elapsed);
    }

    private void runSwitchToMemory() {
        animateSwipe(0, height / 4, width - finger.getWidth(), height / 4, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME_CALC);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME_CALC;;
        changeToMemory(elapsed);
    }

    private void runSwitchToMultiplication() {
        animateSwipe(width / 2, 0, width / 2, height * 9 / 10, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME_CALC);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME_CALC;;
        changeToMultiplication(elapsed);
    }

    private void runClear() {
        ImageView button = findViewById(org.myoralvillage.cashcalculatormodule.R.id.clear_button);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        pressClear(elapsed);
    }

    private void runCalculate() {
        ImageView button = findViewById(org.myoralvillage.cashcalculatormodule.R.id.calculate_button);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1] - button.getWidth() / 2, elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        pressCalculate(elapsed);
    }

    private void runEnterHistory() {
        ImageView button = findViewById(org.myoralvillage.cashcalculatormodule.R.id.enter_history_button);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        pressEnterHistory(elapsed);
    }

    private void runNext() {
        ImageView button = findViewById(org.myoralvillage.cashcalculatormodule.R.id.right_history_button);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        pressNext(elapsed);
    }

    private void runPrev() {
        ImageView button = findViewById(org.myoralvillage.cashcalculatormodule.R.id.left_history_button);
        int coords[] = new int[2];
        button.getLocationOnScreen(coords);
        animateFingerTap(coords[0] + button.getWidth() / 2 - finger.getWidth() / 2, coords[1], elapsed, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME);
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
        pressPrev(elapsed);
    }

    private void runFadeOutAndIn() {
        AnimatorSet as = new AnimatorSet();
        as.playSequentially(getFadeIn(black, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME), getFadeOut(black, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME));
        as.setStartDelay(elapsed);
        animations.add(as);
        elapsed += CashCalculatorConstants.ADV_VIDEO_FADE_TIME;
    }

    private void runFadeOut() {
        animations.add(getFadeIn(black, CashCalculatorConstants.ADV_VIDEO_DURATION_TIME));
        elapsed += CashCalculatorConstants.ADV_VIDEO_ELAPSED_TIME;
    }

    private void runExit() {
        exit(elapsed);
    }

    private void wait(int time) {
        elapsed += time;
    }

    private void exit(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ZeroVideoActivity_1.this, MainActivity.class);
                intent.putExtra("currencyCode", currencyName);
                intent.putExtra("numericMode", numericMode);
                startActivity(intent);
                finish();
            }
        }, time);
    }

    private void addDenomination(int denominationIndex, int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currencyScrollbar.getCurrencyScrollbarListener()
                        .onTapDenomination(getDenomination(denominationIndex));
            }
        }, time);
    }

    private void removeDenomination(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean removeSuccessful = false;
                for (int i = numDenominations - 1; i >= 0 && !removeSuccessful; i--) {
                    removeSuccessful = countingTable.getCountingTableSurfaceView().removeDenomination(getDenomination(i));
                }
            }
        }, time);
    }

    private void clickNumPadButton(View view, int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                numberPad.clickView(view);
            }
        }, time);
    }

    private void changeToAddition(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onSwipeAddition();
            }
        }, time);
    }

    private void changeFinger(int time, boolean single) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                makeCurrencyChange(single);
            }
        }, time);
    }
    private void changeToSubtraction(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onSwipeSubtraction();
            }
        }, time);
    }
    private void changeToMemory(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onMemorySwipe(true);
            }
        }, time);
    }

    private void changeToMultiplication(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onSwipeMultiplication();
            }
        }, time);
    }

    private void pressClear(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onTapClearButton();
            }
        }, time);
    }

    private void pressCalculate(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onTapCalculateButton();
            }
        }, time);
    }

    private void pressEnterHistory(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onTapEnterHistory();
            }
        }, time);
    }

    private void pressNext(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onTapNextHistory();
            }
        }, time);
    }

    private void pressPrev(int time) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countingTable.getListener().onTapPreviousHistory();
            }
        }, time);
    }

    private void animateScrollbarScroll(int xValue, int time, int duration) {
        ObjectAnimator animator = ObjectAnimator.ofInt(fragment.getCurrencyScrollbarView(), "scrollX", xValue);
        animator.setDuration(duration);
        animator.setStartDelay(time);
        scrollbarScrollPosition = xValue;
        animations.add(animator);
    }

    private void animateFingerTap(int x, int y, int time, int duration) {
        AnimatorSet moveSet = new AnimatorSet();
        AnimatorSet fadeSet = new AnimatorSet();
        AnimatorSet overallSet = new AnimatorSet();
        ObjectAnimator xMove = getXAnimation(finger, x, 0);
        ObjectAnimator yMove = getYAnimation(finger, y, 0);
        ObjectAnimator fadeIn = getFadeIn(finger, duration / 4);
        ObjectAnimator fadeOut = getFadeOut(finger, duration / 4);
        fadeOut.setStartDelay(duration * 3 / 4);
        moveSet.playTogether(xMove, yMove);
        fadeSet.playTogether(fadeIn, fadeOut);
        overallSet.playSequentially(moveSet, fadeSet);
        overallSet.setStartDelay(time);
        animations.add(overallSet);
    }

    private void animateSwipe(int xStart, int yStart, int xEnd, int yEnd, int time, int duration) {
        AnimatorSet initialMoveSet = new AnimatorSet();
        AnimatorSet swipeSet = new AnimatorSet();
        AnimatorSet overalllSet = new AnimatorSet();
        ObjectAnimator initialXMove = getXAnimation(finger, xStart, 0);
        ObjectAnimator initialYMove = getYAnimation(finger, yStart, 0);
        initialMoveSet.playTogether(initialXMove, initialYMove);
        ObjectAnimator fadeIn = getFadeIn(finger, duration / 8);
        ObjectAnimator swipeXMove = getXAnimation(finger, xEnd, duration);
        ObjectAnimator swipeYMove = getYAnimation(finger, yEnd, duration);
        ObjectAnimator fadeOut = getFadeOut(finger, duration / 8);
        fadeOut.setStartDelay(duration * 7 / 8);
        swipeSet.playTogether(swipeXMove, swipeYMove, fadeIn, fadeOut);
        overalllSet.playSequentially(initialMoveSet, swipeSet);
        overalllSet.setStartDelay(time);
        animations.add(overalllSet);
    }

    private ObjectAnimator getXAnimation(View view, float dX, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationX", dX);
        animation.setDuration(duration);
        return animation;
    }

    private ObjectAnimator getYAnimation(View view, float dY, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", dY);
        animation.setDuration(duration);
        return animation;
    }

    private ObjectAnimator getFadeOut(View view, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", 0f);
        animation.setDuration(duration);
        return animation;
    }

    private ObjectAnimator getFadeIn(View view, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "alpha", 1f);
        animation.setDuration(duration);
        return animation;
    }

    private DenominationModel getDenomination(int index) {
        Iterator<DenominationModel> denominationModelIterator = currency.getDenominations().iterator();
        DenominationModel current = null;
        for (int i = 0; i <= index; i++)
            current = denominationModelIterator.next();
        return current;
    }

    private boolean usesDecimal() {
        if (getDenomination(numDenominations - 1).getValue().compareTo(new BigDecimal("1")) >= 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }
}
