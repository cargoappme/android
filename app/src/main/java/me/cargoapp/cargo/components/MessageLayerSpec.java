package me.cargoapp.cargo.components;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Image;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.messaging.MessagingApplication;

/**
 * Created by Marvin on 10/05/2017.
 */

@LayoutSpec
public class MessageLayerSpec {
    @OnCreateLayout
    static ComponentLayout onCreateLayout(
            ComponentContext c,
            @Prop MessagingApplication application,
            @Prop String author,
            @Prop Bitmap picture) {

        int appIconResId;

        if (application == MessagingApplication.MESSENGER) {
            appIconResId = R.drawable.messenger;
        } else if (application == MessagingApplication.SMS) {
            appIconResId = R.drawable.sms;
        } else {
            appIconResId = R.drawable.sms;
        }

        return Column.create(c)
                .justifyContent(YogaJustify.CENTER)
                .backgroundColor(Color.parseColor("#80000000"))
                .child(Image.create(c)
                        .drawableRes(appIconResId))
                .child(Image.create(c)
                        .drawable(new BitmapDrawable(Resources.getSystem(), picture))
                        .withLayout()
                        .marginDip(YogaEdge.TOP, -50)
                        .marginDip(YogaEdge.LEFT, 50))
                .child(Text.create(c)
                        .text(author)
                        .textColor(Color.parseColor("#ffffff"))
                        .textSizeSp(25)
                        .textAlignment(Layout.Alignment.ALIGN_CENTER))
                .build();
    }
}
