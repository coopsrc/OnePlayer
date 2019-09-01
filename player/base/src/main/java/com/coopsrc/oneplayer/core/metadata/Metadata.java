package com.coopsrc.oneplayer.core.metadata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.coopsrc.oneplayer.core.utils.PlayerUtils;

import org.checkerframework.checker.nullness.compatqual.NullableType;

import java.util.Arrays;
import java.util.List;

/**
 * @author tingkuo
 * <p>
 * Date: 2019-06-27 18:13
 */
public final class Metadata implements Parcelable {

    /**
     * A metadata entry.
     */
    public interface Entry extends Parcelable {
    }

    private final Entry[] entries;

    /**
     * @param entries The metadata entries.
     */
    public Metadata(Entry... entries) {
        this.entries = entries == null ? new Entry[0] : entries;
    }

    /**
     * @param entries The metadata entries.
     */
    public Metadata(List<? extends Entry> entries) {
        if (entries != null) {
            this.entries = new Entry[entries.size()];
            entries.toArray(this.entries);
        } else {
            this.entries = new Entry[0];
        }
    }

    /* package */ Metadata(Parcel in) {
        entries = new Metadata.Entry[in.readInt()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = in.readParcelable(Entry.class.getClassLoader());
        }
    }

    /**
     * Returns the number of metadata entries.
     */
    public int length() {
        return entries.length;
    }

    /**
     * Returns the entry at the specified index.
     *
     * @param index The index of the entry.
     * @return The entry at the specified index.
     */
    public Metadata.Entry get(int index) {
        return entries[index];
    }

    /**
     * Returns a copy of this metadata with the entries of the specified metadata appended. Returns
     * this instance if {@code other} is null.
     *
     * @param other The metadata that holds the entries to append. If null, this methods returns this
     *              instance.
     * @return The metadata instance with the appended entries.
     */
    public Metadata copyWithAppendedEntriesFrom(@Nullable Metadata other) {
        if (other == null) {
            return this;
        }
        return copyWithAppendedEntries(other.entries);
    }

    /**
     * Returns a copy of this metadata with the specified entries appended.
     *
     * @param entriesToAppend The entries to append.
     * @return The metadata instance with the appended entries.
     */
    public Metadata copyWithAppendedEntries(Entry... entriesToAppend) {
        @NullableType Entry[] merged = Arrays.copyOf(entries, entries.length + entriesToAppend.length);
        System.arraycopy(entriesToAppend, 0, merged, entries.length, entriesToAppend.length);
        return new Metadata(PlayerUtils.castNonNullTypeArray(merged));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Metadata other = (Metadata) obj;
        return Arrays.equals(entries, other.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    // Parcelable implementation.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(entries.length);
        for (Entry entry : entries) {
            dest.writeParcelable(entry, 0);
        }
    }

    public static final Parcelable.Creator<Metadata> CREATOR =
            new Parcelable.Creator<Metadata>() {
                @Override
                public Metadata createFromParcel(Parcel in) {
                    return new Metadata(in);
                }

                @Override
                public Metadata[] newArray(int size) {
                    return new Metadata[size];
                }
            };
}

