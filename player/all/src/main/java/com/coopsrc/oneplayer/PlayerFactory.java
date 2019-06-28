package com.coopsrc.oneplayer;

import android.content.Context;

import androidx.annotation.StringDef;

import com.coopsrc.oneplayer.core.OnePlayer;
import com.coopsrc.oneplayer.kernel.exo.OneExoPlayer;
import com.coopsrc.oneplayer.kernel.exo2.OneExoPlayer2;
import com.coopsrc.oneplayer.kernel.ijkmedia.OneIjkPlayer;
import com.coopsrc.oneplayer.kernel.media.OneMediaPlayer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-05-09 16:49
 */
public final class PlayerFactory {
    public static final String TypeMedia = "media";
    public static final String TypeMedia2 = "media2";
    public static final String TypeExo = "exo";
    public static final String TypeExo2 = "exo2";
    public static final String TypeMercury = "mercury";
    public static final String TypeIjk = "ijk";

    @StringDef({TypeMedia, TypeMedia2, TypeExo, TypeExo2, TypeMercury, TypeIjk})
    @Retention(RetentionPolicy.SOURCE)
    @Target({FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
    public @interface PlayerType {
    }

    public static OnePlayer createMediaPlayer(Context context) {
        return createPlayer(context, TypeMedia);
    }

    public static OnePlayer createMediaPlayer2(Context context) {
        return createPlayer(context, TypeMedia2);
    }

    public static OnePlayer createExoPlayer(Context context) {
        return createPlayer(context, TypeExo);
    }

    public static OnePlayer createExoPlayer2(Context context) {
        return createPlayer(context, TypeExo2);
    }

    public static OnePlayer createMercuryPlayer(Context context) {
        return createPlayer(context, TypeMercury);
    }

    public static OnePlayer createIjkPlayer(Context context) {
        return createPlayer(context, TypeIjk);
    }

    public static OnePlayer createPlayer(Context context, @PlayerType String type) {
        switch (type) {
            case TypeMedia:
                return new OneMediaPlayer(context);
            case TypeMedia2:
                return new OneMediaPlayer(context);
            case TypeExo:
                return new OneExoPlayer(context);
            case TypeExo2:
                return new OneExoPlayer2(context);
            case TypeMercury:
                return new OneMediaPlayer(context);
            case TypeIjk:
                return new OneIjkPlayer(context);
            default:
                return new OneMediaPlayer(context);
        }
    }
}
