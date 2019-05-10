package com.coopsrc.oneplayer;

import android.content.Context;
import android.support.annotation.StringDef;

import com.coopsrc.oneplayer.core.AbsOnePlayer;
import com.coopsrc.oneplayer.ijkmedia.OneIjkPlayer;
import com.coopsrc.oneplayer.media.OneMediaPlayer;

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
    public static final String TypeExo = "exo";
    public static final String TypeExo2 = "exo2";
    public static final String TypeOne = "one";
    public static final String TypeIjk = "ijk";

    @StringDef({TypeMedia, TypeExo, TypeExo2, TypeOne, TypeIjk})
    @Retention(RetentionPolicy.RUNTIME)
    @Target({FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
    public @interface PlayerType {
    }

    public static AbsOnePlayer createPlayer(Context context, @PlayerType String type) {
        switch (type) {
            case TypeMedia:
                return new OneMediaPlayer(context);
            case TypeExo:
                return new OneMediaPlayer(context);
            case TypeExo2:
                return new OneMediaPlayer(context);
            case TypeOne:
                return new OneMediaPlayer(context);
            case TypeIjk:
                return new OneIjkPlayer(context);
            default:
                return new OneMediaPlayer(context);
        }
    }
}
