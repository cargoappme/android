package me.cargoapp.cargo.components.navui;

import android.graphics.Color;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.EventHandler;
import com.facebook.litho.Row;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Image;
import com.facebook.yoga.YogaJustify;

import org.greenrobot.eventbus.EventBus;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.event.NavuiLaunchEvent;

/**
 * Created by Marvin on 10/05/2017.
 */


@LayoutSpec
public class MainSpec {
    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c) {

        return Column.create(c)
                .justifyContent(YogaJustify.SPACE_AROUND)
                .backgroundColor(Color.parseColor("#00000000"))
                .child(Row.create(c)
                        .justifyContent(YogaJustify.SPACE_AROUND)
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_phone)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.CALL)))
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_message)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.MESSAGE))))
                .child(Row.create(c)
                        .justifyContent(YogaJustify.SPACE_AROUND)
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_music)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.MUSIC)))
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_oil)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.OIL))))
                .child(Row.create(c)
                        .justifyContent(YogaJustify.SPACE_AROUND)
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_parking)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.PARKING)))
                        .child(Image.create(c)
                                .drawableRes(R.drawable.navui_quit)
                                .withLayout()
                                .widthPercent(40)
                                .clickHandler(Main.onItemClick(c, NavuiLaunchEvent.Type.QUIT))))
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onItemClick(
            ComponentContext c,
            @Param NavuiLaunchEvent.Type type) {
        EventBus.getDefault().post(new NavuiLaunchEvent(type));
    }
}
