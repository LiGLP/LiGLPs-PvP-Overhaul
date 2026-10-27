package com.liglp.pvpoverhaul.swordpvp;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * The note block instruments that can be picked as alert sounds.
 */
public enum NoteSound {
	HARP("Harp", SoundEvents.NOTE_BLOCK_HARP),
	BASS("Bass", SoundEvents.NOTE_BLOCK_BASS),
	BASEDRUM("Bass Drum", SoundEvents.NOTE_BLOCK_BASEDRUM),
	SNARE("Snare", SoundEvents.NOTE_BLOCK_SNARE),
	HAT("Hat", SoundEvents.NOTE_BLOCK_HAT),
	BELL("Bell", SoundEvents.NOTE_BLOCK_BELL),
	FLUTE("Flute", SoundEvents.NOTE_BLOCK_FLUTE),
	CHIME("Chime", SoundEvents.NOTE_BLOCK_CHIME),
	GUITAR("Guitar", SoundEvents.NOTE_BLOCK_GUITAR),
	XYLOPHONE("Xylophone", SoundEvents.NOTE_BLOCK_XYLOPHONE),
	IRON_XYLOPHONE("Iron Xylophone", SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE),
	COW_BELL("Cow Bell", SoundEvents.NOTE_BLOCK_COW_BELL),
	DIDGERIDOO("Didgeridoo", SoundEvents.NOTE_BLOCK_DIDGERIDOO),
	BIT("Bit", SoundEvents.NOTE_BLOCK_BIT),
	BANJO("Banjo", SoundEvents.NOTE_BLOCK_BANJO),
	PLING("Pling", SoundEvents.NOTE_BLOCK_PLING);

	public final String label;
	public final Holder<SoundEvent> sound;

	NoteSound(String label, Holder<SoundEvent> sound) {
		this.label = label;
		this.sound = sound;
	}
}
